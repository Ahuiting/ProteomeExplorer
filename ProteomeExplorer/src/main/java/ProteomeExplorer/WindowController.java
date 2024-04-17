package ProteomeExplorer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.ResourceBundle;

public class WindowController implements Initializable {

    @FXML
    private MenuItem closeMenuItem;

    @FXML
    private MenuItem openMenuItem;

    @FXML
    private MenuItem undoMenuItem;

    @FXML
    private MenuItem redoMenuItem;

    @FXML
    private CheckMenuItem darkCheckMenuItem;

    @FXML
    private CheckMenuItem fullScreenCheckMenuItem;
    @FXML
    private MenuItem aboutMenuItem;
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextFlow TextViewer;

    @FXML
    private TextField textField;

    @FXML
    private TextField pdbTextField;

    @FXML
    private Button structureButton;
    @FXML
    private CheckBox ballsCheckButton;

    @FXML
    private CheckBox sticksCheckButton;

    @FXML
    private CheckBox ribbonsCheckButton;

    @FXML
    private Pane centerPane;

    @FXML
    private ListView filesListView;

    @FXML
    private Label infoLabel;

    @FXML
    private Slider ballsSlider;

    @FXML
    private Slider sticksSlider;

    @FXML
    private PieChart selectedPieChart;

    @FXML
    private VBox seqsVBox;

    @FXML
    private VBox chartsVBox;

    @FXML
    private ComboBox<String> colorComboBox;
    private final ObservableList colorList = FXCollections.observableArrayList("by Atoms", "by Amino Acids", "by Structure");

    @Override
    public void initialize(URL url, ResourceBundle resource) {
        this.colorComboBox.setItems(colorList);
        this.colorComboBox.getSelectionModel().selectFirst();
    }

    public ComboBox<String> getColorComboBox() {
        return colorComboBox;
    }

    public MenuItem getCloseMenuItem() {
        return closeMenuItem;
    }

    public MenuItem getOpenMenuItem() {
        return openMenuItem;
    }

    public MenuItem getUndoMenuItem() {
        return undoMenuItem;
    }

    public MenuItem getRedoMenuItem() {
        return redoMenuItem;
    }

    public CheckMenuItem getDarkCheckMenuItem() {
        return darkCheckMenuItem;
    }

    public CheckMenuItem getFullScreenCheckMenuItem() {
        return fullScreenCheckMenuItem;
    }

    public MenuItem getAboutMenuItem() {
        return aboutMenuItem;
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public TextFlow getTextViewer() {
        return TextViewer;
    }

    public TextField getTextField() {
        return textField;
    }

    public TextField getPdbTextField() {
        return pdbTextField;
    }

    public CheckBox getBallsCheckButton() {
        return ballsCheckButton;
    }

    public Pane getCenterPane() {
        return centerPane;
    }

    public Label getInfoLabel() {
        return infoLabel;
    }

    public Slider getBallsSlider() {
        return ballsSlider;
    }

    public Slider getSticksSlider() {
        return sticksSlider;
    }

    public CheckBox getSticksCheckButton() {
        return sticksCheckButton;
    }

    public CheckBox getRibbonsCheckButton() {
        return ribbonsCheckButton;
    }

    public VBox getSeqsVBox() {
        return seqsVBox;
    }

    public VBox getChartsVBox() {
        return chartsVBox;
    }

    public ListView getFilesListView() {
        return filesListView;
    }

    public PieChart getSelectedPieChart() {
        return selectedPieChart;
    }

    public Button getStructureButton() {
        return structureButton;
    }
}

