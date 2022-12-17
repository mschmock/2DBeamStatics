//Autor: Manuel Schmocker
//Datum: 04.03.2014

package ch.manuel.structurecalc2d;

import Jama.Matrix;
import java.util.Arrays;


public class Calculation {
    //Membervariablen Matrizen
    private static Matrix m_jacobi;
    private static Matrix v_fn;
    private static Matrix v_xn;
    private static Matrix v_dx;
    //Membervariablen
    /*Anzahl Punkte, Stäbe, Fixpunkte und Kräfte
    Von den Variablen wird eine Kopie erstellt in dieser Klasse erstellt
    */
    private static final double dh = 0.00001;       //Inkrement für Ableitung
    private static final double START_IF_FIX = 1.0; //Startwert für blockierte x-Werte
    private static int nb_pts;                      //Anzahl Punkte
    private static int nb_beams;                    //Anzahl Stäbe
    private static double pts[];                    //Punkte: x-Werte in Pos. i, y-Werte in Pos i+1
    private static boolean fix[];                   //Fixpunkte: x-Werte in Pos. i, y-Werte in Pos i+1
    private static double force[];                  //Kräfte: x-Werte in Pos. i, y-Werte in Pos i+1
    private static double surf[];                   //Querschnitt-Fläche Stäbe
    private static double eMod[];                   //E-Modul Stäbe
    private static double l0[];                     //Stablänge (t = 0, nicht def.)
    private static double xn[];                     //Vektor mit gesuchten Werten
    private static double fn[];                     //Funktionswerte (Summe Fx, resp. Fy)
    private static double dx[];                     //delta x = xn+1 - xn
    //Variablen für Überwachung
    private static double calculationTime;          //Dauer der Berechnung
    private static int iter;                        //Zähler für Iterationen
    private static double accuracy;                 //Erziehlte Genauigkeit (Für jede Iteration neu berechnen)
    private static final int MAX_ITER = 100;        //Max. Anzahl an Iterationen
    private static final double MIN_ACC = 0.00001;  //Min. geforderte Genauigkeit (accuracy): Δxn / xn < MIN_ACC
    
    //Prüfen, ob Bedingungen für Berechnung erfüllt sind
    //True: Falls alle Bedingungen ok sind
    public static void startCalculation() { 
        boolean inputIsOK = true;
        //Mindestens drei Punkte vorhanden?
        if (Structure.getNbPts() < 2) {
            MyUtilities.getErrorMsg( "Anzahl Punkte" , "Für eine Berechnung sind mind. 2 Punkte notwendig!" );
            inputIsOK = false;
        }
        
        //Keine doppelten Punkte
        for ( int i = 0; i < (Structure.getNbPts() - 1) ; i++ ) {
            for ( int j = (i+1); j < Structure.getNbPts(); j++) {
                if ( (Structure.getPt(i).getValX() == Structure.getPt(j).getValX()) & 
                     (Structure.getPt(i).getValY() == Structure.getPt(j).getValY()) ) {
                    MyUtilities.getErrorMsg( "Doppelte Punkte" , "Punkt Nr. " + (i+1) + " und Punkt Nr. " + (j+1) + " sind identisch.");
                    inputIsOK = false;
                }
            }
        }
        
        //Keine doppelten Stäbe
        for ( int i = 0; i < Structure.getNbBeams(); i++ ) {
            for ( int j = (i+1); j < Structure.getNbBeams(); j++) {
                if ( (Structure.getBeam(i).getStartPt().getPtID() == Structure.getBeam(j).getStartPt().getPtID()) &
                     (Structure.getBeam(i).getEndPt().getPtID() == Structure.getBeam(j).getEndPt().getPtID()) ) {
                    MyUtilities.getErrorMsg( "Doppelte Stäbe" , "Stab Nr. " + (i+1) + " und Stab Nr. " + (j+1) + " sind identisch.");
                    inputIsOK = false;
                }
            }
        }
        
        //Randbedingungen prüfen
        //Mind. 1 Fix in x- und 2 Fix in y-Richtung, resp. 2 Fix in x- und 1 Fix in y-Richtung
        int fixX = 0;
        int fixY = 0;
        for ( int i = 0; i < Structure.getNbPts(); i++ ) {
            if ( Structure.getPt(i).getFixX() ) {
                fixX++;
            }
            if ( Structure.getPt(i).getFixY() ) {
                fixY++;
            }
        }
        if ( (fixX == 0) | (fixY == 0) ) {
            MyUtilities.getErrorMsg( "Randbedingung" , "Randbedingungen prüfen!" );
            inputIsOK = false;
        }
        if ( (fixX == 1) & (fixY == 1) ) {
            MyUtilities.getErrorMsg( "Randbedingung" , "Randbedingungen prüfen!" );
            inputIsOK = false;
        }
        
        //Jeder Punkt muss mit mind. 2 Stäben verbunden sein
        int count;
        for ( int i = 0; i < Structure.getNbPts(); i++ ) {
            count = 0;
            for ( int j = 0; j < Structure.getNbBeams(); j++ ) {
                if ( Structure.getPt(i) == Structure.getBeam(j).getStartPt() ) count++;
                if ( Structure.getPt(i) == Structure.getBeam(j).getEndPt() ) count++;
            }
            if ( count < 2 ) {
                MyUtilities.getErrorMsg( "Stab hinzufügen" , "Der Punkt " + (i+1) + " ist nicht stabil verbunden!");
                inputIsOK = false;
            }
        }

        //Berechnung Schritt 1 starten, wenn keine Bedingungen verletzt wurde
        if ( inputIsOK ) initCalculation();
    }
    
