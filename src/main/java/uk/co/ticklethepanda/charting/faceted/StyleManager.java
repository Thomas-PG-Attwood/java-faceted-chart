package uk.co.ticklethepanda.charting.faceted;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

public class StyleManager {

  public static class Defaults {
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

    public static final double MARKER_SIZE = 2.0;

    public static final double MAX_TICK_LABEL_WIDTH = 40.0;
    public static final double MAX_TICK_LABEL_HEIGHT = 40.0;

    public static final int MIN_TICKS = 2;
    public static final int MIN_TICKS_PIXELS = 7;
    public static final int MIN_MARGIN_BETWEEN_TICKS = 1;
  }

  private int imageWidth = Defaults.IMAGE_SIZE;
  private int imageHeight = Defaults.IMAGE_SIZE;

  private double facadeInnerPercentaggeMargin = Defaults.FACADE_INNER_PERCENTGE_MARGIN;

  private double categoryWidth = Defaults.CATEGORY_WIDTH;

  private double leftMargin = Defaults.LEFT_MARGIN;
  private double rightMargin = Defaults.RIGHT_MARGIN;
  private double topMargin = Defaults.TOP_MARGIN;
  private double bottomMargin = Defaults.BOTTOM_MARGIN;

  private double marginBetweenFacades = Defaults.MARGIN_BETWEEN_FACADES;

  private double maxTickLength = Defaults.MAX_TICK_LENGTH;
  private double minPixelsBetweenTicks = Defaults.MIN_PIXELS_BETWEEN_TICKS;

  private double maxTickLabelWidth = Defaults.MAX_TICK_LABEL_WIDTH;
  private double maxTickLabelHeight = Defaults.MAX_TICK_LABEL_HEIGHT;

  private double minTicks = Defaults.MIN_TICKS;
  private double minTicksPixels = Defaults.MIN_TICKS_PIXELS;
  private double minMarginBetweenTicks = Defaults.MIN_MARGIN_BETWEEN_TICKS;
  
  public double getFacadeInnerPercentaggeMargin() {
    return facadeInnerPercentaggeMargin;
  }

  public double getMinTicks() {
    return minTicks;
  }

  public double getMinTicksPixels() {
    return minTicksPixels;
  }

  public double getMinMarginBetweenTicks() {
    return minMarginBetweenTicks;
  }

  public int getImageWidth() {
    return imageWidth;
  }

  public int getImageHeight() {
    return imageHeight;
  }

  public double getFacadeInnerPercentageMargin() {
    return facadeInnerPercentaggeMargin;
  }

  public double getCategoryWidth() {
    return categoryWidth;
  }

  public double getRightMargin() {
    return rightMargin;
  }

  public double getTopMargin() {
    return topMargin;
  }

  public double getBottomMargin() {
    return bottomMargin;
  }

  public double getMarginBetweenFacades() {
    return marginBetweenFacades;
  }

  public double getMinPixelsBetweenTicks() {
    return minPixelsBetweenTicks;
  }

  public double getMaxTickLabelWidth() {
    return maxTickLabelWidth;
  }

  public double getMaxTickLabelHeight() {
    return maxTickLabelHeight;
  }

  public double getLeftMargin() {
    return leftMargin;
  }

  public double getMaxTickLength() {
    return maxTickLength;
  }

  public double getYAxisWidth() {
    return maxTickLabelWidth + maxTickLength;
  }

  public double getXAxisHeight() {
    return maxTickLabelHeight + maxTickLength;
  }

  private double markerSize = 2.0;

  public double getMarkerSize() {
    return markerSize;
  }

  public double getMarkerOffset() {
    return markerSize / 2.0;
  }

  Rectangle2D calculateDrawBoundsForAxis(int currentFacade, int numberOfFacades) {
    double x = leftMargin;
    double y = getFacadeY(currentFacade, numberOfFacades);
    double width = getYAxisWidth();
    double height = getFacadeHeight(numberOfFacades);

    Rectangle2D drawBounds = new Rectangle2D.Double(x, y, width, height);
    return drawBounds;
  }

  public double getFacadeHeight(int numberOfFacades) {
    return (imageHeight
        - getTopMargin() - getBottomMargin()
        - getMarginBetweenFacades() * (double) (numberOfFacades - 1)
        - (double) getXAxisHeight())
        / (double) numberOfFacades;
  }

  public double getFacadeWidth() {
    return imageWidth
        - leftMargin
        - rightMargin
        - getYAxisWidth();
  }

  public double getFacadeX() {
    return leftMargin + getYAxisWidth();
  }

  public double getFacadeY(int currentFacadeNumber, int numberOfFacades) {
    return topMargin
        + marginBetweenFacades * currentFacadeNumber
        + getFacadeHeight(numberOfFacades) * currentFacadeNumber;
  }

  public Color getColor(int currentFacadeNumber, int numberOfFacades) {
    return Color.getHSBColor(
        (float) (1.0 / numberOfFacades * currentFacadeNumber),
        0.8f,
        0.5f);
  }

  public Rectangle2D getDrawArea(int currentFacadeNumber, int numberOfFacades) {
    return new Rectangle2D.Double(
        getFacadeX(),
        getFacadeY(currentFacadeNumber, numberOfFacades),
        getFacadeWidth(),
        getFacadeHeight(numberOfFacades));
  }
}