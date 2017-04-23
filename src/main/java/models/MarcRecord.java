package models;

import utils.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by jerry on 12/5/16.
 */
public class MarcRecord implements Comparable<MarcRecord> {

    public static final String COLUMN_PRIMARY_KEY = "id";
    public static final String COLUMN_TYPE_OF_MATERIAL = "type_of_material";
    public static final String COLUMN_C99_FIELD_ID = "c99_field_id";
    public static final String COLUMN_CONTROL_FIELD_ID = "control_field_id";
    public static final String COLUMN_LIBRARY_ID = "library_id";
    public static final String COLUMN_UNIQUE_ID = "unique_id";
    public static final String COLUMN_BLOCKING_KEY = "blocking_key";
    public static final String COLUMN_PERSONAL_NAME = "personal_name";
    public static final String COLUMN_PUBLISHER_NAME = "publisher_name";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_NAME_OF_PART = "name_of_part";
    public static final String COLUMN_YEAR_OF_AUTHOR = "year_of_author";
    public static final String COLUMN_YEAR_OF_PUBLICATION = "year_of_publication";
    public static final String COLUMN_INTERNATIONAL_STANDARD_NUMBER = "international_standard_number";
    public static final String COLUMN_915 = "column_915";

    private int primaryKey;
    private TypeOfMaterial typeOfMaterial;
    private String c99FieldId;
    private String controlFieldId;
    private String libraryId;
    private String personalName;
    private String publisherName;
    private String title;
    private String nameOfPart;
    private String yearOfAuthor;
    private String yearOfPublication;
    private String column915;
    private String internationalStandardNumber;

    private String c99FieldIdRaw;
    private String personalNameRaw;
    private String publisherNameRaw;
    private String titleRaw;
    private String nameOfPartRaw;
    private String yearOfAuthorRaw;
    private String yearOfPublicationRaw;
    private String column915Raw;
    private String internationalStandardNumberRaw;

    private String blockingKey;
    private boolean isMasterDatabaseRecord;
    private boolean isInAnyBlock;

    public MarcRecord() {}

    public MarcRecord(String controlFieldId) {
        this.controlFieldId = controlFieldId;
    }

    public MarcRecord(String controlFieldId, String c99FieldId) {
        this.controlFieldId = controlFieldId;
        this.c99FieldId = c99FieldId;
    }

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public TypeOfMaterial getTypeOfMaterial() {
        return typeOfMaterial;
    }

    public void setTypeOfMaterial(TypeOfMaterial typeOfMaterial) {
        this.typeOfMaterial = typeOfMaterial;
    }

    public String getControlFieldId() {
        return controlFieldId == null ? "" : controlFieldId;
    }

    public void setControlFieldId(String controlFieldId) {
        this.controlFieldId = controlFieldId;
    }

