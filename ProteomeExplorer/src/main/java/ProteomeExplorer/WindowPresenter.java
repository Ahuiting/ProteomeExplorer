package ProteomeExplorer;

import ProteomeExplorer.PdbDraw.model.*;
import ProteomeExplorer.moleculeShow.MoleculeShow;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

public class WindowPresenter {


    // set up the program
    public void setup(Stage stage, WindowController controller) {
        controller.getCloseMenuItem().setOnAction(e -> Platform.exit());
        controller.getOpenMenuItem().setOnAction(e -> {
            try {
                open(stage, controller);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        controller.getDarkCheckMenuItem().selectedProperty().addListener((v, o, n) -> {
            String cssFile = String.valueOf(getClass().getResource("dark.css"));
            if (n) stage.getScene().getStylesheets().add(cssFile);
            else stage.getScene().getStylesheets().remove(cssFile);
        });
        controller.getFullScreenCheckMenuItem().selectedProperty().addListener((v, o, n) -> stage.setFullScreen(n));

        controller.getAboutMenuItem().setOnAction(e->{
            try {
                String txtFile=ParserProtein.readPdbFile("src/main/java/ProteomeExplorer/README.txt");
                Text text=new Text(txtFile);
                ScrollPane pane=new ScrollPane();
                text.wrappingWidthProperty().bind(pane.widthProperty().subtract(17));
                pane.setContent(text);
                Scene scene = new Scene(pane,400,600);
                Stage helpStage=new Stage();
                helpStage.setScene(scene);
                helpStage.setTitle("Help");
                helpStage.show();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

    }

    public void open(Stage stage, WindowController controller) {

        // open a file and generate the figure of the protein
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("open a directory with .pdb or .pdz.gz file");

        File folder = dc.showDialog(stage);
        if (folder != null) {
            ArrayList<String> fileNameList = new ArrayList<>();
            File[] files = folder.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    String name = file.getName();
                    if (name.endsWith(".pdb") || name.endsWith(".pdb.gz")) {
                        fileNameList.add(file.getName());
                    }
                }
            }
            // there is no .pdb or .pdb.gz file in the folder
            if (fileNameList.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("Unable to open this type of file!!");
                alert.setContentText("Please select a folder including *.pdb or *.pdb.gz file.");
                alert.show();
                return;
            }

            controller.getFilesListView().getItems().clear();
            controller.getFilesListView().getItems().addAll(fileNameList);
            controller.getFilesListView().getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

            controller.getTextField().setOnAction(e -> {
                String target = controller.getTextField().getText();
                ArrayList<String> resultupdated = new ArrayList<>();
                for (String name : fileNameList) {
                    if (name.toLowerCase().contains(target.toLowerCase())) resultupdated.add(name);
                }
                controller.getFilesListView().getItems().clear();
                controller.getFilesListView().getItems().addAll(resultupdated);
            });


            controller.getFilesListView().getSelectionModel().selectedItemProperty().addListener((ChangeListener<String>)
                    (observableValue, oldValue, newValue) -> {
                if (newValue == null) {
                    controller.getCenterPane().getChildren().clear();
                    controller.getFilesListView().getItems().clear();
                    controller.getTextViewer().getChildren().clear();
                    controller.getChartsVBox().getChildren().clear();
                    controller.getSeqsVBox().getChildren().clear();
                } else {
                    String textInfo = "";
                    String path = folder.getAbsolutePath() + "/" + newValue;
                    if (newValue.endsWith(".pdb")) {
                        try {
                            stage.setTitle(newValue.substring(0, newValue.length() - 4));
                            textInfo = ParserProtein.readPdbFile(path);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    } else if (newValue.endsWith(".pdb.gz")) {
                        try {
                            stage.setTitle(newValue.substring(0, newValue.length() - 7));
                            textInfo = ParserProtein.readGzFile(path);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    controller.getTextViewer().getChildren().clear();
                    Text text = new Text(textInfo);

                    text.setFont(Font.font("Arial"));
                    controller.getTextViewer().getChildren().add(text);
                    controller.getSelectedPieChart().getData().clear();

                    //set model
                    PdbMolecule model = new PdbMolecule(ParserProtein.readAtoms(textInfo));
                    MoleculeShow current = new MoleculeShow();
                    current.setFigure(stage, controller, model);
                }
            });
        }

        //Warning "No files selected!!";
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("No files selected!!");
            alert.setContentText("Please select a folder including *.pdb or *.pdb.gz file.");
            alert.show();
        }

    }


}
