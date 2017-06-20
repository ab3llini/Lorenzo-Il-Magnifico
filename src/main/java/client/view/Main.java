package client.view;

/*
 * @author  ab3llini
 * @since   14/05/17.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {


    @Override


    public void start(Stage primaryStage) throws Exception {

        System.out.println();

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/connect.fxml"));

        primaryStage.setTitle("Connect");

        primaryStage.setScene(new Scene(root, 300, 500));

        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }

}