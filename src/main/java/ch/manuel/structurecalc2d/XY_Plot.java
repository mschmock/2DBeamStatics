//Autor: Manuel Schmocker
//Datum: 25.02.2014

package ch.manuel.structurecalc2d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class XY_Plot {   
//Namen
    private static final String NAME_SERIE1 = "Knoten";
    private static final String NAME_SERIE2 = "Deformation";
//Farben
    private static final Color color1 = Color.BLUE;                //Farbe für Randbedinung: Fix
    private static final Color color2 = Color.BLACK;               //Farbe für Kraft
    private static final Color color3 = new Color (140, 70, 20);   //Farbe für Punkte "ohne Deformation"
//Membervariablen
    private static boolean drawAnnotPts;
    private static boolean drawAnnotBeams;
    private final ChartPanel panel;
    private static XYPlot plot;
    private static JFreeChart chart;
    private static XYSeries series1;        //Punkte Struktur
    private static XYSeries series2;        //Punkte Struktur nach Deformation
    private static XYSeriesCollection dataset;
    private static org.jfree.chart.axis.ValueAxis xAxis, yAxis;
    private static List<XYTextAnnotation> listAnnotPts;
    private static List<XYTextAnnotation> listAnnotBeam;
    private static List<XYLineAnnotation> listAnnotBound;
    private static List<XYPointerAnnotation> listAnnotForce;
    private static List<XYSeries> listBeams;
    private static List<XYSeries> listBeamsDef;
    private static XYLineAndShapeRenderer renderer;
    
    //Konstruktor
    public XY_Plot(java.awt.Dimension dmsn) {
        listAnnotPts = new ArrayList<>();           //Liste Nr. Punkte initialisieren
        listAnnotBeam = new ArrayList<>();          //Liste Nr. Stäbe initialisieren
        listAnnotBound = new ArrayList<>();         //Liste Randbedingungen initialisieren
        listAnnotForce = new ArrayList<>();         //Liste Kräfte
        listBeams = new ArrayList<>();
        listBeamsDef = new ArrayList<>();
        
        series1 = new XYSeries(NAME_SERIE1);        //Datensatz für Knoten erzeugen
        series2 = new XYSeries(NAME_SERIE2);        //Datensatz für Knoten "Deformation"
        dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        chart = createChart(dataset);
        
        panel = new MyChartPanel(chart, this);
        panel.setPreferredSize(dmsn);
 
        drawAnnotPts = true;
        drawAnnotBeams = true;
    }
    
    
    private static JFreeChart createChart(XYDataset dataset) {
        // create the chart...
        chart = ChartFactory.createXYLineChart(
        "Statik",               // chart title (nicht sichtbar)
        null /*"x [m]"*/,       // x axis label
        null /*"y [m]"*/,       // y axis label
        dataset,                // data
        PlotOrientation.VERTICAL,
        true,                   // include legend
        true,                   // tooltips
        false                   // urls
        );
        // SOME OPTIONAL CUSTOMISATION OF THE CHART...
        // get a reference to the plot for further customisation...
        plot = (XYPlot) chart.getPlot();
        renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        xAxis = plot.getDomainAxis();
        yAxis = plot.getRangeAxis();
        //xAxis.setAutoRange(false);
        //yAxis.setAutoRange(false);
        
        setLookChart(chart);
        formatChart();
        
        // change the auto tick unit selection to integer units only...
        //NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        //rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // OPTIONAL CUSTOMISATION COMPLETED.
                
        return chart;
    }
    
    //Change apperence of chart
    private static void setLookChart(JFreeChart cha) {
        cha.setBackgroundPaint( new Color(230, 230, 230) );
        cha.removeLegend();
        cha.setTitle((String) null);
    }
    
    //Panel zurückgeben, z.B. um in ein Fenster zu integrieren
    public ChartPanel getChartPanel(){
        return panel;
    }
    
    //Formatierung
    private static void formatChart() {
        //Zeichenfläche formatieren "plot"
        plot.setBackgroundPaint(Color.WHITE);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        //Serien formatieren
        int a = dataset.getSeriesIndex(NAME_SERIE1);    //Knoten "ohne Deformation"
        int b = dataset.getSeriesIndex(NAME_SERIE2);    //Knoten "mit Deformation"
        //Form für Punkte festlegen
        double diam = 4;
        java.awt.Shape sh = new java.awt.geom.Ellipse2D.Double(-diam/2, -diam/2, diam, diam);
        renderer.setSeriesShape(a, sh);
        renderer.setSeriesShape(b, sh);
        renderer.setSeriesLinesVisible(a, false);
        renderer.setSeriesLinesVisible(b, false);
        renderer.setSeriesShapesFilled(a, true);
        renderer.setSeriesShapesFilled(b, true);
        renderer.setSeriesShapesVisible(a, true);
        renderer.setSeriesShapesVisible(b, true);
        renderer.setSeriesPaint(a, color3);
        renderer.setSeriesPaint(b, color1);
    }

    //Punkte zeichnen
    public void drawPts() {
       int nb = Structure.getNbPts();

       series1.clear();
       series2.clear();
       for (int i = 0; i < nb; i++){
            series1.add(Structure.getPt(i).getValX(), Structure.getPt(i).getValY());
        }
       
       //Deformation, falls Berechnung durchgeführt
       if (Structure.getCalculated()) {
           for (int i = 0; i < nb; i++){
                series2.add(Structure.getPt(i).getDisplScaledX(), Structure.getPt(i).getDisplScaledY());
            
            }
        }
        setAspectRatio();       //Seitenverhältniss festlegen
        setAnnotations();       //Punkte mit Zahlen beschriften (Annotations aktualisieren)
    }
    
    //Stäbe zeichnen als XYSeries
    public void drawBeams() {
        double ptX0, ptY0, ptX1, ptY1;
        int aa, bb, cc;
        //Series zuerst löschen, bevor die Stäbe wieder gezeichnet werden können
        aa = listBeams.size();
        if (aa > 0) {
            for (int i = 0; i < aa; i++) {
                dataset.removeSeries(listBeams.get(i));
            }
        }
        bb = listBeamsDef.size();
        if (bb > 0) {
            for (int i = 0; i < bb; i++) {
                dataset.removeSeries(listBeamsDef.get(i));
            }
        }
        listBeams.clear();
        listBeamsDef.clear();
        
        int nb = Structure.getNbBeams();
        java.awt.Stroke strokeBeam = new java.awt.BasicStroke( 1.0f );
        for (int i = 0; i < nb; i++) {
            listBeams.add(new XYSeries("beam" + (i+1)));
            dataset.addSeries(listBeams.get(i));
            cc = dataset.getSeriesIndex("beam" + (i+1));
            renderer.setSeriesPaint(cc, Color.DARK_GRAY);
            renderer.setSeriesStroke(cc, strokeBeam);
                
            ptX0 = Structure.getBeam(i).getStartPt().getValX();
            ptY0 = Structure.getBeam(i).getStartPt().getValY();
            ptX1 = Structure.getBeam(i).getEndPt().getValX();
            ptY1 = Structure.getBeam(i).getEndPt().getValY();
            
            listBeams.get(i).add(ptX0, ptY0);
            listBeams.get(i).add(ptX1, ptY1);
        }
        //Nr. für Stäbe hinzufügen (Annotation aktualisieren)
        setAnnotations();
        
        //Stäbe Deformation zeichnen, falls Berechnung OK
        float maxStrokeWidth = 10.0f;                     //Max. Dicke der Linie
        float minStrokeWidth = 1.0f;
        double maxF = Structure.getMaxBeamForce();  //Grösste Kraft (Wert absolut)
        if (Structure.getCalculated()) {
            for (int i = 0; i < nb; i++) {
                listBeamsDef.add(new XYSeries("beamDef" + (i+1)));
                dataset.addSeries(listBeamsDef.get(i));
                cc = dataset.getSeriesIndex("beamDef" + (i+1));
                
                //Druck: BLAU, Zug: RED
                if (Structure.getBeam(i).getForce() < 0 ) {
                    renderer.setSeriesPaint(cc, Color.BLUE);
                } else {
                    renderer.setSeriesPaint(cc, Color.RED);
                }
                //Stärke Linie wird proportional zu maxF Skaliert: width
                float width = (float) (Math.abs(Structure.getBeam(i).getForce())/maxF*maxStrokeWidth);
                if ( width < minStrokeWidth ) width = (float) 1.0;
                java.awt.Stroke stroke1 = new java.awt.BasicStroke( width );
                renderer.setSeriesStroke(cc, stroke1);

                ptX0 = Structure.getBeam(i).getStartPt().getDisplScaledX();
                ptY0 = Structure.getBeam(i).getStartPt().getDisplScaledY();
                ptX1 = Structure.getBeam(i).getEndPt().getDisplScaledX();
                ptY1 = Structure.getBeam(i).getEndPt().getDisplScaledY();

                listBeamsDef.get(i).add(ptX0, ptY0);
                listBeamsDef.get(i).add(ptX1, ptY1);
            }
        }
    }
    
    //Seitenverhältnis festlegen
    public void setAspectRatio() {
        //Rand festlegen: Überstand Anteil
        double margin = 0.05;

        double ratio = (double) panel.getWidth() / (double) panel.getHeight();
        double idealXRange, idealYRange;
        double yLower = series1.getMinY();
        double yUpper = series1.getMaxY();
        double xLower = series1.getMinX();
        double xUpper = series1.getMaxX();
        double dy = yUpper - yLower;
        double dx = xUpper - xLower;
        double ratio2 = dx / dy;

        if (ratio < ratio2) {                   //x-Achse ist massgebend, da sie im Verhältnis grösser ist
            idealXRange = (2*margin+1.0) * dx;
            idealYRange = idealXRange / ratio; 
        } else {                                //y-Achse ist massgebend, da sie im Verhältnis grösser ist
            idealYRange = (2*margin+1.0) * dy;
            idealXRange = idealYRange * ratio;
        }
        
        //Grenzen für X-Achse festlegen + AutoRange
        xUpper = xLower+dx/2 + idealXRange/2;
        xLower = xLower+dx/2 - idealXRange/2;
        xAxis.setLowerBound(xLower);
        xAxis.setUpperBound(xUpper);
        //xAxis.setDefaultAutoRange(new org.jfree.data.Range(xLower, xUpper));
        //Grenzen für X-Achse festlegen + AutoRange
        yUpper = yLower+dy/2 + idealYRange/2;
        xLower = yLower+dy/2 - idealYRange/2;
        yAxis.setLowerBound(xLower);
        yAxis.setUpperBound(yUpper);
        //yAxis.setDefaultAutoRange(new org.jfree.data.Range(yLower, yUpper));
    }
    
    //Beschriftung hinzufügen
    //Randbedingungen hinzufügen
    //  1.1 ID Punkte
    //  1.2 Randbedingungen (Fixpunkte)
    //  1.3 Kräfte
    //  2.1 ID Beams
    public void setAnnotations() {
        plot.clearAnnotations();
        listAnnotPts.clear();       //  1.1
        listAnnotBound.clear();     //  1.2
        listAnnotForce.clear();     //  1.3
        listAnnotBeam.clear();      //  2.1

        double posX, posY;      //Position für Annotation
        double offsetX = (xAxis.getUpperBound() - xAxis.getLowerBound())*0.02;
        double offsetY = (yAxis.getUpperBound() - yAxis.getLowerBound())*0.02;
        String txt;
        
        
        //double arrowLen = 0.10; //Länge Pfeil. 0.0 bis 1.0; 1.0 entspricht gesamte Höhe des Plots
        //double scaleFact;       //Faktor für Skalierung Pfeillänge 
        
        //Länge für Zeichnung Pfeil (Kraft) ermitteln -> lengthArrow
        /*double maxF = 0.0;
        double fx, fy;
        for(int i = 0; i < Structure.getNbPts(); i++){
            fx = Math.abs(Structure.getPt(i).getForceX());
            fy = Math.abs(Structure.getPt(i).getForceY());
            if ( fx > maxF ) maxF = fx;
            if ( fy > maxF ) maxF = fy;
        }
        scaleFact = arrowLen * ( yAxis.getUpperBound() - yAxis.getLowerBound() ) / maxF;
        */
        
        //Beschriftungen Punkte, Randbedingungen und Kräfte hinzufügen
        for(int i = 0; i < Structure.getNbPts(); i++){
            posX = Structure.getPt(i).getValX();
            posY = Structure.getPt(i).getValY();
            
            //1.1 Beschriftungen Punkte
            if( drawAnnotPts ) {
                txt = Integer.toString(i+1);
                listAnnotPts.add(new XYTextAnnotation(txt, posX + offsetX, posY + offsetY));
                listAnnotPts.get(i).setPaint(color3);
                plot.addAnnotation(listAnnotPts.get(i));
            }
            
            //1.2 Randbedingungen
            double len = (yAxis.getUpperBound() - yAxis.getLowerBound()) * 0.02;    //Länge der Markierung
            java.awt.Stroke stroke1 = new java.awt.BasicStroke(2.0f);
            if ( Structure.getPt(i).getFixX() ){
                listAnnotBound.add(new XYLineAnnotation(posX, posY, posX + len, posY, stroke1, color1));
                plot.addAnnotation(listAnnotBound.get( listAnnotBound.size()-1 ));
            }
            if ( Structure.getPt(i).getFixY() ){
                listAnnotBound.add(new XYLineAnnotation(posX, posY, posX, posY - len, stroke1, color1));
                plot.addAnnotation(listAnnotBound.get( listAnnotBound.size()-1 ));
            }
            
            //1.3 Kraft hinzufügen
            java.awt.Stroke stroke2 = new java.awt.BasicStroke(1.0f);
            double force = Structure.getPt(i).getForceX();
            double angle = Math.PI;
            String label = Double.toString(Math.abs(Math.round(force)));
            //double len = force *  scaleFact;
            if ( force != 0.0 ) {
                if ( force < 0.0 ) angle = 0.0;
                listAnnotForce.add(new XYPointerAnnotation(label, posX, posY, angle));
                //listAnnotForce.get( listAnnotForce.size()-1 ).setArrowWidth(len);
                listAnnotForce.get( listAnnotForce.size()-1 ).setArrowStroke(stroke2);
                listAnnotForce.get( listAnnotForce.size()-1 ).setArrowPaint(color2);
                plot.addAnnotation(listAnnotForce.get( listAnnotForce.size()-1 ));
            }
            force = Structure.getPt(i).getForceY();
            angle = Math.PI / 2;
            label = Double.toString(Math.abs(Math.round(force)));
            //len = force *  scaleFact;
            if ( force != 0.0 ) {
                if ( force < 0.0 ) angle = 3*Math.PI / 2;
                listAnnotForce.add(new XYPointerAnnotation(label, posX, posY, angle));
                //listAnnotForce.get( listAnnotForce.size()-1 ).setArrowWidth(len);
                listAnnotForce.get( listAnnotForce.size()-1 ).setArrowStroke(stroke2);
                listAnnotForce.get( listAnnotForce.size()-1 ).setArrowPaint(color2);
                plot.addAnnotation(listAnnotForce.get( listAnnotForce.size()-1 ));
            }
        }
        
        //2.1 ID für Stäbe hinzufügen 
        if ( drawAnnotBeams ) {
            for (int i = 0; i < Structure.getNbBeams(); i++) {
                //Stabnummer hinzufügen
                posX = Structure.getBeam(i).getMidPt().getValX() + offsetX;
                posY = Structure.getBeam(i).getMidPt().getValY() + offsetY;

                txt = Integer.toString(i+1);
                listAnnotBeam.add(new XYTextAnnotation(txt, posX, posY));
                listAnnotBeam.get(i).setPaint(Color.DARK_GRAY);
                plot.addAnnotation(listAnnotBeam.get(i));
            }
        }
    }
    
    //Beschriftungen aktualisieren:
    //updateAnnotationPts: ID für Punkte darstellen ja/nein
    //updateAnnotationBeams: ID für Stäbe darstellen ja/nein
    public void updateAnnotation(boolean updateAnnotationPts, boolean updateAnnotationBeams) {
        drawAnnotPts = updateAnnotationPts;
        drawAnnotBeams = updateAnnotationBeams;
        System.out.println(drawAnnotPts + "\t" + drawAnnotBeams);
        setAnnotations();
    }
        
    //TEST
    public void clearAll() {
        renderer.removeAnnotations();
    }
}

