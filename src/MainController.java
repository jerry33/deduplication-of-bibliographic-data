import com.opencsv.CSVWriter;
import com.opencsv.bean.BeanToCsv;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.sun.istack.internal.Nullable;
import info.debatty.java.stringsimilarity.JaroWinkler;
import info.debatty.java.stringsimilarity.Levenshtein;
import models.IdCompVector;
import models.MarcCompVector;
import models.MarcRecord;
import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.MarcXmlWriter;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import utils.MarcFieldsFinder;
import utils.StringComparator;
import utils.StringUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jerry on 11/28/16.
 */
public class MainController {

    private static final String CONTROL_FIELD_SEPARATOR = "-";
    private static final String FILE_NAME_ID_COMP_VECTORS = "id_comp_vectors.csv";
    private static final String FILE_NAME_MARC_COMP_VECTORS = "assets/marc_comp_vectors85_new.csv";
    private static final String FILE_PATH_WITH_C99 = "/Users/Jerry/Desktop/idea/assets/Li14to16sC99a.xml";
    private static final String FILE_PATH_WITH_C99_DEDUP= "/Users/Jerry/Desktop/idea/assets/Vy14to16sC99a_with_dedup.xml";
    //    public static final String FILE_PATH_WITH_C99 = "/Users/Jerry/Desktop/idea/assets/Vy14to16sC99a.xml"; // small: 6,7 MB
    private static final String FILE_PATH_DUPLICATED_ENTRIES = "duplicated5.xml";

    private int numberOfRecords = 0;
    private HashSet<String> c99IdentifiersHashSet;

    public void start() {
        final long beginning = System.nanoTime();

        c99IdentifiersHashSet = printDuplicateIdentifiers();
        saveAllMarcCompVectorsToCsv();
//        printDuplicateIdentifiersWithWrite();
//        createCompVectorsFromControlFields();

        final long end = System.nanoTime();
        final long result = end - beginning;
        final long resultInMillis = result / 1000000;
        System.out.println("Time elapsed: " + resultInMillis + " ms");
        System.out.println("Nano: " + result + " ms");
    }

