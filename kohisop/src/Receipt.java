public class Receipt {

  private static final int W = 60;

  private final Order order;
  private final PaymentChannel channel;
  private final Currency currency;

  private double subtotalIDR;
  private double taxIDR;
  private double totalIDR;
  private double discountIDR;
  private double adminFeeIDR;
  private double finalIDR;

  public Receipt(Order order, PaymentChannel channel, Currency currency) {
    this.order = order;
    this.channel = channel;
    this.currency = currency;
  }

  private void calculate() {
    subtotalIDR = 0;
    taxIDR = 0;

    for (OrderItem oi : order.getDrinkItems()) {
      subtotalIDR += TaxCalculator.calculateSubtotal(oi);
      taxIDR += TaxCalculator.calculateTax(oi);
    }

    for (OrderItem oi : order.getFoodItems()) {
      subtotalIDR += TaxCalculator.calculateSubtotal(oi);
      taxIDR += TaxCalculator.calculateTax(oi);
    }

    totalIDR = subtotalIDR + taxIDR;
    discountIDR = totalIDR * channel.getDiscountRate();
    adminFeeIDR = channel.getAdminFee();
    finalIDR = channel.calculateFinal(totalIDR);
  }

  public void print() {
    calculate();

    String sep = "=".repeat(W);

    System.out.println();
    System.out.println(sep);
    System.out.printf("%" + ((W + 28) / 2) + "s%n", "KUITANSI PEMBAYARAN - KOHISOP");
    System.out.println(sep);

    // Minuman
    if (order.hasDrinkItems()) {
      printSection("MINUMAN");
      for (OrderItem oi : order.getDrinkItems()) {
        printItem(oi);
      }
    }

    // Makanan
    if (order.hasFoodItems()) {
      if (order.hasDrinkItems()) System.out.println("=".repeat(W));
      printSection("MAKANAN");
      for (OrderItem oi : order.getFoodItems()) {
        printItem(oi);
      }
    }

    // Total
    System.out.println(sep);
    printLine("Subtotal", formatIDR(subtotalIDR));
    printLine("Pajak", formatIDR(taxIDR));
    printLine("Total", formatIDR(totalIDR));

    System.out.println(sep);
    printLine("Channel", channel.getName());

    if (channel.getDiscountRate() > 0) {
      printLine("Diskon (" + (int)(channel.getDiscountRate()*100) + "%)",
        "-" + formatIDR(discountIDR));
    }

    if (channel.getAdminFee() > 0) {
      printLine("Biaya admin",
        "+" + formatIDR(adminFeeIDR));
    }

    System.out.println(sep);
    printLine("TOTAL BAYAR", formatIDR(finalIDR));

    // Currency
    if (!(currency instanceof IDR)) {
      System.out.println(sep);
      printLine("Mata Uang", currency.getName());
      printLine("Total (" + currency.getName() + ")",
        currency.format(finalIDR));
    }

    System.out.println(sep);
    System.out.println("Terima kasih telah berkunjung!");
    System.out.println();
  }

  private void printSection(String title) {
    System.out.println("[ " + title + " ]");
  }

  private void printItem(OrderItem oi) {
    MenuItem item = oi.getMenuItem();

    String nama = item.getName().length() > 30
      ? item.getName().substring(0, 27) + "..."
      : item.getName();

    String kiri = item.getCode() + " " + nama;
    String kanan = oi.getQuantity() + " x " + formatIDR(item.getPrice());

    printLine(kiri, kanan);

    double tax = TaxCalculator.calculateTax(oi);
    double rate = TaxCalculator.getTaxRate(item);

    if (tax > 0) {
      printLine("  pajak (" + (int)(rate*100) + "%)", formatIDR(tax));
    }
  }

  private void printLine(String left, String right) {
    int space = W - left.length() - right.length();
    if (space < 1) space = 1;
    System.out.println(left + " ".repeat(space) + right);
  }

  private String formatIDR(double ribuan) {
    return "Rp" + String.format("%,.0f", ribuan) + ".000";
  }
}