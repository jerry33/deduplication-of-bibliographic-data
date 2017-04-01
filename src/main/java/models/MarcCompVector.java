package models;

import utils.StringUtils;

/**
 * Created by jerry on 12/5/16.
 */
public class MarcCompVector {

    private String compControlField1; // TODO: maybe change name to controlField1, as it is not being compared
    private String compControlField2;
    private String compC99id1;
    private String compC99id2;
    private String compLibraryId1;
    private String compLibraryId2;
    private double compTitle;
    private double compPersonalName;
    private double compPublisherName;
    private double compYearOfAuthor;
    private double compYearOfPublication;
    private double compNameOfPart;
    private double compInternationalStandardNumber;
    private boolean compOverall;

    public String getCompControlField1() {
        return compControlField1;
    }

    public void setCompControlField1(String compControlField1) {
        this.compControlField1 = compControlField1;
    }

    public String getCompControlField2() {
        return compControlField2;
    }

    public void setCompControlField2(String compControlField2) {
        this.compControlField2 = compControlField2;
    }

    public String getCompC99id1() {
        return compC99id1;
    }

    public void setCompC99id1(String compC99id1) {
        this.compC99id1 = compC99id1;
    }

    public String getCompC99id2() {
        return compC99id2;
    }

    public void setCompC99id2(String compC99id2) {
        this.compC99id2 = compC99id2;
    }

    public String getCompLibraryId1() {
        return compLibraryId1;
    }

    public void setCompLibraryId1(String compLibraryId1) {
        this.compLibraryId1 = compLibraryId1;
    }

    public String getCompLibraryId2() {
        return compLibraryId2;
    }

    public void setCompLibraryId2(String compLibraryId2) {
        this.compLibraryId2 = compLibraryId2;
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

    public boolean isDuplicate() {
        return compOverall;
    }

    @Override
    public String toString() {
        return "compControlField1: " + compControlField1 + "\n"
                + "compControlField2: " + compControlField2 + "\n"
                + "compC99id1: " + compC99id1 + "\n"
                + "compC99id2: " + compC99id2 + "\n"
                + "compLibraryId1: " + compLibraryId1 + "\n"
                + "compLibraryId2: " + compLibraryId2 + "\n"
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
