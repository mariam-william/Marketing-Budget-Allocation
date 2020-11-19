import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Chromosome {
    private ArrayList<Float> genes = new ArrayList<>();
    private float fitness = 0;

    public Chromosome() {
    }

    public void initialize() {
        float newUB = MBA.marketingBudget;
        for (int i1 = 0; i1 < MBA.nChannels; i1++) {
            float temp = MBA.channels.get(i1).getLowerBound();
            if(temp != -1)
                newUB -= temp;
        }
        for (int i = 0; i < MBA.nChannels; i++) {
            float lb = MBA.channels.get(i).getLowerBound();
            float ub = MBA.channels.get(i).getUpperBound();
            float randd;
            if(lb != -1 && ub != -1)
                randd = lb + (new Random().nextFloat() * (ub - lb));
            else if(lb == -1)
                randd = new Random().nextFloat() * ub;
            else {
                ub = newUB + lb;
                randd = lb + (new Random().nextFloat() * (ub -lb));
            }
            genes.add(randd);
        }
        //handleInfeasiblity();
        //fitnessEvaluation();
    }

    public void handleInfeasiblity(){
        for (int i = 0; i < genes.size(); i++) {

        }
    }


}
