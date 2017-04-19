package models;

import data.XmlDataManager;
import utils.FileUtils;
import utils.MarcUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerry on 4/17/17.
 */
public class AllCompVectors implements CompVectors {
    @Override
    public List<MarcCompVector> createFromFile(String filePath) {
        return createFromMarcRecords(XmlDataManager.getInstance().getAllMarcRecords(null, filePath));
    }

    @Override
    public List<MarcCompVector> createFromMarcRecords(List<MarcRecord> marcRecords) {
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
                        System.out.println("equals: " + record1.getC99FieldId() + "-" + record1.getLibraryId() + "; and " + record2.getC99FieldId() + "-" + record2.getLibraryId());
                        marcCompVectors.add(marcCompVector);
                    } else {
                        System.out.println("NOT equals: " + record1.getC99FieldId() + "-" + record1.getLibraryId() + "; and " + record2.getC99FieldId() + "-" + record2.getLibraryId());
                        marcCompVectors.add(marcCompVector);
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
            newFileName = newFileName + "_all_comp_vectors.csv";
        }

        FileUtils.writeBeansToCsvFile(createFromFile(sourceFilePath),
                newFileName,
                MarcCompVector.class,
                MarcCompVector.COLUMNS);
    }
}