    //Mit Berechnung starten (falls überprüfung ok)
    private static void initCalculation() {
        //Deformation auf null setzten
        Structure.setDispNull();
        
        //Datenumfang festlegen (Grösse der Datenfelder)
        nb_pts = Structure.getNbPts();
        nb_beams = Structure.getNbBeams();
        pts = new double[2*nb_pts];
        fix = new boolean[2*nb_pts];
        force = new double[2*nb_pts];
        surf = new double[nb_beams];
        eMod = new double[nb_beams];
        l0 = new double[nb_beams];
        xn = new double[2*nb_pts];
        fn = new double[2*nb_pts];
        dx = new double[2*nb_pts];
        
        //Datenfelder mit Werten füllen
        for (int i = 0; i < nb_pts; i++){
            pts[2*i] = Structure.getPt(i).getValX();
            pts[2*i + 1] = Structure.getPt(i).getValY();
            fix[2*i] = Structure.getPt(i).getFixX();
            fix[2*i + 1] = Structure.getPt(i).getFixY();
            force[2*i] = Structure.getPt(i).getForceX();
            force[2*i + 1] = Structure.getPt(i).getForceY();
        }
        for (int i = 0; i < nb_beams; i++) {
            surf[i] = Structure.getBeam(i).getSurf();
            eMod[i] = Structure.getBeam(i).getModul() / 1000;   //Damit alle Einheiten auf kN sind

            //L0 festlegen
            double x1 = Structure.getBeam(i).getStartPt().getValX();
            double y1 = Structure.getBeam(i).getStartPt().getValY();
            double x2 = Structure.getBeam(i).getEndPt().getValX();
            double y2 = Structure.getBeam(i).getEndPt().getValY();
            l0[i] = l(x1, y1, x2, y2);
        }
        
        //Vektor mit x-Werten
        //Reihenfolge: x1, y1, x2, y2, x3, ...
        for (int i = 0; i < (2*nb_pts); i++) {
            if (fix[i]) {
                xn[i] = START_IF_FIX;
            } else {
                xn[i] = pts[i];
            }
        }
        
        //Methode zum Lösen des Problems aufrufen
        solve();
    }
    
