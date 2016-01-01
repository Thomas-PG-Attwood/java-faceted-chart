package uk.co.ticklethepanda.charting.faceted;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

import uk.co.ticklethepanda.charting.faceted.internal.Axis;
import uk.co.ticklethepanda.charting.faceted.internal.CoordinateConverter;
import uk.co.ticklethepanda.charting.faceted.internal.PointConverter;

import java.time.LocalDate;
import java.time.LocalTime;

public class FacetedChartPainter<G, X, Y> {
  
  public class Facade {
    
    private int currentFacadeNumber;

    public Facade(int facadeIndex) {
      this.currentFacadeNumber = facadeIndex;
    }

    private Color getColor() {
      return Color.getHSBColor(
          1.0f / (float) data.getGroups().size() * (float) currentFacadeNumber,
          0.8f,
          0.5f);
    }

    private Rectangle2D getDrawArea() {
      return new Rectangle2D.Double(
          getFacadeX(),
          getFacadeY(),
          getFacadeWidth(),
          getFacadeHeight());
    }

    private double getFacadeHeight() {
      return (image.getHeight()
          - TOP_MARGIN - BOTTOM_MARGIN
          - MARGIN_BETWEEN_FACADES * (double) (data.getGroups().size() - 1)
          - (double) xAxis.getHeight())
          / (double) data.getGroups().size();
    }

    private double getFacadeWidth() {
      return image.getWidth()
          - LEFT_MARGIN
          - RIGHT_MARGIN
          - yAxis.getWidth();
    }

    private double getFacadeX() {
      return LEFT_MARGIN + yAxis.getWidth();
    }

    private double getFacadeY() {
      return TOP_MARGIN
          + MARGIN_BETWEEN_FACADES * currentFacadeNumber
          + getFacadeHeight() * currentFacadeNumber;
    }
  }

  /**
   * Inner chart with an axis on the left hand side
   * 
   * @author panda
   *
   */
  public class InnerChartPainter {

    private Rectangle2D drawArea;
    private List<Point2D> points;

    private InnerChartPainter(List<Point2D> points, Rectangle2D drawArea) {
      this.points = points;
      this.drawArea = drawArea;
    }

    public void paint() {
      double startX = drawArea.getX();
      double startY = drawArea.getY();

      double scaleX = drawArea.getWidth();
      double scaleY = drawArea.getHeight();

      for (Point2D point : points) {
        double xNorm = (point.getX() - dataBounds.getMinX())
            / (dataBounds.getMaxX() - dataBounds.getMinX());
        
        double yNorm = (point.getY() - dataBounds.getMinY())
            / (dataBounds.getMaxY() - dataBounds.getMinY());

        double x = startX + xNorm * scaleX - MARKER_SIZE / 2.0;
        double y = startY + yNorm * scaleY - MARKER_SIZE / 2.0;

        graphics.fill(new Rectangle2D.Double(x, y, MARKER_SIZE, MARKER_SIZE));
      }
      graphics.draw(drawArea);
    }
  }

  private static final int IMAGE_SIZE = 1000;

  private static final double FACADE_PERCENTAGE_MARGIN = 0.1;
  
  private static final double LEFT_MARGIN = 10;

  private static final double RIGHT_MARGIN = 10;

  private static final double TOP_MARGIN = 10;

  private static final double BOTTOM_MARGIN = 10;
  
  private static final double MARGIN_BETWEEN_FACADES = 10;

  private static final double MARKER_SIZE = 2;
  
  public static final <G, Y extends Number> FacetedChartPainter<G, LocalDate, Y> createNumberAgainstLocalDatePlot(
      FacetedChartData<?, LocalDate, Y, G> data) {

    PointConverter<LocalDate, Y> converter = new PointConverter<LocalDate, Y>(
        new CoordinateConverter.LocalDatePlotter(),
        new CoordinateConverter.NumberPlotter());

    return new FacetedChartPainter<G, LocalDate, Y>(data, converter);
  }

  public static final <G, Y extends Number> FacetedChartPainter<G, LocalTime, Y> createNumberAgainstLocalTimePlot(
      FacetedChartData<?, LocalTime, Y, G> data) {

    PointConverter<LocalTime, Y> converter = new PointConverter<LocalTime, Y>(
        new CoordinateConverter.LocalTimePlotter(),
        new CoordinateConverter.NumberPlotter());

    return new FacetedChartPainter<G, LocalTime, Y>(data, converter);
  }

  public static final <G, X extends Number, Y extends Number> FacetedChartPainter<G, X, Y> createNumberAgainstNumberPlot(
      FacetedChartData<?, X, Y, G> data) {

    PointConverter<X, Y> converter = new PointConverter<X, Y>(
        new CoordinateConverter.NumberPlotter(),
        new CoordinateConverter.NumberPlotter());

    return new FacetedChartPainter<G, X, Y>(data,
        converter);
  }

  private BufferedImage image;
  private Graphics2D graphics;

  private Axis.X xAxis = new Axis.X();
  private Axis.Y yAxis = new Axis.Y();

  private FacetedChartData<?, X, Y, G> data;

  private Rectangle2D dataBounds;

  private PointConverter<X, Y> pointConverter;

  public FacetedChartPainter(FacetedChartData<?, X, Y, G> data,
      PointConverter<X, Y> pointConverter) {
    this.data = data;
    this.pointConverter = pointConverter;

    this.dataBounds = getDataBounds();
  }
  
  public BufferedImage draw() {
    initialiseImage();
    initialiseGraphics();

    drawFacades();

    return image;
  }

  private void drawFacades() {

    int facadeIndex = 0;
    
    for (G group : data.getGroups()) {
      
      Facade facadeCalculator = new Facade(facadeIndex);
      
      graphics.setColor(facadeCalculator.getColor());

      new InnerChartPainter(
          pointConverter.convertDataGroup(data, group),
          facadeCalculator.getDrawArea())
              .paint();

      facadeIndex++;
    }
  }

  private Rectangle2D getDataBounds() {
    
    double xMin = pointConverter.convertX(data.getMinX());
    double xMax = pointConverter.convertX(data.getMaxX());
    
    double yMin = pointConverter.convertY(data.getMinY());
    double yMax = pointConverter.convertY(data.getMaxY());
    
    double xRange = xMax - xMin;
    double yRange = yMax - yMin;
    
    xMin -= xRange * FACADE_PERCENTAGE_MARGIN;
    xMax += xRange * FACADE_PERCENTAGE_MARGIN;

    yMin -= yRange * FACADE_PERCENTAGE_MARGIN;
    yMax += yRange * FACADE_PERCENTAGE_MARGIN;
    
    double width = xMax - xMin;
    
    double height = yMax - yMin;

    return new Rectangle2D.Double(xMin, yMin, width, height);
  }

  private void initialiseGraphics() {
    this.graphics = image.createGraphics();

    graphics.setBackground(Color.white);
    graphics.clearRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
  }

  private void initialiseImage() {
    this.image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
  }

}
