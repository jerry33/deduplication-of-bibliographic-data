import data.XmlDataManager;
import info.debatty.java.stringsimilarity.Levenshtein;
import models.MarcCompVector;
import models.MarcRecord;
import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.MarcXmlWriter;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import r.RManager;
import utils.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by jerry on 11/28/16.
 */
public class MainController {

    static String[] sColumnNames;

    private RManager rManager = RManager.getInstance();
    private Map<String, Integer> marcRecordsHashMap = new HashMap<>();

    static {
        System.loadLibrary("jri");
        sColumnNames = new String[]{"compC99id1", "compC99id2", "compControlField1", "compControlField2", "compLibraryId1", "compLibraryId2", "compPersonalName", "compPublisherName", "compTitle",
                "compNameOfPart", "compYearOfAuthor", "compYearOfPublication", "compInternationalStandardNumber", "compOverall"};
    }

    private XmlDataManager xmlDataManager = new XmlDataManager();

    void start() {
        final long start = System.nanoTime();
        System.out.println("start()");
        final List<MarcRecord> marcRecords1 = xmlDataManager.getAllMarcRecords(null, "/Users/jerry/Desktop/git/deduplication-of-bibliographic-data/assets/prod/Vy11to16BezC99a.xml");
        final List<MarcRecord> marcRecords2 = xmlDataManager.getAllMarcRecords(null, "/Users/jerry/Desktop/git/deduplication-of-bibliographic-data/assets/prod/Ujep11to16BezC99a_modified.xml");


        final List<List<MarcRecord>> uniqueList = createUniqueListFromTwoFilesSimpler(marcRecords1, marcRecords2);
        for (List<MarcRecord> marcRecordList : uniqueList) {
            if (marcRecordList.size() == 0) {
                System.out.println(marcRecordList.get(0).getControlFieldId());
            } else if (marcRecordList.size() > 0) {
                List<MarcRecord> distinctList = marcRecordList.stream().distinct().collect(Collectors.toList());
                marcRecordList.clear();
                marcRecordList.addAll(distinctList);
                for (MarcRecord marcRecord : marcRecordList) {
                    System.out.print(marcRecord.getControlFieldId() + "(" + marcRecord.getLibraryId() + ", " + marcRecord.getBlockingKey() + ") --> ");
                }
            }
            System.out.println();
        }
        System.out.println("uniqueList.size(): " + uniqueList.size());

//        final List<MarcRecord> mergedMarcRecords = Stream.concat(marcRecords1.stream(), marcRecords2.stream()).collect(Collectors.toList());
//        final List<MarcCompVector> mergedCompVectors = createBlockingCompVectorsFromRecords(mergedMarcRecords);
//        FileUtils.writeBeansToCsvFile(mergedCompVectors, "merged_comp_vectors.csv", MarcCompVector.class, sColumnNames);
//        final List<MarcCompVector> mergedCompVectorsFromFile = FileUtils.readCsv("merged_comp_vectors.csv", MarcCompVector.class, sColumnNames);

//        final List<MarcRecord> marcRecords1 = xmlDataManager.getAllMarcRecords(null, "/Users/jerry/Desktop/git/deduplication-of-bibliographic-data/assets/prod/Vy11to16BezC99a_crop_unique_test.xml");
////        final List<MarcCompVector> marcCompVectors = createBlockingCompVectorsFromRecords(marcRecords1);
////        FileUtils.writeBeansToCsvFile(marcCompVectors, "merged_comp_vectors_unique_test.csv", MarcCompVector.class, sColumnNames);
//        final List<MarcCompVector> marcCompVectorsFromFile = FileUtils.readCsv("merged_comp_vectors_unique_test.csv", MarcCompVector.class, sColumnNames);
//        final List<List<MarcRecord>> uniqueList = createUniqueMarcRecordsList(marcCompVectorsFromFile, marcRecords1);
//        System.out.println("uniqueList.size(): " + uniqueList.size());
//        for (List<MarcRecord> marcRecordList : uniqueList) {
//            if (marcRecordList.size() == 0) {
//                System.out.println(marcRecordList.get(0).getControlFieldId());
//            } else if (marcRecordList.size() > 1) {
//                for (MarcRecord marcRecord : marcRecordList) {
//                    System.out.print(marcRecord.getControlFieldId() + " --> ");
//                }
//            }
//            System.out.println();
//        }

//        Printer.printDuplicates(mergedCompVectorsFromFile, mergedMarcRecords);
//        saveAllMarcCompVectorsToCsv(FileUtils.FILE_PATH_WITH_C99_DEDUP);
//        saveBlockingMarcCompVectorsToCsv(FileUtils.FILE_PATH_WITHOUT_C99);
//        saveBlockingMarcCompVectorsToCsv();
//        rManager.trainDataFromFile(FileUtils.FILE_PATH_WITH_C99_DEDUP_ABSOLUTE_PATH);
//        rManager.trainAndClassifyData();
//        writeDuplicateRecordsToFile();
//        createCompVectorsFromControlFields();
//        List<MarcCompVector> compVectors = FileUtils.readCsv(
//                FileUtils.FILE_NAME_CSV_TO_READ,
//                MarcCompVector.class,
//                sColumnNames);
//        for (MarcCompVector marcCompVector : compVectors) {
//            System.out.println(marcCompVector.toString());
//        }
//        testR();
        final long end = System.nanoTime();
        Printer.printTimeElapsed(start, end);
    }

