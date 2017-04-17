import data.DbDataManager;
import data.XmlDataManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.*;
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

    private static final ObservableList<MarcRecord> observableListOfDuplicates = FXCollections.observableArrayList();
    private static final ObservableList<List<MarcRecord>> observableListOfUniqueRecords = FXCollections.observableArrayList();

    private RManager rManager = RManager.getInstance();
    private Map<String, Integer> marcRecordsHashMap = new HashMap<>();
    private final ListView<MarcRecord> listViewSub = new ListView<>(observableListOfDuplicates);
    private final ListView<List<MarcRecord>> listViewMain = new ListView<>(observableListOfUniqueRecords);

    private List<List<MarcRecord>> masterRecordsUniqueList = new ArrayList<>();

    private String filePathFirstFile, filePathSecondFile;
    private Task<Void> deduplicationTask, deduplicationDbTask;

    private Classifier selectedClassifier = Classifier.C50;
    
    private VBox vBox;

    static {
        System.loadLibrary("jri");
    }

    private XmlDataManager xmlDataManager = XmlDataManager.getInstance();

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

//        rManager.trainAndClassifyData2(Classifier.C50);
//        saveBlockingMarcCompVectorsToCsv("/Users/jerry/Desktop/git/deduplication-of-bibliographic-data/assets/prod/all_records_with_c99.xml");
//        Printer.printOnlyDuplicates(xmlDataManager.getAllMarcRecords(null, "/Users/jerry/Desktop/git/deduplication-of-bibliographic-data/assets/prod/all_records_with_c99.xml"));

    }

    private void initGui(final Stage primaryStage) {
        BorderPane root = new BorderPane();

        vBox = new VBox();
        vBox.setPrefWidth(200);
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(8);
        final Button buttonFirstFile = new Button();
        buttonFirstFile.setText("Načítať prvý súbor");

        final Button buttonSecondFile = new Button();
        buttonSecondFile.setText("Načítať druhý súbor");

        final Button buttonSaveToDb = new Button();
        buttonSaveToDb.setText("Uložiť súbor do databázy");

        final Button buttonLoadFromDb = new Button();
        buttonLoadFromDb.setText("Načítať z databázy");

        final Button buttonDeleteDb = new Button();
        buttonDeleteDb.setText("Vymazať záznamy z databázy");

        final Text textFirstFilePath = new Text();
        textFirstFilePath.setText("/path/to/file1.xml");

        final Text textSecondFilePath = new Text();
        textSecondFilePath.setText("/path/to/file2.xml");

        buttonFirstFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final File file = FileUtils.openFileFromDialog(primaryStage);
                if (file != null) {
                    textFirstFilePath.setText(file.getName());
                    filePathFirstFile = file.getAbsolutePath();
                }
            }
        });
        buttonSecondFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final File file = FileUtils.openFileFromDialog(primaryStage);
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
                final File file = FileUtils.openFileFromDialog(primaryStage);
                if (file != null) {
                    new DbDataManager().insertAllMarcRecordsToDatabase(
                            xmlDataManager.getAllMarcRecords(null, file.getAbsolutePath()),
                            DbDataManager.DB_MASTER_RECORDS);
                }
            }
        });

        buttonDeleteDb.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                new DbDataManager().truncateAllTables();
            }
        });
        vBox.getChildren().add(textFirstFilePath);
        vBox.getChildren().add(textSecondFilePath);

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
                final File file = FileUtils.openFileFromDialog(primaryStage);
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

        vBox.getChildren().add(startDeduplicationButton);
        vBox.getChildren().add(startDeduplicationDbButton);

        initRadioButtons();

        ToolBar toolBar = new ToolBar(buttonFirstFile, buttonSecondFile, buttonLoadFromDb, buttonSaveToDb, buttonDeleteDb);
        root.setTop(toolBar);
        root.setLeft(vBox);
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
                            setText(t.get(0).getFormatted());
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
                            setText(item.getFormatted());
                        } else {
                            setText(""); // very important, so that the rest of ListView is cleaned out!
                        }
                    }
                };
            }
        });
    }

    private void initRadioButtons() {
        final ToggleGroup toggleGroup = new ToggleGroup();
        final RadioButton radioButtonC50 = new RadioButton("C5.0");
        radioButtonC50.setUserData(Classifier.C50);
        radioButtonC50.setSelected(true);
        radioButtonC50.setToggleGroup(toggleGroup);
        final RadioButton radioButtonRandomForest = new RadioButton("Random Forest");
        radioButtonRandomForest.setToggleGroup(toggleGroup);
        radioButtonRandomForest.setUserData(Classifier.RANDOM_FOREST);
        final RadioButton radioButtonNaiveBayes = new RadioButton("Naive Bayes");
        radioButtonNaiveBayes.setToggleGroup(toggleGroup);
        radioButtonNaiveBayes.setUserData(Classifier.NAIVE_BAYES);
        final RadioButton radioButtonSvm = new RadioButton("SVM");
        radioButtonSvm.setToggleGroup(toggleGroup);
        radioButtonSvm.setUserData(Classifier.SVM);
        toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                selectedClassifier = (Classifier) toggleGroup.getSelectedToggle().getUserData();
            }
        });
        vBox.getChildren().add(radioButtonC50);
        vBox.getChildren().add(radioButtonRandomForest);
        vBox.getChildren().add(radioButtonNaiveBayes);
        vBox.getChildren().add(radioButtonSvm);
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
        FileUtils.writeBeansToCsvFile(createBlockingCompVectorsFromRecords(marcRecords), "merged_marc_records_new.csv", MarcCompVector.class, MarcCompVector.COLUMNS);

