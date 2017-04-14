import data.DbDataManager;
import data.XmlDataManager;
import info.debatty.java.stringsimilarity.Levenshtein;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.MarcCompVector;
import models.MarcRecord;
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

    private static String[] sColumnNames;

    private static final ObservableList<MarcRecord> observableListOfDuplicates = FXCollections.observableArrayList();
    private static final ObservableList<List<MarcRecord>> observableListOfUniqueRecords = FXCollections.observableArrayList();

    private RManager rManager = RManager.getInstance();
    private Map<String, Integer> marcRecordsHashMap = new HashMap<>();
    private final ListView<MarcRecord> listViewSub = new ListView<>(observableListOfDuplicates);
    private final ListView<List<MarcRecord>> listViewMain = new ListView<>(observableListOfUniqueRecords);

    private List<List<MarcRecord>> masterRecordsUniqueList = new ArrayList<>();

    private String filePathFirstFile, filePathSecondFile;
    private Task<Void> deduplicationTask, deduplicationDbTask;

    static {
        System.loadLibrary("jri");
        sColumnNames = new String[]{"compC99id1", "compC99id2", "compControlField1", "compControlField2", "compLibraryId1", "compLibraryId2", "compPersonalName", "compPublisherName", "compTitle",
                "compNameOfPart", "compYearOfAuthor", "compYearOfPublication", "compInternationalStandardNumber", "compOverall"};
    }

    private XmlDataManager xmlDataManager = new XmlDataManager();

    public void start(Stage primaryStage) throws Exception {
        final long start = System.nanoTime();
        System.out.println("start(), please select files");

        initMainListView();
        initSubListView();
        initGui(primaryStage);

        deduplicationTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final List<MarcRecord> marcRecords1
                        = xmlDataManager
                        .getAllMarcRecords(null,
                                filePathFirstFile);
                final List<MarcRecord> marcRecords2
                        = xmlDataManager
                        .getAllMarcRecords(null,
                                filePathSecondFile);
                observableListOfUniqueRecords.addAll(createUniqueListFromTwoFilesSimpler(marcRecords1, marcRecords2));
                listViewMain.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        observableListOfDuplicates.clear();
                        for (int i = 1; i < listViewMain.getSelectionModel().getSelectedItem().size(); i++) {
                            observableListOfDuplicates.add(listViewMain.getSelectionModel().getSelectedItem().get(i));
                        }

                    }
                });
                Printer.printUniqueList(observableListOfUniqueRecords);
                final long end = System.nanoTime();
                Printer.printTimeElapsed(start, end);
                return null;
            }
        };

        deduplicationDbTask = new Task<Void>() {
            @SuppressWarnings("Duplicates")
            @Override
            protected Void call() throws Exception {
                observableListOfUniqueRecords.clear();
                final DbDataManager dataManager = new DbDataManager();
                initMasterRecordsUniqueList(dataManager);
                observableListOfUniqueRecords.addAll(masterRecordsUniqueList);
                listViewMain.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        observableListOfDuplicates.clear();
                        for (int i = 1; i < listViewMain.getSelectionModel().getSelectedItem().size(); i++) {
                            observableListOfDuplicates.add(listViewMain.getSelectionModel().getSelectedItem().get(i));
                        }

                    }
                });
                Printer.printUniqueList(observableListOfUniqueRecords);
                return null;
            }
        };

