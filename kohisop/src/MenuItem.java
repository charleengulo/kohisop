public abstract class MenuItem {

  protected String code;
  protected String name;
  protected int price;

  public MenuItem(String code, String name, int price) {
    this.code = code;
    this.name = name;
    this.price = price;
  }

  public String getCode() { return code; }
  public String getName() { return name; }
  public int getPrice() { return price; }

  public abstract int getMaxQuantity();
  public abstract String getCategory();

  @Override
  public String toString() {
    return String.format("%-4s %-38s Rp%,d.000", code, name, price);
  }

}