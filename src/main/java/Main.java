import javafx.application.Application;
import javafx.stage.Stage;
import view.MainView;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        final MainView mainView = new MainView(primaryStage);
        new MainController().start(mainView);
    }
}
