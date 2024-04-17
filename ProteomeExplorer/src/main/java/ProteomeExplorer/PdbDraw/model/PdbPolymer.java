package ProteomeExplorer.PdbDraw.model;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Huiting Xu, Yuxin Ning
 */
public class PdbPolymer {

    private ArrayList<PdbMonomer> monomerList;
    private int polymerNumber;
    private String polymerLabel;
    private PdbAtom[] pdbAtoms;

    public ArrayList<PdbMonomer> getMonomerList() {
        return monomerList;
    }

    public void setMonomerList(ArrayList<PdbMonomer> monomerList) {
        this.monomerList = monomerList;
    }

    public int getPolymerNumber() {
        return polymerNumber;
    }

    public void setPolymerNumber(int polymerNumber) {
        this.polymerNumber = polymerNumber;
    }

    public String getPolymerLabel() {
        return polymerLabel;
    }

    public void setPolymerLabel(String polymerLabel) {
        this.polymerLabel = polymerLabel;
    }


    // return atoms array which including all atoms
    public PdbAtom[] getAllAtoms() {
        setPdbAtoms();
        return this.pdbAtoms;
    }

    public void setPdbAtoms() {
        int arraySize = 0;
        // get size of array
        for (PdbMonomer monomer : this.getMonomerList()) {
            arraySize += monomer.getAtomList().size();

        }
        PdbAtom[] atomArray = new PdbAtom[arraySize];
        int i = 0;
        // put all atoms in the array
        for (PdbMonomer monomer : this.getMonomerList()) {
            for (PdbAtom atom : monomer.getAtomList()) {
                atomArray[i] = atom;
                i++;
            }
        }
        this.pdbAtoms = atomArray;
    }

    //set bonds if the distance of two atoms is smaller than 2, return a pair-list
    public List<Pair<Integer, Integer>> getBonds() {
        List<Pair<Integer, Integer>> pairList = new ArrayList<>();
        PdbAtom[] atomArray = this.getAllAtoms();
        for (int i = 0; i < atomArray.length - 1; i++) {
            for (int j = i + 1; j < atomArray.length; j++) {
                if (!atomArray[i].equals(atomArray[j]) &&
                        (atomArray[i].getCoordinates().distance((atomArray[j].getCoordinates())) <= 2)) {
                    pairList.add(new Pair<>(atomArray[i].getID(), atomArray[j].getID()));
                }
            }
        }
        return pairList;
    }

    public ArrayList<String> getSeqsInfo() {
        ArrayList<String> seqs = new ArrayList<>();
        for (PdbMonomer monomer : this.getMonomerList()) {
            seqs.add(monomer.getMonomerLabel());
        }
        return seqs;
    }

}
