package lk.ijse.wood_management;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class HelloApplication extends Application {
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("/lk/ijse/wood_management/loginviwe.fxml"));
        Scene scene = new Scene((Parent) fxmlLoader.load(), 1104, 622);
        stage.setTitle("Wood Management System");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }
}
