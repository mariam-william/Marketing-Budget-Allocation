import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Marketing Budget Allocation Problem
 **/

public class MBA {
    public static int nChannels, populationSize = 5;
    public static float marketingBudget;
    public static float commonUB;
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

    public void crossover(int parent1, int parent2) {
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

    public void elitistReplacementStrategy() {
        ArrayList<Chromosome> newGeneration = new ArrayList<>();
        newGeneration.addAll(offsprings);
        int difference = populationSize - offsprings.size();
        generation.sort(Chromosome.sortByFitness);
        newGeneration.addAll(generation.subList(0, difference));
        generation = newGeneration;
    }

    public void getFinalSolution() {
        generation.sort(Chromosome.sortByFitness);
        System.out.println("\nThe final marketing budget allocation is:\n");
        for (int i = 0; i < nChannels; i++) {
            System.out.println(channels.get(i).getName() + " -> " + generation.get(0).getGenes().get(i) + "K");
        }
        System.out.println("\nThe total profit is " + generation.get(0).getFitness() + "K");
    }

    public void writeToFile(int runNum) throws IOException {
        BufferedWriter writer = null;
        writer = new BufferedWriter(new FileWriter("results.txt"));
        writer.write("Run no.: " + runNum + "\tPobulation Size = " + populationSize + "\n");
        writer.append("Total profit = ");
        writer.append(String.valueOf(generation.get(0).getFitness()));
        writer.append('K');
        writer.append('\n');
    }

    public static void runGA() {
        initPopulation();
        /*System.out.println();
        for (Chromosome chromosome : generation) {
            System.out.println(Arrays.toString(chromosome.getGenes().toArray()));
            System.out.println("Sum = " + chromosome.getGenes().stream().mapToDouble(Float::doubleValue).sum());
            System.out.println("Fitness = " + chromosome.getFitness());
        }
        System.out.println();*/
    }

    public static void main(String[] args) {
        getInput();
        runGA();
    }
}