    private List<List<MarcRecord>> createUniqueListFromTwoFilesSimpler(final List<MarcRecord> marcRecordList1, final List<MarcRecord> marcRecordList2) {
        System.out.println("Merging records...");
        final List<MarcRecord> mergedMarcRecords = Stream.concat(marcRecordList1.stream(), marcRecordList2.stream()).collect(Collectors.toList());
        for (MarcRecord marcRecord : mergedMarcRecords) {
            marcRecordsHashMap.put(marcRecord.getControlFieldId() + "-" + marcRecord.getLibraryId(), -1);
        }
        System.out.println("Creating blocking vectors...");
        FileUtils.writeBeansToCsvFile(createBlockingCompVectorsFromRecords(mergedMarcRecords), "merged_marc_records_new.csv", MarcCompVector.class, sColumnNames);

        System.out.println("Training data...");
        rManager.trainDataFromFile("/Users/jerry/Desktop/git/deduplication-of-bibliographic-data/assets/prod/comp_vectors_all_train.csv");
        rManager.classifyData("/Users/jerry/Desktop/git/deduplication-of-bibliographic-data/merged_marc_records_new.csv");

        System.out.println("Loading blocking vectors...");
        final List<MarcCompVector> mergedCompVectors = FileUtils.readCsv("merged_marc_records_new.csv", MarcCompVector.class, sColumnNames);
        System.out.println("Creating unique list...");
        return createUniqueMarcRecordsList(mergedCompVectors, mergedMarcRecords, null);
    }

    private List<MarcRecord> createUniqueMarcRecords(final List<List<MarcRecord>> uniqueList) {
        final List<MarcRecord> marcRecordList = new ArrayList<>();
        for (List<MarcRecord> marcRecords : uniqueList) {
            marcRecordList.add(marcRecords.get(0));
        }
        return marcRecordList;
    }

