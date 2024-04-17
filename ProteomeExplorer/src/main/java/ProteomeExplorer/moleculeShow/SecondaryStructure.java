package ProteomeExplorer.moleculeShow;

import ProteomeExplorer.ParserProtein;
import ProteomeExplorer.PdbDraw.model.PdbAtom;
import ProteomeExplorer.PdbDraw.model.PdbMolecule;
import ProteomeExplorer.PdbDraw.model.PdbMonomer;
import ProteomeExplorer.PdbDraw.model.PdbPolymer;
import ProteomeExplorer.WindowController;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SecondaryStructure {

    static void getSecodaryFromFile(WindowController controller, PdbMolecule molecule) {
        String text = controller.getTextViewer().getChildren().get(0).toString();
        ArrayList<String> arrayList = ParserProtein.readSecondary(text);
        if (!arrayList.isEmpty()) {
            HashMap<String, ArrayList<Pair<String, int[]>>> helixSheetMap = ParserProtein.getSecondaryMap(arrayList);
            setSecondaryStructure(helixSheetMap, molecule);
        } else {
            setSecondaryStructure(null, molecule);
        }
    }

    static void setSecondaryStructure(HashMap<String, ArrayList<Pair<String, int[]>>> map, PdbMolecule model) {
        if (map==null) {
            for (PdbPolymer polymer : model.getPdbComplex().getPolymerList()) {
                for (PdbMonomer monomer : polymer.getMonomerList()) {
                    monomer.setSecondaryStructure("UNKNOWN");
                    for (PdbAtom atom : monomer.getAtomList()) {
                        atom.setSecondaryStructure(monomer.getSecondaryStructure());
                        atom.setStructureColor();
                    }
                }
            }
        } else {
            for (PdbPolymer polymer : model.getPdbComplex().getPolymerList()) {
                ArrayList<Pair<String, int[]>> pairList = map.get(polymer.getPolymerLabel());
                for (PdbMonomer monomer : polymer.getMonomerList()) {
                    for (Pair<String, int[]> arraypair : pairList) {
                        int residueNumber = monomer.getResidueNumber();
                        int[] range = arraypair.getValue();
                        if (residueNumber >= range[0] && residueNumber <= range[1]) {
                            monomer.setSecondaryStructure(arraypair.getKey());
                            break;
                        } else {
                            monomer.setSecondaryStructure("UNKNOWN");
                        }
                    }
                    for (PdbAtom atom:monomer.getAtomList()) {
                        atom.setSecondaryStructure(monomer.getSecondaryStructure());
                        atom.setStructureColor();

                    }
                }
            }
        }
    }

    static HashMap loadSecondary(String entry) throws IOException {
        String helixSheetText = PdbAPI.getSecondaryInfo(entry);
        return ParserProtein.getSecondaryMap(ParserProtein.readSecondary(helixSheetText));
    }

}
