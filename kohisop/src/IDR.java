public class IDR implements Currency {

  @Override
  public String getName() { return "IDR"; }

  @Override
  public String getSymbol() { return "Rp"; }

  @Override
  public double getExchangeRateToIDR() { return 1.0; }

  @Override
  public double convert(double idrThousands) {
    return idrThousands;
  }

  @Override
  public String format(double idrThousands) {
    long rupiah = Math.round(idrThousands);
    return String.format("Rp %,d.000", rupiah);
  }

}