//        rManager.trainAndClassifyData2();

    }

    private void initGui(final Stage primaryStage) {
        BorderPane root = new BorderPane();

        VBox vbox = new VBox();
        vbox.setPrefWidth(200);
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);
        final Button buttonFirstFile = new Button();
        buttonFirstFile.setText("Načítať prvý súbor");

        final Button buttonSecondFile = new Button();
        buttonSecondFile.setText("Načítať druhý súbor");

        final Button buttonSaveToDb = new Button();
        buttonSaveToDb.setText("Uložiť súbor do databázy");

        final Button buttonLoadFromDb = new Button();
        buttonLoadFromDb.setText("Načítať z databázy");

        final Text textFirstFilePath = new Text();
        textFirstFilePath.setText("/path/to/file1.xml");

        final Text textSecondFilePath = new Text();
        textSecondFilePath.setText("/path/to/file2.xml");

        buttonFirstFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Vyberte prvý súbor na deduplikáciu");
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
                    textFirstFilePath.setText(file.getName());
                    filePathFirstFile = file.getAbsolutePath();
                }
            }
        });
        buttonSecondFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Vyberte druhý súbor na deduplikáciu");
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
                    textSecondFilePath.setText(file.getName());
                    filePathSecondFile = file.getAbsolutePath();
                }
            }
        });
        buttonLoadFromDb.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                new Thread(deduplicationDbTask).start();
            }
        });
        buttonSaveToDb.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Vyberte");
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
                    new DbDataManager().insertAllMarcRecordsToDatabase(
                            xmlDataManager.getAllMarcRecords(null, file.getAbsolutePath()),
                            DbDataManager.DB_MASTER_RECORDS);
                }
            }
        });
        vbox.getChildren().add(textFirstFilePath);
        vbox.getChildren().add(textSecondFilePath);

        final Button startDeduplicationButton = new Button();
        startDeduplicationButton.setText("Spustiť deduplikáciu");
        startDeduplicationButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (StringUtils.isValid(filePathFirstFile) && StringUtils.isValid(filePathSecondFile) && deduplicationTask != null) {
                    new Thread(deduplicationTask).start();
                }
            }
        });

        final Button startDeduplicationDbButton = new Button();
        startDeduplicationDbButton.setText("Spustiť deduplikáciu DB");
        startDeduplicationDbButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final DbDataManager dataManager = new DbDataManager();
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Vyberte súbor");
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
                    final List<MarcRecord> marcRecordsFromDb = getListWithoutDuplicates(masterRecordsUniqueList);
                    final List<MarcRecord> marcRecordsFromFile = xmlDataManager.getAllMarcRecords(null, file.getAbsolutePath());
                    System.out.println("marcRecordsFromDb.size: " + marcRecordsFromDb + "; marcRecordsFromFile.size: " + marcRecordsFromFile.size());
                    final List<List<MarcRecord>> mergedUniqueList = createUniqueListFromTwoFilesSimpler(marcRecordsFromDb, marcRecordsFromFile);
                    for (List<MarcRecord> marcRecordsList : mergedUniqueList) {
                        if (marcRecordsList.size() > 1) {
                            final MarcRecord masterRecord = findMasterRecord(marcRecordsList);
                            for (MarcRecord marcRecord : marcRecordsList) {
                                if (!marcRecord.isMasterDatabaseRecord()) { // marcRecord.equals(masterRecord) ?
                                    if (masterRecord == null) {
                                        // TODO: change Collections.singleton() to one element
                                        dataManager.insertAllMarcRecordsToDatabase(new ArrayList<>(Collections.singleton(marcRecord)), DbDataManager.DB_MASTER_RECORDS);
                                    } else {
                                        dataManager.insertAllMarcRecordsToDatabaseWithPrimaryKey(new ArrayList<>(Collections.singleton(marcRecord)), masterRecord.getPrimaryKey());
                                    }
                                }
                            }
                        } else {
                            if (!marcRecordsList.get(0).isMasterDatabaseRecord()) {
                                dataManager.insertAllMarcRecordsToDatabase(new ArrayList<>(Collections.singleton(marcRecordsList.get(0))), DbDataManager.DB_MASTER_RECORDS);
                            }
                        }
                    }

