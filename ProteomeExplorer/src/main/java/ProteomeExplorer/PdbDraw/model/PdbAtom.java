package ProteomeExplorer.PdbDraw.model;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

import java.util.HashMap;

/**
 * @author Huiting Xu, Yuxin Ning
 */
public class PdbAtom {
    private String letter;
    private double radius;
    private Color color1;
    private Color color2;
    private Color color3;
    private String role;
    private String residue;
    private String chain;
    private String secondaryStructure;
    private int ID;
    private Point3D coordinates;
    private Point3D currentCoordinates;

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius() {
        String letter = this.getLetter();
        HashMap<String, Integer> radiusMap = new HashMap();
        radiusMap.put("O", 73);
        radiusMap.put("H", 37);
        radiusMap.put("C", 77);
        radiusMap.put("N", 70);
        radiusMap.put("S", 103);
        this.radius = radiusMap.get(letter);
    }

    public Color getAtomColor() {
        return color1;
    }

    public void setAtomColor() {
        String letter = this.getLetter();
        HashMap<String, Color> colorMap = new HashMap();
        colorMap.put("O", Color.RED);
        colorMap.put("H", Color.WHITE);
        colorMap.put("C", Color.GRAY);
        colorMap.put("N", Color.LIGHTBLUE);
        colorMap.put("S", Color.YELLOW);
        this.color1 = colorMap.get(letter);
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Point3D getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Point3D coordinates) {
        this.coordinates = coordinates;
    }

    public Point3D getCurrentCoordinates() {
        return currentCoordinates;
    }

    public void setCurrentCoordinates(Point3D currentCoordinates) {
        this.currentCoordinates = currentCoordinates;
    }

    public String getResidue() {
        return residue;
    }

    public void setResidue(String residue) {
        this.residue = residue;
    }

    public Color getAminoAcidColor() {
        return color2;
    }

    public void setAminoAcidColor() {
        String residue = this.getResidue();
        HashMap<String, Color> colorMap = new HashMap();
        colorMap.put("ARG", Color.PURPLE);
        colorMap.put("HIS", Color.PLUM);
        colorMap.put("LYS", Color.BLUE);
        colorMap.put("ASP", Color.VIOLET);
        colorMap.put("GLU", Color.TEAL);
        colorMap.put("SER", Color.PINK);
        colorMap.put("THR", Color.ORANGE);
        colorMap.put("ASN", Color.RED);
        colorMap.put("GIN", Color.PEACHPUFF);
        colorMap.put("CYS", Color.YELLOW);
        colorMap.put("SEC", Color.BROWN);
        colorMap.put("GLY", Color.GAINSBORO);
        colorMap.put("PRO", Color.GREENYELLOW);
        colorMap.put("ALA", Color.GREEN);
        colorMap.put("VAL", Color.DARKGREEN);
        colorMap.put("ILE", Color.LIGHTBLUE);
        colorMap.put("LEU", Color.LIGHTSKYBLUE);
        colorMap.put("MET", Color.LIGHTGREEN);
        colorMap.put("PHE", Color.GOLD);
        colorMap.put("TYR", Color.ORCHID);
        colorMap.put("TRP", Color.CHOCOLATE);
        colorMap.put("GLN", Color.HOTPINK);
        this.color2 = colorMap.get(residue);
    }

    public String getSecondaryStructure() {
        return secondaryStructure;
    }

    public void setSecondaryStructure(String secondaryStructure) {
        this.secondaryStructure = secondaryStructure;
    }

    public Color getStructureColor() {
        return color3;
    }

    public void setStructureColor() {
        String secondaryStructure = this.getSecondaryStructure();
        HashMap<String, Color> colorMap = new HashMap();
        colorMap.put("HELIX", Color.MAGENTA);
        colorMap.put("SHEET", Color.YELLOW);
        colorMap.put("UNKNOWN", Color.GRAY);

        if (colorMap.containsKey(secondaryStructure)) {
            this.color3 = colorMap.get(secondaryStructure);
        } else {
            System.out.println(secondaryStructure);
        }
    }

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }
}
