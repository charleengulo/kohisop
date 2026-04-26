public class EUR implements Currency {

  private static final double RATE = 14.0;

  @Override
  public String getName() { return "EUR"; }

  @Override
  public String getSymbol() { return "€"; }

  @Override
  public double getExchangeRateToIDR() { return RATE; }

  @Override
  public double convert(double idrThousands) {
    return idrThousands / RATE;
  }

  @Override
  public String format(double idrThousands) {
    return String.format("€ %.2f", convert(idrThousands));
  }

}