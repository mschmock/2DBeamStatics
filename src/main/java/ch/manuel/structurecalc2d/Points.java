//Autor: Manuel Schmocker
//Datum: 24.02.2014

package ch.manuel.structurecalc2d;

public class Points {
    //Membervariablen
    private int ptID;           //ID Punkt (Bei Bedarf benutzen)
    private double posX;        //X-Koordinate
    private double posY;        //Y-Koordinate
    private double posXn;       //X-Koordinate nach Verschiebung
    private double posYn;       //Y-Koordinate nach Verschiebung
    private double posXnScaled; //X-Koordinate nach Verschiebung (mit Faktor für Skalierung)
    private double posYnScaled; //Y-Koordinate nach Verschiebung (mit Faktor für Skalierung)
    private double forceX;      //Kraft in X-Richtung (nur ungleich null, falls eine exteren Kraft angreift)
    private double forceY;      //Kraft in Y-Richtung (nur ungleich null, falls eine exteren Kraft angreift)
    private boolean fixX;       //Fixpunkt in X-Richtung
    private boolean fixY;       //Fixpunkt in Y-Richtung
    
    //Konstruktor
    //------------------------------------
    public Points() {
        //ID Punkt (wird anschliessend in "Structure" festgelegt
        this.ptID = -1;
        
        //Koordinate
        this.posX = 0;
        this.posY = 0;
        this.posXn = 0;
        this.posYn = 0;
        this.posXnScaled = 0;
        this.posYnScaled = 0;
        
         //Kraft
        this.forceX = 0;
        this.forceY = 0;
        
        //Fixpunkt
        this.fixX = false;
        this.fixY = false;
    }
    
    //Konstruktor mit Wertzuweisung
    public Points(double posX, double posY) {
        //ID Punkt (wird anschliessend in "Structure" festgelegt
        this.ptID = -1;
        
        //Koordinaten
        this.posX = posX;
        this.posY = posY;
        this.posXn = posX;
        this.posYn = posY;
        this.posXnScaled = posX;
        this.posYnScaled = posY;
        
        //Kraft
        this.forceX = 0;
        this.forceY = 0;
        
        //Fixpunkt
        this.fixX = false;
        this.fixY = false;
    }
    
    
    //Methoden
    //------------------------------------
    
    //Punkte bearbeiten: Neue Koordinaten setzen
    //Setzt immer auch die Verschiebung neu
    public void setVal(double posX, double posY) {
        this.posX = posX;
        this.posY = posY;
        this.posXn = posX;
        this.posYn = posY;
        this.posXnScaled = posX;
        this.posYnScaled = posY;
    }
    
    //Verschiebung festlegen (x-Wert)
    public void setDisplX(double posXn) {
        this.posXn = posXn;
        this.posXnScaled = posXn;
    }
    
    //Verschiebung festlegen (y-Wert)
    public void setDisplY(double posYn) {
        this.posYn = posYn;
        this.posYnScaled = posYn;
    }
    
    //Verschiebung festlegen für Werte skaliert
    public void setDisplXScaled(double posXn, double posYn) {
        this.posXnScaled = posXn;
        this.posYnScaled = posYn;
    }
    
    //Kräfte definieren (X-Richtung)
    public void setForceX(double forceX) {
        this.forceX = forceX;
    }
    
    //Kräfte definieren (Y-Richtung)
    public void setForceY(double forceY) {
        this.forceY = forceY;
    }
    
    //Fixpunkte definieren (X-Richtung)
    public void setFixX(boolean fixX) {
        this.fixX = fixX;
    }
    
    //Fixpunkte definieren (Y-Richtung)
    public void setFixY(boolean fixY) {
        this.fixY = fixY;
    } 
    
    //ID festlegen
    public void setPtID(int id) {
        this.ptID = id;
    }
    
    //ID ausgeben
    public int getPtID() {
        return this.ptID;
    }
    
    //Koordinaten ausgeben: X-Wert
    public double getValX() {
        return this.posX;
    }
    
    //Koordinaten ausgeben: Y-Wert
    public double getValY() {
        return this.posY;
    }
    
    //Koordinate nach Verschiebung ausgeben: X-Wert
    public double getDisplacementX() {
        return this.posXn;
    }
    
    //Koordinate nach Verschiebung ausgeben: Y-Wert
    public double getDisplacementY() {
        return this.posYn;
    }
    
    //Koordinate nach Verschiebung (skaliert): X-Wert
    public double getDisplScaledX() {
        return this.posXnScaled;
    }
    
    //Koordinate nach Verschiebung (skaliert): Y-Wert
    public double getDisplScaledY() {
        return this.posYnScaled;
    }
    
    //Kraft ausgeben: X-Kraft
    public double getForceX() {
        return this.forceX;
    }
    
    //Kraft ausgeben: Y-Kraft
    public double getForceY() {
        return this.forceY;
    }
    
    //Lagerung ausgeben: Lager X-Richtung
    public boolean getFixX() {
        return this.fixX;
    }
    
    //Lagerung ausgeben: Lager Y-Richtung
    public boolean getFixY() {
        return this.fixY;
    }
    
    
    
    //-----------------------------------------
    //TESTFUNKTIONEN
    
    //Punkt in der Konsole ausgeben
    public void showPoints()
    {
        String tmpTXT;
        tmpTXT = Double.toString(posX) + "  " + Double.toString(posY);
        System.out.println(tmpTXT);
    }
}
