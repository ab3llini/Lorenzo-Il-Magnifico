package client;

import javafx.stage.Stage;

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

}
