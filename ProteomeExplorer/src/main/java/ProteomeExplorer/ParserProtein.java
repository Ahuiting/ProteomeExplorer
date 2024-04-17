package ProteomeExplorer;

import ProteomeExplorer.PdbDraw.model.*;
import javafx.geometry.Point3D;
import javafx.util.Pair;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * @author Huiting Xu, Yuxin Ning
 */
public class ParserProtein {

    // read protein data from the file.
    public static ArrayList<String> readAtoms(String text) {
        ArrayList<String> atomsArray = new ArrayList<>();
        String[] lines = text.split("\n");
        for (String line : lines) {
            // The information is the lines starts with "ATOM"
            if (line.startsWith("ATOM")) {
                atomsArray.add(line);
            }
        }
        return atomsArray;
    }

    public static ArrayList<String> readSecondary(String text) {
        ArrayList<String> arrayList = new ArrayList<>();
        String[] lines = text.split("\n");
        for (String line : lines) {
            // The information is the lines starts with "ATOM"
            if (line.startsWith("HELIX") || line.startsWith("SHEET")) {
                arrayList.add(line);
            }
        }
        return arrayList;
    }

    public static HashMap<String, ArrayList<Pair<String, int[]>>> getSecondaryMap(ArrayList<String> list) {
        HashMap<String, ArrayList<Pair<String, int[]>>> helixSheet = new HashMap<>();
        for (String line : list) {
            // The information is the lines starts with "HELIX" or "SHEET"
            if (line.startsWith("HELIX")) {
                //begin id and end id
                String chainId = line.substring(19, 20).strip();
                ArrayList<Pair<String, int[]>> pairs;
                if (helixSheet.containsKey(chainId)) {
                    pairs = helixSheet.get(chainId);

                } else {
                    pairs = new ArrayList<>();
                    helixSheet.put(chainId, pairs);
                }
                int[] beginEndIdx = {Integer.parseInt(line.substring(21, 25).strip()),
                        Integer.parseInt(line.substring(33, 37).strip())};
                pairs.add(new Pair<>("HELIX", beginEndIdx));
                helixSheet.put(chainId, pairs);
            }

            if (line.startsWith("SHEET")) {
                //begin id and end id
                String chainId = line.substring(21, 22).strip();
                ArrayList<Pair<String, int[]>> pairs;
                if (helixSheet.containsKey(chainId)) {
                    pairs = helixSheet.get(chainId);

                } else {
                    pairs = new ArrayList<>();
                    helixSheet.put(chainId, pairs);
                }
                int[] beginEndIdx = {Integer.parseInt(line.substring(22, 26).strip()),
                        Integer.parseInt(line.substring(33, 37).strip())};
                pairs.add(new Pair<>("SHEET", beginEndIdx));
                helixSheet.put(chainId, pairs);
            }
        }
        return helixSheet;
    }


    public static String readPdbFile(String pathway) throws Exception {
        StringBuilder proteinInfo = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(pathway))) {
            String line;
            while ((line = br.readLine()) != null) {
                proteinInfo.append(line + "\n");
            }
        }
        return proteinInfo.toString();
    }

    public static String readGzFile(String pathway) throws Exception {
        StringBuilder proteinInfo = new StringBuilder();
        //from: https://stackoverflow.com/questions/1080381/gzipinputstream-reading-line-by-line
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new GZIPInputStream(
                                new FileInputStream(pathway))))) {
            String line;
            while ((line = br.readLine()) != null) {
                proteinInfo.append(line + "\n");
            }
        }
        return proteinInfo.toString();
    }

    public static PdbComplex infoToPdbComplex(List<String> atomsInfo) {
        PdbComplex pdbComplex = new PdbComplex();
        // chain map: key is the Chain identifier, value is a hashmap(residueMap)
        // residueMap: key is a Residue sequence number, value is a PdbMonomer
        HashMap<String, HashMap<Integer, PdbMonomer>> chainMap = new HashMap<>();
        PdbAtom[] pdbAtoms = new PdbAtom[atomsInfo.size()];

        for (int i = 0; i < atomsInfo.size(); i++) {
            String info = atomsInfo.get(i);
            //set atoms
            PdbAtom currentAtom = new PdbAtom();
            currentAtom.setID(Integer.parseInt(info.substring(7, 11).strip()));
            currentAtom.setCoordinates(new Point3D(
                    Double.parseDouble(info.substring(31, 38).strip()),
                    Double.parseDouble(info.substring(39, 46).strip()),
                    Double.parseDouble(info.substring(47, 54).strip())));
            currentAtom.setLetter(info.substring(77, 78).strip());
            currentAtom.setRole(info.substring(13, 16).strip());
            currentAtom.setRadius();
            currentAtom.setAtomColor();
            currentAtom.setResidue(info.substring(17, 20).strip());
            currentAtom.setAminoAcidColor();
            currentAtom.setChain(info.substring(21, 22).strip());
            pdbAtoms[i] = currentAtom;

            // get chain identity
            String chain = info.substring(21, 22).strip();
            // get Residue sequence number
            int residueNumber = Integer.parseInt(info.substring(23, 26).strip());


            if (!chainMap.containsKey(chain)) {
                chainMap.put(chain, new HashMap<>());
            }

            // creat a PdbMonomer, if this residueNumber not in keys, creat a new PdbMonomer
            if (!chainMap.get(chain).containsKey(residueNumber)) {
                PdbMonomer currentMonomer = new PdbMonomer();
                currentMonomer.setAtomList(new ArrayList<>());
                currentMonomer.setResidueNumber(residueNumber);
                currentMonomer.setMonomerLabel(info.substring(17, 20).strip());
                chainMap.get(chain).put(residueNumber, currentMonomer);
            }
            // set PdbMonomer, set AtomList
            HashMap<Integer, PdbMonomer> residueMap = chainMap.get(chain);
            ArrayList<PdbAtom> list = residueMap.get(residueNumber).getAtomList();
            list.add(currentAtom);
            residueMap.get(residueNumber).setAtomList(list);
        }

        ArrayList<PdbPolymer> listOfPolymer = new ArrayList<>();
        for (String key : chainMap.keySet()) {
            //get the list of PdbMonomer and set PdbPolymer
            PdbPolymer currentPolymer = new PdbPolymer();
            currentPolymer.setPolymerLabel(key);
            Collection<PdbMonomer> values = chainMap.get(key).values();
            ArrayList<PdbMonomer> listOfValues = new ArrayList<>(values);
            currentPolymer.setMonomerList(listOfValues);

            //get the list of PdbPolymer and set PdbComplex
            listOfPolymer.add(currentPolymer);

        }
        pdbComplex.setAllAtoms(pdbAtoms);
        pdbComplex.setPolymerList(listOfPolymer);
        return pdbComplex;
    }


}
