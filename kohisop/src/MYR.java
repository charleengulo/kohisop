public class MYR implements Currency {

  private static final double RATE = 4.0;

  @Override
  public String getName() { return "MYR"; }

  @Override
  public String getSymbol() { return "RM"; }

  @Override
  public double getExchangeRateToIDR() { return RATE; }

  @Override
  public double convert(double idrThousands) {
    return idrThousands / RATE;
  }

  @Override
  public String format(double idrThousands) {
    return String.format("RM %.2f", convert(idrThousands));
  }

}