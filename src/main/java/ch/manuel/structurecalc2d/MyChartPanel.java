//Autor: Manuel Schmocker
//Datum: 24.02.2014

package ch.manuel.structurecalc2d;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;


public class MyChartPanel extends ChartPanel {
    private final XY_Plot plot;
    
    public MyChartPanel(JFreeChart chart, XY_Plot plot) {
            super(chart, true);
            this.plot = plot;
        }
    
    @Override
    public void restoreAutoBounds() {
        /*super.restoreAutoDomainBounds();
        // Set your desired range
        myDomainAxis.setRange(desiredRange);
        myRangeAxis.setRange(desiredRange);*/
        plot.setAspectRatio();
    }
    
    @Override
    public void restoreAutoRangeBounds() {
        plot.setAspectRatio();
    }
    
    @Override
    public void restoreAutoDomainBounds() {
        plot.setAspectRatio();
    }
    
    @Override
    public void zoom(java.awt.geom.Rectangle2D selection) {
        super.zoom(selection);
        plot.setAnnotations();
    }
}
