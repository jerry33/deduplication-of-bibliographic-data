package data;

import com.sun.istack.internal.NotNull;
import models.MarcRecord;
import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.Record;
import utils.FileUtils;
import utils.MarcFieldsFinder;
import utils.MarcUtils;
import utils.StringUtils;

import javax.servlet.ServletContext;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jerry on 3/25/17.
 */
public class XmlDataManager {

    public List<MarcRecord> getAllMarcRecords(final ServletContext servletContext, final String filePath) {
        List<Record> records;
        if (servletContext == null) {
            records = getAllRecords(filePath);
        } else {
            records = getAllRecords(servletContext, filePath);
        }
        final List<MarcRecord> marcRecords = new ArrayList<>();

        for (final Record r : records) {
            if (StringUtils.isValid(MarcFieldsFinder.find915(r))) {
                continue;
            }
            final MarcRecord marcRecord = new MarcRecord();
            final String charAt6 = String.valueOf(r.getLeader().marshal().charAt(6));
            final String charAt7 = String.valueOf(r.getLeader().marshal().charAt(7));
            final String typeOfMaterial = String.valueOf(charAt6.concat(charAt7));
            for (ControlField c : r.getControlFields()) {
                if (c.getTag().equals("003")) {
                    marcRecord.setLibraryId(c.getData()); // set library id
                }
            }
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
        return marcRecords;
    }

    private List<Record> getAllRecords(final String filePath) {
        final MarcReader reader = new MarcXmlReader(FileUtils.getNewFileInputStream(filePath));
        final List<Record> records = new ArrayList<>();
        while (reader.hasNext()) {
            final Record record = reader.next();
            records.add(record);
        }
        return records;
    }

    private List<Record> getAllRecords(final ServletContext servletContext, final String filePath) {
        final InputStream is = servletContext.getResourceAsStream(filePath);
        final MarcReader reader = new MarcXmlReader(servletContext.getResourceAsStream(filePath));
        final List<Record> records = new ArrayList<>();
        while (reader.hasNext()) {
            records.add(reader.next());
        }
        return records;
    }

    public List<String> getDuplicateIdentifiers(final String filePath) {
        final List<String> identifiers = MarcUtils.getAllIdentifiers(filePath);
        Collections.sort(identifiers);
        final List<String> deduplicationList = new ArrayList<>();
        for (int i = 0; i < identifiers.size(); i++) {
            for (int j = i + 1; j < identifiers.size(); j++) {
                if (identifiers.get(j).equals(identifiers.get(i))) {
                    deduplicationList.add(identifiers.get(j));
                }
            }
        }
        System.out.println("deduplicationList.size() == " + deduplicationList.size());
        final List<String> distinctDeduplicationList = deduplicationList.stream().distinct().collect(Collectors.toList());
        distinctDeduplicationList.forEach(System.out::println);
        return distinctDeduplicationList;
    }

}