    private void saveMarcCompVectorsToCsv() {
        final List<Record> records = getAllRecords();
        final List<MarcRecord> marcRecords = new ArrayList<>();
        for (final Record r : records) {
            final MarcRecord marcRecord = new MarcRecord();
            final String charAt6 = String.valueOf(r.getLeader().marshal().charAt(6));
            final String charAt7 = String.valueOf(r.getLeader().marshal().charAt(7));
            final String typeOfMaterial = String.valueOf(charAt6.concat(charAt7));

            marcRecord.setTypeOfMaterial(typeOfMaterial);
            marcRecord.setControlFieldId(r.getControlNumber());
            marcRecord.setC99FieldId(MarcFieldsFinder.findC99FieldId(r));
            marcRecord.setPersonalName(MarcFieldsFinder.findPersonalName(r));
            marcRecord.setPublisherName(MarcFieldsFinder.findPublisherName(r));
            marcRecord.setTitle(MarcFieldsFinder.findTitle(r));
            marcRecord.setNameOfPart(MarcFieldsFinder.findNameOfPart(r));
            marcRecord.setYearOfAuthor(MarcFieldsFinder.findYearOfAuthor(r));
            marcRecord.setYearOfPublication(MarcFieldsFinder.findYearOfPublication(r));
            marcRecord.setInternationalStandardNumbers(MarcFieldsFinder.findInternationalStandardNumbers(r));
            marcRecord.generateBlockingKey();

            marcRecords.add(marcRecord);
        }

        Collections.sort(marcRecords);
//        for (int i = 0; i < marcRecords.size(); i++) {
//            final MarcRecord marcRecord = marcRecords.get(i);
//            System.out.println("RECORD " + i);
//            System.out.println("---------------------");
//            System.out.println("TYPE OF MATERIAL: " + marcRecord.getTypeOfMaterial());
//            System.out.println("PERSONAL NAME: " + marcRecord.getPersonalName());
//            System.out.println("PUBLISHER: " + marcRecord.getPublisherName());
//            System.out.println("TITLE: " + marcRecord.getTitle());
//            System.out.println("NAME OF PART: " + marcRecord.getNameOfPart());
//            System.out.println("YEAR OF AUTHOR: " + marcRecord.getYearOfAuthor());
//            System.out.println("YEAR OF PUBLICATION: " + marcRecord.getYearOfPublication());
//            System.out.println("BLOCKING KEY: " + marcRecord.getBlockingKey());
//            System.out.println();
//            System.out.println();
//        }

        final List<List<MarcRecord>> listOfBlockingLists = new ArrayList<>();

        int startOfBlock = 0;
        List<MarcRecord> blockingList = new ArrayList<>();
        final JaroWinkler jaroWinkler = new JaroWinkler();
        final Levenshtein levenshtein = new Levenshtein();
        for (int i = 0; i < marcRecords.size() - 1; i++) {
            if (startOfBlock == i) {
                blockingList.add(marcRecords.get(i));
            }
            if (levenshtein.distance(marcRecords.get(startOfBlock).getBlockingKey(), marcRecords.get(i + 1).getBlockingKey()) <= 3) { // jaroWinkler.similarity(marcRecords.get(startOfBlock).getBlockingKey(), marcRecords.get(i + 1).getBlockingKey()) >= 0.95
                blockingList.add(marcRecords.get(i + 1));
            } else {
                startOfBlock = i + 1;
                final List<MarcRecord> tempBlockingList = new ArrayList<>(blockingList);
                listOfBlockingLists.add(tempBlockingList);
                blockingList.clear();
            }
        }

//        for (int i = 0; i < listOfBlockingLists.size(); i++) {
//            System.out.println("blocking list = " + listOfBlockingLists.get(i));
//        }

        final List<MarcCompVector> marcCompVectors = new ArrayList<>();
        final List<MarcCompVector> vectorsDuplicated = new ArrayList<>();
        final List<MarcCompVector> vectorsNonDuplicated = new ArrayList<>();
        for (int h = 0; h < listOfBlockingLists.size(); h++) {
            final List<MarcRecord> blockOfMarcRecords = listOfBlockingLists.get(h);
            for (int i = 0; i < blockOfMarcRecords.size(); i++) {
                for (int j = i + 1; j< blockOfMarcRecords.size(); j++) {
                    final MarcRecord record1 = blockOfMarcRecords.get(i);
                    final MarcRecord record2 = blockOfMarcRecords.get(j);
                    final MarcCompVector marcCompVector = new MarcCompVector();
                    final boolean typesOfMaterialMatch = record1.getTypeOfMaterial().equals(record2.getTypeOfMaterial());
                    if (typesOfMaterialMatch) {
                        marcCompVector.setCompControlFields(record1.getControlFieldId() + ";" + record2.getControlFieldId());
                        marcCompVector.setCompPersonalName(StringComparator.comparePersonalName(record1.getPersonalName(), record2.getPersonalName()));
                        marcCompVector.setCompPublisherName(StringComparator.comparePublisher(record1.getPublisherName(), record2.getPublisherName()));
                        marcCompVector.setCompTitle(StringComparator.compareTitle(record1.getTitle(), record2.getTitle()));
                        marcCompVector.setCompNameOfPart(StringComparator.compareNameOfPart(record1.getNameOfPart(), record2.getNameOfPart()));
                        marcCompVector.setCompYearOfAuthor(StringComparator.compareYears(record1.getYearOfAuthor(), record2.getYearOfAuthor()));
                        marcCompVector.setCompYearOfPublication(StringComparator.compareYears(record1.getYearOfPublication(), record2.getYearOfPublication()));
                        marcCompVector.setCompInternationalStandardNumber(StringComparator.compareInternationalStandardNumbers(record1.getInternationalStandardNumbers(), record2.getInternationalStandardNumbers()));
                        marcCompVector.setCompOverall(record1.getC99FieldId().equals(record2.getC99FieldId()));
                        if (record1.getC99FieldId().equals(record2.getC99FieldId())) {
                            vectorsDuplicated.add(marcCompVector);
                        } else {
                            vectorsNonDuplicated.add(marcCompVector);
                        }
                    }
                }
            }

        }
        marcCompVectors.addAll(vectorsDuplicated);
        marcCompVectors.addAll(vectorsNonDuplicated);

        writeBeansToCsvFile(marcCompVectors,
                FILE_NAME_MARC_COMP_VECTORS,
                MarcCompVector.class,
                "compControlFields", "compPersonalName", "compPublisherName", "compTitle",
                "compNameOfPart", "compYearOfAuthor", "compYearOfPublication", "compInternationalStandardNumber", "compOverall");

    }

