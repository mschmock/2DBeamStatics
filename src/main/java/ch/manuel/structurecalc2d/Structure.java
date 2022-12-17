//Autor: Manuel Schmocker
//Datum: 01.03.2014

package ch.manuel.structurecalc2d;

import java.util.ArrayList;
import java.util.List;

//Klasse zum Verwalten der Struktur (Knoten, Balken)
public class Structure {
    private static int nbNodes;                 //Anzahl Knoten (x, y)
    private static int nbBeams;                 //Anzahl Stäbe
    private static boolean isCalculated;        //Wurde die Deformation berechnet?
    public static double scaleFactor;           //Faktor für Skalierung: Standard = 1.0
    public static final String FILE_EXT = ".str";       //Endung für Dateiname
    
    private static List<Points> listPts;                //Liste mit Punkten: Klasse Points.java
    private static List<Beams> listBeams;               //Liste mit Stäben: Klasse Beam.java
    private static List<Double[]> listSections;         //Liste mit Definition Querschnitt (Fläche A und E-Modul E)

    
    //Konstruktor
    public Structure() {
        listPts = new ArrayList<>();
        listBeams = new ArrayList<>();
        listSections = new ArrayList<>();       //Liste mit A und E initialisieren
        
        isCalculated = false;                   //Struktur noch nicht berechnet
        scaleFactor = 1.0;
    }
    
    //Punkt hinzufügen
    public static void addPoint(double x, double y) {
        listPts.add(new Points(x,y));
        nbNodes = listPts.size();
        resetStructure();               //Resultate zurücksetzen
    }
    
    //Stab hinzufügen
    public static void addBeam(Points p1, Points p2, double eMod, double surf) {
        listBeams.add(new Beams(p1, p2, eMod, surf));
        nbBeams = listBeams.size();
        resetStructure();               //Resultate zurücksetzen
    }
    
    //Struktur löschen
    public static void clearStruct() {
        listBeams.clear();
        listPts.clear();
        listSections.clear();
        nbNodes = listPts.size();
        nbBeams = listBeams.size();
        resetStructure();               //Resultate zurücksetzen
    }
    
    //Objekt Punkt aus der Liste zurückgeben
    //Achtung: direkter Zugriff auf Liste vermeiden, z.B. mit: return listPts
    public static Points getPt(int pos) {
        return listPts.get(pos);
    }
    
    //Objekt Stab aus der Liste zurückgeben
    public static Beams getBeam(int pos) {
        return listBeams.get(pos);
    }
    
    //Punkt aus der Liste löschen (als Liste)
    public static void delPt(int pos) {
        //Stäbe, die mit diesem Punkt verbunden sind, müssen auch gelöscht werden
        //Diese Stäbe dürfen ohne Punkt nicht existieren
        for ( int i = (nbBeams-1); i >= 0; i--) {
            if ( (listBeams.get(i).getStartPt().getPtID() == pos) | (listBeams.get(i).getEndPt().getPtID() == pos) ) {
                System.out.println("lösche Stab " + i);
                delBeam(i);
            }
        }
        listPts.remove(pos);
        nbNodes = listPts.size();
        resetStructure();               //Resultate zurücksetzen
    }
    
    //Punkt aus der Liste löschen (als Liste)
    public static void delBeam(int pos) {
        listBeams.remove(pos);
        nbBeams = listBeams.size();
        resetStructure();               //Resultate zurücksetzen
    }
    
    //Anzahl Knoten zurückgeben
    public static int getNbPts() {
        return nbNodes;
    }
    
    //Anzahl Stäbe zurückgeben
    public static int getNbBeams() {
        return nbBeams;
    }
    
    //Update IDs der Punkte
    private static void updatePtID() {
        for (int i = 0; i < nbNodes; i++) {
            listPts.get(i).setPtID(i);
        }
    }
    
    //Update IDs der Punkte
    private static void updateBeamID() {
        for (int i = 0; i < nbBeams; i++) {
            listBeams.get(i).setBeamID(i);
        }
    }
    
