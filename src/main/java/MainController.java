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

    private String masterLibraryId;
    private RManager rManager = RManager.getInstance();
    private Map<MarcRecord, List<MarcRecord>> uniqueMarcRecordsHashMap = new HashMap<>();

    static {
        System.loadLibrary("jri");
        sColumnNames = new String[]{"compC99id1", "compC99id2", "compControlField1", "compControlField2", "compLibraryId1", "compLibraryId2", "compPersonalName", "compPublisherName", "compTitle",
                "compNameOfPart", "compYearOfAuthor", "compYearOfPublication", "compInternationalStandardNumber", "compOverall"};
    }

    private XmlDataManager xmlDataManager = new XmlDataManager();

    void start() {
        final long start = System.nanoTime();
        System.out.println("start()");
        final List<MarcRecord> marcRecords1 = xmlDataManager.getAllMarcRecords(null, "/Users/jerry/Desktop/git/deduplication-of-bibliographic-data/assets/prod/Vy11to16BezC99a_crop.xml");
        FileUtils.writeBeansToCsvFile(createBlockingCompVectorsFromRecords(marcRecords1), "Vy11to16BezC99a_crop_comp_vectors_unique.csv", MarcCompVector.class, sColumnNames);
        final List<MarcCompVector> marcRecords1UniqueCompVectors = FileUtils.readCsv("Vy11to16BezC99a_crop_comp_vectors_unique.csv", MarcCompVector.class, sColumnNames);
        final List<List<MarcRecord>> marcRecordsUniqueList1 = createUniqueMarcRecordsList(marcRecords1UniqueCompVectors, marcRecords1, null);

//        masterLibraryId = marcRecords1.get(0).getLibraryId();
//        for (MarcRecord marcRecord : marcRecords1) {
//            marcRecord.setIsMasterDatabaseRecord(true);
//            uniqueMarcRecordsHashMap.put(marcRecord, new ArrayList<>());
//        }

        final List<MarcRecord> marcRecords2 = xmlDataManager.getAllMarcRecords(null, "/Users/jerry/Desktop/git/deduplication-of-bibliographic-data/assets/prod/Ujep11to16BezC99a_crop.xml");
        FileUtils.writeBeansToCsvFile(createBlockingCompVectorsFromRecords(marcRecords2), "Ujep11to16BezC99a_crop_comp_vectors_unique.csv", MarcCompVector.class, sColumnNames);
        final List<MarcCompVector> marcRecords2UniqueCompVectors = FileUtils.readCsv("Ujep11to16BezC99a_crop_comp_vectors_unique.csv", MarcCompVector.class, sColumnNames);
        final List<List<MarcRecord>> marcRecordsUniqueList2 = createUniqueMarcRecordsList(marcRecords2UniqueCompVectors, marcRecords2, null);

        final List<List<MarcRecord>> marcRecordsUniqueListMerged = Stream.concat(marcRecordsUniqueList1.stream(), marcRecordsUniqueList2.stream()).collect(Collectors.toList());

        final List<MarcRecord> distinctMarcRecords1 = createUniqueMarcRecords(marcRecordsUniqueList1);
        final List<MarcRecord> distinctMarcRecords2 = createUniqueMarcRecords(marcRecordsUniqueList2);
        final List<MarcRecord> mergedMarcRecords = Stream.concat(distinctMarcRecords1.stream(), distinctMarcRecords2.stream()).collect(Collectors.toList());
        final List<MarcCompVector> mergedCompVectors = createBlockingCompVectorsFromRecords(mergedMarcRecords);
        FileUtils.writeBeansToCsvFile(mergedCompVectors, "merged_comp_vectors.csv", MarcCompVector.class, sColumnNames);
        final List<MarcCompVector> mergedCompVectorsFromFile = FileUtils.readCsv("merged_comp_vectors.csv", MarcCompVector.class, sColumnNames);
        final List<List<MarcRecord>> uniqueList = createUniqueMarcRecordsList(mergedCompVectorsFromFile, marcRecords1, marcRecordsUniqueListMerged);
        System.out.println("uniqueList.size(): " + uniqueList.size());
        for (List<MarcRecord> marcRecordList : uniqueList) {
            if (marcRecordList.size() == 0) {
                System.out.println(marcRecordList.get(0).getControlFieldId());
            } else if (marcRecordList.size() > 0) {
                for (MarcRecord marcRecord : marcRecordList) {
                    System.out.print(marcRecord.getControlFieldId() + " --> ");
                }
            }
            System.out.println();
        }

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

    private List<MarcRecord> createUniqueMarcRecords(final List<List<MarcRecord>> uniqueList) {
        final List<MarcRecord> marcRecordList = new ArrayList<>();
        for (List<MarcRecord> marcRecords : uniqueList) {
            marcRecordList.add(marcRecords.get(0));
        }
        return marcRecordList;
    }

    private List<List<MarcRecord>> createUniqueMarcRecordsList(final List<MarcCompVector> marcCompVectors, final List<MarcRecord> marcRecords, final List<List<MarcRecord>> existingUniqueList) {
        final List<List<MarcRecord>> uniqueList = existingUniqueList == null ? new ArrayList<>() : existingUniqueList;
        for (MarcCompVector marcCompVector : marcCompVectors) {
            System.out.println(marcCompVector.toString());
            if (!marcCompVector.isDuplicate()) {
                if (!isControlFieldInUniqueList(marcCompVector.getCompControlField1(), uniqueList)) {
                    uniqueList.add(new ArrayList<>(Collections.singleton(findMarcRecordByControlField(marcCompVector.getCompControlField1(), marcRecords))));
                }
                if (!isControlFieldInUniqueList(marcCompVector.getCompControlField2(), uniqueList)) {
                    uniqueList.add(new ArrayList<>(Collections.singleton(findMarcRecordByControlField(marcCompVector.getCompControlField2(), marcRecords))));
                }
            } else { // is duplicate
                int controlField1PositionInList = getPositionOfDuplicateList(marcCompVector.getCompControlField1(), uniqueList);
                int controlField2PositionInList = getPositionOfDuplicateList(marcCompVector.getCompControlField2(), uniqueList);
                if (controlField1PositionInList != -1 && controlField2PositionInList != -1) { // they are both already added
                    continue;
                }
                if (controlField1PositionInList != -1) {
                    uniqueList.get(controlField1PositionInList).add(findMarcRecordByControlField(marcCompVector.getCompControlField2(), marcRecords));
                } else if (controlField2PositionInList != -1) {
                    uniqueList.get(controlField2PositionInList).add(findMarcRecordByControlField(marcCompVector.getCompControlField1(), marcRecords));
                } else {
                    final List<MarcRecord> newList = new ArrayList<>();
                    newList.add(findMarcRecordByControlField(marcCompVector.getCompControlField1(), marcRecords));
                    newList.add(findMarcRecordByControlField(marcCompVector.getCompControlField2(), marcRecords));
                    uniqueList.add(newList);
                }
            }
        }
        return uniqueList;
    }

    private boolean isControlFieldInUniqueList(final String controlField, final List<List<MarcRecord>> uniqueList) {
        for (List<MarcRecord> list : uniqueList) {
            for (MarcRecord marcRecord : list) {
                if (StringUtils.isValid(controlField) && StringUtils.isValid(marcRecord.getControlFieldId()) && controlField.equals(marcRecord.getControlFieldId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private int getPositionOfDuplicateList(final String controlField, final List<List<MarcRecord>> uniqueList) {
        for (int i = 0; i < uniqueList.size(); i++) {
            final List<MarcRecord> duplicateList = uniqueList.get(i);
            for (int j = 0; j < duplicateList.size(); j++) {
                if (controlField.equals(duplicateList.get(j).getControlFieldId())) {
                    return i;
                }
            }
        }
        return -1;
    }

    private MarcRecord findMarcRecordByControlField(final String controlField, final List<MarcRecord> marcRecords) {
        for (MarcRecord marcRecord : marcRecords) {
            if (controlField.equals(marcRecord.getControlFieldId())) {
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
