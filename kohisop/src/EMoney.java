public class EMoney implements PaymentChannel {

  private static final double DISCOUNT = 0.07;
  private static final double ADMIN_FEE = 20.0;

  @Override public String getName() { return "eMoney"; }
  @Override public double getDiscountRate() { return DISCOUNT; }
  @Override public double getAdminFee() { return ADMIN_FEE; }
  @Override public boolean requiresBalanceCheck() { return true; }

  @Override
  public double calculateFinal(double totalAfterTax) {
    return (totalAfterTax * (1 - DISCOUNT)) + ADMIN_FEE;
  }

}