    //Deformation wurde berechnet -> auf True setzen
    public static void setCalculatedTrue() {
        isCalculated = true;
    }
    
    public static boolean getCalculated() {
        return isCalculated;
    }
    
    
    //Skalierung für Deformation anwenden
    //Faktor 2 bedeutet, dass die Deformation ∆s doppelt so gross dargestellt wird
    public static void setScaleFactor(double factor) {
        int nb = listPts.size();
        double x0, y0, xn, yn;          //Koordinaten Punkt (Ausgang: 0 - mit Verschiebung: n)
        double op[] = new double[2];    //Gerade durch p und q (Ursprung o)
        double pq[] = new double[2];    //p: ohne Verschiebung, q: mit Verschiebung
        
        for (int i = 0; i < nb; i++) {
            x0 = listPts.get(i).getValX();
            y0 = listPts.get(i).getValY();
            xn = listPts.get(i).getDisplacementX();
            yn = listPts.get(i).getDisplacementY();
            
            //Gerade für Skalierung (Vektoren): x = op + t * pq
            op[0] = x0;
            op[1] = y0;
            pq[0] = xn - x0;
            pq[1] = yn - y0;
            
            listPts.get(i).setDisplXScaled(op[0] + factor*pq[0], op[1] + factor*pq[1]);
        }
    }
    
    //Stäbe: E-Modul zurückgeben (als Array)
    public static double[] getModul() {
        int nb = listBeams.size();
        double[] a = new double[nb];
        
        for (int i = 0; i < nb; i++) {
            a[i] = listBeams.get(i).getModul();
        }
        return a;
    }
    
    //Stäbe: Fläche zurückgeben (als Array)
    public static double[] getSurface() {
        int nb = listBeams.size();
        double[] a = new double[nb];
        
        for (int i = 0; i < nb; i++) {
            a[i] = listBeams.get(i).getSurf();
        }
        return a;
    }
    
    //Liste mit Querschnitten zurückgeben "listSections"
    public static List<Double[]> getSections() {
        return listSections;
    }
    
    //Deformation auf null setzen
    public static void setDispNull() {
        for (int i = 0; i < nbNodes; i++) {
            listPts.get(i).setDisplX(listPts.get(i).getValX());
            listPts.get(i).setDisplY(listPts.get(i).getValY());
        }
    }
    
    //Reset Struktur
    private static void resetStructure() {
        updatePtID();                   //Bei Änderung ID anpassen
        updateBeamID();                 //Bei Änderung ID anpassen
        isCalculated = false;           //Bei Änderung Neuberechnung notwendig
        //Deformation auf null setzen
        for (int i = 0; i < nbBeams; i++) {
            listBeams.get(i).setDef(0.0);
            listBeams.get(i).calculateForce();
        }
        setDispNull();
    }
    
    //Get Max. Kraft in den Stäben (Absoluter Wert)
    public static double getMaxBeamForce() {
        double maxF = 0;
        for (int i = 0; i < nbBeams; i++) {
            if ( Math.abs(listBeams.get(i).getForce()) > maxF ) maxF = Math.abs(listBeams.get(i).getForce());
        }
        return maxF;
    }
    
    
    //-----------------------------------------
    //TESTFUNKTIONEN
    
    //Punkt in der Konsole ausgeben
    public static void showPts() {
        int nb = listPts.size();
        for (int i = 0; i < nb; i++) {
            System.out.println(listPts.get(i).getValX() + "  " + listPts.get(i).getValY());
        }
    }
    
    //Stäbe in der Konsole ausgeben
    public static void showBeams() {
        int nb = listBeams.size();
        for (int i = 0; i < nb; i++) {
            System.out.println(listBeams.get(i).getStartPt().getValX() + "   " + listBeams.get(i).getStartPt().getValY());
            System.out.println(listBeams.get(i).getEndPt().getValX() + "   " + listBeams.get(i).getEndPt().getValY());
        }
    }
}
