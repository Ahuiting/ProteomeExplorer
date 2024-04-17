package ProteomeExplorer.mySelectionModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import java.util.Collection;

public class MySelectionSphere<T> implements MySelectionModel<T> {
    private ObservableSet<T> selectedSet;

    public MySelectionSphere() {
        this.selectedSet = FXCollections.observableSet();
    }

    @Override
    public boolean selectAll(Collection list) {
        this.selectedSet.addAll(list);
        return true;
    }

    @Override
    public boolean select(T t) {
        this.selectedSet.add(t);
        return true;
    }

    @Override
    public boolean setSelected(T t, boolean select) {
        if (select) {
            this.select(t);
        } else {
            clearSelection(t);
        }
        return true;
    }

    @Override
    public void clearSelection() {
        this.selectedSet.clear();

    }

    @Override
    public boolean clearSelection(T t) {
        this.selectedSet.remove(t);
        return true;
    }

    @Override
    public boolean clearSelection(Collection list) {
        this.selectedSet.removeAll(list);
        return true;
    }

    @Override
    public ObservableSet getSelectedItems() {
        return this.selectedSet;
    }
}



