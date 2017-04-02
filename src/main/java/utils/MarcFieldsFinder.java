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
            return StringUtils.standardizeString(personalName);
        }
        personalName = getValueBySubfieldOfDataField(r, "700", 'a');
        if (StringUtils.isValid(personalName)) {
            return StringUtils.standardizeString(personalName);
        }
        personalName = getValueBySubfieldOfDataField(r, "264", 'b');
        if (StringUtils.isValid(personalName)) {
            return StringUtils.standardizeString(personalName);
        }
        personalName = getValueBySubfieldOfDataField(r, "928", 'a');
        if (StringUtils.isValid(personalName)) {
            return StringUtils.standardizeString(personalName);
        }
        return "";
    }

    public static String findPublisherName(final Record r) {
        String publisherName = StringUtils.standardizeString(getValueBySubfieldOfDataField(r, "264", 'b'));
        if (StringUtils.isValid(publisherName)) {
            return publisherName;
        }
        publisherName = StringUtils.standardizeString(getValueBySubfieldOfDataField(r, "260", 'b'));
        if (StringUtils.isValid(publisherName)) {
            return publisherName;
        }
        return "";
    }

    public static String findC99FieldId(final Record r) {
        return StringUtils.standardizeString(getValueBySubfieldOfDataField(r, "C99", 'a'));
    }

    public static String findTitle(final Record r) {
        return StringUtils.standardizeString(getValueBySubfieldOfDataField(r, "245", 'a'));
    }

    public static String findNameOfPart(final Record r) {
        return StringUtils.standardizeString(getValueBySubfieldOfDataField(r, "245", 'p'));
    }

    public static String find915(final Record r) {
        return StringUtils.standardizeString(getValueBySubfieldOfDataField(r, "915", 'c'));
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

    public static List<String> findInternationalStandardNumbers(final Record r) {
        final ArrayList<String> numbers = new ArrayList<>();
        String isbn = StringUtils.removeNonNumericCharacters(getValueBySubfieldOfDataField(r, "020", 'a'));
        if (StringUtils.isValid(isbn)) {
            numbers.add(isbn);
            return numbers;
        }

        String issnA = StringUtils.removeNonNumericCharacters(getValueBySubfieldOfDataField(r, "022", 'a'));
        String issnY = StringUtils.removeNonNumericCharacters(getValueBySubfieldOfDataField(r, "022", 'y'));

        if (StringUtils.isValid(issnA)) {
            numbers.add(issnA);
        }
        if (StringUtils.isValid(issnY)) {
            numbers.add(issnY);
        }
        return numbers;
    }

    @Nullable
    public static String getValueBySubfieldOfDataField(final Record record, final String field, final char subfield) {
        final DataField dataField = (DataField) record.getVariableField(field);
        if (dataField != null) {
            if (dataField.getSubfield(subfield) != null) {
                return dataField.getSubfield(subfield).getData();
            }
        }
        return null;
    }

}
