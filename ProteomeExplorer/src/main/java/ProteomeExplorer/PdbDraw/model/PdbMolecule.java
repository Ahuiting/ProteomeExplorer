package ProteomeExplorer.PdbDraw.model;

import ProteomeExplorer.ParserProtein;
import javafx.geometry.Point3D;
import javafx.util.Pair;

import java.util.*;

/**
 * @author Huiting Xu, Yuxin Ning
 */
public class PdbMolecule {
    private PdbAtom[] atoms;
    private Point3D[] locations;
    private List<Pair<Integer, Integer>> bonds;
    private PdbComplex pdbComplex;


    public PdbMolecule(List<String> atomsInfo) {
        this.pdbComplex = ParserProtein.infoToPdbComplex(atomsInfo);
        this.atoms = pdbComplex.getAllAtoms();
        this.locations = pdbComplex.setLocations();
        this.bonds = pdbComplex.getBonds();
    }
    public PdbComplex getPdbComplex() {
        return pdbComplex;
    }

    public String getName() {
      return this.pdbComplex.getName();
    }

    public String getFormula() {
        return "";
    }

    public int getNumberOfAtoms() {
        return atoms.length;
    }

    public PdbAtom getAtom(int pos) {
        return atoms[pos];
    }

    public PdbAtom[] getAllAtoms() {
        return this.atoms;
    }

    public Point3D getLocation(int pos) {
        return locations[pos];
    }

    public List<Pair<Integer, Integer>> getBonds() {
        return bonds;
    }



}