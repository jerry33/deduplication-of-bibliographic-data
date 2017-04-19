package models;

import data.XmlDataManager;
import info.debatty.java.stringsimilarity.Levenshtein;
import utils.FileUtils;
import utils.MarcUtils;
import utils.Printer;
import utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jerry on 4/17/17.
 */
public class BlockingCompVectors implements CompVectors {

    @Override
    public List<MarcCompVector> createFromFile(String filePath) {
        return createFromMarcRecords(XmlDataManager.getInstance().getAllMarcRecords(null, filePath));
    }

    @Override
    public List<MarcCompVector> createFromMarcRecords(List<MarcRecord> marcRecords) {
        Collections.sort(marcRecords);
        Printer.printBlockingKeys(marcRecords);

        final List<List<MarcRecord>> listOfBlockingLists = new ArrayList<>();
        int startOfBlock = 0;
        final List<MarcRecord> blockingList = new ArrayList<>();
//        final JaroWinkler jaroWinkler = new JaroWinkler();
        final Levenshtein levenshtein = new Levenshtein();
        for (int i = 0; i < marcRecords.size() - 1; i++) {
            if (startOfBlock == i) {
                marcRecords.get(i).setIsInAnyBlock(false);
                blockingList.add(marcRecords.get(i));
            }
            if (levenshtein.distance(marcRecords.get(startOfBlock).getBlockingKey(), marcRecords.get(i + 1).getBlockingKey()) <= 3) { // jaroWinkler.similarity(marcRecords.get(startOfBlock).getBlockingKey(), marcRecords.get(i + 1).getBlockingKey()) >= 0.95
                marcRecords.get(i).setIsInAnyBlock(true);
                marcRecords.get(i + 1).setIsInAnyBlock(true);
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
//                            System.out.println("equals: " + record1.getC99FieldId() + "-" + record1.getLibraryId() + "; and " + record2.getC99FieldId() + "-" + record2.getLibraryId());
                            marcCompVectors.add(marcCompVector);
                        } else {
                            marcCompVectors.add(marcCompVector);
                        }
                    }
                }
            }

        }
//        marcCompVectors.addAll(vectorsDuplicated);
//        marcCompVectors.addAll(vectorsNonDuplicated);
        return marcCompVectors;
    }

    @Override
    public void saveToCsvFromMarcXMLFile(String sourceFilePath) {
        String newFileName = "";
        if(sourceFilePath.contains(".")) {
            newFileName = sourceFilePath.substring(0, sourceFilePath.lastIndexOf('.'));
            newFileName = newFileName + "_blocking_comp_vectors.csv";
        }
        if (StringUtils.isValid(newFileName)) {
            FileUtils.writeBeansToCsvFile(createFromFile(sourceFilePath),
                    newFileName,
                    MarcCompVector.class,
                    MarcCompVector.COLUMNS);
        }
    }
}