    //Problem lösen
    private static void solve() {
        double jacobi[][] = new double[2*nb_pts][2*nb_pts];
        double xn1, yn1;                    //Koordinaten Startpunkt (Iteration n)
        double xn2, yn2;                    //Koordinaten Endpunkt (Iteration n)
        double res_temp;                    //Temp. Resultat
        double fx_tmp, fy_tmp;              //Temp. Resultate
        int posx, posy;                     //Pos. für jacobi Matrix i,j
        int posx2;
        boolean is_Fx;                      //Fx oder Fy
        //Überwachung
        long t1, t2;                        //Für Berechnungsdauer
        iter = 0;                           //Zähler auf null setzen
        
        //Loop erstellen, bis ausreichende Genauigkeit erreicht ist
        do {
            //Starte Zähler für Berechnungsdauer
            t1 = System.currentTimeMillis();
            
            //Jacobi Matrix auf null setzten
            setNull(jacobi);
            
            for (int i = 0; i < nb_beams; i++) {
                //Werte Start- und Endpunkt festlegen
                xn1 = Structure.getBeam(i).getStartPt().getDisplacementX();
                yn1 = Structure.getBeam(i).getStartPt().getDisplacementY();
                xn2 = Structure.getBeam(i).getEndPt().getDisplacementX();
                yn2 = Structure.getBeam(i).getEndPt().getDisplacementY();
                
                //Die 4 Gleichungen (Fx1, Fy1, Fx2, Fy2)
                for (int j = 0; j < 2; j++) {
                    if (j == 0) {           //Fx im StartPunkt
                        posx = 2*Structure.getBeam(i).getStartPt().getPtID();
                        posx2 = 2*Structure.getBeam(i).getEndPt().getPtID();
                        is_Fx = true;
                    } else {    //Fy im StartPunkt 
                        posx = 2*Structure.getBeam(i).getStartPt().getPtID() + 1;
                        posx2 = 2*Structure.getBeam(i).getEndPt().getPtID() + 1;
                        is_Fx = false;
                    }
                    
                    //Werte Jacobi-Matrix bestimmen
                    for (int k = 0; k < 4; k++) {
                       if (k == 0) {
                           posy = 2*Structure.getBeam(i).getStartPt().getPtID();
                           //Ableiten nach x1
                           if (is_Fx) {     //Fx
                               res_temp = eMod[i] * surf[i] * ( funct1(xn1 + dh, yn1, xn2, yn2, l0[i]) 
                                                                    - funct1(xn1, yn1, xn2, yn2, l0[i])) / dh;
                           } else {         //Fy
                               res_temp = eMod[i] * surf[i] * ( funct2(xn1 + dh, yn1, xn2, yn2, l0[i]) 
                                                                    - funct2(xn1, yn1, xn2, yn2, l0[i])) / dh;
                           }
                       } else if (k == 1) {
                           posy = 2*Structure.getBeam(i).getStartPt().getPtID() + 1;
                           //Ableiten nach y1
                           if (is_Fx) {     //Fx
                               res_temp = eMod[i] * surf[i] * ( funct1(xn1, yn1 + dh, xn2, yn2, l0[i]) 
                                                                    - funct1(xn1, yn1, xn2, yn2, l0[i])) / dh;
                           } else {         //Fy
                               res_temp = eMod[i] * surf[i] * ( funct2(xn1, yn1 + dh, xn2, yn2, l0[i]) 
                                                                    - funct2(xn1, yn1, xn2, yn2, l0[i])) / dh;
                           }
                       } else if (k == 2) {
                           posy = 2*Structure.getBeam(i).getEndPt().getPtID();
                           //Ableiten nach x2
                           if (is_Fx) {     //Fx
                               res_temp = eMod[i] * surf[i] * ( funct1(xn1, yn1, xn2 + dh, yn2, l0[i]) 
                                                                    - funct1(xn1, yn1, xn2, yn2, l0[i])) / dh;
                           } else {         //Fy
                               res_temp = eMod[i] * surf[i] * ( funct2(xn1, yn1, xn2 + dh, yn2, l0[i]) 
                                                                    - funct2(xn1, yn1, xn2, yn2, l0[i])) / dh;
                           }
                       } else {
                           posy = 2*Structure.getBeam(i).getEndPt().getPtID() + 1;
                           //Ableiten nach y2
                           if (is_Fx) {     //Fx
                               res_temp = eMod[i] * surf[i] * ( funct1(xn1, yn1, xn2, yn2 + dh, l0[i]) 
                                                                    - funct1(xn1, yn1, xn2, yn2, l0[i])) / dh;
                           } else {         //Fy
                               res_temp = eMod[i] * surf[i] * ( funct2(xn1, yn1, xn2, yn2 + dh, l0[i]) 
                                                                    - funct2(xn1, yn1, xn2, yn2, l0[i])) / dh;
                           }
                       }
                       //System.out.println(i + " " + j + " " + posx + " " + posy + " " + res_temp);    //TEST
                       
                       //'Sonderfall:
                       //1. Falls nach Auflager abgeleitet wird: a(ij) = 1
                       //2. Falls blockiert, jedoch nicht von der Kraft (Lager) abhängig
                       if (fix[posy]) {
                           //Unabhängig von der Kraft Px im Auflager
                           if (posx == posy) {
                               jacobi[posx][posy] = 1;
                           } else if (posx2 == posy) {
                               jacobi[posx2][posy] = 1;
                           //Die Gleichung hängt nicht von der Variable "Kraft Px" ab
                           } else {
                               jacobi[posx][posy] = 0;
                           }
                       //Ohne Auflager
                       } else {
                           //Wert hinzufügen
                           jacobi[posx][posy] += res_temp;
                           jacobi[posx2][posy] += -res_temp;
                       }
                    
                    }
                }
            }
            
            //Funktionswerte berechnen
            //------------------------
            //1) Kräfte in Stäbe berechnen
            Arrays.fill(fn, 0.0);               //Vektor auf null setzen
            for (int i = 0; i < nb_beams; i++) {
                //Werte Start- und Endpunkt festlegen
                xn1 = Structure.getBeam(i).getStartPt().getDisplacementX();
                yn1 = Structure.getBeam(i).getStartPt().getDisplacementY();
                xn2 = Structure.getBeam(i).getEndPt().getDisplacementX();
                yn2 = Structure.getBeam(i).getEndPt().getDisplacementY();
                
                fx_tmp = eMod[i] * surf[i] * funct1(xn1, yn1, xn2, yn2, l0[i]);
                fy_tmp = eMod[i] * surf[i] * funct2(xn1, yn1, xn2, yn2, l0[i]);
                
                fn[2*Structure.getBeam(i).getStartPt().getPtID()]     += fx_tmp;
                fn[2*Structure.getBeam(i).getStartPt().getPtID() + 1] += fy_tmp;
                fn[2*Structure.getBeam(i).getEndPt().getPtID()]       -= fx_tmp;
                fn[2*Structure.getBeam(i).getEndPt().getPtID() + 1]   -= fy_tmp;
            }
            
            //2) Kräfte hinzufügen
            for (int i = 0; i < (2*nb_pts); i++) {
                //2.1) Fixpunkt
                if (fix[i]) {
                    fn[i] += xn[i];
                }
                //2.2) Einwirkung hinzufügen
                fn[i] += force[i];
            }
            //System.out.println("Durchgang " + iter);      //TEST
            //printArr(fn);                                 //TEST
            
            //Lösen des Gleichungssystems
            //J(xn) * Δxn = - f(xn)
            m_jacobi = new Matrix(jacobi);
            
            //Neues xn berechnen
            v_xn = new Matrix(xn, xn.length);       //xn
            v_fn = new Matrix(fn, fn.length);       //f(xn)
            v_dx = new Matrix(dx, xn.length);       //Δxn
            v_dx = m_jacobi.solve(v_fn.times(-1.0));
            v_xn = v_xn.plus(v_dx);
            
            //xn aktualisieren
            for (int i = 0; i < nb_pts; i++) {
                if (!fix[2*i]) {
                    Structure.getPt(i).setDisplX(v_xn.get(2*i, 0));
                }
                if (!fix[2*i + 1]) {
                    Structure.getPt(i).setDisplY(v_xn.get(2*i + 1, 0));
                }
                xn[2*i] = v_xn.get(2*i, 0);
                xn[2*i + 1] = v_xn.get(2*i + 1, 0);
            }
            
            //Konvergenz bestimmen: Δxn / xn < Definierte Genauigkeit
            accuracy = 0.0;
            double tmp;
            for ( int i = 0; i < (2*nb_pts); i++ ) {
                tmp = v_dx.get(i, 0) / v_xn.get(i, 0);
                if ( tmp > accuracy ) {
                    accuracy = tmp;
                }
            }
            iter ++;
        //Abbruch sobald:
            //a) Genauigkeit erreicht
            //b) Max. Iterationen erreich
        } while ( (accuracy > MIN_ACC) && (iter < MAX_ITER) );
        
        /*//TEST
        //v_xn.print(5, 3);
        for (int i = 0; i < nb_pts; i++) {
            System.out.println("Px " + i + ": " + Structure.getPt(i).getDisplacementX());
            System.out.println("Py " + i + ": " + Structure.getPt(i).getDisplacementY());
        }*/
        
        //Berechnung ok? -> OK
        Structure.setCalculatedTrue();
        //Skalierung auf 1.0 setzen
        Structure.setScaleFactor(1.0);
        //Member-Variablen updaten (Deformation eps, Kraft)
        update();
        //Berechnungsdauer
        t2 = System.currentTimeMillis();
        calculationTime = t2 - t1;
    }
    
