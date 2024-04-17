module com.example.proteomeexplorer {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.glassfish.java.json;


    opens ProteomeExplorer to javafx.fxml;
    exports ProteomeExplorer;
    exports ProteomeExplorer.undoredo;
    opens ProteomeExplorer.undoredo to javafx.fxml;
    exports ProteomeExplorer.mySelectionModel;
    opens ProteomeExplorer.mySelectionModel to javafx.fxml;
    exports ProteomeExplorer.moleculeShow;
    opens ProteomeExplorer.moleculeShow to javafx.fxml;
    exports ProteomeExplorer.statisticalDataShow;
    opens ProteomeExplorer.statisticalDataShow to javafx.fxml;
}