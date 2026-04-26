public class JPY implements Currency {

  private static final double MULTIPLIER = 10.0;

  @Override
  public String getName() { return "JPY"; }

  @Override
  public String getSymbol() { return "¥"; }

  @Override
  public double getExchangeRateToIDR() {
    return 1.0 / MULTIPLIER;
  }

  @Override
  public double convert(double idrThousands) {
    return idrThousands * MULTIPLIER;
  }

  @Override
  public String format(double idrThousands) {
    return String.format("¥ %,.0f", convert(idrThousands));
  }

}