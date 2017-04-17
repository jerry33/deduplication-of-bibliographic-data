package models;

import java.util.List;

/**
 * Created by jerry on 4/17/17.
 */
public interface CompVectors {

    List<MarcCompVector> createFromFile(String filePath);
    List<MarcCompVector> createFromMarcRecords(List<MarcRecord> marcRecords);
    void saveToCsvFromMarcXMLFile(String sourceFilePath);

}
