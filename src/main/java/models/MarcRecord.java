package models;

import info.debatty.java.stringsimilarity.JaroWinkler;
import utils.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by jerry on 12/5/16.
 */
public class MarcRecord implements Comparable<MarcRecord> {

    private String typeOfMaterial;
    private String c99FieldId;
    private String controlFieldId;
    private String libraryId;
    private String personalName;
    private String publisherName;
    private String title;
    private String nameOfPart;
    private String yearOfAuthor;
    private String yearOfPublication;
    private List<String> internationalStandardNumbers;
    private String blockingKey;
    private boolean isMasterDatabaseRecord;

    public MarcRecord() {}

    public MarcRecord(String controlFieldId) {
        this.controlFieldId = controlFieldId;
    }

    public MarcRecord(String controlFieldId, String c99FieldId) {
        this.controlFieldId = controlFieldId;
        this.c99FieldId = c99FieldId;
    }

    public String getTypeOfMaterial() {
        return typeOfMaterial;
    }

    public void setTypeOfMaterial(String typeOfMaterial) {
        this.typeOfMaterial = typeOfMaterial;
    }

    public String getControlFieldId() {
        return controlFieldId;
    }

    public void setControlFieldId(String controlFieldId) {
        this.controlFieldId = controlFieldId;
    }

    public String getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(String libraryId) {
        this.libraryId = libraryId;
    }

    public String getC99FieldId() {
        return c99FieldId;
    }

    public void setC99FieldId(String c99FieldId) {
        this.c99FieldId = c99FieldId;
    }

    public String getPersonalName() {
        return personalName;
    }

    public void setPersonalName(String personalName) {
        this.personalName = personalName;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisher) {
        this.publisherName = publisher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNameOfPart() {
        return nameOfPart;
    }

    public void setNameOfPart(String nameOfPart) {
        this.nameOfPart = nameOfPart;
    }

    public String getYearOfAuthor() {
        return yearOfAuthor;
    }

    public void setYearOfAuthor(String year) {
        this.yearOfAuthor = year;
    }

    public String getYearOfPublication() {
        return yearOfPublication;
    }

    public void setYearOfPublication(String yearOfPublication) {
        this.yearOfPublication = yearOfPublication;
    }

    public List<String> getInternationalStandardNumbers() {
        return internationalStandardNumbers;
    }

    public void setInternationalStandardNumbers(final List<String> internationalStandardNumbers) {
        this.internationalStandardNumbers = internationalStandardNumbers;
    }

    public String getBlockingKey() {
        return blockingKey == null ? "" : blockingKey;
    }

    public void setBlockingKey(String blockingKey) {
        this.blockingKey = blockingKey;
    }

    public boolean isMasterDatabaseRecord() {
        return isMasterDatabaseRecord;
    }

    public void setIsMasterDatabaseRecord(boolean isMasterDatabaseRecord) {
        this.isMasterDatabaseRecord = isMasterDatabaseRecord;
    }

    public void generateBlockingKey() {
        String authorAbbreviation;
        String titleAbbreviation;
        String year = "";
        if (StringUtils.isValid(getPersonalName())) {
            authorAbbreviation = StringUtils.sortCharactersInString(StringUtils.getFirstCharactersFromEachWord(getPersonalName()));
            if (StringUtils.isValid(getTitle())) {
                titleAbbreviation = StringUtils.sortCharactersInString(StringUtils.getFirstCharactersFromEachWord(getTitle()));
                if (StringUtils.isValid(getYearOfAuthor())) {
                    year = getYearOfAuthor();
                } else if (StringUtils.isValid(getYearOfPublication())) {
                    year = getYearOfPublication();
                }
                setBlockingKey(authorAbbreviation.concat("-").concat(titleAbbreviation).concat("-").concat(year));
            }
        } else if (StringUtils.isValid(getPublisherName())) {
            authorAbbreviation = StringUtils.sortCharactersInString(StringUtils.getFirstCharactersFromEachWord(getPublisherName()));
            if (StringUtils.isValid(getTitle())) {
                titleAbbreviation = StringUtils.sortCharactersInString(StringUtils.getFirstCharactersFromEachWord(getTitle()));
                if (StringUtils.isValid(getYearOfPublication())) {
                    year = getYearOfPublication();
                } else if (StringUtils.isValid(getYearOfAuthor())) {
                    year = getYearOfAuthor();
                }
                setBlockingKey(authorAbbreviation.concat("-").concat(titleAbbreviation).concat("-").concat(year));
            }
        }
    }

    @Override
    public int compareTo(MarcRecord o) {
        return this.getBlockingKey().compareTo(o.getBlockingKey());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + controlFieldId.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MarcRecord && controlFieldId.equals(((MarcRecord) obj).getControlFieldId());
    }

    @Override
    public String toString() {
        return "typeOfMaterial: " + typeOfMaterial + "\n" +
                "c99FieldId: " + c99FieldId + "\n" +
                "controlFieldId: " + controlFieldId + "\n" +
                "libraryId: " + libraryId + "\n" +
                "personalName: " + personalName + "\n" +
                "publisherName: " + publisherName + "\n" +
                "title: " + title + "\n" +
                "nameOfPart: " + nameOfPart + "\n" +
                "yearOfAuthor: " + yearOfAuthor + "\n" +
                "yearOfPublication: " + yearOfPublication + "\n" +
                "internationalStandardNumbers: " + internationalStandardNumbers + "\n" +
                "blockingKey: " + blockingKey;
    }

}
