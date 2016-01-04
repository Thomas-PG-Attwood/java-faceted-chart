package uk.co.ticklethepanda.charting.faceted;

import java.time.LocalDate;
import java.time.LocalTime;

import uk.co.ticklethepanda.charting.faceted.internal.CoordinateConverter;
import uk.co.ticklethepanda.charting.faceted.internal.PointConverter;

public class FacetedChartFactory {
  
  private FacetedChartFactory() {
    
  }

  public static final <G, X extends Object, Y extends Number> FacetedChart<G, X, Y> createNumberAgainstCategoryPlot(
      FacetedChartData<?, X, Y, G> data) {
  
    PointConverter<X, Y> converter = new PointConverter<X, Y>(
        new CoordinateConverter.CategoryConverter<X>(),
        new CoordinateConverter.NumberConverter());
  
    return new FacetedChart<G, X, Y>(data,
        converter);
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

}
