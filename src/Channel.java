public class Channel {
    private String name;
    private float channelROI, lowerBound, upperBound;

    public Channel(String name, float channelROI) {
        this.name = name;
        this.channelROI = channelROI;
    }

    public void setLowerBound(float lowerBound) {
        this.lowerBound = lowerBound;
    }

    public void setUpperBound(float upperBound) {
        this.upperBound = upperBound;
    }

    public String getName() {
        return name;
    }

    public float getChannelROI() {
        return channelROI;
    }

    public float getLowerBound() {
        return lowerBound;
    }

    public float getUpperBound() {
        return upperBound;
    }

}
