package ProteomeExplorer.PdbDraw.model;
import java.util.ArrayList;


/**
 * @author Huiting Xu, Yuxin Ning
 */
public class PdbMonomer {
    private ArrayList<PdbAtom> atomList;
    private String monomerLabel;
    private String secondaryStructure;

    private  int residueNumber;


    public int getResidueNumber() {
        return residueNumber;
    }

    public void setResidueNumber(int residueNumber) {
        this.residueNumber = residueNumber;
    }

    public ArrayList<PdbAtom> getAtomList() {
        return atomList;
    }

    public void setAtomList(ArrayList<PdbAtom> atomList) {
        this.atomList = atomList;
    }

    public String getMonomerLabel() {
        return monomerLabel;
    }

    public void setMonomerLabel(String monomerLabel) {
        this.monomerLabel = monomerLabel;
    }

    public String getSecondaryStructure() {
        return secondaryStructure;
    }

    public void setSecondaryStructure(String secondaryStructure) {
        this.secondaryStructure = secondaryStructure;
    }


}
