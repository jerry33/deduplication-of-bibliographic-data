package models;

/**
 * Created by jerry on 12/5/16.
 */
public class MarcCompVector {

    private String compControlFields;
    private String compC99ids;
    private double compTitle;
    private double compPersonalName;
    private double compPublisherName;
    private double compYearOfAuthor;
    private double compYearOfPublication;
    private double compNameOfPart;
    private double compInternationalStandardNumber;
    private boolean compOverall;

    public String getCompControlFields() {
        return compControlFields;
    }

    public void setCompControlFields(String compControlFields) {
        this.compControlFields = compControlFields;
    }

    public double getCompTitle() {
        return compTitle;
    }

    public void setCompTitle(double compTitle) {
        this.compTitle = compTitle;
    }

    public double getCompPersonalName() {
        return compPersonalName;
    }

    public void setCompPersonalName(double compPersonalName) {
        this.compPersonalName = compPersonalName;
    }

    public double getCompPublisherName() {
        return compPublisherName;
    }

    public void setCompPublisherName(double compPublisher) {
        this.compPublisherName = compPublisher;
    }

    public double getCompYearOfAuthor() {
        return compYearOfAuthor;
    }

    public void setCompYearOfAuthor(double compYear) {
        this.compYearOfAuthor = compYear;
    }

    public double getCompYearOfPublication() {
        return compYearOfPublication;
    }

    public void setCompYearOfPublication(double compYearOfPublication) {
        this.compYearOfPublication = compYearOfPublication;
    }

    public double getCompNameOfPart() {
        return compNameOfPart;
    }

    public void setCompNameOfPart(double compNameOfPart) {
        this.compNameOfPart = compNameOfPart;
    }

    public double getCompInternationalStandardNumber() {
        return compInternationalStandardNumber;
    }

    public void setCompInternationalStandardNumber(double compInternationalStandardNumber) {
        this.compInternationalStandardNumber = compInternationalStandardNumber;
    }

    public boolean isCompOverall() {
        return compOverall;
    }

    public void setCompOverall(boolean compOverall) {
        this.compOverall = compOverall;
    }

    public String getCompC99ids() {
        return compC99ids;
    }

    public void setCompC99ids(String compC99ids) {
        this.compC99ids = compC99ids;
    }

    @Override
    public String toString() {
        return "compControlFields: " + compControlFields + "\n"
                + "compC99Ids: " + compC99ids + "\n"
                + "compTitle: " + compTitle + "\n"
                + "compPersonalName: " + compPersonalName + "\n"
                + "compPublisherName: " + compPublisherName + "\n"
                + "compYearOfAuthor: " + compYearOfAuthor + "\n"
                + "compYearOfPublication: " + compYearOfPublication + "\n"
                + "compNameOfPart: " + compNameOfPart + "\n"
                + "compInternationalStandardNumber: " + compInternationalStandardNumber + "\n"
                + "compOverall: " + compOverall + "\n";
    }
}
