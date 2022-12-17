//Autor: Manuel Schmocker
//Datum: 24.02.2014

package ch.manuel.structurecalc2d;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

//Klasse mit verschiedenen Hilfsmittel
public class MyUtilities {
    
    //Kann der String in eine Zahl umgewandelt werden
    //Gibt true zurück, falls kein Fehler auftritt
    public static boolean isNumeric(String str) {
        try {  
            Double.parseDouble(str);  
        } catch(NumberFormatException nfe) {  
            return false;  
        }  
        return true;  
    }
    
    //Kann der String in einen Int umgewandelt werden
    //Gibt true zurück, falls kein Fehler auftritt
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
    
    //Kann der String in eine Boolean umgewandelt werden
    //Gibt true zurück, falls kein Fehler auftritt
    public static boolean isBoolean (String str) {
        if (str.equals("true")) return true;
        if (str.equals("True")) return true;
        if (str.equals("TRUE")) return true;
        if (str.equals("false")) return true;
        if (str.equals("False")) return true;
        if (str.equals("FALSE")) return true;
        return false;
    }
    
    //String in Boolean
    //Achtung: nur in Kombination mit "isBoolean" anwenden
    private static boolean stringToBoolean (String str) {
        if (str.equals("true")) return true;
        if (str.equals("True")) return true;
        if (str.equals("TRUE")) return true;
        if (str.equals("false")) return false;
        if (str.equals("False")) return false;
        else return false;
    }
    
    //Eingabefenster zum Erstellen neuer Punkt
    //Speichert Punkt in der Klasse "Point"
    public static void getPointDialog(String titel, String initTxt){
        //Lokale Variablen
        boolean validInput = false;
        String txt;
        String txt2[] = new String[1];
        
        txt =  JOptionPane.showInputDialog(null, titel, initTxt);
        if (txt != null){
            String txt3[] = txt.split(" ");
            if (txt3.length == 2) {             //Nur 2 Elemente werden akzeptiert (x, y)
                for (String str : txt3){
                    if( !isNumeric(str) ){      //Ist der Input eine Zahl
                        validInput = false;
                        break;
                    }
                    validInput = true;
                    txt2 = txt3;
                }
            }
        }              
        
        //Fehlermeldung, falls Input fehlerhaft
        if ( txt == null ){     //Dialog Schliessen bei Abbruch: True
            //nichts...
        } else if ( validInput ) {
            Structure.addPoint(Double.parseDouble(txt2[0]), Double.parseDouble(txt2[1]));
            
        } else {
            getErrorMsg("Eingabefehler", "Eingabe überprüfen: " +txt);
            getPointDialog(titel, txt);         //Eingabe wiederholen
        }
    }
    
    //Eingabefenster zum Löschen Punkt
    //Anderung in Klasse "Structure"
    public static void getPointDelDialog(){
        String txt;
        int nb = Structure.getNbPts();
        String obj[] = new String[nb];
        
        //Text für Auswahlfeld erstellen
        for (int i = 0; i < nb; i++) {
            obj[i] = Integer.toString(i+1);
        }
        
        txt = (String) JOptionPane.showInputDialog(
                null,                           //parentComponent
                "Punkt Wählen:",                //message
                "Punkt löschen",                //title
                JOptionPane.PLAIN_MESSAGE,      //message type
                null,                           //icon
                obj,                            //selection Values
                "Bitte wählen");                //initial Value
         
        if ( txt == null ){     //Dialog Schliessen bei Abbruch: True
            //nichts...
        } else {
             //Punkt löschen
            String tmp[] = txt.split(":");
            int pos = Integer.parseInt(tmp[0]) - 1;
            Structure.delPt(pos);
        }
    }
    
