package models;

import org.rosuda.JRI.REXP;

/**
 * Created by jerry on 4/14/17.
 */
public class ConfusionMatrix {

    private int truePositive;
    private int trueNegative;
    private int falsePositive;
    private int falseNegative;
    private long timeElapsed;

    public ConfusionMatrix(int truePositive, int trueNegative, int falsePositive, int falseNegative) {
        this.truePositive = truePositive;
        this.trueNegative = trueNegative;
        this.falsePositive = falsePositive;
        this.falseNegative = falseNegative;
    }

    public static ConfusionMatrix createFromRexp(final REXP rexp) {
        return new ConfusionMatrix(rexp.asList().at(1).asIntArray()[3],
                rexp.asList().at(1).asIntArray()[0],
                rexp.asList().at(1).asIntArray()[1],
                rexp.asList().at(1).asIntArray()[2]);
    }

    public int getTruePositive() {
        return truePositive;
    }

    public void setTruePositive(int truePositive) {
        this.truePositive = truePositive;
    }

    public int getTrueNegative() {
        return trueNegative;
    }

    public void setTrueNegative(int trueNegative) {
        this.trueNegative = trueNegative;
    }

    public int getFalsePositive() {
        return falsePositive;
    }

    public void setFalsePositive(int falsePositive) {
        this.falsePositive = falsePositive;
    }

    public int getFalseNegative() {
        return falseNegative;
    }

    public void setFalseNegative(int falseNegative) {
        this.falseNegative = falseNegative;
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public double getSensitivity() {
        return (double)truePositive / ((double)truePositive + (double)falseNegative);
    }

    public double getAccuracy() {
        return ((double)truePositive + (double)trueNegative) /
                ((double)truePositive + (double)falsePositive + (double)trueNegative + (double)falseNegative);
    }

    public double getSpecificity() {
        return (double)trueNegative / ((double)trueNegative + (double)falsePositive);
    }

    public double getPrecision() {
        return (double)truePositive / ((double)truePositive + (double)falsePositive);
    }

    public double getNegativePredictiveValue() {
        return (double)trueNegative / ((double)trueNegative + (double)falseNegative);
    }

    @Override
    public String toString() {
        return "Test dataset size: " + (truePositive + trueNegative + falsePositive + falseNegative) + "\n"
                + "True positive: " + truePositive + "\n"
                + "True negative: " + trueNegative + "\n"
                + "False positive: " + falsePositive + "\n"
                + "False negative: " + falseNegative + "\n"
                + "Accuracy: " + getAccuracy() + "\n"
                + "Sensitivity: " + getSensitivity() + "\n"
                + "Precision: " + getPrecision() + "\n"
                + "Negative predictive value: " + getNegativePredictiveValue() + "\n"
                + "Specificity: " + getSpecificity() + "\n"
                + "Time elapsed (ms): " + getTimeElapsed() + "\n";
    }
}
