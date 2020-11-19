import java.util.ArrayList;
import java.util.Scanner;

/**
 * Marketing Budget Allocation Problem
 **/

public class MBA {
    public static int marketingBudget, nChannels;
    public static Float totalProfit;
    public static ArrayList<Channel> channels = new ArrayList<>();

    public void getInput() {
        Scanner reader = new Scanner(System.in);

        System.out.println("Enter the marketing budget (in thousands):");
        marketingBudget = reader.nextInt();

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
            else
                channels.get(i).setLowerBound(Float.parseFloat(lb));
            if (ub.equalsIgnoreCase("x"))
                channels.get(i).setUpperBound(-1);
            else {
                float temp = (Float.parseFloat(ub) / 100) * marketingBudget;
                channels.get(i).setUpperBound(temp);
            }
        }

        /*for (int i = 0; i < channels.size(); i++) {
            System.out.println(channels.get(i).getName() + " " + channels.get(i).getChannelROI());
            System.out.println(channels.get(i).getLowerBound() + " " + channels.get(i).getUpperBound());
        }*/

        System.out.println("\nPlease wait while running the GA...\n");
    }


    public static void main(String[] args) {
        MBA mba = new MBA();
        mba.getInput();
    }
}
