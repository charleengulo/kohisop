public class QRIS implements PaymentChannel {

  private static final double DISCOUNT = 0.05;

  @Override public String getName() { return "QRIS"; }
  @Override public double getDiscountRate() { return DISCOUNT; }
  @Override public double getAdminFee() { return 0.00; }
  @Override public boolean requiresBalanceCheck() { return true; }

  @Override
  public double calculateFinal(double totalAfterTax) {
    return totalAfterTax * (1 - DISCOUNT);
  }

}