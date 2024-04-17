package ProteomeExplorer.PdbDraw.model;
import javafx.geometry.Point3D;
import javafx.util.Pair;

import java.util.*;

/**
 * @author Huiting Xu, Yuxin Ning
 */
public class PdbComplex {
    private ArrayList<PdbPolymer> polymerList;

    private PdbAtom[] pdbAtoms;
    private Point3D[] locations;
    public ArrayList<PdbPolymer> getPolymerList() {
        return polymerList;
    }
    public void setPolymerList(ArrayList<PdbPolymer> polymerList) {
        this.polymerList = polymerList;
    }

    public PdbAtom[] getAllAtoms() {
        return pdbAtoms;
    }

    public void setAllAtoms(PdbAtom[] pdbAtoms) {
        this.pdbAtoms = pdbAtoms;
    }

    // store all locations as an array
    public  Point3D[] setLocations() {
        PdbAtom[] atomArray = this.getAllAtoms();
        Point3D[] locationArray = new Point3D[atomArray.length];
        for (int i = 0; i < atomArray.length; i++) {
            locationArray[i] = atomArray[i].getCoordinates();
        }
        double[] xArray = new double[locationArray.length];
        double[] yArray = new double[locationArray.length];
        double[] zArray = new double[locationArray.length];
        for (int m = 0; m < locationArray.length; m++) {
            xArray[m] = locationArray[m].getX();
            yArray[m] = locationArray[m].getY();
            zArray[m] = locationArray[m].getZ();
        }
        double midX = Arrays.stream(xArray).average().getAsDouble();
        double midY = Arrays.stream(yArray).average().getAsDouble();
        double midZ = Arrays.stream(zArray).average().getAsDouble();
        for (int j = 0; j < locationArray.length; j++) {
            locationArray[j] = new Point3D(
                    (locationArray[j].getX() - midX)*10,
                    (locationArray[j].getY() - midY)*10,
                    (locationArray[j].getZ() - midZ)*10);
            atomArray[j].setCurrentCoordinates( locationArray[j]);
        }
        return locationArray;
    }

    //set bonds if the distance of two atoms is smaller than 2, return a pair-list
    public List<Pair<Integer, Integer>> getBonds() {
        List<Pair<Integer, Integer>> pairList = new ArrayList<>();
        for (PdbPolymer polymer : this.getPolymerList()) {
            pairList.addAll(polymer.getBonds());
        }
        return pairList;
    }
    public String getName() {
        int polymerNumber = this.getPolymerList().size();
        int monomerNumber = 0;
        for (PdbPolymer polymer : this.getPolymerList()) {
            monomerNumber += polymer.getMonomerList().size();
        }
        int atomNumber = this.getAllAtoms().length;
        return polymerNumber + "\spolymers, " + monomerNumber + "\smonomers, " + atomNumber + "\satoms";
    }

}
