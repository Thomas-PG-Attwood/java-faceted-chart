package uk.co.ticklethepanda.charting.faceted;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

public class FacetedLineChartDataTest {

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

  private static final List<Item> multiGroupItems = new ArrayList<>();

  static {
    multiGroupItems.add(new Item(0, 1, 1));
    multiGroupItems.add(new Item(0, 2, 1));
    multiGroupItems.add(new Item(0, 3, 1));

    multiGroupItems.add(new Item(0, -1, -1));
    multiGroupItems.add(new Item(0, -2, -1));
    multiGroupItems.add(new Item(0, -3, -1));
  }

  private static final List<Item> singleGroupItems = new ArrayList<>();

  static {
    singleGroupItems.add(new Item(0, -1, -1));
    singleGroupItems.add(new Item(0, -2, -1));
    singleGroupItems.add(new Item(0, -3, -1));
  }

  private static final List<Item> emptyList = new ArrayList<>();

  @Test
  public void TestGetGroup_MultiGroups_GetMultiGroup() {

    FacetedChartData<Item, Integer, Integer, Integer> chart =
        FacetedChartData.withNaturalOrdering(
            multiGroupItems,
            Item::getValueX,
            Item::getValueY,
            Item::getGroup);

    List<Item> expectedItemsList = new ArrayList<Item>();

    expectedItemsList.add(new Item(0, -1, -1));
    expectedItemsList.add(new Item(0, -2, -1));
    expectedItemsList.add(new Item(0, -3, -1));

    Item[] expectedItems = expectedItemsList.toArray(new Item[0]);

    assertThat(chart.getGroup(-1), hasItems(expectedItems));
  }

  @Test
  public void TestGetGroup_EmptyGroup_NewArrayList() {

    FacetedChartData<Item, Integer, Integer, Integer> chart =
        FacetedChartData.withNaturalOrdering(
            emptyList,
            Item::getValueX,
            Item::getValueY,
            Item::getGroup);

    List<Item> expectedItems = new ArrayList<Item>();

    assertEquals(expectedItems, chart.getGroup(-1));
  }

  @Test
  public void TestGetXValuesForGroup_EmptyGroup_EmptyListReturned() {

    FacetedChartData<Item, Integer, Integer, Integer> chart =
        FacetedChartData.withNaturalOrdering(
            emptyList,
            Item::getValueX,
            Item::getValueY,
            Item::getGroup);

    List<Integer> expectedValues = new ArrayList<Integer>();

    assertEquals(expectedValues, chart.getXValues(-1));
  }

  @Test
  public void TestGetXValuesForGroup_GroupWithItems_ListWithItemsReturned() {

    FacetedChartData<Item, Integer, Integer, Integer> chart =
        FacetedChartData.withNaturalOrdering(
            multiGroupItems,
            Item::getValueX,
            Item::getValueY,
            Item::getGroup);

    List<Integer> expectedValuesList = new ArrayList<Integer>();

    expectedValuesList.add(0);
    expectedValuesList.add(0);
    expectedValuesList.add(0);

    Integer[] expectedValues = expectedValuesList.toArray(new Integer[0]);

    assertThat(chart.getXValues(-1), hasItems(expectedValues));
  }

  @Test
  public void TestGetYValuesForGroup_EmptyGroup_EmptyListReturned() {

    FacetedChartData<Item, Integer, Integer, Integer> chart =
        FacetedChartData.withNaturalOrdering(
            emptyList,
            Item::getValueX,
            Item::getValueY,
            Item::getGroup);

    List<Integer> expectedItems = new ArrayList<Integer>();

    assertEquals(expectedItems, chart.getYValues(-1));
  }

  @Test
  public void TestGetYValuesForGroup_GroupWithItems_ListWithItemsReturned() {

    FacetedChartData<Item, Integer, Integer, Integer> chart =
        FacetedChartData.withNaturalOrdering(
            multiGroupItems,
            Item::getValueX,
            Item::getValueY,
            Item::getGroup);

    List<Integer> expectedItemsList = new ArrayList<Integer>();

    expectedItemsList.add(-1);
    expectedItemsList.add(-2);
    expectedItemsList.add(-3);

    Integer[] expectedItems = expectedItemsList.toArray(new Integer[0]);

    assertThat(chart.getYValues(-1), hasItems(expectedItems));
  }

  @Test
  public void TestGetGroups_groups_CorrectGroups() {

    FacetedChartData<Item, Integer, Integer, Integer> chart =
        FacetedChartData.withNaturalOrdering(
            multiGroupItems,
            Item::getValueX,
            Item::getValueY,
            Item::getGroup);

    List<Integer> expectedGroupsList = new ArrayList<>();
    expectedGroupsList.add(1);
    expectedGroupsList.add(-1);

    Integer[] expectedGroups = expectedGroupsList.toArray(new Integer[0]);

    assertThat(chart.getGroups(), hasItems(expectedGroups));
  }

  @Test
  public void TestGetGroups_NoGroups_EmptyList() {

    FacetedChartData<Item, Integer, Integer, Integer> chart =
        FacetedChartData.withNaturalOrdering(
            emptyList,
            Item::getValueX,
            Item::getValueY,
            Item::getGroup);

    List<Integer> expectedGroups = new ArrayList<>();

    assertEquals(expectedGroups, chart.getGroups());
  }
}