//                    masterRecordsUniqueList.clear();
//                    masterRecordsUniqueList.addAll(mergedUniqueList);
                    initMasterRecordsUniqueList(dataManager);
                    final ObservableList<List<MarcRecord>> observableList = FXCollections.observableList(masterRecordsUniqueList);
                    listViewMain.getSelectionModel().clearSelection();
                    System.out.println("set items");
                    listViewMain.setItems(observableList);


                }
            }
        });

        vbox.getChildren().add(startDeduplicationButton);
        vbox.getChildren().add(startDeduplicationDbButton);

        ToolBar toolBar = new ToolBar(buttonFirstFile, buttonSecondFile, buttonLoadFromDb, buttonSaveToDb);
        root.setTop(toolBar);
        root.setLeft(vbox);
        root.setCenter(listViewMain);
        root.setRight(listViewSub);
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    private void initMainListView() {
        listViewMain.setPrefWidth(300);
        listViewMain.setCellFactory(new Callback<ListView<List<MarcRecord>>, ListCell<List<MarcRecord>>>(){
            @Override
            public ListCell<List<MarcRecord>> call(ListView<List<MarcRecord>> p) {
                return new ListCell<List<MarcRecord>>(){
                    @Override
                    protected void updateItem(List<MarcRecord> t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(getFormattedMarcRecord(t.get(0)));
                            if (t.size() > 1) {
                                setStyle("-fx-control-inner-background: red");
                            } else {
                                setStyle(null);
                            }
                        } else {
                            setText("");
                        }
                    }
                };
            }
        });
    }

    private void initSubListView() {
        listViewSub.setPrefWidth(300);
        listViewSub.setCellFactory(new Callback<ListView<MarcRecord>, ListCell<MarcRecord>>() {
            @Override
            public ListCell<MarcRecord> call(ListView<MarcRecord> param) {
                return new ListCell<MarcRecord>() {
                    @Override
                    protected void updateItem(MarcRecord item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(getFormattedMarcRecord(item));
                        } else {
                            setText(""); // very important, so that the rest of ListView is cleaned out!
                        }
                    }
                };
            }
        });
    }

    private void initMasterRecordsUniqueList(final DbDataManager dataManager) {
        masterRecordsUniqueList.clear();
        final List<MarcRecord> marcRecords = dataManager.getAllMarcRecords(DbDataManager.DB_MASTER_RECORDS);
        for (MarcRecord marcRecord : marcRecords) {
            final List<MarcRecord> duplicateRecords = dataManager.getMarcRecordsWhereEquals(DbDataManager.DB_DUPLICATE_RECORDS, "fk_master_id", marcRecord.getPrimaryKey());
            if (duplicateRecords != null) {
                final List<MarcRecord> masterWithDuplicatesList = new ArrayList<>();
                masterWithDuplicatesList.add(marcRecord);
                masterWithDuplicatesList.addAll(duplicateRecords);
                masterRecordsUniqueList.add(masterWithDuplicatesList);
            } else {
                masterRecordsUniqueList.add(new ArrayList<>(Collections.singleton(marcRecord)));
            }
        }
    }

    private MarcRecord findMasterRecord(final List<MarcRecord> marcRecordList) {
        for (MarcRecord marcRecord : marcRecordList) {
            if (marcRecord.isMasterDatabaseRecord()) {
                return marcRecord;
            }
        }
        return null;
    }

    private String getFormattedMarcRecord(final MarcRecord marcRecord) {
        return "Názov diela: " + marcRecord.getTitleRaw() + "\n"
                + "Autor: " + (StringUtils.isValid(marcRecord.getPersonalNameRaw()) ? marcRecord.getPersonalNameRaw() : marcRecord.getPublisherNameRaw()) + "\n"
                + "Rok vydania: " + (StringUtils.isValid(marcRecord.getYearOfAuthorRaw()) ? marcRecord.getYearOfAuthorRaw() : marcRecord.getYearOfPublicationRaw()) + "\n"
                + "Id knižničného katalógu: " + marcRecord.getLibraryId() + "\n"
                + "Id bibliografického diela: " + marcRecord.getControlFieldId();
    }

    private List<MarcRecord> getListWithoutDuplicates(final List<List<MarcRecord>> uniqueList) {
        final List<MarcRecord> listWithoutDuplicates = new ArrayList<>();
        for (List<MarcRecord> list : uniqueList) {
            listWithoutDuplicates.add(list.get(0));
        }
        return listWithoutDuplicates;
    }

    @SuppressWarnings("Duplicates")
    private List<List<MarcRecord>> createUniqueListFromOneFile(final List<MarcRecord> marcRecords) {
        System.out.println("Merging records...");
        for (MarcRecord marcRecord : marcRecords) {
            marcRecordsHashMap.put(marcRecord.getControlFieldId() + "-" + marcRecord.getLibraryId(), -1);
        }
        System.out.println("Creating blocking vectors...");
        FileUtils.writeBeansToCsvFile(createBlockingCompVectorsFromRecords(marcRecords), "merged_marc_records_new.csv", MarcCompVector.class, sColumnNames);

//        System.out.println("Training data...");
//        rManager.trainDataFromFile("/Users/jerry/Desktop/git/deduplication-of-bibliographic-data/assets/prod/comp_vectors_all_train.csv");
//        rManager.classifyData("/Users/jerry/Desktop/git/deduplication-of-bibliographic-data/merged_marc_records_new.csv");

        System.out.println("Loading blocking vectors...");
        final List<MarcCompVector> mergedCompVectors = FileUtils.readCsv("merged_marc_records_new.csv", MarcCompVector.class, sColumnNames);
        System.out.println("Creating unique list...");
        return createUniqueMarcRecordsList(mergedCompVectors, marcRecords, null);
    }

    @SuppressWarnings("Duplicates")
    private List<List<MarcRecord>> createUniqueListFromTwoFilesSimpler(final List<MarcRecord> marcRecordList1, final List<MarcRecord> marcRecordList2) {
        System.out.println("Merging records...");
        final List<MarcRecord> mergedMarcRecords = Stream.concat(marcRecordList1.stream(), marcRecordList2.stream()).collect(Collectors.toList());
        for (MarcRecord marcRecord : mergedMarcRecords) {
            marcRecordsHashMap.put(marcRecord.getControlFieldId() + "-" + marcRecord.getLibraryId(), -1);
        }
        System.out.println("Creating blocking vectors...");
        FileUtils.writeBeansToCsvFile(createBlockingCompVectorsFromRecords(mergedMarcRecords), "merged_marc_records_new.csv", MarcCompVector.class, sColumnNames);

        System.out.println("Training data...");
        rManager.trainDataFromFile("/Users/jerry/Desktop/git/deduplication-of-bibliographic-data/assets/prod/comp_vectors_all_train2.csv", 0, 6, 7, 14);
        rManager.classifyData("/Users/jerry/Desktop/git/deduplication-of-bibliographic-data/merged_marc_records_new.csv", 0, 6, 7, 13);

        System.out.println("Loading blocking vectors...");
        final List<MarcCompVector> mergedCompVectors = FileUtils.readCsv("merged_marc_records_new.csv", MarcCompVector.class, sColumnNames);
        System.out.println("Creating unique list...");
        return createUniqueMarcRecordsList(mergedCompVectors, mergedMarcRecords, null);
    }

    private void addAllRecordsWithoutBlock(final List<List<MarcRecord>> uniqueList, final List<MarcRecord> marcRecords) {
        int counter = 0;
        for (MarcRecord marcRecord : marcRecords) {
            if (!marcRecord.isInAnyBlock()) {
                counter++;
//                System.out.println(counter + ". not in block - " + marcRecord.getControlFieldId());
                uniqueList.add(new ArrayList<>(Collections.singleton(marcRecord)));
            }
        }
        System.out.println("marcRecords.size(): " + marcRecords.size());
    }

    private List<MarcRecord> createUniqueMarcRecords(final List<List<MarcRecord>> uniqueList) {
        final List<MarcRecord> marcRecordList = new ArrayList<>();
        for (List<MarcRecord> marcRecords : uniqueList) {
            marcRecordList.add(marcRecords.get(0));
        }
        return marcRecordList;
    }

    @SuppressWarnings("Duplicates")
    private List<List<MarcRecord>> createUniqueMarcRecordsList(final List<MarcCompVector> marcCompVectors, final List<MarcRecord> marcRecords, final List<List<MarcRecord>> existingUniqueList) {
        final List<List<MarcRecord>> uniqueList = existingUniqueList == null ? new ArrayList<>() : existingUniqueList;
        for (MarcCompVector marcCompVector : marcCompVectors) {
            if (!marcCompVector.isDuplicate()) {
                if (!isControlFieldInUniqueList(marcCompVector.getCompControlField1(), marcCompVector.getCompLibraryId1(), uniqueList)) {
                    final MarcRecord marcRecord = findMarcRecordByControlField(marcCompVector.getCompControlField1(), marcCompVector.getCompLibraryId1(), marcRecords);
                    if (marcRecord != null) {
                        uniqueList.add(new ArrayList<>(Collections.singleton(marcRecord)));
                        if (marcRecordsHashMap.containsKey(marcRecord.getControlFieldId() + "-" + marcRecord.getLibraryId())) {
                            marcRecordsHashMap.put(marcRecord.getControlFieldId() + "-" + marcRecord.getLibraryId(), uniqueList.size() - 1);
                        }
                    }
                }
                if (!isControlFieldInUniqueList(marcCompVector.getCompControlField2(), marcCompVector.getCompLibraryId2(), uniqueList)) {
                    final MarcRecord marcRecord = findMarcRecordByControlField(marcCompVector.getCompControlField2(), marcCompVector.getCompLibraryId2(), marcRecords);
                    if (marcRecord != null) {
                        uniqueList.add(new ArrayList<>(Collections.singleton(marcRecord)));
                        if (marcRecordsHashMap.containsKey(marcRecord.getControlFieldId() + "-" + marcRecord.getLibraryId())) {
                            marcRecordsHashMap.put(marcRecord.getControlFieldId() + "-" + marcRecord.getLibraryId(), uniqueList.size() - 1);
                        }
                    }
                }
            } else { // is duplicate
                int controlField1PositionInList = getPositionOfDuplicateList(marcCompVector.getCompControlField1(),
                        marcCompVector.getCompLibraryId1(), uniqueList);
                int controlField2PositionInList = getPositionOfDuplicateList(marcCompVector.getCompControlField2(),
                        marcCompVector.getCompLibraryId2(), uniqueList);
                if (controlField1PositionInList != -1 && controlField2PositionInList != -1) { // they are both already added
                    if (controlField1PositionInList != controlField2PositionInList) {
                        final List<MarcRecord> newList = Stream.concat(
                                uniqueList.get(controlField1PositionInList).stream(),
                                uniqueList.get(controlField2PositionInList).stream())
                                .collect(Collectors.toList());
                        uniqueList.get(controlField1PositionInList).clear();
                        uniqueList.get(controlField1PositionInList).addAll(newList);
                        uniqueList.remove(controlField2PositionInList);
                        for (Map.Entry<String, Integer> entry : marcRecordsHashMap.entrySet()) {
                            if (entry.getValue() >= controlField2PositionInList) {
                                entry.setValue(entry.getValue() - 1);
                            }
                        }
                        for (MarcRecord marcRecord : newList) {
                            final int position = controlField1PositionInList < controlField2PositionInList
                                    ? controlField1PositionInList
                                    : (controlField1PositionInList - 1);
                            marcRecordsHashMap.put(marcRecord.getControlFieldId() + "-"
                                    + marcRecord.getLibraryId(), position);
                        }
                    }
                    continue;
                }
                if (controlField1PositionInList != -1) {
                    final MarcRecord marcRecord = findMarcRecordByControlField(marcCompVector.getCompControlField2(),
                            marcCompVector.getCompLibraryId1(), marcRecords);
                    if (marcRecord != null) {
                        uniqueList.get(controlField1PositionInList).add(marcRecord);
                        marcRecordsHashMap.put(marcRecord.getControlFieldId() + "-"
                                + marcRecord.getLibraryId(),controlField1PositionInList);
                    }
                } else if (controlField2PositionInList != -1) {
                    final MarcRecord marcRecord = findMarcRecordByControlField(marcCompVector.getCompControlField1(),
                            marcCompVector.getCompLibraryId2(), marcRecords);
                    if (marcRecord != null) {
                        uniqueList.get(controlField2PositionInList).add(marcRecord);
                        marcRecordsHashMap.put(marcRecord.getControlFieldId() + "-"
                                + marcRecord.getLibraryId(), controlField2PositionInList);
                    }
                } else {
                    final List<MarcRecord> newList = new ArrayList<>();
                    final MarcRecord marcRecord1 = findMarcRecordByControlField(marcCompVector.getCompControlField1(),
                            marcCompVector.getCompLibraryId1(), marcRecords);
                    final MarcRecord marcRecord2 = findMarcRecordByControlField(marcCompVector.getCompControlField2(),
                            marcCompVector.getCompLibraryId2(), marcRecords);
                    if (marcRecord1 != null && marcRecord2 != null) {
                        newList.add(marcRecord1);
                        newList.add(marcRecord2);
                        uniqueList.add(newList);
                        if (marcRecordsHashMap.containsKey(marcRecord1.getControlFieldId() + "-"
                                + marcRecord1.getLibraryId())) {
                            marcRecordsHashMap.put(marcRecord1.getControlFieldId() + "-"
                                    + marcRecord1.getLibraryId(), uniqueList.size() - 1);
                        }
                        if (marcRecordsHashMap.containsKey(marcRecord2.getControlFieldId()
                                + "-" + marcRecord2.getLibraryId())) {
                            marcRecordsHashMap.put(marcRecord2.getControlFieldId()
                                    + "-" + marcRecord2.getLibraryId(), uniqueList.size() - 1);
                        }
                    }
                }
            }
        }
        addAllRecordsWithoutBlock(uniqueList, marcRecords);
        return uniqueList;
    }

    private boolean isControlFieldInUniqueList(final String controlField, final String libraryId, final List<List<MarcRecord>> uniqueList) {
        final String controlFieldWithLibraryId = controlField + "-" + libraryId;
        return marcRecordsHashMap.get(controlFieldWithLibraryId) != -1;
    }

    private int getPositionOfDuplicateList(final String controlField, final String libraryId, final List<List<MarcRecord>> uniqueList) {
        final String controlFieldWithLibraryId = controlField + "-" + libraryId;
        if (marcRecordsHashMap.containsKey(controlFieldWithLibraryId)) {
            return marcRecordsHashMap.get(controlFieldWithLibraryId);
        }
        return -1;
    }

    private MarcRecord findMarcRecordByControlField(final String controlField, final String libraryId, final List<MarcRecord> marcRecords) {
        for (MarcRecord marcRecord : marcRecords) {
            if (controlField.equals(marcRecord.getControlFieldId()) && marcRecord.getLibraryId().equals(libraryId)) {
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
                            System.out.println("equals: " + record1.getC99FieldId() + "-" + record1.getLibraryId() + "; and " + record2.getC99FieldId() + "-" + record2.getLibraryId());
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
    private List<MarcCompVector> createAllCompVectorsFromRecords(final List<MarcRecord> marcRecords) {
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
                        vectorsDuplicated.add(marcCompVector);
                    } else {
                        System.out.println("NOT equals: " + record1.getC99FieldId() + "-" + record1.getLibraryId() + "; and " + record2.getC99FieldId() + "-" + record2.getLibraryId());
                        vectorsNonDuplicated.add(marcCompVector);
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

}