    public String getLibraryId() {
        return libraryId == null ? "" : libraryId;
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

    public String getColumn915() {
        return column915;
    }

    public void setColumn915(String column915) {
        this.column915 = column915;
    }

    public String getColumn915Raw() {
        return column915Raw;
    }

    public void setColumn915Raw(String column915raw) {
        this.column915Raw = column915raw;
    }

    public String getInternationalStandardNumber() {
        return internationalStandardNumber;
    }

    public void setInternationalStandardNumber(String internationalStandardNumber) {
        this.internationalStandardNumber = internationalStandardNumber;
    }

    public String getInternationalStandardNumberRaw() {
        return internationalStandardNumberRaw;
    }

    public void setInternationalStandardNumberRaw(String internationalStandardNumberRaw) {
        this.internationalStandardNumberRaw = internationalStandardNumberRaw;
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

    public String getC99FieldIdRaw() {
        return c99FieldIdRaw;
    }

    public void setC99FieldIdRaw(String c99FieldIdRaw) {
        this.c99FieldIdRaw = c99FieldIdRaw;
    }

    public String getPersonalNameRaw() {
        return personalNameRaw;
    }

    public void setPersonalNameRaw(String personalNameRaw) {
        this.personalNameRaw = personalNameRaw;
    }

    public String getPublisherNameRaw() {
        return publisherNameRaw;
    }

    public void setPublisherNameRaw(String publisherNameRaw) {
        this.publisherNameRaw = publisherNameRaw;
    }

    public String getTitleRaw() {
        return titleRaw;
    }

    public void setTitleRaw(String titleRaw) {
        this.titleRaw = titleRaw;
    }

    public String getNameOfPartRaw() {
        return nameOfPartRaw;
    }

    public void setNameOfPartRaw(String nameOfPartRaw) {
        this.nameOfPartRaw = nameOfPartRaw;
    }

    public String getYearOfAuthorRaw() {
        return yearOfAuthorRaw;
    }

    public void setYearOfAuthorRaw(String yearOfAuthorRaw) {
        this.yearOfAuthorRaw = yearOfAuthorRaw;
    }

    public String getYearOfPublicationRaw() {
        return yearOfPublicationRaw;
    }

    public void setYearOfPublicationRaw(String yearOfPublicationRaw) {
        this.yearOfPublicationRaw = yearOfPublicationRaw;
    }

    public boolean isInAnyBlock() {
        return isInAnyBlock;
    }

    public void setIsInAnyBlock(boolean isInAnyBlock) {
        this.isInAnyBlock = isInAnyBlock;
    }

    public void generateBlockingKey() {
        String authorAbbreviation;
        String titleAbbreviation;
        String year = "";
        if (StringUtils.isValid(getPersonalName())) {
            authorAbbreviation = StringUtils.sortCharactersInString(StringUtils.getFirstCharactersFromEachWord(getPersonalName()));
            if (StringUtils.isValid(getTitle())) {
                titleAbbreviation = StringUtils.sortCharactersInString(StringUtils.getFirstCharactersFromEachWord(getTitle()));
                if (StringUtils.isValid(getYearOfPublication())) {
                    year = getYearOfPublication();
                } else if (StringUtils.isValid(getYearOfAuthor())) {
                    year = getYearOfAuthor();
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
        } else if (StringUtils.isValid(getTitle())) {
            if (StringUtils.isValid(getYearOfPublication())) {
                setBlockingKey(StringUtils.sortCharactersInString(StringUtils.getFirstCharactersFromEachWord(getTitle())).concat(getYearOfPublication()));
            } else if (StringUtils.isValid(getYearOfAuthor())) {
                setBlockingKey(StringUtils.sortCharactersInString(StringUtils.getFirstCharactersFromEachWord(getTitle())).concat(getYearOfAuthor()));
            } else {
                setBlockingKey(StringUtils.sortCharactersInString(StringUtils.getFirstCharactersFromEachWord(getTitle())));
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
        return obj instanceof MarcRecord
                && getControlFieldId().equals(((MarcRecord) obj).getControlFieldId())
                && getLibraryId().equals(((MarcRecord) obj).getLibraryId());
    }

    public void bindData(final ResultSet rs) {
        try {
            // raw data
            setPrimaryKey(rs.getInt(MarcRecord.COLUMN_PRIMARY_KEY));
            setTypeOfMaterial(new TypeOfMaterial(rs.getString(MarcRecord.COLUMN_TYPE_OF_MATERIAL)));
            setC99FieldIdRaw(rs.getString(MarcRecord.COLUMN_C99_FIELD_ID));
            setControlFieldId(rs.getString(MarcRecord.COLUMN_CONTROL_FIELD_ID));
            setLibraryId(rs.getString(MarcRecord.COLUMN_LIBRARY_ID));
            setPersonalNameRaw(rs.getString(MarcRecord.COLUMN_PERSONAL_NAME));
            setPublisherNameRaw(rs.getString(MarcRecord.COLUMN_PUBLISHER_NAME));
            setTitleRaw(rs.getString(MarcRecord.COLUMN_TITLE));
            setNameOfPartRaw(rs.getString(MarcRecord.COLUMN_NAME_OF_PART));
            setYearOfAuthorRaw(rs.getString(MarcRecord.COLUMN_YEAR_OF_AUTHOR));
            setYearOfPublicationRaw(rs.getString(MarcRecord.COLUMN_YEAR_OF_PUBLICATION));
            setColumn915Raw(rs.getString(MarcRecord.COLUMN_915));
            setInternationalStandardNumberRaw(rs.getString(MarcRecord.COLUMN_INTERNATIONAL_STANDARD_NUMBER));
            setBlockingKey(rs.getString(MarcRecord.COLUMN_BLOCKING_KEY));

            // standardized data
            setC99FieldId(StringUtils.standardizeString(getC99FieldIdRaw()));
            setPersonalName(StringUtils.standardizeString(getPersonalNameRaw()));
            setPublisherName(StringUtils.standardizeString(getPublisherNameRaw()));
            setTitle(StringUtils.standardizeString(getTitleRaw()));
            setNameOfPart(StringUtils.standardizeString(getNameOfPartRaw()));
            setYearOfAuthor(StringUtils.standardizeYearOfAuthor(getYearOfAuthorRaw()));
            setYearOfPublication(StringUtils.standardizeYearOfPublication(getYearOfPublicationRaw()));
            setColumn915(StringUtils.standardizeString(getColumn915Raw()));
            setInternationalStandardNumber(StringUtils.removeNonNumericCharactersAndDots(getInternationalStandardNumberRaw()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getFormatted() {
        return "Názov diela: " + getTitleRaw() + "\n"
                + "Autor: " + getPersonalNameRaw() + "\n"
                + "Vydavateľ: " + getPublisherNameRaw() + "\n"
                + "Typ záznamu: " + getTypeOfMaterial().getBibliographicLevel().getFormattedTitle() + ", " + getTypeOfMaterial().getTypeOfRecord().getFormattedTitle() + "\n"
                + "Rok vydania: " + (StringUtils.isValid(getYearOfAuthorRaw()) ? getYearOfAuthorRaw() : getYearOfPublicationRaw()) + "\n"
                + "ISBN / ISSN: " + getInternationalStandardNumberRaw() + "\n"
                + "Id knižničného katalógu: " + getLibraryId() + "\n"
                + "Id bibliografického diela: " + getControlFieldId();
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
                "915: " + column915 + "\n" +
                "internationalStandardNumber: " + internationalStandardNumber + "\n" +
                "blockingKey: " + blockingKey;
    }

}
