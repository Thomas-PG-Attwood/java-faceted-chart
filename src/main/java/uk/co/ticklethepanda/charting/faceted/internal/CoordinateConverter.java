package uk.co.ticklethepanda.charting.faceted.internal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Map;

public interface CoordinateConverter<E> {

  public static class CategoryConverter<E> implements CoordinateConverter<E> {
    private Map<E, Double> map = new HashMap<E, Double>();

    @Override
    public Double getDouble(E value) {
      if (!map.containsKey(value)) {
        map.put(value, Double.valueOf(map.size() + 1));
      }
      return map.get(value);
    }

    @Override
    public E getActual(Double converted) {
      for (E e : map.keySet()) {
        if (map.get(e).equals(converted)) {
          return e;
        }
      }
      return null;
    }
  }

  public static class NumberConverter implements CoordinateConverter<Number> {
    @Override
    public Double getDouble(Number value) {
      return value.doubleValue();
    }

    @Override
    public Number getActual(Double value) {
      return value;
    }
  }

  public static class LocalTimeConverter implements CoordinateConverter<LocalTime> {
    @Override
    public Double getDouble(LocalTime value) {
      return Double.valueOf(((LocalTime) value).get(ChronoField.SECOND_OF_DAY));
    }

    @Override
    public LocalTime getActual(Double value) {
      return LocalTime.ofSecondOfDay(value.longValue());
    }
  }

  public static class LocalDateConverter implements CoordinateConverter<LocalDate> {
    @Override
    public Double getDouble(LocalDate value) {
      return Double.valueOf(((LocalDate) value).get(ChronoField.EPOCH_DAY));
    }

    @Override
    public LocalDate getActual(Double converted) {
      return LocalDate.ofEpochDay(converted.longValue());
    }
  }

  Double getDouble(E value);

  E getActual(Double converted);
}