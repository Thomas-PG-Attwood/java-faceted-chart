package uk.co.ticklethepanda.charting.faceted;

import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;

public interface Chart {

  RenderedImage draw();

  Rectangle2D getMarginedDataBounds();

  StyleManager getStyleManager();

}