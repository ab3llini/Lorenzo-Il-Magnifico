package Controller;/*
 * Created by alberto on 09/05/17.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class ClientLauncher extends Application {

    Controller controller = new Controller();

    @Override
    public void start(Stage primaryStage) throws Exception{
        try {

            Parent root = FXMLLoader.load(getClass().getResource("/fxml/scene.fxml"));

            System.out.println(root);

            primaryStage.setTitle("ChatFX");
            primaryStage.setScene(new Scene(root, 300, 500));
            primaryStage.show();

        }
        catch (Exception e) {
            System.out.println("Unable to load XML");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
