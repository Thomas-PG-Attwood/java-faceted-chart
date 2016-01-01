package uk.co.ticklethepanda.charting.faceted.internal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Map;

public interface CoordinateConverter<E> {
  
  public static class ObjectPlotter<E> implements CoordinateConverter<E> {
    private Map<E, Double> map = new HashMap<E, Double>();
    
    @Override
    public double getDouble(E value) {
      if (!map.containsKey(value)) {
        map.put(value, (double) (map.size() + 1));
      }
      return map.get(value);
    }
  }
  
  public static class NumberPlotter implements CoordinateConverter<Number> {
    @Override
    public double getDouble(Number value) {
      return value.doubleValue();
    }
  }
  
  public static class LocalTimePlotter implements CoordinateConverter<LocalTime> {
    @Override
    public double getDouble(LocalTime value) {
      return ((LocalTime) value).get(ChronoField.SECOND_OF_DAY);
    }
    
  }
  public static class LocalDatePlotter implements CoordinateConverter<LocalDate> {
    @Override
    public double getDouble(LocalDate value) {
      return ((LocalDate) value).get(ChronoField.EPOCH_DAY);
    }
  }
  
  double getDouble(E value);
}