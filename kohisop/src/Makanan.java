public class Makanan extends MenuItem {

  public Makanan(String code, String name, int price) {
    super(code, name, price);
  }

  @Override
  public int getMaxQuantity() {
    return 2;
  }

  @Override
  public String getCategory() {
    return "Makanan";
  }

}