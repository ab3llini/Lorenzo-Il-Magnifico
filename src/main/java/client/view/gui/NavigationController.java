package client.view.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

/*
 * @author  ab3llini
 * @since   22/05/17.
 */
public abstract class NavigationController {

    protected Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    protected void showAlert(Alert.AlertType type, String title, String header, String content) {

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();

    }

    protected void navigateTo(View view) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + view));
            Parent root = loader.load();
            this.stage.setTitle(view.getTitle());
            this.stage.setScene(new Scene(root, view.getW(), view.getH()));

        } catch (IOException e) {

            this.showAlert(Alert.AlertType.ERROR, "Exception raised", "Unable to navigate to view", e.getMessage());

        }

    }




}
