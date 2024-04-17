package ProteomeExplorer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Huiting Xu
 */

public class ProteomeExplorer extends Application {
    public void start(Stage stage) throws Exception {
        var view = new WindowView(); // create view
        var presenter = new WindowPresenter();
        presenter.setup(stage, view.getController());

        // set the stage and show
        stage.setScene(new Scene(view.getRoot()));

        stage.setTitle("ProteomeExplorer");
        stage.show();
    }
}