    @SuppressWarnings("Duplicates")
    private List<List<MarcRecord>> createUniqueMarcRecordsList(final List<MarcCompVector> marcCompVectors, final List<MarcRecord> marcRecords, final List<List<MarcRecord>> existingUniqueList) {
        final List<List<MarcRecord>> uniqueList = existingUniqueList == null ? new ArrayList<>() : existingUniqueList;
        for (MarcCompVector marcCompVector : marcCompVectors) {
            if (!marcCompVector.isDuplicate()) {
                if (marcCompVector.getCompControlField1().equals("0178053")) {
                    System.out.println("debug");
                }
                if (!isControlFieldInUniqueList(marcCompVector.getCompControlField1(), marcCompVector.getCompLibraryId1(), uniqueList)) {
                    final MarcRecord marcRecord = findMarcRecordByControlField(marcCompVector.getCompControlField1(), marcCompVector.getCompLibraryId1(), marcRecords);
                    if (marcRecord != null) {
                        uniqueList.add(new ArrayList<>(Collections.singleton(marcRecord)));
                        if (marcRecordsHashMap.containsKey(marcRecord.getControlFieldId() + "-" + marcRecord.getLibraryId())) {
                            marcRecordsHashMap.put(marcRecord.getControlFieldId() + "-" + marcRecord.getLibraryId(), uniqueList.size() - 1);
                        }
                    }
                }
                if (!isControlFieldInUniqueList(marcCompVector.getCompControlField2(), marcCompVector.getCompLibraryId2(), uniqueList)) {
                    final MarcRecord marcRecord = findMarcRecordByControlField(marcCompVector.getCompControlField2(), marcCompVector.getCompLibraryId2(), marcRecords);
                    if (marcRecord != null) {
                        uniqueList.add(new ArrayList<>(Collections.singleton(marcRecord)));
                        if (marcRecordsHashMap.containsKey(marcRecord.getControlFieldId() + "-" + marcRecord.getLibraryId())) {
                            marcRecordsHashMap.put(marcRecord.getControlFieldId() + "-" + marcRecord.getLibraryId(), uniqueList.size() - 1);
                        }
                    }
                }
            } else { // is duplicate
                int controlField1PositionInList = getPositionOfDuplicateList(marcCompVector.getCompControlField1(), marcCompVector.getCompLibraryId1(), uniqueList);
                int controlField2PositionInList = getPositionOfDuplicateList(marcCompVector.getCompControlField2(), marcCompVector.getCompLibraryId2(), uniqueList);
                if (controlField1PositionInList != -1 && controlField2PositionInList != -1) { // they are both already added
                    if (controlField1PositionInList != controlField2PositionInList) {
                        final List<MarcRecord> newList = Stream.concat(uniqueList.get(controlField1PositionInList).stream(), uniqueList.get(controlField2PositionInList).stream()).collect(Collectors.toList());
                        for (MarcRecord marcRecord : uniqueList.get(controlField1PositionInList)) {
                            if (marcRecord.getControlFieldId().equals("0178053")) {
                                System.out.println("debug");
                            }
                        }

                        for (MarcRecord marcRecord : uniqueList.get(controlField2PositionInList)) {
                            if (marcRecord.getControlFieldId().equals("0178053")) {
                                System.out.println("debug");
                            }
                        }
                        uniqueList.get(controlField1PositionInList).clear();
                        uniqueList.get(controlField1PositionInList).addAll(newList);
                        uniqueList.remove(controlField2PositionInList);
                        for (Map.Entry<String, Integer> entry : marcRecordsHashMap.entrySet()) {
                            if (entry.getValue() >= controlField2PositionInList) {
                                entry.setValue(entry.getValue() - 1);
                            }
                        }
                        for (MarcRecord marcRecord : newList) {
                            final int position = controlField1PositionInList < controlField2PositionInList ? controlField1PositionInList : (controlField1PositionInList - 1);
                            marcRecordsHashMap.put(marcRecord.getControlFieldId() + "-" + marcRecord.getLibraryId(), position);
                        }
                    }
                    continue;
                }
                if (controlField1PositionInList != -1) {
                    final MarcRecord marcRecord = findMarcRecordByControlField(marcCompVector.getCompControlField2(), marcCompVector.getCompLibraryId1(), marcRecords);
                    if (marcRecord != null) {
                        uniqueList.get(controlField1PositionInList).add(marcRecord);
                        marcRecordsHashMap.put(marcRecord.getControlFieldId() + "-" + marcRecord.getLibraryId(), controlField1PositionInList);
                    }
                } else if (controlField2PositionInList != -1) {
                    final MarcRecord marcRecord = findMarcRecordByControlField(marcCompVector.getCompControlField1(), marcCompVector.getCompLibraryId2(), marcRecords);
                    if (marcRecord != null) {
                        uniqueList.get(controlField2PositionInList).add(marcRecord);
                        marcRecordsHashMap.put(marcRecord.getControlFieldId() + "-" + marcRecord.getLibraryId(), controlField2PositionInList);
                    }
                } else {
                    final List<MarcRecord> newList = new ArrayList<>();
                    final MarcRecord marcRecord1 = findMarcRecordByControlField(marcCompVector.getCompControlField1(), marcCompVector.getCompLibraryId1(), marcRecords);
                    final MarcRecord marcRecord2 = findMarcRecordByControlField(marcCompVector.getCompControlField2(), marcCompVector.getCompLibraryId2(), marcRecords);
                    if (marcRecord1 != null && marcRecord2 != null) {
                        newList.add(marcRecord1);
                        newList.add(marcRecord2);
                        uniqueList.add(newList);
                        if (marcRecordsHashMap.containsKey(marcRecord1.getControlFieldId() + "-" + marcRecord1.getLibraryId())) {
                            marcRecordsHashMap.put(marcRecord1.getControlFieldId() + "-" + marcRecord1.getLibraryId(), uniqueList.size() - 1);
                        }
                        if (marcRecordsHashMap.containsKey(marcRecord2.getControlFieldId() + "-" + marcRecord2.getLibraryId())) {
                            marcRecordsHashMap.put(marcRecord2.getControlFieldId() + "-" + marcRecord2.getLibraryId(), uniqueList.size() - 1);
                        }
                    }
                }
            }
        }
        return uniqueList;
    }