    //Berechne Länge 
    private static double l(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }
    
    //Deformation ε Stab berechnen
    private static double epsilon(double len_t0, double len_ti) {
        return (len_ti -len_t0) / len_t0;
    }
    
    //Funktion 1
    private static double funct1(double xn1, double yn1, double xn2, double yn2, double len0) {
        double len = l(xn1, yn1, xn2, yn2);
        double eps = epsilon(len0, len);
        //Berechne cos(alpha) * epsilon
        return (xn2 - xn1) / len * eps;
    }
    
    //Funktion 2
    private static double funct2(double xn1, double yn1, double xn2, double yn2, double len0) {
        double len = l(xn1, yn1, xn2, yn2);
        double eps = epsilon(len0, len);
        //Berechne sin(alpha) * epsilon
        return (yn2 - yn1) / len * eps;
    }
    
    //Deformation und Kraft updaten
    private static void update() {
        double x1, y1, x2, y2;
        double l1, l2;
        for ( int i = 0; i < nb_beams; i++ ) {
            //Länge Grundzustand
            l1 = l0[i];
            //Länge Deformiert
            x1 = Structure.getBeam(i).getStartPt().getDisplacementX();
            y1 = Structure.getBeam(i).getStartPt().getDisplacementY();
            x2 = Structure.getBeam(i).getEndPt().getDisplacementX();
            y2 = Structure.getBeam(i).getEndPt().getDisplacementY();
            l2 = l(x1, y1, x2, y2);
            //Deformation festlegen
            Structure.getBeam(i).setDef( epsilon(l1, l2) );
            //Kraft berechnen
            Structure.getBeam(i).calculateForce();
        }
    }
    
