package uk.co.ticklethepanda.charting.faceted.internal;

public interface Axis {

  enum AxisType {
    X_AXIS, Y_AXIS
  }

  public class X implements Axis {

    public int getHeight() {
      return 40;
    }

    @Override
    public AxisType getType() {
      return AxisType.X_AXIS;
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

  Axis.AxisType getType();

}