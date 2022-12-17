//Autor: Manuel Schmocker
//Datum: 04.03.2014

package ch.manuel.structurecalc2d;

public class Beams {
    //Membervariablen
    private int beamID;             //ID Stab (Bei Bedarf benutzen)
    private double eMod;            //E-Modul Stab
    private double surf;            //Fläche Stab
    private Points startPt;         //Start Punkt
    private Points endPt;           //End Punkt
    private Points midPt;           //Stabmitte (wird hier berechnet)
    private double force;           //Kraft im Stab
    private double epsilon;         //Deformation ε
    
    
    //Konstruktor
    public Beams(Points startPt, Points endPt, double eMod, double surf) {
        this.beamID = -1;
        this.startPt = startPt;
        this.endPt = endPt;
        this.eMod = eMod;
        this.surf = surf;
        this.epsilon = 0;
        this.force = 0;
        this.midPt = new Points();
        getMidPt(startPt, endPt);
    }
    public Beams() {
        this.beamID = -1;
        this.midPt = new Points();
        this.epsilon = 0;
        this.force = 0;
    }
    
    
    //Methoden
    //------------------------------------
    
    //Punkte festlegen
    public void setBeam(Points pt1, Points pt2) {
        this.startPt = pt1;
        this.endPt = pt2;
        getMidPt(pt1, pt2);
    }
    
    //E-Modul festlegen
    public void setModul(double mod) {
        this.eMod = mod;
    }
    
    //Fläche Stab festlegen
    public void setSurf(double surf) {
        this.surf = surf;
    }
    
    //Punkt 1 zurückgeben (Startpunkt)
    public Points getStartPt() {
        return this.startPt;
    }
    
    //Punkt 2 zurückgeben (Endpunkt)
    public Points getEndPt() {
        return this.endPt;
    }
    
    //E-Modul zurückgeben
    public double getModul() {
        return this.eMod;
    }
    
    //Fläche A zurückgeben
    public double getSurf() {
        return this.surf;
    }
    
    //ID festlegen
    public void setBeamID(int id) {
        this.beamID = id;
    }
    
    //ID zurückgeben
    public int getBeamID() {
        return this.beamID;
    }
    
    //Mittelpunkt Stab berechnen und in midPt speichern
    private void getMidPt(Points pt1, Points pt2) {
        double midX, midY;
        midX = (pt1.getValX() + pt2.getValX()) / 2;
        midY = (pt1.getValY() + pt2.getValY()) / 2;
        this.midPt.setVal(midX, midY);  
    }
    
    //Mittelpunkt Stab zurückgeben
    public Points getMidPt() {
        return this.midPt;
    }
    
    //Deformation im Stab berechnen
    public void setDef(double eps) {
        this.epsilon = eps;
    }
    
    //Kraft im Stabe berechnen
    public void calculateForce() {
        this.force = this.eMod * this.surf * this.epsilon / 1000;
    }
    
    //Deformation zurückgeben
    public double getDef() {
        return this.epsilon;
    }
    
    //Kraft zurückgeben
    public double getForce() {
        return this.force;
    }
}
