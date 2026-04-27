public class Minuman extends MenuItem {
  
  public Minuman(String code, String name, int price) {
    super(code, name, price);
  }
  
  @Override
  public int getMaxQuantity() {
    return 3;
  }
  
  @Override
  public String getCategory() {
    return "Minuman";
  }
  
}
