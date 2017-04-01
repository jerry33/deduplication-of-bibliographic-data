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

/**
 * Created by jerry on 11/28/16.
 */
public class MainController {

    static String[] sColumnNames;

    private RManager rManager = RManager.getInstance();

    static {
        System.loadLibrary("jri");
        sColumnNames = new String[]{"compC99id1", "compC99id2", "compControlField1", "compControlField2", "compPersonalName", "compPublisherName", "compTitle",
                "compNameOfPart", "compYearOfAuthor", "compYearOfPublication", "compInternationalStandardNumber", "compOverall"};
    }

    private XmlDataManager xmlDataManager = new XmlDataManager();

    void start() {
        final long start = System.nanoTime();
        saveAllMarcCompVectorsToCsv(FileUtils.FILE_PATH_WITH_C99_DEDUP);
        saveBlockingMarcCompVectorsToCsv(FileUtils.FILE_PATH_WITHOUT_C99);
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

    @SuppressWarnings("Duplicates")
    private void saveBlockingMarcCompVectorsToCsv(final String sourceFilePath) {
        final List<MarcRecord> marcRecords = xmlDataManager.getAllMarcRecords(null, sourceFilePath);
        Collections.sort(marcRecords);
        System.out.println("marcRecords.size(): " + marcRecords.size());
        for (int i = 0; i < marcRecords.size(); i++) {
            if (marcRecords.get(i).getBlockingKey().equals("")) {
                System.out.println("EMPTY " + (i + 1));
            } else {
                System.out.println(marcRecords.get(i).getBlockingKey() + " " + (i + 1));
            }
        }

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

        String newFileName = "";
        if(sourceFilePath.contains(".")) {
            newFileName = sourceFilePath.substring(0, sourceFilePath.lastIndexOf('.'));
            newFileName = newFileName + "_blocking_comp_vectors.csv";
        }

        if (StringUtils.isValid(newFileName)) {
            FileUtils.writeBeansToCsvFile(marcCompVectors,
                    newFileName,
                    MarcCompVector.class,
                    sColumnNames);
        }

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
