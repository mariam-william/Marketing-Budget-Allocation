import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Marketing Budget Allocation Problem
 **/

public class MBA {
    public static int nChannels, populationSize = 10;
    public static float marketingBudget;
    public static float commonUB;
    public static Chromosome bestSolution1, bestSolution2;
    public static ArrayList<Channel> channels = new ArrayList<>();
    public static ArrayList<Chromosome> generation = new ArrayList<>();
    public static ArrayList<Chromosome> offsprings = new ArrayList<>();

    public static void getInput() {
        Scanner reader = new Scanner(System.in);

        System.out.println("Enter the marketing budget (in thousands):");
        marketingBudget = reader.nextFloat();
        commonUB = marketingBudget;

        System.out.println("\nEnter the number of marketing channels:");
        nChannels = reader.nextInt();

        String channel;
        System.out.println("\nEnter the name and ROI (in %) of each channel separated by space:");
        reader.nextLine();
        for (int i = 0; i < nChannels; i++) {
            channel = reader.nextLine();
            int index = channel.lastIndexOf(" ");
            Channel chTemp = new Channel(channel.substring(0, index), Float.parseFloat(channel.substring(index + 1, channel.length())));
            channels.add(chTemp);
        }

        System.out.println("\nEnter the lower (k) and upper bounds (%) of investment in each channel:\n(enter x if there is no bound)");
        for (int i = 0; i < nChannels; i++) {
            String lb = reader.next();
            String ub = reader.next();
            if (lb.equalsIgnoreCase("x"))
                channels.get(i).setLowerBound(-1);
            else {
                channels.get(i).setLowerBound(Float.parseFloat(lb));
                commonUB -= Float.parseFloat(lb);
            }
            if (ub.equalsIgnoreCase("x"))
                channels.get(i).setUpperBound(-1);
            else {
                float temp = (Float.parseFloat(ub) / 100) * marketingBudget;
                channels.get(i).setUpperBound(temp);
            }
        }

        System.out.println("\nPlease wait while running the GA...\n");
    }

    public static boolean contain(Chromosome c) {
        for (Chromosome chromosome : generation) {
            if (c.getGenes() == chromosome.getGenes())
                return true;
        }
        return false;
    }

    public static void initPopulation() {
        generation.clear();
        for (int i = 0; i < populationSize; i++) {
            Chromosome chromosome = new Chromosome();
            chromosome.initialize();
            if (contain(chromosome))
                i--;
            else
                generation.add(chromosome);
        }
    }

    public static ArrayList<Integer> tournamentSelection(int k, int nParents) {
        ArrayList<Integer> chromosomeIndex = new ArrayList<>();
        ArrayList<Chromosome> parents = new ArrayList<>();
        for (int i = 0; i < nParents; i++) {
            ArrayList<Integer> fitIndices = new ArrayList<>();
            for (int j = 0; j < k; j++) {
                int fitIndex = ThreadLocalRandom.current().nextInt(0, generation.size());
                if (!(fitIndices.contains(fitIndex)) && !(chromosomeIndex.contains(fitIndex)) && !(parents.contains(generation.get(fitIndex)))) {
                    fitIndices.add(fitIndex);
                } else
                    j--;
            }
            ArrayList<Float> values = new ArrayList<>();
            for (int j = 0; j < fitIndices.size(); j++)
                values.add(generation.get(fitIndices.get(j)).getFitness());
            float max = Collections.max(values);
            chromosomeIndex.add(fitIndices.get(values.indexOf(max)));
            parents.add(generation.get(chromosomeIndex.get(chromosomeIndex.size() - 1)));
        }
        return chromosomeIndex;
    }

    public static void crossover(int parent1, int parent2) {
        float Pc = (float) 0.9;
        double rc = Math.random();
        if (rc <= Pc) {
            int cp1, cp2;
            cp1 = ThreadLocalRandom.current().nextInt(1, generation.get(0).getGenes().size());
            do {
                cp2 = ThreadLocalRandom.current().nextInt(1, generation.get(0).getGenes().size());
            } while (cp1 == cp2);
            if (cp2 < cp1) {
                int temp = cp1;
                cp1 = cp2;
                cp2 = temp;
            }
            ArrayList<Float> genes1 = new ArrayList<>();
            ArrayList<Float> genes2 = new ArrayList<>();
            genes1.addAll(generation.get(parent1).getGenes().subList(0, cp1));
            genes1.addAll(generation.get(parent2).getGenes().subList(cp1, cp2));
            genes1.addAll(generation.get(parent1).getGenes().subList(cp2, generation.get(0).getGenes().size()));
            genes2.addAll(generation.get(parent2).getGenes().subList(0, cp1));
            genes2.addAll(generation.get(parent1).getGenes().subList(cp1, cp2));
            genes2.addAll(generation.get(parent2).getGenes().subList(cp2, generation.get(0).getGenes().size()));
            offsprings.add(new Chromosome(genes1));
            offsprings.add(new Chromosome(genes2));
        } else {
            offsprings.add(generation.get(parent1));
            offsprings.add(generation.get(parent2));
        }
    }

