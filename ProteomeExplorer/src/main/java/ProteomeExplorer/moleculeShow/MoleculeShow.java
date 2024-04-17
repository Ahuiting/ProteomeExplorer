package ProteomeExplorer.moleculeShow;

import ProteomeExplorer.statisticalDataShow.StatisticalDataShow;
import ProteomeExplorer.WindowController;
import ProteomeExplorer.mySelectionModel.*;
import ProteomeExplorer.PdbDraw.model.*;
import ProteomeExplorer.undoredo.UndoRedo;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.*;
import javafx.geometry.Orientation;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.scene.transform.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class MoleculeShow {
    private final HashMap<Sphere, PdbAtom> sphereToPdbAtomMap = new HashMap<>();
    private final ArrayList<Cylinder> cylinderList = new ArrayList<>();
    private final ArrayList<Pair<MeshView, Color>> meshViewPairs = new ArrayList<>();

    // setup the program
    public void setFigure(Stage stage, WindowController controller, PdbMolecule model) {
        //set secondary
        SecondaryStructure.getSecodaryFromFile(controller, model);

        showMolecule(stage, controller, model);

        //load secondary structure from PDB
        controller.getStructureButton().setOnAction(e -> {
            String entry = controller.getPdbTextField().getText();
            //warning
            if (entry.toCharArray().length == 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("Not Right Entry!!");
                alert.setContentText("Please input a right entry.");
                alert.show();
                return;
            }
            HashMap<String, ArrayList<Pair<String, int[]>>> map;
            try {
                map = SecondaryStructure.loadSecondary(entry);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            SecondaryStructure.setSecondaryStructure(map, model);
            controller.getCenterPane().getChildren().clear();
            showMolecule(stage, controller, model);
        });

        controller.getInfoLabel().setText(model.getName());

        // balls
        controller.getBallsCheckButton().selectedProperty().addListener(e -> {
            double scale = controller.getBallsSlider().getValue();
            if (controller.getBallsCheckButton().isSelected()) {
                for (Sphere sphere : sphereToPdbAtomMap.keySet()) {
                    sphere.setRadius(sphereToPdbAtomMap.get(sphere).getRadius() * scale);
                }
            } else {
                for (Sphere sphere : sphereToPdbAtomMap.keySet()) {
                    sphere.setRadius(0);
                }
            }
        });
        //ball slider
        controller.getBallsSlider().valueProperty().addListener(e -> {
            double scale = controller.getBallsSlider().getValue();
            for (Sphere sphere : sphereToPdbAtomMap.keySet()) {
                if (!controller.getBallsCheckButton().isSelected()) {
                    sphere.setRadius(0);
                } else {
                    sphere.setRadius(sphereToPdbAtomMap.get(sphere).getRadius() * scale);
                }
            }
        });

        //sticks
        controller.getSticksCheckButton().selectedProperty().addListener(e -> {
            double scale = controller.getSticksSlider().getValue();
            if (controller.getSticksCheckButton().isSelected()) {
                for (Cylinder cylinder : cylinderList) {
                    cylinder.setRadius(2 * scale);
                }
            } else {
                for (Cylinder cylinder : cylinderList) {
                    cylinder.setRadius(0);
                }
            }
        });

        //sticks slider
        controller.getSticksSlider().valueProperty().addListener(e -> {
            double scale = controller.getSticksSlider().getValue();
            for (Cylinder cylinder : cylinderList) {
                if (!controller.getSticksCheckButton().isSelected()) cylinder.setRadius(0);
                else cylinder.setRadius(2 * scale);
            }
        });


        //getColorComboBox to set color
        controller.getColorComboBox().setOnAction(e -> {
            if (controller.getColorComboBox().getValue().equals("by Atoms")) {
                for (Sphere sphere : this.sphereToPdbAtomMap.keySet()) {
                    sphere.setMaterial(new PhongMaterial(sphereToPdbAtomMap.get(sphere).getAtomColor()));
                }
            }
            if (controller.getColorComboBox().getValue().equals("by Amino Acids")) {
                for (Sphere sphere : this.sphereToPdbAtomMap.keySet()) {
                    sphere.setMaterial(new PhongMaterial(sphereToPdbAtomMap.get(sphere).getAminoAcidColor()));
                }
            }
            if (controller.getColorComboBox().getValue().equals("by Structure")) {
                for (Sphere sphere : this.sphereToPdbAtomMap.keySet()) {
                    sphere.setMaterial(new PhongMaterial(sphereToPdbAtomMap.get(sphere).getStructureColor()));
                }
            }
        });

        //set ribbon
        controller.getRibbonsCheckButton().selectedProperty().addListener(e -> {
            if (controller.getRibbonsCheckButton().isSelected()) {
                for (Pair<MeshView, Color> pair : this.meshViewPairs) {
                    pair.getKey().setMaterial(new PhongMaterial(pair.getValue()));
                }
            } else {
                for (Pair<MeshView, Color> pair : this.meshViewPairs) {
                    pair.getKey().setMaterial(new PhongMaterial(Color.TRANSPARENT));
                }
            }
        });
    }


    // build the 3d elements
    private void showMolecule(Stage stage, WindowController controller, PdbMolecule model) {

        Pane pane = controller.getCenterPane();
        pane.getChildren().clear();
        double width = pane.getWidth();
        double height = pane.getHeight();

        Group molecule = new Group();

        //(polymer label ---- (the name of amino acid ---- amount))
        HashMap<String, HashMap<String, Integer>> polymerToAminoAcidToAmountMapMap = new HashMap<>();
        //(sphere ---- sphere list that belong to a same monomer)
        HashMap<Sphere, List<Sphere>> sphereToMonomerSpheresMap = new HashMap<>();
        //(polymer label ---- (sphere ---- monomerIdx))
        HashMap<String, HashMap<Sphere, Integer>> polymerToSphereToMonomerIdxMapMap = new HashMap<>();
        //(polymer label ---- (monomerIdx ---- sphere list that belong to a same monomer))
        HashMap<String, HashMap<Integer, List>> polymerToMonomerIdxToSphereListMapMap = new HashMap<>();
        //(polymer label ---- (monomerIdx ---- monomer label))
        HashMap<String, HashMap<Integer, String>> monomerIdxToLabelMapMap = new HashMap<>();
        //(polymer label ---- listview for sequence)
        HashMap<String, ListView> polymerToListViewMap = new HashMap<>();


        for (PdbPolymer polymer : model.getPdbComplex().getPolymerList()) {
            //(the name of amino acid ---- amount)
            HashMap<String, Integer> AminoAcidToAmountMap = new HashMap<>();
            //(sphere ---- monomerIdx)
            HashMap<Sphere, Integer> sphereToMonomerIdxMap = new HashMap<>();
            //(monomerIdx ---- sphere list that belong to a same monomer)
            HashMap<Integer, List> monomerIdxToSphereListMap = new HashMap<>();
            //(monomerIdx ---- monomer label)
            HashMap<Integer, String> monomerIdxToLabelMap = new HashMap<>();

            int monomerIdx = 0;
            for (PdbMonomer monomer : polymer.getMonomerList()) {

                //count the number of amino acid,key is the type of amino acid,the value is the amount
                if (AminoAcidToAmountMap.containsKey(monomer.getMonomerLabel()))
                    AminoAcidToAmountMap.put(monomer.getMonomerLabel(), AminoAcidToAmountMap.get(monomer.getMonomerLabel()) + 1);
                else AminoAcidToAmountMap.put(monomer.getMonomerLabel(), 1);

                //sphere list that belong to a same monomer
                ArrayList<Sphere> aminoAcidSpheresList = new ArrayList<>();

                for (PdbAtom atom : monomer.getAtomList()) {

                    //set Radius
                    double radius;
                    double scale = controller.getBallsSlider().getValue();

                    // if the checkbox ball is selected, show all the balls
                    if (controller.getBallsCheckButton().isSelected()) {
                        radius = atom.getRadius() * scale;
                    }
                    // if not, set radius as 0
                    else radius = 0;


                    Sphere sphere = new Sphere(radius);

                    //set color
                    if (controller.getColorComboBox().getValue().equals("by Atoms")) {
                        sphere.setMaterial(new PhongMaterial(atom.getAtomColor()));
                    } else if (controller.getColorComboBox().getValue().equals("by Amino Acids")) {
                        sphere.setMaterial(new PhongMaterial(atom.getAminoAcidColor()));
                    } else if (controller.getColorComboBox().getValue().equals("by Structure")) {
                        sphere.setMaterial(new PhongMaterial(atom.getStructureColor()));
                    }

                    //set a label for sphere, when mouse clicked, it will show
                    sphere.setAccessibleText(polymer.getPolymerLabel() + ": " + monomer.getMonomerLabel());

                    // put the figure in the centre of the scene
                    sphere.setTranslateX(atom.getCurrentCoordinates().getX());
                    sphere.setTranslateY(atom.getCurrentCoordinates().getY());
                    sphere.setTranslateZ(atom.getCurrentCoordinates().getZ());

                    //set map
                    aminoAcidSpheresList.add(sphere);
                    sphereToMonomerSpheresMap.put(sphere, aminoAcidSpheresList);
                    sphereToMonomerIdxMap.put(sphere, monomerIdx);
                    monomerIdxToSphereListMap.put(monomerIdx, aminoAcidSpheresList);
                    monomerIdxToLabelMap.put(monomerIdx, monomer.getMonomerLabel());

                    sphereToPdbAtomMap.put(sphere, atom);
                    molecule.getChildren().add(sphere);
                }
                monomerIdx++;
            }

            //set listview
            ListView listView = new ListView();
            listView.setOrientation(Orientation.HORIZONTAL);
            listView.setPrefHeight(45);
            listView.getItems().clear();
            listView.scrollTo(0);
            listView.getItems().addAll(polymer.getSeqsInfo());
            listView.prefWidthProperty().bind(controller.getCenterPane().widthProperty().subtract(20));
            listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            //set map
            String polymerLabel = polymer.getPolymerLabel();
            polymerToListViewMap.put(polymerLabel, listView);
            polymerToSphereToMonomerIdxMapMap.put(polymerLabel, sphereToMonomerIdxMap);
            polymerToMonomerIdxToSphereListMapMap.put(polymerLabel, monomerIdxToSphereListMap);
            monomerIdxToLabelMapMap.put(polymerLabel, monomerIdxToLabelMap);
            polymerToAminoAcidToAmountMapMap.put(polymerLabel, AminoAcidToAmountMap);

        }

        //put list view in the pane
        controller.getSeqsVBox().getChildren().clear();
        for (String key : polymerToListViewMap.keySet()) {
            Label label = new Label(key);
            label.setPrefHeight(45);
            label.setPrefWidth(20);
            label.setAlignment(Pos.CENTER);
            HBox hBox = new HBox(label, polymerToListViewMap.get(key));
            controller.getSeqsVBox().getChildren().add(hBox);
        }

        //statistic data
        StatisticalDataShow statisticalDataShow = new StatisticalDataShow();
        statisticalDataShow.setup(controller, polymerToAminoAcidToAmountMapMap);

        //draw ribbon
        HashMap<String, ArrayList> cacbListMap = getCaCb(model);
        for (ArrayList cacbList : cacbListMap.values()) {
            setRibbon(cacbList, controller, molecule);
        }

        //draw sticks
        setCylinder(model, controller, molecule);

        //selection listview
        MySelectionModel aminoAcidSelectionModel = new MySelectionSphere<Sphere>();
        Text text = (Text) controller.getTextViewer().getChildren().get(0);
        for (String k : polymerToListViewMap.keySet()) {
            ListView lv = polymerToListViewMap.get(k);
            BooleanProperty clicked = new SimpleBooleanProperty(false);
            lv.setOnMouseClicked(e -> {
                clicked.set(true);
                for (ListView l : polymerToListViewMap.values()) {
                    if (!l.equals(lv)) l.getSelectionModel().clearSelection();
                }
            });
            ObservableList<Integer> indexes = lv.getSelectionModel().getSelectedIndices();
            indexes.addListener((ListChangeListener<Integer>) change -> {
                aminoAcidSelectionModel.getSelectedItems().clear();
                if (!clicked.get() && (!indexes.isEmpty())) {
                    lv.scrollTo((int) indexes.get(0));
                }
                clicked.set(false);
                setTextChange(text, controller, polymerToListViewMap);
                HashMap<String, Integer> selectedLabelCount = new HashMap();
                for (String key : polymerToListViewMap.keySet()) {
                    if (!polymerToListViewMap.get(key).getSelectionModel().getSelectedIndices().isEmpty()) {
                        for (int idx : (ObservableList<Integer>) polymerToListViewMap.get(key).getSelectionModel().getSelectedIndices()) {
                            aminoAcidSelectionModel.selectAll(polymerToMonomerIdxToSphereListMapMap.get(key).get(idx));
                            String label = monomerIdxToLabelMapMap.get(key).get(idx);
                            int count = selectedLabelCount.containsKey(label) ? selectedLabelCount.get(label) + 1 : 1;
                            selectedLabelCount.put(label, count);
                        }
                    }
                }

                //Pie Chart about selected amino acid
                PieChart selectedPieChart = controller.getSelectedPieChart();
                selectedPieChart.getData().clear();
                for (String k1 : selectedLabelCount.keySet()) {
                    selectedPieChart.getData().add(new PieChart.Data(k1, selectedLabelCount.get(k1)));
                }

            });
        }

        //select balls
        ArrayList<Sphere> sphereList = (ArrayList<Sphere>) sphereToPdbAtomMap.keySet().stream().collect(Collectors.toList());
        aminoAcidSelectionModel.getSelectedItems().addListener((SetChangeListener<? super Sphere>) c ->
                Platform.runLater(() -> {
                    if (aminoAcidSelectionModel.getSelectedItems().isEmpty()) {
                        updateOpacity(sphereList, 1);
                    } else {
                        ArrayList<Sphere> hideList = (ArrayList<Sphere>) sphereList.clone();
                        hideList.removeAll(aminoAcidSelectionModel.getSelectedItems());
                        updateOpacity(hideList, 0.2);
                        updateOpacity(aminoAcidSelectionModel.getSelectedItems(), 1);
                    }
                }));

        for (Sphere sphere : sphereList) {
            sphere.setOnMouseClicked(e -> {
                if (!e.isShiftDown()) {
                    aminoAcidSelectionModel.clearSelection();
                    for (ListView lv : polymerToListViewMap.values()) {
                        if (!lv.getSelectionModel().getSelectedIndices().isEmpty()) {
                            lv.getSelectionModel().clearSelection();
                        }
                    }
                }

                // when click again it will be removed
                if (aminoAcidSelectionModel.getSelectedItems().contains(sphere)) {
                    aminoAcidSelectionModel.clearSelection(sphereToMonomerSpheresMap.get(sphere));
                    for (String k : polymerToListViewMap.keySet()) {
                        if (polymerToSphereToMonomerIdxMapMap.get(k).containsKey(sphere))
                            polymerToListViewMap.get(k).getSelectionModel().clearSelection(polymerToSphereToMonomerIdxMapMap.get(k).get(sphere));
                    }
                } else {
                    aminoAcidSelectionModel.selectAll(sphereToMonomerSpheresMap.get(sphere));
                    for (String k : polymerToListViewMap.keySet()) {
                        if (polymerToSphereToMonomerIdxMapMap.get(k).containsKey(sphere))
                            polymerToListViewMap.get(k).getSelectionModel().selectIndices(polymerToSphereToMonomerIdxMapMap.get(k).get(sphere));
                    }

                }

                // when click a sphere the name of residue will show up and then fade out
                setFadeLabel(sphere, controller, e);

            });
        }

        // set camera
        SubScene subScene = new SubScene(molecule, width, height, true, SceneAntialiasing.BALANCED);
        subScene.widthProperty().bind(pane.widthProperty());
        subScene.heightProperty().bind(pane.heightProperty());

        Camera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-1500);
        camera.setNearClip(0.1);
        camera.setFarClip(10000);
        subScene.setCamera(camera);

        // mouse scroll zoom
        stage.addEventHandler(ScrollEvent.SCROLL, e -> {
            double delta = e.getDeltaY();
            camera.setTranslateZ(camera.getTranslateZ() + delta);
        });

        pane.getChildren().add(subScene);

        Property<Transform> figureTransformProperty = new SimpleObjectProperty<>(new Rotate());
        figureTransformProperty.addListener((v, o, n) -> molecule.getTransforms().setAll(n));
        MouseInteraction.installRotate(stage, pane, figureTransformProperty, molecule);


        //redo and undo
        var undoredo = new UndoRedo();
        undoredo.setup(controller);
    }

    public static void updateOpacity(Collection<? extends Shape3D> list, double opacity) {
        for (var shape : list) {
            var color = ((PhongMaterial) shape.getMaterial()).getDiffuseColor();
            color = new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity);
            ((PhongMaterial) shape.getMaterial()).setDiffuseColor(color);
        }
    }

    //set cylinders
    private void setCylinder(PdbMolecule model, WindowController controller, Group molecule) {
        ArrayList<PdbPolymer> pdbPolymers = model.getPdbComplex().getPolymerList();
        for (int i = 0; i < pdbPolymers.size(); i++) {
            for (Pair<Integer, Integer> pair : pdbPolymers.get(i).getBonds()) {
                int k = pair.getKey();
                int v = pair.getValue();
                var k_loc = model.getLocation(k - i - 1);
                var v_loc = model.getLocation(v - i - 1);
                var YAXIS = new Point3D(0, 1, 0);

                var midpoint = k_loc.midpoint(v_loc);
                var direction = v_loc.subtract(k_loc);

                var perpendicularAxis = YAXIS.crossProduct(direction);
                var angle = YAXIS.angle(direction);
                var stickLength = k_loc.distance(v_loc);
                double stickRadius;
                double scale = controller.getSticksSlider().getValue();
                // if the checkbox sticker is selected, show all the sticks
                if (controller.getSticksCheckButton().isSelected()) {
                    stickRadius = 2 * scale;
                }
                // if not, set radius as 0
                else {
                    stickRadius = 0;
                }
                var cylinder = new Cylinder(stickRadius, stickLength);
                cylinder.setMaterial(new PhongMaterial(Color.GRAY));
                cylinder.setRotationAxis(perpendicularAxis);
                cylinder.setRotate(angle);
                cylinder.setTranslateX(midpoint.getX());
                cylinder.setTranslateY(midpoint.getY());
                cylinder.setTranslateZ(midpoint.getZ());
                cylinder.setScaleY(k_loc.distance(v_loc) / cylinder.getHeight());
                cylinderList.add(cylinder);
                molecule.getChildren().add(cylinder);
            }
        }
    }

    //get all the location of ca and cb atoms
    private HashMap<String, ArrayList> getCaCb(PdbMolecule molecule) {

        HashMap<String, ArrayList> cacbListMap = new HashMap<>();
        ArrayList<Pair<Point3D, String>> cacbList;
        PdbAtom[] atoms = molecule.getPdbComplex().getAllAtoms();

        for (int i = 0; i < atoms.length; i++) {
            PdbAtom atom = atoms[i];
            if (cacbListMap.containsKey(atom.getChain())) {
                cacbList = cacbListMap.get(atom.getChain());
            } else {
                cacbList = new ArrayList<>();
            }
            if ((atom.getRole().equals("CA")) || (atom.getRole().equals("CB"))) {
                cacbList.add(new Pair<Point3D, String>(atom.getCurrentCoordinates(), atom.getSecondaryStructure()));
            }
            cacbListMap.put(atom.getChain(), cacbList);
        }

        return cacbListMap;
    }

    // set ribbons
    private void setRibbon(ArrayList<Pair<Point3D, String>> cacbList, WindowController controller, Group molecule) {
        for (int i = 0; i < cacbList.size() - 3; i += 2) {
            Point3D ca_1 = cacbList.get(i).getKey();
            Point3D cb_1 = cacbList.get(i + 1).getKey();
            Point3D v_1 = ca_1.add(ca_1).subtract(cb_1);
            Point3D ca_2 = cacbList.get(i + 2).getKey();
            Point3D cb_2 = cacbList.get(i + 3).getKey();
            Point3D v_2 = ca_2.add(ca_2).subtract(cb_2);
            float[] points = {
                    (float) v_1.getX(), (float) v_1.getY(), (float) v_1.getZ(),
                    (float) ca_1.getX(), (float) ca_1.getY(), (float) ca_1.getZ(),
                    (float) cb_1.getX(), (float) cb_1.getY(), (float) cb_1.getZ(),
                    (float) v_2.getX(), (float) v_2.getY(), (float) v_2.getZ(),
                    (float) ca_2.getX(), (float) ca_2.getY(), (float) ca_2.getZ(),
                    (float) cb_2.getX(), (float) cb_2.getY(), (float) cb_2.getZ()};

            int[] faces = {
                    0, 0, 1, 1, 4, 4,
                    0, 0, 4, 4, 5, 5,
                    1, 1, 2, 2, 3, 3,
                    1, 1, 3, 3, 4, 4,
                    0, 0, 4, 4, 1, 1,
                    0, 0, 5, 5, 4, 4,
                    1, 1, 3, 3, 2, 2,
                    1, 1, 4, 4, 3, 3,};
            // float[] texCoords = {0, 0};
            float[] texCoords = {
                    0, 0,
                    0, 0.5f,
                    0, 1,
                    1, 1,
                    1, 0.5f,
                    1, 0};
            int[] smoothing = {1, 1, 1, 1, 2, 2, 2, 2};
            var mesh = new TriangleMesh();
            mesh.getPoints().addAll(points);
            mesh.getTexCoords().addAll(texCoords);
            mesh.getFaces().addAll(faces);
            mesh.getFaceSmoothingGroups().addAll(smoothing);
            var meshView = new MeshView(mesh);
            HashMap<String, Color> colorMap = new HashMap<>();
            colorMap.put("HELIX", Color.MAGENTA);
            colorMap.put("SHEET", Color.YELLOW);
            colorMap.put("UNKNOWN", Color.GRAY);
            Color color = colorMap.get(cacbList.get(i + 2).getValue());
            if (controller.getRibbonsCheckButton().isSelected()) {
                meshView.setMaterial(new PhongMaterial(color));
            } else {
                meshView.setMaterial(new PhongMaterial(Color.TRANSPARENT));
            }
            molecule.getChildren().add(meshView);
            meshViewPairs.add(new Pair<>(meshView, color));
        }

    }

    //when sphere is clicked, the label will show and then fade out
    private void setFadeLabel(Sphere sphere, WindowController windowController, MouseEvent e) {
        // when click a sphere the name of residue will show up and then fade out
        Label label = new Label(sphere.getAccessibleText());
        label.setFont(new Font("Arial", 15));
        label.setTranslateX(e.getSceneX() - 250);
        label.setTranslateY(e.getSceneY() - 80);
        label.setTextFill(Color.WHITE);
        label.backgroundProperty().setValue(Background.fill(Color.BLACK));
        windowController.getCenterPane().getChildren().add(label);
        FadeTransition ft = new FadeTransition();
        ft.setNode(label);
        ft.setDuration(Duration.seconds(2.5));
        ft.setFromValue(10);
        ft.setToValue(0);
        ft.play();

    }

    //after selection, get the index of all the related atoms in the text
    private ArrayList<Integer> getSelectedLinesIdx(Text text, HashMap<String, ListView> map) {
        ArrayList<Integer> idxList = new ArrayList<>();
        String[] linesArray = text.getText().split("\n");
        for (String k : map.keySet()) {
            HashSet<Integer> set = new HashSet();
            for (int i = 0; i < linesArray.length; i++) {
                if (linesArray[i].startsWith("ATOM") && linesArray[i].substring(21, 22).strip().equals(k)) {
                    set.add(Integer.valueOf(linesArray[i].substring(23, 26).strip()));
                    ObservableList indexes = map.get(k).getSelectionModel().getSelectedIndices();
                    if (indexes.contains(set.size() - 1)) {
                        idxList.add(i);
                    }
                }
            }
        }
        return idxList;
    }

    //after selection, change the color of all the related atoms in the text
    private void setTextChange(Text text, WindowController controller, HashMap<String, ListView> map) {
        String[] infoArray = text.getText().split("\n");
        controller.getTextViewer().getChildren().clear();
        ArrayList selected = getSelectedLinesIdx(text, map);
        if (!selected.isEmpty()) {
            for (int i = 0; i < infoArray.length; i++) {
                Text atomsLine = new Text(infoArray[i] + "\n");
                if (selected.contains(i)) {
                    atomsLine.setFont(Font.font("Arial", FontWeight.BOLD, atomsLine.getFont().getSize()));
                    atomsLine.setFill(Color.BLUE);
                } else {
                    atomsLine.setFont(Font.font("Arial"));
                }

                controller.getTextViewer().getChildren().add(atomsLine);
            }
            float scroll = (float) ((int) selected.get(0)) / (float) (infoArray.length - 20);
            controller.getScrollPane().setVvalue(scroll);
        }
    }

}
