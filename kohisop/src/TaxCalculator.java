public class TaxCalculator {

  public static double getTaxRate(MenuItem item) {
    int price = item.getPrice();
    if (item instanceof Minuman) {
      if (price < 50) return 0.00;
      else if (price <= 55) return 0.08;
      else return 0.11;
    } else {
      return price > 50 ? 0.08 : 0.11;
    }
  }

  public static double getTaxRate(MenuItem item, Member member) {
    if (member != null && member.isTaxExempt()) return 0.00;
    return getTaxRate(item);
  }

  public static double calculateSubtotal(OrderItem oi) {
    return (double) oi.getMenuItem().getPrice() * oi.getQuantity();
  }

  public static double calculateTax(OrderItem oi) {
    return calculateSubtotal(oi) * getTaxRate(oi.getMenuItem());
  }

  public static double calculateTax(OrderItem oi, Member member) {
    return calculateSubtotal(oi) * getTaxRate(oi.getMenuItem(), member);
  }
}