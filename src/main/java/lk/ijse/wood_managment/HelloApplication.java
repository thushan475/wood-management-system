
package lk.ijse.wood_managment;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    private static Scene scene;
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("loginviwe" +".fxml"));
        Scene scene = new Scene((Parent)fxmlLoader.load(), 1104,622) ;
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}

//package lk.ijse.wood_managment;
//
//import java.io.IOException;
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//
//public class HelloApplication extends Application {
//
//    @Override
//    public void start(Stage stage) throws IOException {
//
//        FXMLLoader fxmlLoader = new FXMLLoader(
//                HelloApplication.class.getResource("/lk/ijse/wood_managment/loginviwe.fxml")
//        );
//
//        Scene scene = new Scene(fxmlLoader.load());
//
//        stage.setTitle("Login");
//        stage.setMaximized(true);
//        stage.setScene(scene);
//        stage.show();
//    }
//}
