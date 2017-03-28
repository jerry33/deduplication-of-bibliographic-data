package servlets;

import data.XmlDataManager;
import models.MarcCompVector;
import models.MarcRecord;
import utils.FileUtils;
import utils.MarcUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
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

}
