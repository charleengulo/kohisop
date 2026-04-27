public class TaxCalculator {

  public static double getTaxRate(MenuItem item) {
    int price = item.getPrice();

    if (item instanceof Minuman) {
      if (price < 50) return 0.00;
      else if (price <= 55) return 0.08;
      else return 0.11;
    } else {
      if (price > 50) return 0.08;
      else return 0.11;
    }
  }
  

  public static double calculateTax(OrderItem oi) {
    double subtotal = (double) oi.getMenuItem().getPrice() * oi.getQuantity();
    return subtotal * getTaxRate(oi.getMenuItem());
  }


  public static double calculateSubtotal(OrderItem oi) {
    return (double) oi.getMenuItem().getPrice() * oi.getQuantity();
  }

}