    //Eingabefenster zum Löschen Stab (Beam)
    //Speichert Änderung in der Klasse "Structure"
    public static void getBeamDelDialog(){
        String txt;
        int nb = Structure.getNbBeams();
        String obj[] = new String[nb];
        
        //Text für Auswahlfeld erstellen
        for (int i = 0; i < nb; i++) {
            obj[i] = Integer.toString(i+1);
        }
                
        txt = (String) JOptionPane.showInputDialog(
                null,                           //parentComponent
                "Stab Wählen:",                 //message
                "Stab löschen",                 //title
                JOptionPane.PLAIN_MESSAGE,      //message type
                null,                           //icon
                obj,                            //selection Values
                "Bitte wählen");                //initial Value
         
        if ( txt == null ){     //Dialog Schliessen bei Abbruch: True
            //nichts...
        } else {
             //Stab löschen
            String tmp[] = txt.split(":");
            int pos = Integer.parseInt(tmp[0]) - 1;
            Structure.delBeam(pos);
        }
    }
    
    //Fehler bei Eingabe: Fenster mit Fehlercode
    public static void getErrorMsg(String titel, String initTxt){
        JOptionPane.showMessageDialog(null, initTxt, titel , JOptionPane.ERROR_MESSAGE);
    }
    
    //Look and Feel festlegen
    public static void setLaF(String lf){
        //Setze das Look & Feel: "Windows" falls vorhanden
        String lookandfeel = lf;     //Nimbus, Metal, Windows, Windows Classic, CDE/Motif
        boolean isSupported = false;        //Wird das gewünschte L&F unterstützt
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if (lookandfeel.equals(info.getName())) {
                isSupported = true;
                lookandfeel = info.getClassName();
                break;
            }
        }
        //System.out.println(UIManager.getLookAndFeel().getName());
        try {
            if (isSupported){
                UIManager.setLookAndFeel(lookandfeel);
            }
            else{
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        //System.out.println(UIManager.getLookAndFeel().getName());
    }
    
    
    //Die Zahl auf eine bestimmte Anzahl Stellen runden: s
    public static double runden(double d, int s){
        double d1;
        d1 = d * Math.pow(10, s);
        d1 = Math.round(d1) / Math.pow(10, s);
        
        return d1;
    }

    
    //Look and Feel während der Laufzeit ändern
    //Siehe Menu: "Edit"
    public static void laf() {
        int nb = 0;
        String txt;
        
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            nb++;
        }
        String obj[] = new String[nb];
        int i = 0;
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            obj[i] = info.getName();
            i++;
        }
        
        //Auswahlfeld anzeigen
        txt = (String) JOptionPane.showInputDialog(
                null,                           //parentComponent
                "Look & Feel:",                 //message
                "Design wählen",                //title
                JOptionPane.PLAIN_MESSAGE,      //message type
                null,                           //icon
                obj,                            //selection Values
                "Bitte wählen");                //initial Value
         
