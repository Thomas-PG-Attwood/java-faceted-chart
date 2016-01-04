package uk.co.ticklethepanda.charting.faceted;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import uk.co.ticklethepanda.charting.faceted.internal.Axis;
import uk.co.ticklethepanda.charting.faceted.internal.AxisType;
import uk.co.ticklethepanda.charting.faceted.internal.FacadePainter;
import uk.co.ticklethepanda.charting.faceted.internal.PointConverter;

public class FacetedChart<G, X, Y> implements Chart {

  public class XAxis implements Axis<G, X, Y> {

    @Override
    public void draw() {

    }

    @Override
    public AxisType getType() {
      return AxisType.X_AXIS;
    }

  }

  public class YAxis implements Axis<G, X, Y> {

    @Override
    public void draw() {
      for (int i = 0; i < data.getGroups().size(); i++) {
        drawAxisForFacade(i);
      }
    }

    @Override
    public AxisType getType() {
      return AxisType.Y_AXIS;
    }

    private void drawAxisForFacade(int i) {

      graphics.setColor(styleManager.getColor(i, data.getGroups().size()));

      Rectangle2D drawBounds = styleManager.calculateDrawBoundsForAxis(i, data.getGroups().size());

      int numberOfTicks =
          (int) Math.floor(drawBounds.getHeight()
              / styleManager.getMinPixelsBetweenTicks());

      double pixelsBetweenTicks =
          drawBounds.getHeight()
              / (double) numberOfTicks;

      for (int j = 0; j <= numberOfTicks; j++) {

        double xStart = drawBounds.getMaxX() - styleManager.getMaxTickLength();
        double yStart = drawBounds.getY() + (double) j * pixelsBetweenTicks;

        double xEnd = drawBounds.getMaxX();
        double yEnd = drawBounds.getY() + (double) j * pixelsBetweenTicks;

        Line2D line = new Line2D.Double(xStart, yStart, xEnd, yEnd);
        graphics.draw(line);
      }
    }
  }

  private BufferedImage image;
  private Graphics2D graphics;

  private XAxis xAxis;
  private YAxis yAxis;

  private FacetedChartData<?, X, Y, G> data;

  private Rectangle2D marginedDataBounds;

  private PointConverter<X, Y> pointConverter;
  private StyleManager styleManager;

  public FacetedChart(FacetedChartData<?, X, Y, G> data,
      PointConverter<X, Y> pointConverter) {

    this.styleManager = new StyleManager();

    this.data = data;
    this.pointConverter = pointConverter;

    this.xAxis = new XAxis();
    this.yAxis = new YAxis();

    this.marginedDataBounds = initialiseMarginedDataBounds();
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

      graphics.setColor(styleManager.getColor(facadeIndex, data.getGroups().size()));

      new FacadePainter(
          this,
          data.convertGroupToPoints(pointConverter, group),
          styleManager.getDrawArea(facadeIndex, data.getGroups().size()))
              .paint(graphics);

      facadeIndex++;
    }
  }

  private Rectangle2D initialiseMarginedDataBounds() {
    Rectangle2D dataBounds = data.getDataBounds(pointConverter);

    double width = dataBounds.getWidth();
    double height = dataBounds.getHeight();
    double x = dataBounds.getMinX();
    double y = dataBounds.getMinY();

    double widthMargin = styleManager.getFacadeInnerPercentageMargin() * width;
    double heightMargin = styleManager.getFacadeInnerPercentageMargin() * height;

    double newWidth = width + widthMargin * 2.0;
    double newHeight = height + heightMargin * 2.0;

    double newX = x - widthMargin;
    double newY = y - heightMargin;

    return new Rectangle2D.Double(newX, newY, newWidth, newHeight);
  }

  @Override
  public Rectangle2D getMarginedDataBounds() {
    return marginedDataBounds;
  }

  private void initialiseGraphics() {
    this.graphics = image.createGraphics();

    graphics.setBackground(Color.white);
    graphics.clearRect(0, 0, styleManager.getImageWidth(), styleManager.getImageHeight());
  }

  private void initialiseImage() {
    this.image =
        new BufferedImage(styleManager.getImageWidth(), styleManager.getImageHeight(), BufferedImage.TYPE_INT_RGB);
  }

  @Override
  public StyleManager getStyleManager() {
    return styleManager;
  }

}
