//Autor: Manuel Schmocker
//Datum: 24.02.2014

package ch.manuel.structurecalc2d;

public class Main {
    
    public static Structure myStructure;
    public static NewJFrame myFrame;
    //@param args the command line arguments
    public static void main(String[] args) {
        
        //Set Look and Feel
        MyUtilities.setLaF("Windows");
        
        //Struktur erstellen
        myStructure = new Structure();
        
        //Fenster erstellen und Anzeigen (Hauptfenster)
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                myFrame = new NewJFrame();
                myFrame.setVisible(true);
            }
        });
          
    }
      
}