    private void saveAllMarcCompVectorsToCsv() {
        final List<Record> records = getAllRecords();
        final List<MarcRecord> marcRecords = new ArrayList<>();
        for (final Record r : records) {
            final MarcRecord marcRecord = new MarcRecord();
            final String charAt6 = String.valueOf(r.getLeader().marshal().charAt(6));
            final String charAt7 = String.valueOf(r.getLeader().marshal().charAt(7));
            final String typeOfMaterial = String.valueOf(charAt6.concat(charAt7));

            marcRecord.setTypeOfMaterial(typeOfMaterial);
            marcRecord.setControlFieldId(r.getControlNumber());
            marcRecord.setC99FieldId(MarcFieldsFinder.findC99FieldId(r));
            marcRecord.setPersonalName(MarcFieldsFinder.findPersonalName(r));
            marcRecord.setPublisherName(MarcFieldsFinder.findPublisherName(r));
            marcRecord.setTitle(MarcFieldsFinder.findTitle(r));
            marcRecord.setNameOfPart(MarcFieldsFinder.findNameOfPart(r));
            marcRecord.setYearOfAuthor(MarcFieldsFinder.findYearOfAuthor(r));
            marcRecord.setYearOfPublication(MarcFieldsFinder.findYearOfPublication(r));
            marcRecord.setInternationalStandardNumbers(MarcFieldsFinder.findInternationalStandardNumbers(r));
            marcRecord.generateBlockingKey();

            marcRecords.add(marcRecord);
        }

        final List<MarcCompVector> marcCompVectors = new ArrayList<>();
        final List<MarcCompVector> vectorsDuplicated = new ArrayList<>();
        final List<MarcCompVector> vectorsNonDuplicated = new ArrayList<>();
        for (int i = 0; i < marcRecords.size(); i++) {
            for (int j = i + 1; j< marcRecords.size(); j++) {
                final MarcRecord record1 = marcRecords.get(i);
                final MarcRecord record2 = marcRecords.get(j);
                final MarcCompVector marcCompVector = new MarcCompVector();
                final boolean typesOfMaterialMatch = record1.getTypeOfMaterial().equals(record2.getTypeOfMaterial());
                if (typesOfMaterialMatch) {
                    marcCompVector.setCompC99ids(record1.getC99FieldId() + ";" + record2.getC99FieldId());
                    marcCompVector.setCompControlFields(record1.getControlFieldId() + ";" + record2.getControlFieldId());
                    marcCompVector.setCompPersonalName(StringComparator.comparePersonalName(record1.getPersonalName(), record2.getPersonalName()));
                    marcCompVector.setCompPublisherName(StringComparator.comparePublisher(record1.getPublisherName(), record2.getPublisherName()));
                    marcCompVector.setCompTitle(StringComparator.compareTitle(record1.getTitle(), record2.getTitle()));
                    marcCompVector.setCompNameOfPart(StringComparator.compareNameOfPart(record1.getNameOfPart(), record2.getNameOfPart()));
                    marcCompVector.setCompYearOfAuthor(StringComparator.compareYears(record1.getYearOfAuthor(), record2.getYearOfAuthor()));
                    marcCompVector.setCompYearOfPublication(StringComparator.compareYears(record1.getYearOfPublication(), record2.getYearOfPublication()));
                    marcCompVector.setCompInternationalStandardNumber(StringComparator.compareInternationalStandardNumbers(record1.getInternationalStandardNumbers(), record2.getInternationalStandardNumbers()));
                    marcCompVector.setCompOverall(record1.getC99FieldId().equals(record2.getC99FieldId()));
                    if (record1.getC99FieldId().equals(record2.getC99FieldId())) {
                        vectorsDuplicated.add(marcCompVector);
                    } else {
                        vectorsNonDuplicated.add(marcCompVector);
                    }
                }
            }
        }

        marcCompVectors.addAll(vectorsDuplicated);
        marcCompVectors.addAll(vectorsNonDuplicated);

        writeBeansToCsvFile(marcCompVectors,
                FILE_NAME_MARC_COMP_VECTORS,
                MarcCompVector.class,
                "compC99ids", "compPersonalName", "compPublisherName", "compTitle",
                "compNameOfPart", "compYearOfAuthor", "compYearOfPublication", "compInternationalStandardNumber", "compOverall");
    }

    private void createCompVectorsFromControlFields() {
        final MarcReader reader = getNewMarcReader();
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
                        .concat(CONTROL_FIELD_SEPARATOR)
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

        writeBeansToCsvFile(idCompVectors, "data.csv", IdCompVector.class, "id", "isDuplicate");
    }

