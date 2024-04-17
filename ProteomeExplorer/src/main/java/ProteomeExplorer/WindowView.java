package ProteomeExplorer;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.Objects;

public class WindowView {
    private final WindowController controller;

    private final Parent root;

    public WindowView() throws IOException {
        try (var ins = Objects.requireNonNull(getClass().getResource("MainWindow.fxml")).openStream()) {
            var fxmlLoader = new FXMLLoader();
            root = fxmlLoader.load(ins);
            controller = fxmlLoader.getController();
        }
    }

    public WindowController getController() {
        return controller;
    }

    public Parent getRoot() {
        return root;
    }
}


