package uk.co.ticklethepanda.charting.faceted;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

public class VisualNumberTest {

  private static class Item {
    private int valueX;

    @Override
    public String toString() {
      return "Item [valueX=" + valueX + ", valueY=" + valueY + ", group=" + group + "]";
    }

    private int valueY;

    private int group;

    public Item(int valueX, int valueY, int group) {
      this.valueX = valueX;
      this.valueY = valueY;
      this.group = group;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Item other = (Item) obj;
      if (group != other.group)
        return false;
      if (valueX != other.valueX)
        return false;
      if (valueY != other.valueY)
        return false;
      return true;
    }

    public Integer getGroup() {
      return group;
    }

    public Integer getValueX() {
      return valueX;
    }

    public Integer getValueY() {
      return valueY;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + group;
      result = prime * result + valueX;
      result = prime * result + valueY;
      return result;
    }
  }

  public static Random random = new Random();

  public static void main(String[] args) throws IOException {
    List<Item> items = new ArrayList<>();

    for (int i = 0; i < 1000; i++) {
      items.add(createRandomItem());
    }

    FacetedChartData<Item, Integer, Integer, Integer> data =
        FacetedChartData.withNaturalOrdering(
            items,
            Item::getValueX,
            Item::getValueY,
            Item::getGroup);

    FacetedChart<Integer, Integer, Integer> painter =
        FacetedChart.createNumberAgainstNumberPlot(data);

    BufferedImage image = painter.draw();
    image.flush();

    new File("output").mkdirs();

    File outputFile = new File("output/image-" + VisualNumberTest.class.getCanonicalName() + ".png");

    System.out.println("writing out to file: " + outputFile.getAbsolutePath());

    ImageIO.write(image, "png", outputFile);
  }

  private static Item createRandomItem() {
    return new Item(
        random.nextInt(100) - 13,
        random.nextInt(100) + 50,
        random.nextInt(20));
  }

}