    //Elemente NxN Matrix auf null setzen
    private static void setNull(double[][] arr) {
        int aa = arr.length;
        
        for (int i = 0; i < aa; i++) {
            Arrays.fill(arr[i], 0.0);
        }
        
        /*
        for (int i = 0; i < aa; i++) {
            int bb = arr[i].length;
            for (int j = 0; j < bb; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.print("\n");
        }*/

    }
    
    //Dauer der Berechnung zurückgeben
    public static double getCalculationTime() {
        if (Structure.getCalculated()) return calculationTime;
        else return 0.0;
    }
    
    //Anzahl Iterationen zurückgeben
    public static int getIterations() {
        if (Structure.getCalculated()) return iter;
        else return 0;
    }
    
    //Erziehlte Genauigkeit zurückgeben
    public static double getAccuracy() {
        if (Structure.getCalculated()) return accuracy;
        else return 1.0;
    }
    
    //HILFSFUNKTIONEN
    /*
    //Array in der Konsole ausgeben
    private static void printArr(double[][] arr) {
        int aa = arr.length;
        
        for (int i = 0; i < aa; i++) {
            int bb = arr[i].length;
            
            for (int j = 0; j < bb; j++) {
                System.out.print(arr[i][j] + "  ");
            }
            System.out.print("\n");
        }
    }
    
    //Array in der Konsole ausgeben
    private static void printArr(double[] arr) {
        int aa = arr.length;
        
        for (int i = 0; i < aa; i++) {
            System.out.println(arr[i]);
        }
    }
    
    
    //Matrix in der Konsole ausgeben
    private static void printM(Matrix m) {
        int a = m.getRowDimension();
        int b = m.getColumnDimension();
        System.out.println(a + "   " + b);
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < b; j++) {
                System.out.print(m.get(i, j) + "  ");
            }
            System.out.print("\n");
        }
    }
    */
}
