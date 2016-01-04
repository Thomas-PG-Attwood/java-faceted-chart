package uk.co.ticklethepanda.charting.faceted.internal;


public interface Axis<G, X, Y> {

  abstract AxisType getType();

  abstract void draw();

}