//        System.out.println("Training data...");
//        rManager.trainDataFromFile("/Users/jerry/Desktop/git/deduplication-of-bibliographic-data/assets/prod/comp_vectors_all_train.csv");
//        rManager.classifyData("/Users/jerry/Desktop/git/deduplication-of-bibliographic-data/merged_marc_records_new.csv");

        System.out.println("Loading blocking vectors...");
        final List<MarcCompVector> mergedCompVectors = FileUtils.readCsv("merged_marc_records_new.csv", MarcCompVector.class, MarcCompVector.COLUMNS);
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
        FileUtils.writeBeansToCsvFile(createBlockingCompVectorsFromRecords(mergedMarcRecords), "merged_marc_records_new.csv", MarcCompVector.class, MarcCompVector.COLUMNS);

        System.out.println("Training data...");
        rManager.trainDataFromFile("/Users/jerry/Desktop/git/deduplication-of-bibliographic-data/assets/prod/comp_vectors_all_train2_without915.csv", 0, 6, 7, 14);
        rManager.classifyData(selectedClassifier, "/Users/jerry/Desktop/git/deduplication-of-bibliographic-data/merged_marc_records_new.csv", 0, 6, 7, 13);

        System.out.println("Loading blocking vectors...");
        final List<MarcCompVector> mergedCompVectors = FileUtils.readCsv("merged_marc_records_new.csv", MarcCompVector.class, MarcCompVector.COLUMNS);
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

    private List<MarcCompVector> createBlockingCompVectorsFromFile(final String filePath) {
        return new BlockingCompVectors().createFromFile(filePath);
    }

    private List<MarcCompVector> createBlockingCompVectorsFromRecords(final List<MarcRecord> marcRecords) {
        return new BlockingCompVectors().createFromMarcRecords(marcRecords);
    }

    private List<MarcCompVector> createAllCompVectorsFromFile(final String filePath) {
        return new AllCompVectors().createFromFile(filePath);
    }

    private List<MarcCompVector> createAllCompVectorsFromRecords(final List<MarcRecord> marcRecords) {
        return new AllCompVectors().createFromMarcRecords(marcRecords);
    }

}
