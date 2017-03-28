package utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.BeanToCsv;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerry on 3/25/17.
 */
public class FileUtils {

    public static final String FILE_PATH_WITH_C99_DEDUP = "assets/Vy14to16sC99a_with_dedup.xml";
//    public static final String FILE_PATH_WITH_C99_DEDUP = "/WEB-INF/assets/Vy14to16sC99a_with_dedup.xml";
    public static final String FILE_NAME_ALL_MARC_COMP_VECTORS = "assets/marc_comp_vectors85_new.csv";
//    public static final String FILE_NAME_ALL_MARC_COMP_VECTORS = "/WEB-INF/assets/marc_comp_vectors85_new.csv";
    public static final String FILE_NAME_CSV_TO_READ = "assets/Vy14to16sC99a_with_dedup_blocking_comp_vectors.csv";
    public static final String FILE_PATH_DUPLICATED_ENTRIES = "assets/duplicated66.xml";
    public static final String CONTROL_FIELD_SEPARATOR = "-";

    public static <T> void writeBeansToCsvFile(final List<T> beans, final String fileName, Class<T> klazz,
                                         final String... columnNames) {
        CSVWriter csvWriter = null;
        try {
            final BeanToCsv<T> beanToCsv = new BeanToCsv<>();
            csvWriter = new CSVWriter(new FileWriter(fileName));
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

    public static void writeToCsvFile(final String filePath, final String... values) {
        try {
            final CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath));
            csvWriter.writeNext(values);
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> List<T> readCsv(final String filePath, Class<T> klazz, final String... columnNames) {
        try {
            final CSVReader csvReader = new CSVReader(new FileReader(filePath), ',', '"', 1);
            final CsvToBean<T> csvToBean = new CsvToBean<>();
            final ColumnPositionMappingStrategy<T> mappingStrategy = new ColumnPositionMappingStrategy<>();
            mappingStrategy.setType(klazz);
            mappingStrategy.setColumnMapping(columnNames);
            return csvToBean.parse(mappingStrategy, csvReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Nullable
    public static FileInputStream getNewFileInputStream(@NotNull final String filePath) {
        final File file = new File(filePath);
        final FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
            return fileInputStream;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
