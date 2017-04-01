package utils;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import models.IdCompVector;
import models.MarcCompVector;
import models.MarcRecord;
import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerry on 3/25/17.
 */
public final class MarcUtils {

    public static MarcCompVector createCompVector(@NotNull final MarcRecord record1, @NotNull final MarcRecord record2) {
        final MarcCompVector marcCompVector = new MarcCompVector();
        marcCompVector.setCompC99id1(record1.getC99FieldId());
        marcCompVector.setCompC99id2(record2.getC99FieldId());
        marcCompVector.setCompControlField1(record1.getControlFieldId());
        marcCompVector.setCompControlField2(record2.getControlFieldId());
        marcCompVector.setCompLibraryId1(record1.getLibraryId());
        marcCompVector.setCompLibraryId2(record2.getLibraryId());
        marcCompVector.setCompPersonalName(StringComparator.comparePersonalName(record1.getPersonalName(), record2.getPersonalName()));
        marcCompVector.setCompPublisherName(StringComparator.comparePublisher(record1.getPublisherName(), record2.getPublisherName()));
        marcCompVector.setCompTitle(StringComparator.compareTitle(record1.getTitle(), record2.getTitle()));
        marcCompVector.setCompNameOfPart(StringComparator.compareNameOfPart(record1.getNameOfPart(), record2.getNameOfPart()));
        marcCompVector.setCompYearOfAuthor(StringComparator.compareYears(record1.getYearOfAuthor(), record2.getYearOfAuthor()));
        marcCompVector.setCompYearOfPublication(StringComparator.compareYears(record1.getYearOfPublication(), record2.getYearOfPublication()));
        marcCompVector.setCompInternationalStandardNumber(StringComparator.compareInternationalStandardNumbers(record1.getInternationalStandardNumbers(), record2.getInternationalStandardNumbers()));
        marcCompVector.setCompOverall(record1.getC99FieldId().equals(record2.getC99FieldId()));
        return marcCompVector;
    }

    public static void createCompVectorsFromControlFields() {
        final MarcReader reader = new MarcXmlReader(FileUtils.getNewFileInputStream(FileUtils.FILE_PATH_WITH_C99_DEDUP));
        final List<MarcRecord> marcRecords = new ArrayList<>();
        while (reader.hasNext()) {
            final Record record = reader.next();
            final String controlNumber = record.getControlNumber();
            final String c99Identifier;
            final DataField c99DataField = ((DataField) record.getVariableField("C99"));

            if (c99DataField != null) {
                c99Identifier = c99DataField.getSubfield('a').getData();
            } else throw new NullPointerException("missing c99 field!");

            marcRecords.add(new MarcRecord(controlNumber, c99Identifier));
        }

        marcRecords.forEach(t -> System.out.println(t.getControlFieldId() + "-" + t.getC99FieldId()));

        final List<IdCompVector> idCompVectors = new ArrayList<>();
        for (int i = 0; i < marcRecords.size(); i++) {
            for (int j = i + 1; j < marcRecords.size(); j++) {
//                System.out.println("comparing " + i + " with " + j);
                final MarcRecord record1 = marcRecords.get(i);
                final MarcRecord record2 = marcRecords.get(j);
                final String mergedIdentifier = record1.getControlFieldId()
                        .concat(FileUtils.CONTROL_FIELD_SEPARATOR)
                        .concat(record2.getControlFieldId());
                idCompVectors.add(
                        new IdCompVector(mergedIdentifier, record2.getC99FieldId().equals(record1.getC99FieldId())));
            }
        }

        int numberOfIdCompVectors = 0;
        for (final IdCompVector i : idCompVectors) {
            if (i.getIsDuplicate()) {
                System.out.println(i.getId() + " ; isDuplicate == " + i.getIsDuplicate());
                numberOfIdCompVectors++;
            }
        }
        System.out.println("number of id comp vectors == " + numberOfIdCompVectors);

        FileUtils.writeBeansToCsvFile(idCompVectors, "data.csv", IdCompVector.class, "id", "isDuplicate");
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

    public static List<String> getValuesBySubfieldOfDataField(final String field, final char subfield,
                                                              final String filePath) {
        final List<String> values = new ArrayList<>();
        final MarcReader reader = new MarcXmlReader(FileUtils.getNewFileInputStream(filePath));
        while (reader.hasNext()) {
            final Record record = reader.next();
            if (record != null) {
                final DataField dataField = (DataField) record.getVariableField(field);
                if (dataField != null) {
                    values.add(dataField.getSubfield(subfield).getData());
                }
            }
        }
        return values;
    }

    public static List<String> getValuesFromDataSetByControlField(final String filePath) {
        final List<String> values = new ArrayList<>();
        final MarcReader reader = new MarcXmlReader(FileUtils.getNewFileInputStream(filePath));
        while (reader.hasNext()) {
            final Record record = reader.next();
            if (record != null) {
                values.add(record.getControlNumber());
            }
        }
        return values;
    }

    public static int getNumberOfRecords(final String filePath) {
        int count = 0;
        final MarcReader reader = new MarcXmlReader(FileUtils.getNewFileInputStream(filePath));
        while (reader.hasNext()) {
            reader.next();
            count++;
        }
        return count;
    }

    public static List<String> getAllIdentifiers(final String filePath) {
        final MarcReader reader = new MarcXmlReader(FileUtils.getNewFileInputStream(filePath));
        final List<String> identifiers = new ArrayList<>();
        while (reader.hasNext()) {
            final Record record = reader.next();
            final DataField dataField = (DataField) record.getVariableField("C99"); // 245
            if (dataField != null) {
                if (dataField.getSubfield('a') != null) {
                    identifiers.add(dataField.getSubfield('a').getData());
                }
            }
        }
        return identifiers;
    }

}