    private void printDuplicateIdentifiersWithWrite() {
        final MarcReader reader = getNewMarcReader();
        final File file = new File(FILE_PATH_DUPLICATED_ENTRIES);
        final List<Record> records = new ArrayList<>();
        int count = 0;
        while (reader.hasNext()) {
            records.add(reader.next());
            System.out.println("added record " + ++count);
        }

        try {
            if (file.createNewFile()) {
                final OutputStream outputStream = new FileOutputStream(FILE_PATH_DUPLICATED_ENTRIES);
                final MarcXmlWriter writer = new MarcXmlWriter(outputStream, true);

                int numberOfDuplicatedRecords = 0;
                for (int i = 0; i < records.size(); i++) {
                    final Record record1 = records.get(i);
                    if (((DataField) record1.getVariableField("C99")).getSubfield('a') != null) {
                        final String c99DataField1 = ((DataField) record1.getVariableField("C99")).getSubfield('a').getData();
                        if (StringUtils.isValid(c99DataField1) && c99IdentifiersHashSet.contains(c99DataField1)) {
                            writer.write(record1);
                            System.out.println("DUPLICATE == " + c99DataField1);
                            numberOfDuplicatedRecords++;
                        }
                    }
                }

                System.out.println("number of duplicated records == " + numberOfDuplicatedRecords);

                writer.close();
                outputStream.flush();
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HashSet<String> printDuplicateIdentifiers() {
        final MarcReader reader = getNewMarcReader();
        final List<String> identifiers = new ArrayList<>();
        while (reader.hasNext()) {
            numberOfRecords++;
            Record record = reader.next();
            final Leader leader = record.getLeader();
            final DataField dataField = (DataField) record.getVariableField("C99"); // 245
            if (dataField != null) {
                if (dataField.getSubfield('a') != null) {
                    identifiers.add(dataField.getSubfield('a').getData());
                }
            }
        }
        Collections.sort(identifiers);

        final List<String> deduplicationList = new ArrayList<>();
        for (int i = 0; i < identifiers.size(); i++) {
            for (int j = i + 1; j < identifiers.size(); j++) {
                if (identifiers.get(j).equals(identifiers.get(i))) {
                    System.out.println("DUPLICATE == " + identifiers.get(j));
                    deduplicationList.add(identifiers.get(j));
                }
            }
        }

        final List<String> distinctDeduplicationList = deduplicationList.stream().distinct().collect(Collectors.toList());
        distinctDeduplicationList.forEach(System.out::println);

        System.out.println("numberOfDuplicates == " + distinctDeduplicationList.size());
        System.out.println("numberOfRecords == " + numberOfRecords);

        return new HashSet<>(distinctDeduplicationList);
    }

    @Nullable
    private String getValueBySubfieldOfDataField(final Record record, final String field, final char subfield) {
        final DataField dataField = (DataField) record.getVariableField(field);
        if (dataField != null) {
            if (dataField.getSubfield(subfield) != null) {
                return dataField.getSubfield(subfield).getData();
            }
        }
        return null;
    }

    private List<String> getValuesBySubfieldOfDataField(final String field, final char subfield) {
        final List<String> values = new ArrayList<>();
        final MarcReader reader = getNewMarcReader();
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

    private List<String> getValuesFromDataSetByControlField() {
        final List<String> values = new ArrayList<>();
        final MarcReader reader = getNewMarcReader();
        while (reader.hasNext()) {
            final Record record = reader.next();
            if (record != null) {
                values.add(record.getControlNumber());
            }
        }
        return values;
    }

    private List<Record> getAllRecords() {
        final MarcReader reader = getNewMarcReader();
        final List<Record> records = new ArrayList<>();
        while (reader.hasNext()) {
            records.add(reader.next());
        }
        return records;
    }

    private int getNumberOfRecords() {
        int count = 0;
        final MarcReader reader = getNewMarcReader();
        while (reader.hasNext()) {
            reader.next();
            count++;
        }
        return count;
    }

    private MarcReader getNewMarcReader() {
        return new MarcXmlReader(getNewFileInputStream());
    }

    @Nullable
    private FileInputStream getNewFileInputStream() {
        final File marcFile = new File(FILE_PATH_WITH_C99_DEDUP);
        final FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(marcFile);
            return fileInputStream;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void writeToCsvFile(final String filePath, final String... values) {
        try {
            final CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath));
            csvWriter.writeNext(values);
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T> void writeBeansToCsvFile(final List<T> beans, final String fileName, Class<T> klazz,
                                         final String... columnNames) {
        final String csvFile = fileName;
        CSVWriter csvWriter = null;
        try {
            final BeanToCsv<T> beanToCsv = new BeanToCsv<>();
            csvWriter = new CSVWriter(new FileWriter(csvFile));
            final ColumnPositionMappingStrategy<T> mappingStrategy = new ColumnPositionMappingStrategy<>();
            mappingStrategy.setType(klazz);
            mappingStrategy.setColumnMapping(columnNames);
            beanToCsv.write(mappingStrategy, csvWriter, beans);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (csvWriter != null) {
                    csvWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
