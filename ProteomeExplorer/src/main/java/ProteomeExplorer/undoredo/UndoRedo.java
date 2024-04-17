package ProteomeExplorer.undoredo;

import ProteomeExplorer.WindowController;
import javafx.beans.property.*;


public class UndoRedo {
    public void setup(WindowController controller) {
        var undoManager = new UndoRedoManager();
        controller.getUndoMenuItem().setOnAction(e -> undoManager.undo());
        controller.getUndoMenuItem().textProperty().bind(undoManager.undoLabelProperty());
        controller.getUndoMenuItem().disableProperty().bind(undoManager.canUndoProperty().not());
        controller.getRedoMenuItem().setOnAction(e -> undoManager.redo());
        controller.getRedoMenuItem().textProperty().bind(undoManager.redoLabelProperty());
        controller.getRedoMenuItem().disableProperty().bind(undoManager.canRedoProperty().not());
        controller.getBallsSlider().valueProperty().addListener((v, o, n) ->
                undoManager.add(new PropertyCommand<>("radius", (DoubleProperty) v, o, n)));
        controller.getSticksSlider().valueProperty().addListener((v, o, n) ->
                undoManager.add(new PropertyCommand<>("width", (DoubleProperty) v, o, n)));
        controller.getRibbonsCheckButton().selectedProperty().addListener((v, o, n) ->
                undoManager.add(new PropertyCommand<>("ribbons", (BooleanProperty) v, o, n)));
        controller.getBallsCheckButton().selectedProperty().addListener((v, o, n) ->
                undoManager.add(new PropertyCommand<>("balls", (BooleanProperty) v, o, n)));
        controller.getSticksCheckButton().selectedProperty().addListener((v, o, n) ->
                undoManager.add(new PropertyCommand<>("sticks", (BooleanProperty) v, o, n)));
        controller.getColorComboBox().valueProperty().addListener((v, o, n)
                -> undoManager.add(new PropertyCommand<>("color", (SimpleObjectProperty) v, o, n)));

    }
}
