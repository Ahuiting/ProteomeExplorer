package ProteomeExplorer.PdbDraw.model;

import javafx.geometry.Point3D;
import javafx.util.Pair;

import java.util.List;

public interface Molecule {
    String getName();

    String getFormula();
    String getSeqsInfo();

    int getNumberOfAtoms();

    PdbAtom getAtom(int pos);

    Point3D getLocation(int pos);

}