    public static void uniformMutation(){
        float Pm = (float) 0.1;
        for (int i = offsprings.size()-2; i < offsprings.size(); i++) {
            for (int j = 0; j < offsprings.get(i).getGenes().size(); j++) {
                double rm = Math.random();
                if (rm <= Pm){
                    float newValue = offsprings.get(i).getGenes().get(j);
                    float deltaL = offsprings.get(i).getGenes().get(j) - channels.get(j).getLowerBound();
                    float deltaU = channels.get(j).getUpperBound() - offsprings.get(i).getGenes().get(j);
                    float delta;

                    double r1 = Math.random();
                    if(r1 <= 0.5)
                        delta = deltaL;
                    else
                        delta = deltaU;

                    float r2 = (new Random().nextFloat() * (delta));
                    if(delta == deltaL)
                        newValue -= r2;
                    else
                        newValue += r2;

                    offsprings.get(i).getGenes().set(j, newValue);
                }
            }
        }
    }

    public static void non_uniformMutation(int t, int nGenerations){
        float Pm = 0.1f;
        float b = 0.8f;
        float gen = (t * 1.0f)/nGenerations;
        for (int i = offsprings.size()-2; i < offsprings.size(); i++) {
            for (int j = 0; j < offsprings.get(i).getGenes().size(); j++) {
                double rm = Math.random();
                if (rm <= Pm){
                    float newValue = offsprings.get(i).getGenes().get(j);
                    float deltaL = offsprings.get(i).getGenes().get(j) - channels.get(j).getLowerBound();
                    float deltaU = channels.get(j).getUpperBound() - offsprings.get(i).getGenes().get(j);
                    float y, delta_ty;

                    double r1 = Math.random();
                    if(r1 <= 0.5)
                        y = deltaL;
                    else
                        y = deltaU;

                    double r = Math.random();
                    delta_ty = (float) (y * (1 - Math.pow(r, Math.pow((1 - gen), b))));

                    if(y == deltaL)
                        newValue -= delta_ty;
                    else
                        newValue += delta_ty;

                    offsprings.get(i).getGenes().set(j, newValue);
                }
            }
        }
    }

    public static void elitistReplacementStrategy() {
        ArrayList<Chromosome> newGeneration = new ArrayList<>();
        newGeneration.addAll(offsprings);
        int difference = populationSize - offsprings.size();
        generation.sort(Chromosome.sortByFitness);
        newGeneration.addAll(generation.subList(0, difference));
        generation = newGeneration;
    }

    public static void getBestSolution(Chromosome bestSolution) {
        System.out.println("\nThe final marketing budget allocation is:\n");
        for (int i = 0; i < nChannels; i++)
            System.out.println(channels.get(i).getName() + " -> " + bestSolution.getGenes().get(i) + "K");
        System.out.println("\nThe total profit is " + bestSolution.getFitness() + "K");
    }

    public static void writeToFile(int runNum) throws IOException {
        generation.sort(Chromosome.sortByFitness);
        BufferedWriter writer = null;
        writer = new BufferedWriter(new FileWriter("results.txt"));

        if(runNum == 0)
            writer.write("Uniform Mutation:\n\n");
        else if(runNum == 20)
            writer.write("Non-uniform Mutation:\n\n");

        writer.append("Iteration ").append(String.valueOf(runNum + 1)).append(":");
        writer.append("\nThe final marketing budget allocation is:\n");
        for (int i = 0; i < nChannels; i++)
            writer.append(channels.get(i).getName()).append(" -> ").append(String.valueOf(generation.get(0).getGenes().get(i))).append("K \n");
        writer.append("Total profit = ");
        writer.append(String.valueOf(generation.get(0).getFitness()));
        writer.append('K');
        writer.append('\n');

        if(runNum < 20){
            if(runNum == 0)
                bestSolution1 = generation.get(0);
            else if(generation.get(0).getFitness() > bestSolution1.getFitness())
                bestSolution1 = generation.get(0);
        }
        else{
            if(runNum == 20)
                bestSolution2 = generation.get(0);
            else if(generation.get(0).getFitness() > bestSolution2.getFitness())
                bestSolution2 = generation.get(0);
        }
    }

    public static void runGA() throws IOException {
        for (int i = 0; i < 40; i++) {
            initPopulation();
            for (int j = 0; j < 5; j++) {
                offsprings.clear();
                while (offsprings.size() != 8){
                    ArrayList<Integer> parents = tournamentSelection(4, 2);
                    crossover(parents.get(0), parents.get(1));
                    if(i < 20)
                        uniformMutation();
                    else
                        non_uniformMutation(j, 5);
                }
                for (int k = 0; k < offsprings.size(); k++) {
                    offsprings.get(k).handleInfeasiblity();
                    offsprings.get(k).fitnessEvaluation();
                }
                elitistReplacementStrategy();
            }
            writeToFile(i);
        }

        System.out.println("The best solution obtained from running the algorithm with uniform mutation:\n");
        getBestSolution(bestSolution1);
        System.out.println("\nThe best solution obtained from running the algorithm with non-uniform mutation:\n");
        getBestSolution(bestSolution2);
    }

    public static void main(String[] args) throws IOException {
        getInput();
        runGA();
    }
}
