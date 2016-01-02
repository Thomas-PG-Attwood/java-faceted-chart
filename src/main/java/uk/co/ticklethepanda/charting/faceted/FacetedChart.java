package uk.co.ticklethepanda.charting.faceted;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

import uk.co.ticklethepanda.charting.faceted.internal.CoordinateConverter;
import uk.co.ticklethepanda.charting.faceted.internal.PointConverter;

import java.time.LocalDate;
import java.time.LocalTime;

public class FacetedChart<G, X, Y> {

  enum AxisType {
    X_AXIS, Y_AXIS
  }

  public abstract class Axis {

    abstract AxisType getType();

    abstract void draw();

  }

  public class XAxis extends Axis {

    public double getHeight() {
      return StyleManager.X_AXIS_HEIGHT;
    }

    @Override
    public AxisType getType() {
      return AxisType.X_AXIS;
    }

    @Override
    public void draw() {

    }

  }

  public class YAxis extends Axis {

    @Override
    public AxisType getType() {
      return AxisType.Y_AXIS;
    }

    public double getWidth() {
      return StyleManager.Y_AXIS_WIDTH;
    }

    @Override
    public void draw() {
      for (int i = 0; i < data.getGroups().size(); i++) {
        StyleManager facadeCalculator = new StyleManager(i);

        graphics.setColor(facadeCalculator.getColor());

        double x = StyleManager.LEFT_MARGIN;
        double y = facadeCalculator.getFacadeY();
        double width = this.getWidth();
        double height = facadeCalculator.getFacadeHeight();

        Rectangle2D drawBounds = new Rectangle2D.Double(x, y, width, height);

        int numberOfTicks = (int) Math.floor(drawBounds.getHeight() / StyleManager.MIN_PIXELS_BETWEEN_TICKS);

        double pixelsBetweenTicks = drawBounds.getHeight() / (double) numberOfTicks;

        for (int j = 0; j <= numberOfTicks; j++) {

          double xStart = drawBounds.getMaxX() - StyleManager.MAX_TICK_LENGTH;
          double yStart = drawBounds.getY() + (double) j * pixelsBetweenTicks;

          double xEnd = drawBounds.getMaxX();
          double yEnd = drawBounds.getY() + (double) j * pixelsBetweenTicks;

          Line2D line = new Line2D.Double(xStart, yStart, xEnd, yEnd);
          graphics.draw(line);
        }

      }
    }
  }

  public class StyleManager {

    public static final int IMAGE_SIZE = 1000;

    public static final double FACADE_INNER_PERCENTGE_MARGIN = 0.1;

    public static final double CATEGORY_WIDTH = 10.0;

    public static final double LEFT_MARGIN = 10.0;
    public static final double RIGHT_MARGIN = 10.0;
    public static final double TOP_MARGIN = 50.0;
    public static final double BOTTOM_MARGIN = 10.0;

    public static final double MARGIN_BETWEEN_FACADES = 10.0;

    public static final double MAX_TICK_LENGTH = 5.0;
    public static final double MIN_PIXELS_BETWEEN_TICKS = 6.0;

    public static final double X_AXIS_HEIGHT = 40.0 + MAX_TICK_LENGTH;
    public static final double Y_AXIS_WIDTH = 40.0 + MAX_TICK_LENGTH;

    public static final double MARKER_SIZE = 2.0;
    public static final double MARKER_OFFSET = MARKER_SIZE / 2.0;

    private int currentFacadeNumber;

    public StyleManager(int facadeIndex) {
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
          - StyleManager.TOP_MARGIN - StyleManager.BOTTOM_MARGIN
          - StyleManager.MARGIN_BETWEEN_FACADES * (double) (data.getGroups().size() - 1)
          - (double) xAxis.getHeight())
          / (double) data.getGroups().size();
    }

    private double getFacadeWidth() {
      return image.getWidth()
          - StyleManager.LEFT_MARGIN
          - StyleManager.RIGHT_MARGIN
          - yAxis.getWidth();
    }

    private double getFacadeX() {
      return StyleManager.LEFT_MARGIN + yAxis.getWidth();
    }

