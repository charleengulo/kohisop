public interface Currency {

  String getName();
  String getSymbol();
  double getExchangeRateToIDR();
  double convert(double idrThousands);
  String format(double idrThousands);
  
}