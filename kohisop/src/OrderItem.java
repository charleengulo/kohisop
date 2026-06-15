public class OrderItem {

  private final MenuItem menuItem;
  private int quantity;

  public OrderItem(MenuItem menuItem, int quantity) {
    this.menuItem = menuItem;
    this.quantity = quantity;
  }

  public MenuItem getMenuItem() { return menuItem; }
  public int getQuantity() { return quantity; }
  public void setQuantity(int q) { this.quantity = q; }

}
