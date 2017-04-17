package view;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.MarcRecord;
import utils.StringUtils;

import java.util.List;

/**
 * Created by jerry on 4/17/17.
 */
public class MainView {

    private final ListView<MarcRecord> listViewSub = new ListView<>();
    private final ListView<List<MarcRecord>> listViewMain = new ListView<>();
    private Stage primaryStage;

    public MainView(final Stage primaryStage) {
        this.primaryStage = primaryStage;
        initMainListView();
        initSubListView();
    }

    public ListView<MarcRecord> getSubListView() {
        return listViewSub;
    }

    public ListView<List<MarcRecord>> getMainListView() {
        return listViewMain;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void initMainListView() {
        getMainListView().setPrefWidth(300);
        getMainListView().setCellFactory(new Callback<ListView<List<MarcRecord>>, ListCell<List<MarcRecord>>>(){
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
        getSubListView().setPrefWidth(300);
        getSubListView().setCellFactory(new Callback<ListView<MarcRecord>, ListCell<MarcRecord>>() {
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

}
