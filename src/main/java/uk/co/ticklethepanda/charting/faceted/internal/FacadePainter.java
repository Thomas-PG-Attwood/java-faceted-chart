package uk.co.ticklethepanda.charting.faceted.internal;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import uk.co.ticklethepanda.charting.faceted.Chart;

/**
 * Inner chart with an axis on the left hand side
 * 
 * @author panda
 *
 */
public class FacadePainter {

  private final Rectangle2D facetDrawArea;
  private final List<Point2D> points;
  private Chart chart;

  public FacadePainter(Chart chart, List<Point2D> points, Rectangle2D facetArea) {
    this.points = points;
    this.facetDrawArea = facetArea;
    this.chart = chart;
  }

  public void paint(Graphics2D graphics) {
    
    for (Point2D point : points) {
      final double xNorm = (point.getX() - chart.getMarginedDataBounds().getMinX())
          / (chart.getMarginedDataBounds().getWidth());

      final double yNorm = (point.getY() - chart.getMarginedDataBounds().getMinY())
          / (chart.getMarginedDataBounds().getHeight());

      double innerChartX = xNorm * facetDrawArea.getWidth();
      
      //must be -yNorm because y=0 is at the top.
      double innerChartY = facetDrawArea.getHeight() - yNorm * facetDrawArea.getHeight();

      double x = facetDrawArea.getX() + innerChartX - chart.getStyleManager().getMarkerOffset();
      double y = facetDrawArea.getY() + innerChartY - chart.getStyleManager().getMarkerOffset();

      graphics.fill(
          new Rectangle2D.Double(
              x,
              y,
              chart.getStyleManager().getMarkerSize(),
              chart.getStyleManager().getMarkerSize()));
    }
    graphics.draw(facetDrawArea);
  }
}