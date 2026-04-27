public class Tunai implements PaymentChannel {

  @Override public String getName() { return "Tunai"; }
  @Override public double getDiscountRate() { return 0.00; }
  @Override public double getAdminFee() { return 0.00; }
  @Override public boolean requiresBalanceCheck() { return false; }

  @Override
  public double calculateFinal(double totalAfterTax) {
    return totalAfterTax;
  }

}