        if ( txt == null ){     //Dialog Schliessen bei Abbruch: True
            //nichts...
        } else {
             //L&F ändern
            setLaF(txt);
            Main.myFrame.updateLaF();
        }
    }
    
    public static void setScaleFactor() {
        String txt = Double.toString(runden(Structure.scaleFactor, 3));
        boolean validInput = true;
        
        while (validInput) {
            txt =  JOptionPane.showInputDialog(null, "Faktor Skalierung", txt);
            if ( txt == null ){     //Dialog Schliessen bei Abbruch: True
                //nichts...
                validInput = false;
            } else if( isNumeric(txt) ) {         //Ist der Input eine Zahl
                Structure.scaleFactor = Double.parseDouble(txt);
                Structure.setScaleFactor(Structure.scaleFactor);
                validInput = false;
            } else {
                getErrorMsg("Eingabefehler", "Eingabe überprüfen: " + txt);
                validInput = true;
            }
        }
        //Plot neu zeichnen
        Main.myFrame.updateMyPlot();
    }
    
    public static void saveStructure() {
        String nameDatei = getSaveFileDialog(new java.awt.Frame(), "Datei speichern", "D:\\", "MyStructure" + Structure.FILE_EXT);
        
        //Speichervorgang wird nur fortgesetzt, wenn der Pfad OK ist
        if ( !(nameDatei == null)) {
            //Endung ergänzen, falls notwendig
            String endDatei = nameDatei.substring(nameDatei.length() - Structure.FILE_EXT.length(), nameDatei.length());
            if ( !endDatei.equals(Structure.FILE_EXT) ){
                nameDatei = nameDatei + Structure.FILE_EXT;
            }
            
            // Textausgabe in die gewählte Datei
            try{
                BufferedWriter bw = new BufferedWriter(new FileWriter(nameDatei));
                
                //Starten mit schreiben...
                //1: Punkte:
                bw.write("<<POINTS");
                bw.newLine();
                bw.write("<< ID\t posX\t posY\t fixX\t fixY\t forceX [kN]\t forceY [kN]");
                bw.newLine();
                for (int i = 0; i < Structure.getNbPts(); i++) {
                    bw.write( Structure.getPt(i).getPtID() + "\t" + Structure.getPt(i).getValX()+ "\t" 
                              + Structure.getPt(i).getValY() + "\t" + Structure.getPt(i).getFixX() + "\t" 
                              + Structure.getPt(i).getFixY() + "\t" + Structure.getPt(i).getForceX() + "\t" 
                              + Structure.getPt(i).getForceY());
                    bw.newLine();
                }
                bw.write("<<END_POINTS");
                bw.newLine();
                //2. Querschnitts-Typen
                bw.write("<<SECTIONS");
                bw.newLine();
                bw.write("<< ID\t E-Modul [N/mm2]\t Surface [mm2]");
                bw.newLine();
                int nb = Structure.getSections().size();
                if (nb < 1) {
                    bw.write("<< ---no data ---");
                    bw.newLine();
                } else {
                    for (int i = 0; i < Structure.getSections().size(); i++) {
                        bw.write( i + "\t" + Structure.getSections().get(i)[0] + "\t"
                                  + Structure.getSections().get(i)[1]);
                        bw.newLine();
                    }
                }
                bw.write("<<END_SECTIONS");
                bw.newLine();
                
                //3. Stäbe
                bw.write("<<BEAMS");
                bw.newLine();
                bw.write("<< ID\t StartPt\t EndPt\t E-Modul [N/mm2]\t Surface [mm2]\t");
                bw.newLine();
                for (int i = 0; i < Structure.getNbBeams(); i++) {
                    bw.write( Structure.getBeam(i).getBeamID() + "\t" + Structure.getBeam(i).getStartPt().getPtID() + "\t" 
                              + Structure.getBeam(i).getEndPt().getPtID() + "\t" + Structure.getBeam(i).getModul() + "\t" 
                              + Structure.getBeam(i).getSurf());
                    bw.newLine();
                }
                bw.write("<<END_BEAMS");
                bw.newLine();
                
                bw.write("<<END_OF_FILE");
                bw.close();
                
            } catch ( IOException e ) {
                System.err.println( "Konnte Datei nicht erstellen" );
            }             
        }
    }
    
    // Dialog zum Speichern der Datei (wird von der Methode "saveFile()" aufgerufen
    private static String getSaveFileDialog(java.awt.Frame f, String title, String defDir, String fileType) {
        java.awt.FileDialog fd = new java.awt.FileDialog(f, title, java.awt.FileDialog.SAVE);
        fd.setFile(fileType);
        fd.setDirectory(defDir);
        fd.setLocation(50, 50);
        fd.setVisible(true);
        if ( fd.getDirectory() == null) {
            return null;
        } else {
            return fd.getDirectory() + fd.getFile();
        }
    }
    
        
    //Objekt (Struktur) laden
    public static void loadStructure() {
        String text = "";
        String text2 = "";
        boolean loadingOK = true;       //Wird auf false gesetzt, falls ein Fehler auftritt (Abschnitt)
        boolean loadingOK2 = true;      //Idem
        
        getMessage("Achtung: Nicht gespeicherte Daten gehen verloren!", "Datei öffnen");

        //Dialog öffnen
        String nameDatei = getOpenFileDialog(new java.awt.Frame(), "Datei öffnen", "D:\\", "*" + Structure.FILE_EXT);
        
        //Fall "null\null" tritt ein, wenn im Dialog abgebrochen wird
        //Gesamter Inhalt der Datei in einem String speichern
        if ( !nameDatei.equals("null\\null") ){
            try {
                BufferedReader br = new BufferedReader(new FileReader(nameDatei));
                while (text2 != null) {
                    text2 = br.readLine();
                    text = text + text2 + "\n";
                }
                
            } catch (IOException e) {
                System.err.println("Fehler beim Laden der Datei");
                getMessage("Datei korrupt", "Datei kann nicht geöffnen werden");
                loadingOK = false;
            }
        }
        
        String subStr = "";
        String lines[];
        Structure.clearStruct();    //Strukture löschen
        
        //1. Substring auswählen: "POINTS"
        if ( loadingOK ) {
            subStr = text.substring(text.indexOf("<<POINTS"), text.indexOf("<<END_POINTS"));
            lines = subStr.split("\n");
            //Erwarter Aufbau:
            //b0: ID    b1: posX    b2: posY    b3: fixX    b4: fixY    b5: forceX  b6: force Y
            for (String ln:lines){
                String b[];
                int id;
                //Testen, ob Linie ok ist
                //Zeilen, die mit "<<" beginnen nicht beachten
                if ( !ln.substring(0, 2).equals("<<") ) {
                    b = ln.split("\t");
                    
                    if ( b.length > 6 ) {
                        //1.1 ID lesen
                        //Kann der Inhalt als Zahl interpretiert werden?
                        if ( isNumeric(b[0]) ) {
                            id = (int) Double.parseDouble(b[0]);
                        } else {
                            loadingOK = false;
                            break;
                        }

                        //1.2 Koordination Punkte einlesen
                        //Kann der Inhalt als Zahl interpretiert werden?
                        if ( isNumeric(b[1]) && isNumeric(b[2]) ) {
                            Structure.addPoint(Double.parseDouble(b[1]), Double.parseDouble(b[2]));
                        } else {
                            loadingOK = false;
                            break;
                        }

                        //1.3 Fixpunkte einlesen
                        //Inhalt muss "true" oder "false" sein
                        if ( isBoolean(b[3]) && isBoolean(b[4]) ) {
                            Structure.getPt(id).setFixX(stringToBoolean(b[3]));
                            Structure.getPt(id).setFixY(stringToBoolean(b[4]));
                        } else {
                            loadingOK = false;
                            break;
                        }

                        //1.4 Kräfte einlesen
                        if ( isNumeric(b[5]) && isNumeric(b[6]) ) {
                            Structure.getPt(id).setForceX(Double.parseDouble(b[5]));
                            Structure.getPt(id).setForceY(Double.parseDouble(b[6]));
                        } else {
                            loadingOK = false;
                            break;
                        }
                    } else {
                        loadingOK = false;
                        break;
                    }
                }
            }
        }
        if ( !loadingOK ) {
            getMessage("Format Punkte fehlerhaft!", "Laden abgebrochen!");
            Structure.clearStruct();
            loadingOK2 = false;
        }
        
        //2.0 Substring auswählen: "SECTION"
        loadingOK = true;
        if ( loadingOK2 ) {  //Abbruch, falls laden Punkte fehlgeschlagen
            subStr = text.substring(text.indexOf("<<SECTION"), text.indexOf("<<END_SECTION"));
            lines = subStr.split("\n");
            //Erwarter Aufbau:
            //b0: ID    b1: E-Modul [N/mm2]    b2: Surface [mm2]
            for (String ln:lines){
                String b[];
                int id;
                
                //Zeilen, die mit "<<" beginnen nicht beachten
                if ( !ln.substring(0, 2).equals("<<") ) {
                    b = ln.split("\t");
                    
                    if ( b.length > 2 ) {
                        //2.1 ID lesen
                        //Kann der Inhalt als Zahl interpretiert werden?
                        if ( isNumeric(b[0]) ) {
                            id = (int) Double.parseDouble(b[0]);
                        } else {
                            loadingOK = false;
                            break;
                        }

                        //2.2 E-Modul und Fläche einlesen
                        if ( isNumeric(b[1]) && isNumeric(b[2]) ) {
                            double mod = Double.parseDouble(b[1]);
                            double surf = Double.parseDouble(b[2]);
                            Structure.getSections().add(new Double[] {surf, mod});
                            NewJDialog2.setContentTable();      //Tabelle mit Fläche und E-Modul laden
                        } else {
                            loadingOK = false;
                            break;
                        }
                    } else {
                        loadingOK = false;
                        break;
                    }
                }
            }
        }
        if ( !loadingOK ) {
            getMessage("Format Section fehlerhaft!", "Laden abgebrochen!");
            Structure.clearStruct();
            loadingOK2 = false;
        }
        
        //3.0 Substring auswählen: "BEAMS"
        loadingOK = true;
        if ( loadingOK2 ) {  //Abbruch, falls laden Punkte fehlgeschlagen
            subStr = text.substring(text.indexOf("<<BEAMS"), text.indexOf("<<END_BEAMS"));
            lines = subStr.split("\n");
            //Erwarter Aufbau:
            //b0: ID    b1: StartPt     b2: EndPt   b3: E-Modul [N/mm2]     b4: Surface [mm2]	
            for (String ln:lines){
                String b[];
                int id;
                
                //Zeilen, die mit "<<" beginnen nicht beachten
                if ( !ln.substring(0, 2).equals("<<") ) {
                    b = ln.split("\t");
                    
                    if ( b.length > 4 ) {
                        //3.1 ID lesen
                        //Kann der Inhalt als Zahl interpretiert werden?
                        if ( isNumeric(b[0]) ) {
                            id = (int) Double.parseDouble(b[0]);
                        } else {
                            loadingOK = false;
                            break;
                        }

                        //3.2 Stab einlesen
                        if ( isNumeric(b[1]) && isNumeric(b[2]) && isNumeric(b[3]) && isNumeric(b[4]) ) {
                            Points pt1 = Structure.getPt( (int) Double.parseDouble(b[1]) );     //Start Punkt
                            Points pt2 = Structure.getPt( (int) Double.parseDouble(b[2]) );     //End Punkt
                            double mod = Double.parseDouble(b[3]);      //E-Modul
                            double surf = Double.parseDouble(b[4]);     //Flächet
                            //Stab erstellen
                            Structure.addBeam(pt1, pt2, mod, surf);
                        } else {
                            loadingOK = false;
                            break;
                        }
                    } else {
                        loadingOK = false;
                        break;
                    }
                }
            }
        }
        if ( !loadingOK ) {
            getMessage("Format Beams fehlerhaft!", "Laden abgebrochen!");
            Structure.clearStruct();
            loadingOK2 = false;
        } 
        
        //Kein Fehler in einem Block: Struktur zeichnen
        if ( loadingOK2 ) {    //Laden OK, kein fehler angetroffen
            Main.myFrame.updateMyPlot();
        }
        
    }
    
    // Dialog zum Speichern der Datei (wird von der Methode "saveFile()" aufgerufen
    private static String getOpenFileDialog(java.awt.Frame f, String title, String defDir, String fileType) {
        java.awt.FileDialog fd = new java.awt.FileDialog(f, title, java.awt.FileDialog.LOAD);
        fd.setFile(fileType);
        fd.setDirectory(defDir);
        fd.setLocation(50, 50);
        fd.setVisible(true);
        String path = fd.getDirectory() + fd.getFile();
        return path;
    }
    
    
    /*
    //Testen, ob ein Pfad existiert. Gibt FALSE, wenn Pfad nicht existiert.
    private static boolean testPath(String str) {
        java.nio.file.Path pp;
        boolean isOK = false;
        
        pp = java.nio.file.Paths.get(str);
        
        //Existert der Pfad?
        if( java.nio.file.Files.exists(pp) ) {
            isOK = true;
        }

        return isOK;
    }
    
    //Objekt in Datei speichern (nicht verwendet)
    /*
    public static void saveObject(Structure obj) {
        ObjectOutputStream oos = null;
        FileOutputStream fos = null;
        try {
          fos = new FileOutputStream("D://Temp//myStruct.str");
          oos = new ObjectOutputStream(fos);
        }
        catch (IOException e) {
          e.printStackTrace();
        }
        finally {
          if (oos != null) try { oos.close(); } catch (IOException e) {}
          if (fos != null) try { fos.close(); } catch (IOException e) {}
        }
        
        try {
            oos.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
    
    //Nachricht anzeigen: Titel + Warning icon
    private static void getMessage(String text1, String text2){
        JOptionPane.showMessageDialog(null, text1, text2, JOptionPane.WARNING_MESSAGE);
    }
}