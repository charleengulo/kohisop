import java.util.ArrayList;
import java.util.List;

public class Order {

  private List<OrderItem> drinkItems;
  private List<OrderItem> foodItems;

  private static final int MAX_DRINK_TYPES = 5;
  private static final int MAX_FOOD_TYPES  = 5;


  public Order() {
    drinkItems = new ArrayList<>();
    foodItems  = new ArrayList<>();
  }


  public String addItem(MenuItem item, int qty) {
    if (item instanceof Minuman) {
      if (drinkItems.size() >= MAX_DRINK_TYPES)
        return "Maksimal " + MAX_DRINK_TYPES + " jenis minuman per pesanan.";
      drinkItems.add(new OrderItem(item, qty));
    } else {
      if (foodItems.size() >= MAX_FOOD_TYPES)
        return "Maksimal " + MAX_FOOD_TYPES + " jenis makanan per pesanan.";
      foodItems.add(new OrderItem(item, qty));
    }
    return null;
  }


  public void removeItemByCode(String code) {
    drinkItems.removeIf(oi -> oi.getMenuItem().getCode().equalsIgnoreCase(code));
    foodItems.removeIf(oi  -> oi.getMenuItem().getCode().equalsIgnoreCase(code));
  }


  public boolean containsCode(String code) {
    for (OrderItem oi : drinkItems)
      if (oi.getMenuItem().getCode().equalsIgnoreCase(code)) return true;

    for (OrderItem oi : foodItems)
      if (oi.getMenuItem().getCode().equalsIgnoreCase(code)) return true;

    return false;
  }


  public OrderItem findItemByCode(String code) {
    for (OrderItem oi : drinkItems)
      if (oi.getMenuItem().getCode().equalsIgnoreCase(code)) return oi;

    for (OrderItem oi : foodItems)
      if (oi.getMenuItem().getCode().equalsIgnoreCase(code)) return oi;

    return null;
  }


  public List<OrderItem> getDrinkItems() { return drinkItems; }
  public List<OrderItem> getFoodItems()  { return foodItems; }


  public boolean hasDrinkItems() { return !drinkItems.isEmpty(); }
  public boolean hasFoodItems()  { return !foodItems.isEmpty(); }
  public boolean isEmpty() {
    return drinkItems.isEmpty() && foodItems.isEmpty();
  }


  public boolean isDrinkFull() {
    return drinkItems.size() >= MAX_DRINK_TYPES;
  }

  public boolean isFoodFull() {
    return foodItems.size() >= MAX_FOOD_TYPES;
  }

}