    private double getFacadeY() {
      return StyleManager.TOP_MARGIN
          + StyleManager.MARGIN_BETWEEN_FACADES * currentFacadeNumber
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

    private final Rectangle2D facetDrawArea;
    private final List<Point2D> points;

    private InnerChartPainter(List<Point2D> points, Rectangle2D facetArea) {
      this.points = points;
      this.facetDrawArea = facetArea;
    }

    public void paint() {
      for (Point2D point : points) {
        final double xNorm = (point.getX() - dataBoundsWithBorder.getMinX())
            / (dataBoundsWithBorder.getWidth());

        final double yNorm = (point.getY() - dataBoundsWithBorder.getMinY())
            / (dataBoundsWithBorder.getHeight());

        double innerChartX = xNorm * facetDrawArea.getWidth();
        double innerChartY = yNorm * facetDrawArea.getHeight();

        double x = facetDrawArea.getX() + innerChartX - StyleManager.MARKER_OFFSET;
        double y = facetDrawArea.getY() + innerChartY - StyleManager.MARKER_OFFSET;

        graphics.fill(new Rectangle2D.Double(x, y, StyleManager.MARKER_SIZE, StyleManager.MARKER_SIZE));
      }
      graphics.draw(facetDrawArea);
    }
  }

  public static final <G, Y extends Number> FacetedChart<G, LocalDate, Y> createNumberAgainstLocalDatePlot(
      FacetedChartData<?, LocalDate, Y, G> data) {

    PointConverter<LocalDate, Y> converter = new PointConverter<LocalDate, Y>(
        new CoordinateConverter.LocalDateConverter(),
        new CoordinateConverter.NumberConverter());

    return new FacetedChart<G, LocalDate, Y>(data, converter);
  }

  public static final <G, Y extends Number> FacetedChart<G, LocalTime, Y> createNumberAgainstLocalTime(
      FacetedChartData<?, LocalTime, Y, G> data) {

    PointConverter<LocalTime, Y> converter = new PointConverter<LocalTime, Y>(
        new CoordinateConverter.LocalTimeConverter(),
        new CoordinateConverter.NumberConverter());

    return new FacetedChart<G, LocalTime, Y>(data, converter);
  }

  public static final <G, X extends Number, Y extends Number> FacetedChart<G, X, Y> createNumberAgainstNumberPlot(
      FacetedChartData<?, X, Y, G> data) {

    PointConverter<X, Y> converter = new PointConverter<X, Y>(
        new CoordinateConverter.NumberConverter(),
        new CoordinateConverter.NumberConverter());

    return new FacetedChart<G, X, Y>(data,
        converter);
  }

  public static final <G, X extends Object, Y extends Number> FacetedChart<G, X, Y> createNumberAgainstCategoryPlot(
      FacetedChartData<?, X, Y, G> data) {

    PointConverter<X, Y> converter = new PointConverter<X, Y>(
        new CoordinateConverter.CategoryConverter<X>(),
        new CoordinateConverter.NumberConverter());

    return new FacetedChart<G, X, Y>(data,
        converter);
  }

  private BufferedImage image;
  private Graphics2D graphics;

  private XAxis xAxis;
  private YAxis yAxis;

  private FacetedChartData<?, X, Y, G> data;

  private Rectangle2D dataBounds;
  private Rectangle2D dataBoundsWithBorder;

  private PointConverter<X, Y> pointConverter;

  public FacetedChart(FacetedChartData<?, X, Y, G> data,
      PointConverter<X, Y> pointConverter) {

    this.data = data;
    this.pointConverter = pointConverter;

    this.xAxis = new XAxis();
    this.yAxis = new YAxis();

    this.dataBounds = getDataBounds();
    this.dataBoundsWithBorder = getDataBoundsWithBorder();
  }

  public BufferedImage draw() {
    initialiseImage();
    initialiseGraphics();

    drawFacades();

    xAxis.draw();
    yAxis.draw();

    return image;
  }

  private void drawFacades() {
    int facadeIndex = 0;

    for (G group : data.getGroups()) {

      StyleManager facadeCalculator = new StyleManager(facadeIndex);

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

    double width = xMax - xMin;

    double height = yMax - yMin;

    return new Rectangle2D.Double(xMin, yMin, width, height);
  }

  private Rectangle2D getDataBoundsWithBorder() {
    double width = dataBounds.getWidth();
    double height = dataBounds.getHeight();
    double x = dataBounds.getMinX();
    double y = dataBounds.getMinY();

    double widthMargin = StyleManager.FACADE_INNER_PERCENTGE_MARGIN * width;
    double heightMargin = StyleManager.FACADE_INNER_PERCENTGE_MARGIN * height;

    double newWidth = width + widthMargin * 2.0;
    double newHeight = height + heightMargin * 2.0;

    double newX = x - widthMargin;
    double newY = y - heightMargin;

    return new Rectangle2D.Double(newX, newY, newWidth, newHeight);
  }

  private void initialiseGraphics() {
    this.graphics = image.createGraphics();

    graphics.setBackground(Color.white);
    graphics.clearRect(0, 0, StyleManager.IMAGE_SIZE, StyleManager.IMAGE_SIZE);
  }

  private void initialiseImage() {
    this.image = new BufferedImage(StyleManager.IMAGE_SIZE, StyleManager.IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
  }

}
