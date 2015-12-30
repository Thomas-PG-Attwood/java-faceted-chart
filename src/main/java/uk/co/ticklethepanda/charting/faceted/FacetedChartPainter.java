package uk.co.ticklethepanda.charting.faceted;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

public class FacetedChartPainter<G, X, Y> {

  public static final <G, X extends Number, Y extends Number> FacetedChartPainter<G, X, Y> createNumberAgainstNumberPlot(
      FacetedChartData<?, X, Y, G> data) {
    
    return new FacetedChartPainter<G, X, Y>(data,
        new Plotter.NumberPlotter(),
        new Plotter.NumberPlotter());
  }
  
  public static final <G, Y extends Number> FacetedChartPainter<G, LocalDate, Y> createNumberAgainstLocalDatePlot(
      FacetedChartData<?, LocalDate, Y, G> data) {
    
    return new FacetedChartPainter<G, LocalDate, Y>(data,
        new Plotter.LocalDatePlotter(),
        new Plotter.NumberPlotter());
  }
  
  public static final <G, Y extends Number> FacetedChartPainter<G, LocalTime, Y> createNumberAgainstLocalTimePlot(
      FacetedChartData<?, LocalTime, Y, G> data) {
    
    return new FacetedChartPainter<G, LocalTime, Y>(data,
        new Plotter.LocalTimePlotter(),
        new Plotter.NumberPlotter());
  }

  private static final int IMAGE_SIZE = 1000;

  public interface Axis {

    enum AxisType {
      X_AXIS, Y_AXIS
    }

    AxisType getType();

    public class X implements Axis {

      @Override
      public AxisType getType() {
        return AxisType.X_AXIS;
      }

      public int getHeight() {
        return 40;
      }

    }

    public class Y implements Axis {

      @Override
      public AxisType getType() {
        return AxisType.Y_AXIS;
      }

      public int getWidth() {
        return 40;
      }

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
        double xNorm = (point.getX() - xMin) / (xMax - xMin);
        double yNorm = (point.getY() - yMin) / (yMax - yMin);

        double x = startX + xNorm * scaleX - MARKER_SIZE / 2.0;
        double y = startY + yNorm * scaleY - MARKER_SIZE / 2.0;

        graphics.fill(new Rectangle2D.Double(x, y, MARKER_SIZE, MARKER_SIZE));
      }
      graphics.draw(drawArea);
    }
  }

  public static final double LEFT_MARGIN = 10;
  public static final double RIGHT_MARGIN = 10;

  public static final double TOP_MARGIN = 10;
  public static final double BOTTOM_MARGIN = 10;

  public static final double MARGIN_BETWEEN_FACADES = 10;

  public static final double MARKER_SIZE = 4;

  private BufferedImage image;
  private Graphics2D graphics;

  private Axis.X xAxis = new Axis.X();
  private Axis.Y yAxis = new Axis.Y();

  private FacetedChartData<?, X, Y, G> data;

  private double xMin;
  private double xMax;

  private double yMin;
  private double yMax;

  private Plotter<? super X> xConverter;
  private Plotter<? super Y> yConverter;

  public FacetedChartPainter(FacetedChartData<?, X, Y, G> data,
      Plotter<? super X> xConverter,
      Plotter<? super Y> yConverter) {
    this.data = data;

    this.xConverter = xConverter;
    this.yConverter = yConverter;

    calculateXRange();
    calculateYRange();
  }

  private void calculateYRange() {
    yMin = yConverter.getDouble(data.getMinY());
    yMax = yConverter.getDouble(data.getMaxY());

    double range = yMax - yMin;

    yMin -= range / 10.0;
    yMax += range / 10.0;
  }

  private void calculateXRange() {
    xMin = xConverter.getDouble(data.getMinX());
    xMax = xConverter.getDouble(data.getMaxX());

    double range = xMax - xMin;

    xMin -= range / 10.0;
    xMax += range / 10.0;
  }

  public BufferedImage draw() {
    initialiseImage();
    initialiseGraphics();

    int facadeNumber = 0;

    for (G group : data.getGroups()) {

      graphics.setColor(getColorForFacade(facadeNumber));

      Rectangle2D drawArea = getDrawAreaForFacade(facadeNumber);

      List<Point2D> points = convertDataToPoints(group);

      new InnerChartPainter(
          points,
          drawArea)
              .paint();

      facadeNumber++;
    }
    return image;
  }

  private void initialiseGraphics() {
    this.graphics = image.createGraphics();

    graphics.setBackground(Color.white);
    graphics.clearRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
  }

  private void initialiseImage() {
    this.image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
  }

  private Rectangle2D getDrawAreaForFacade(int i) {
    final double facadeWidth = getFacadeWidth();
    final double facadeHeight = getFacadeHeight();

    double x = getFacadeX();
    double y = getFacadeY(facadeHeight, i);

    Rectangle2D drawArea = new Rectangle2D.Double(x, y, facadeWidth, facadeHeight);
    return drawArea;
  }

  private double getFacadeY(final double facadeHeight, int i) {
    return TOP_MARGIN
        + MARGIN_BETWEEN_FACADES * i
        + facadeHeight * i;
  }

  private double getFacadeX() {
    return LEFT_MARGIN + yAxis.getWidth();
  }

  private Color getColorForFacade(int i) {
    return Color.getHSBColor(
        1.0f / (float) data.getGroups().size() * (float) i,
        0.8f,
        0.5f);
  }

  private double getFacadeWidth() {
    return image.getWidth()
        - LEFT_MARGIN
        - RIGHT_MARGIN
        - yAxis.getWidth();
  }

  private double getFacadeHeight() {
    return (image.getHeight()
        - TOP_MARGIN - BOTTOM_MARGIN
        - MARGIN_BETWEEN_FACADES * (double) (data.getGroups().size() - 1)
        - (double) xAxis.getHeight())
        / (double) data.getGroups().size();
  }

  private List<Point2D> convertDataToPoints(final G group) {
    List<Point2D> points = new ArrayList<Point2D>();

    int numPoints = data.size(group);

    List<X> xValues = data.getXValues(group);
    List<Y> yValues = data.getYValues(group);

    for (int j = 0; j < numPoints; j++) {
      points.add(new Point2D.Double(
          xConverter.getDouble(xValues.get(j)),
          yConverter.getDouble(yValues.get(j))));
    }
    return points;
  }

}