    private boolean isControlFieldInUniqueList(final String controlField, final String libraryId, final List<List<MarcRecord>> uniqueList) {
        final String controlFieldWithLibraryId = controlField + "-" + libraryId;
        return marcRecordsHashMap.get(controlFieldWithLibraryId) != -1;
    }

    private int getPositionOfDuplicateList(final String controlField, final String libraryId, final List<List<MarcRecord>> uniqueList) {
        final String controlFieldWithLibraryId = controlField + "-" + libraryId;
        if (marcRecordsHashMap.containsKey(controlFieldWithLibraryId)) {
            return marcRecordsHashMap.get(controlFieldWithLibraryId);
        }
        return -1;
    }

    private MarcRecord findMarcRecordByControlField(final String controlField, final String libraryId, final List<MarcRecord> marcRecords) {
        for (MarcRecord marcRecord : marcRecords) {
            if (controlField.equals(marcRecord.getControlFieldId()) && marcRecord.getLibraryId().equals(libraryId)) {
                return marcRecord;
            }
        }
        return null;
    }

    @SuppressWarnings("Duplicates")
    private void saveBlockingMarcCompVectorsToCsv(final String sourceFilePath) {
        String newFileName = "";
        if(sourceFilePath.contains(".")) {
            newFileName = sourceFilePath.substring(0, sourceFilePath.lastIndexOf('.'));
            newFileName = newFileName + "_blocking_comp_vectors.csv";
        }
        if (StringUtils.isValid(newFileName)) {
            FileUtils.writeBeansToCsvFile(createBlockingCompVectorsFromFile(sourceFilePath),
                    newFileName,
                    MarcCompVector.class,
                    sColumnNames);
        }
    }

    @SuppressWarnings("Duplicates")
    private List<MarcCompVector> createBlockingCompVectorsFromFile(final String filePath) {
        return createBlockingCompVectorsFromRecords(xmlDataManager.getAllMarcRecords(null, filePath));
    }

