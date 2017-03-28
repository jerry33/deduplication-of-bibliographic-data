package servlets;

import data.XmlDataManager;
import info.debatty.java.stringsimilarity.Levenshtein;
import models.MarcCompVector;
import models.MarcRecord;
import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.MarcXmlWriter;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import utils.FileUtils;
import utils.MarcUtils;
import utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * Created by jerry on 3/28/17.
 */
public class MainServlet extends HttpServlet {

    private XmlDataManager xmlDataManager;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        out.println("<html>");
        out.println("<body style='background:black'>");
        out.println("<h1 style='color:white'>Hello Servlet Get Out!</h1>");
        out.println("</body>");
        out.println("</html>");
        System.out.println("doGet starting something");
//        getServletContext().getRequestDispatcher("/index.jsp").forward
//                (req, resp);
        xmlDataManager = new XmlDataManager(FileUtils.FILE_PATH_WITH_C99_DEDUP);
        System.out.println("doGet after xmlDataManager init");
        saveAllMarcCompVectorsToCsv();
    }

    @SuppressWarnings("Duplicates")
    private void saveAllMarcCompVectorsToCsv() {
        System.out.println("saveAllMarcCompVectorsToCsv");
        final List<MarcRecord> marcRecords = xmlDataManager.getAllMarcRecords(getServletContext());
        final List<MarcCompVector> marcCompVectors = new ArrayList<>();
        final List<MarcCompVector> vectorsDuplicated = new ArrayList<>();
        final List<MarcCompVector> vectorsNonDuplicated = new ArrayList<>();
        System.out.println("marcRecords.size(): " + marcRecords.size());
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
        FileUtils.writeBeansToCsvFile(marcCompVectors,
                getServletContext().getRealPath(FileUtils.FILE_NAME_ALL_MARC_COMP_VECTORS),
                MarcCompVector.class,
                "compC99ids", "compPersonalName", "compPublisherName", "compTitle",
                "compNameOfPart", "compYearOfAuthor", "compYearOfPublication", "compInternationalStandardNumber", "compOverall");
    }

    @SuppressWarnings("Duplicates")
    private void saveBlockingMarcCompVectorsToCsv() {
        final List<MarcRecord> marcRecords = xmlDataManager.getAllMarcRecords(null);
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

        System.out.println("listOfBlockingLists.size(): " + listOfBlockingLists.size());
        final List<Integer> sizesOfBlockingLists = new ArrayList<>();
        for (List<MarcRecord> b : listOfBlockingLists) {
            sizesOfBlockingLists.add(b.size());
            System.out.println("blockingKey: " + b.get(0).getBlockingKey() + " ; size: " + b.size());
        }
        Collections.sort(sizesOfBlockingLists);
        for (Integer i : sizesOfBlockingLists) {
            System.out.println("size of blocking list: " + i);
        }

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
                        System.out.println("numberOfComparisons: " + numberOfComparisons);
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
        if(FileUtils.FILE_PATH_WITH_C99_DEDUP.contains(".")) {
            newFileName = FileUtils.FILE_PATH_WITH_C99_DEDUP.substring(0, FileUtils.FILE_PATH_WITH_C99_DEDUP.lastIndexOf('.'));
            newFileName = newFileName + "_blocking_comp_vectors.xml";
        }

        if (StringUtils.isValid(newFileName)) {
            FileUtils.writeBeansToCsvFile(marcCompVectors,
                    newFileName,
                    MarcCompVector.class,
                    "compControlFields", "compPersonalName", "compPublisherName", "compTitle",
                    "compNameOfPart", "compYearOfAuthor", "compYearOfPublication", "compInternationalStandardNumber");
        }

    }

    @SuppressWarnings("Duplicates")
    private void writeDuplicateRecordsToFile() {
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
            final Set<String> c99IdentifiersHashSet = new HashSet<>(xmlDataManager.getDuplicateIdentifiers());
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
