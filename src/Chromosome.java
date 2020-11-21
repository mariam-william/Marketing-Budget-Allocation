import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Chromosome {
    private ArrayList<Float> genes = new ArrayList<>();
    private float fitness = 0;

    public Chromosome() {
    }

    public Chromosome(ArrayList<Float> genes) {
        this.genes = genes;
        fitnessEvaluation();
    }

    public void initialize() {
        float totalInvestment = 0;
        for (int i = 0; i < MBA.nChannels; i++) {
            float lb = MBA.channels.get(i).getLowerBound();
            float ub = MBA.channels.get(i).getUpperBound();

            if (ub == -1)
                ub = MBA.commonUB + lb;
            if ((totalInvestment + ub) > MBA.marketingBudget)
                ub = MBA.marketingBudget - totalInvestment;
            if (ub < lb) {
                i = -1;
                totalInvestment = 0;
                genes.clear();
            }
            else{
                float randValue = lb + (new Random().nextFloat() * (ub - lb));
                totalInvestment += randValue;
                genes.add(randValue);
            }
        }
        fitnessEvaluation();
    }

    public boolean isFeasible() {
        return (genes.stream().mapToDouble(Float::doubleValue).sum() <= MBA.marketingBudget);
    }

    public void handleInfeasiblity() {
        ArrayList<Channel> sortedChannels = new ArrayList<>();
        sortedChannels.addAll(MBA.channels);
        sortedChannels.sort(Channel.sortByROI);
        double extraInvestment = genes.stream().mapToDouble(Float::doubleValue).sum() - MBA.marketingBudget;
        while (extraInvestment > 0) {
            Channel channel = sortedChannels.remove(0);
            int index = MBA.channels.indexOf(channel);
            float diff = genes.get(index) - channel.getLowerBound();
            if (diff <= extraInvestment) {
                genes.set(index, channel.getLowerBound());
                extraInvestment -= diff;
            } else {
                float newInv = (float) (genes.get(index) - extraInvestment);
                genes.set(index, newInv);
                extraInvestment = 0;
            }
        }
    }

    public void fitnessEvaluation() {
        fitness = 0;
        for (int i = 0; i < genes.size(); i++)
            fitness += (genes.get(i) * (MBA.channels.get(i).getChannelROI() / 100));
    }

    public ArrayList<Float> getGenes() {
        return genes;
    }

    public float getFitness() {
        return fitness;
    }

    public static Comparator<Chromosome> sortByFitness = (o1, o2) -> {
        return Float.compare(o2.fitness, o1.fitness);
    };
}