    @SuppressWarnings("Duplicates")
    private List<MarcCompVector> createBlockingCompVectorsFromRecords(final List<MarcRecord> marcRecords) {
        Collections.sort(marcRecords);
        Printer.printBlockingKeys(marcRecords);

        final List<List<MarcRecord>> listOfBlockingLists = new ArrayList<>();
        int startOfBlock = 0;
        final List<MarcRecord> blockingList = new ArrayList<>();
//        final JaroWinkler jaroWinkler = new JaroWinkler();
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

//        System.out.println("listOfBlockingLists.size(): " + listOfBlockingLists.size());
//        final List<Integer> sizesOfBlockingLists = new ArrayList<>();
//        for (List<MarcRecord> b : listOfBlockingLists) {
//            sizesOfBlockingLists.add(b.size());
//            System.out.println("blockingKey: " + b.get(0).getBlockingKey() + " ; size: " + b.size());
//        }
//        Collections.sort(sizesOfBlockingLists);
//        for (Integer i : sizesOfBlockingLists) {
//            System.out.println("size of blocking list: " + i);
//        }

//        listOfBlockingLists.removeIf(marcRecords1 -> marcRecords1.size() > 10);
//        System.out.println("listOfBlockingLists.size() new: " + listOfBlockingLists.size());

        int numberOfComparisons = 0;
        final List<MarcCompVector> marcCompVectors = new ArrayList<>();
        final List<MarcCompVector> vectorsDuplicated = new ArrayList<>();
        final List<MarcCompVector> vectorsNonDuplicated = new ArrayList<>();
        for (final List<MarcRecord> blockOfMarcRecords : listOfBlockingLists) {
            for (int i = 0; i < blockOfMarcRecords.size(); i++) {
                for (int j = i + 1; j < blockOfMarcRecords.size(); j++) {
                    final MarcRecord record1 = blockOfMarcRecords.get(i);
                    final MarcRecord record2 = blockOfMarcRecords.get(j);
                    final boolean typesOfMaterialMatch = record1.getTypeOfMaterial().equals(record2.getTypeOfMaterial());
                    if (typesOfMaterialMatch) {
                        numberOfComparisons++;
//                        System.out.println("numberOfComparisons: " + numberOfComparisons);
                        final MarcCompVector marcCompVector = MarcUtils.createCompVector(record1, record2);
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
        return marcCompVectors;
    }

    @SuppressWarnings("Duplicates")
    private void saveAllMarcCompVectorsToCsv(final String sourceFilePath) {
        final List<MarcRecord> marcRecords = xmlDataManager.getAllMarcRecords(null, sourceFilePath);
        final List<MarcCompVector> marcCompVectors = new ArrayList<>();
        final List<MarcCompVector> vectorsDuplicated = new ArrayList<>();
        final List<MarcCompVector> vectorsNonDuplicated = new ArrayList<>();
        for (int i = 0; i < marcRecords.size(); i++) {
            for (int j = i + 1; j < marcRecords.size(); j++) {
                final MarcRecord record1 = marcRecords.get(i);
                final MarcRecord record2 = marcRecords.get(j);
                final boolean typesOfMaterialMatch = record1.getTypeOfMaterial().equals(record2.getTypeOfMaterial());
                if (typesOfMaterialMatch) {
                    final MarcCompVector marcCompVector = MarcUtils.createCompVector(record1, record2);
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

        String newFileName = "";
        if(sourceFilePath.contains(".")) {
            newFileName = sourceFilePath.substring(0, sourceFilePath.lastIndexOf('.'));
            newFileName = newFileName + "_all_comp_vectors.csv";
        }

        FileUtils.writeBeansToCsvFile(marcCompVectors,
                newFileName,
                MarcCompVector.class,
                sColumnNames);
    }

    @SuppressWarnings("Duplicates")
    private void writeDuplicateRecordsToFile(final String sourceFilePath) {
        final MarcReader reader = new MarcXmlReader(FileUtils.getNewFileInputStream(FileUtils.FILE_PATH_WITH_C99_DEDUP));
        final File file = new File(FileUtils.FILE_PATH_DUPLICATED_ENTRIES);
        final List<Record> records = new ArrayList<>();
        while (reader.hasNext()) {
            records.add(reader.next());
        }
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            final OutputStream outputStream = new FileOutputStream(FileUtils.FILE_PATH_DUPLICATED_ENTRIES);
            final MarcXmlWriter writer = new MarcXmlWriter(outputStream, true);
            final Set<String> c99IdentifiersHashSet = new HashSet<>(xmlDataManager.getDuplicateIdentifiers(sourceFilePath));
            int numberOfDuplicateRecords = 0;
            for (final Record record : records) {
                if (((DataField) record.getVariableField("C99")).getSubfield('a') != null) {
                    final String c99DataField1 = ((DataField) record.getVariableField("C99")).getSubfield('a').getData();
                    if (StringUtils.isValid(c99DataField1) && c99IdentifiersHashSet.contains(c99DataField1)) {
                        writer.write(record);
                        System.out.println("DUPLICATE == " + c99DataField1);
                        numberOfDuplicateRecords++;
                    }
                }
            }
            System.out.println("number of duplicated records == " + numberOfDuplicateRecords);

            writer.close();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
