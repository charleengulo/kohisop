public interface PaymentChannel {

  String getName();
  double getDiscountRate();
  double getAdminFee();
  boolean requiresBalanceCheck();
  double calculateFinal(double totalAfterTax);

  default boolean isSaldoCukup(double saldo, double total) {
    return saldo >= total;
  }

}