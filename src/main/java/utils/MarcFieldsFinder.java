package utils;

import com.sun.istack.internal.Nullable;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerry on 12/10/16.
 */
public class MarcFieldsFinder {

    public static String findPersonalName(final Record r) {
        String personalName = getValueBySubfieldOfDataField(r, "100", 'a');
        if (StringUtils.isValid(personalName)) {
            return personalName;
        }
        personalName = getValueBySubfieldOfDataField(r, "700", 'a');
        if (StringUtils.isValid(personalName)) {
            return personalName;
        }
        personalName = getValueBySubfieldOfDataField(r, "264", 'b');
        if (StringUtils.isValid(personalName)) {
            return personalName;
        }
        personalName = getValueBySubfieldOfDataField(r, "928", 'a');
        if (StringUtils.isValid(personalName)) {
            return personalName;
        }
        return "";
    }

    public static String findPublisherName(final Record r) {
        String publisherName = getValueBySubfieldOfDataField(r, "264", 'b');
        if (StringUtils.isValid(publisherName)) {
            return publisherName;
        }
        publisherName = getValueBySubfieldOfDataField(r, "260", 'b');
        if (StringUtils.isValid(publisherName)) {
            return publisherName;
        }
        return "";
    }

    public static String findEditionStatement(final Record r) {
        String editionStatement = getValueBySubfieldOfDataField(r, "250", 'a');
        if (StringUtils.isValid(editionStatement)) {
            return editionStatement;
        }
        return "";
    }

    public static String findC99FieldId(final Record r) {
        return getValueBySubfieldOfDataField(r, "C99", 'a');
    }

    public static String findTitle(final Record r) {
        return getValueBySubfieldOfDataField(r, "245", 'a');
    }

    public static String findNameOfPart(final Record r) {
        return getValueBySubfieldOfDataField(r, "245", 'p');
    }

    public static String find915(final Record r) {
        return getValueBySubfieldOfDataField(r, "915", 'b');
    }

    public static String findYearOfAuthor(final Record r) {
        final String year = StringUtils.removeNonNumericCharacters(getValueBySubfieldOfDataField(r, "100", 'd'));
        if (year.length() >= 4) {
            return year.substring(0, 4);
        }
        return "";
    }

    public static String findYearOfPublication(final Record r) {
        String publisherYear = StringUtils.removeNonNumericCharacters(getValueBySubfieldOfDataField(r, "264", 'c'));
        if (publisherYear.length() >= 4) {
            return publisherYear.substring(0, 4);
        }
        publisherYear = StringUtils.removeNonNumericCharacters(getValueBySubfieldOfDataField(r, "260", 'c'));
        if (publisherYear.length() >= 4) {
            return publisherYear.substring(0, 4);
        }
        return "";
    }

    public static String findInternationalStandardNumber(final Record r) {
        final String isbn = getValueBySubfieldOfDataField(r, "020", 'a');
        if (StringUtils.isValid(isbn)) {
            return isbn;
        }
        final String issn1 = getValueBySubfieldOfDataField(r, "022", 'a');
        if (StringUtils.isValid(issn1)) {
            return issn1;
        }
        final String issn2 = getValueBySubfieldOfDataField(r, "022", 'y');
        if (StringUtils.isValid(issn2)) {
            return issn2;
        }
        return "";
    }

    public static String getValueBySubfieldOfDataField(final Record record, final String field, final char subfield) {
        final DataField dataField = (DataField) record.getVariableField(field);
        if (dataField != null) {
            if (dataField.getSubfield(subfield) != null) {
                return dataField.getSubfield(subfield).getData();
            }
        }
        return "";
    }

    public static boolean isFieldInRecord(final Record record, final String field) {
        final DataField dataField = (DataField) record.getVariableField(field);
        return dataField != null;
    }

}