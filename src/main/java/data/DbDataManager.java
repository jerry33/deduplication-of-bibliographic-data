package data;

import models.MarcRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerry on 4/10/17.
 */
public class DbDataManager {

    private static DbDataManager INSTANCE;
    private static Connection CONNECTION;

    public static final String DB_MASTER_RECORDS = "master_records";
    public static final String DB_DUPLICATE_RECORDS = "duplicate_records";

    public static DbDataManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DbDataManager();
        }
        return INSTANCE;
    }

    public List<MarcRecord> getAllMarcRecords(final String tableName) {
        Connection conn;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        conn = getConnection();

        final List<MarcRecord> marcRecordsList = new ArrayList<>();
        final String query = "SELECT * FROM " + tableName;
        try {
            if (conn != null) {
                final Statement statement = conn.createStatement();
                final ResultSet rs = statement.executeQuery(query);
                while (rs.next()) {
                    final MarcRecord marcRecord = new MarcRecord();
                    if (tableName.equals(DB_MASTER_RECORDS)) {
                        marcRecord.setIsMasterDatabaseRecord(true);
                    }
                    marcRecord.bindData(rs);
                    marcRecordsList.add(marcRecord);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        if (conn != null) {
//            try {
//                conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
        System.out.println("number of records in DB: " + marcRecordsList.size());
        return marcRecordsList;
    }

    public List<MarcRecord> getMarcRecordsWhereEquals(final String tableName, final String columnName, final Object value) {
        Connection conn;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        conn = getConnection();

        final List<MarcRecord> marcRecordsList = new ArrayList<>();
        final String query = "SELECT * FROM " + tableName + " WHERE " + columnName + " = " + value;
        try {
            if (conn != null) {
                final Statement statement = conn.createStatement();
                final ResultSet rs = statement.executeQuery(query);
                if (!rs.isBeforeFirst()) {
                    return null;
                }
                while (rs.next()) {
                    final MarcRecord marcRecord = new MarcRecord();
                    marcRecord.bindData(rs);
                    marcRecordsList.add(marcRecord);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        if (conn != null) {
//            try {
//                conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
        return marcRecordsList;
    }

    @SuppressWarnings("Duplicates")
    public void insertAllMarcRecordsToDatabase(final List<MarcRecord> marcRecordsList, final String tableName) {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        conn = getConnection();
        String query = "INSERT INTO " + tableName + " (" + MarcRecord.COLUMN_TYPE_OF_MATERIAL
                + ", " + MarcRecord.COLUMN_C99_FIELD_ID
                + ", " + MarcRecord.COLUMN_CONTROL_FIELD_ID
                + ", " + MarcRecord.COLUMN_LIBRARY_ID
                + ", " + MarcRecord.COLUMN_UNIQUE_ID
                + ", " + MarcRecord.COLUMN_BLOCKING_KEY
                + ", " + MarcRecord.COLUMN_PERSONAL_NAME
                + ", " + MarcRecord.COLUMN_PUBLISHER_NAME
                + ", " + MarcRecord.COLUMN_TITLE
                + ", " + MarcRecord.COLUMN_NAME_OF_PART
                + ", " + MarcRecord.COLUMN_YEAR_OF_AUTHOR
                + ", " + MarcRecord.COLUMN_YEAR_OF_PUBLICATION + ")"
                + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        for (final MarcRecord marcRecord : marcRecordsList) {
            PreparedStatement preparedStatement;
            try {
                preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, marcRecord.getTypeOfMaterial());
                preparedStatement.setString(2, marcRecord.getC99FieldIdRaw());
                preparedStatement.setString(3, marcRecord.getControlFieldId());
                preparedStatement.setString(4, marcRecord.getLibraryId());
                preparedStatement.setString(5, marcRecord.getControlFieldId() + "-" + marcRecord.getLibraryId());
                preparedStatement.setString(6, marcRecord.getBlockingKey());
                preparedStatement.setString(7, marcRecord.getPersonalNameRaw());
                preparedStatement.setString(8, marcRecord.getPublisherNameRaw());
                preparedStatement.setString(9, marcRecord.getTitleRaw());
                preparedStatement.setString(10, marcRecord.getNameOfPartRaw());
                preparedStatement.setString(11, marcRecord.getYearOfAuthorRaw());
                preparedStatement.setString(12, marcRecord.getYearOfPublicationRaw());
                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
//        if (conn != null) {
//            try {
//                conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @SuppressWarnings("Duplicates")
    public void insertAllMarcRecordsToDatabaseWithPrimaryKey(final List<MarcRecord> marcRecordsList, int primaryKey) {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        conn = getConnection();
        String query = "INSERT INTO duplicate_records ("
                + "fk_master_id"
                + ", " + MarcRecord.COLUMN_TYPE_OF_MATERIAL
                + ", " + MarcRecord.COLUMN_C99_FIELD_ID
                + ", " + MarcRecord.COLUMN_CONTROL_FIELD_ID
                + ", " + MarcRecord.COLUMN_LIBRARY_ID
                + ", " + MarcRecord.COLUMN_UNIQUE_ID
                + ", " + MarcRecord.COLUMN_BLOCKING_KEY
                + ", " + MarcRecord.COLUMN_PERSONAL_NAME
                + ", " + MarcRecord.COLUMN_PUBLISHER_NAME
                + ", " + MarcRecord.COLUMN_TITLE
                + ", " + MarcRecord.COLUMN_NAME_OF_PART
                + ", " + MarcRecord.COLUMN_YEAR_OF_AUTHOR
                + ", " + MarcRecord.COLUMN_YEAR_OF_PUBLICATION + ")"
                + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        for (final MarcRecord marcRecord : marcRecordsList) {
            PreparedStatement preparedStatement;
            try {
                preparedStatement = conn.prepareStatement(query);
                preparedStatement.setInt(1, primaryKey);
                preparedStatement.setString(2, marcRecord.getTypeOfMaterial());
                preparedStatement.setString(3, marcRecord.getC99FieldIdRaw());
                preparedStatement.setString(4, marcRecord.getControlFieldId());
                preparedStatement.setString(5, marcRecord.getLibraryId());
                preparedStatement.setString(6, marcRecord.getControlFieldId() + "-" + marcRecord.getLibraryId());
                preparedStatement.setString(7, marcRecord.getBlockingKey());
                preparedStatement.setString(8, marcRecord.getPersonalNameRaw());
                preparedStatement.setString(9, marcRecord.getPublisherNameRaw());
                preparedStatement.setString(10, marcRecord.getTitleRaw());
                preparedStatement.setString(11, marcRecord.getNameOfPartRaw());
                preparedStatement.setString(12, marcRecord.getYearOfAuthorRaw());
                preparedStatement.setString(13, marcRecord.getYearOfPublicationRaw());
                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
//        if (conn != null) {
//            try {
//                conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public void deleteAllRows(final String tableName) {
        Connection conn;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        conn = getConnection();

        final String query = "DELETE FROM " + tableName;
        try {
            final Statement statement = conn.createStatement();
            int deletedRows = statement.executeUpdate(query);
            if (deletedRows > 0) {
                System.out.println("deleted all rows");
            } else {
                System.out.println("unsuccessful deletion");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void truncateAllTables() {
        Connection conn;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        conn = getConnection();
        try {
            final Statement statement = conn.createStatement();
            statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
            statement.executeUpdate("TRUNCATE master_records");
            statement.executeUpdate("TRUNCATE duplicate_records");
            statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() {
//        Connection conn = null;
        if (CONNECTION == null) {
            try {
                CONNECTION = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliographic_database?" +
                        "user=root&password=root&characterEncoding=utf8");
            } catch (SQLException ex) {
                // handle any errors
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            }
        }
        return CONNECTION;
    }

}
