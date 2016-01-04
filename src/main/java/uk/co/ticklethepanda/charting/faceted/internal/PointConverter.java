package uk.co.ticklethepanda.charting.faceted.internal;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import uk.co.ticklethepanda.charting.faceted.FacetedChartData;

public class PointConverter<X, Y> {
  private CoordinateConverter<? super X> xConverter;
  private CoordinateConverter<? super Y> yConverter;

  public PointConverter(CoordinateConverter<? super X> xConverter, CoordinateConverter<? super Y> yConverter) {
    this.xConverter = xConverter;
    this.yConverter = yConverter;
  }

  public double convertX(X xValue) {
    return xConverter.getDouble(xValue);
  }

  public double convertY(Y yValue) {
    return yConverter.getDouble(yValue);
  }

  public Point2D getPoint(X xValue, Y yValue) {
    return new Point2D.Double(convertX(xValue), convertY(yValue));
  }
}