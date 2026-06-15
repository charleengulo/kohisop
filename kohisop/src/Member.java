import java.util.Random;

public class Member {

  private static final char[] CHARS = "ABCDEF0123456789".toCharArray();

  private final String kode;
  private final String nama;
  private int poin;

  public Member(String nama) {
    this.kode = generateKode();
    this.nama = nama;
    this.poin = 0;
  }

  private static String generateKode() {
    Random rnd = new Random();
    StringBuilder sb = new StringBuilder(6);

    for (int i = 0; i < 6; i++) {
      sb.append(CHARS[rnd.nextInt(CHARS.length)]);
    }

    return sb.toString();
  }

  public String getKode() {
    return kode;
  }

  public String getNama() {
    return nama;
  }

  public int getPoin() {
    return poin;
  }

  public void tambahPoin(int jumlah) {
    this.poin += jumlah;
  }

  public void kurangiPoin(int jumlah) {
    this.poin = Math.max(0, this.poin - jumlah);
  }

  public boolean hasBonusPoin() {
    return kode.contains("A");
  }

  public boolean isTaxExempt() {
    return kode.contains("A");
  }

  @Override
  public String toString() {
    return String.format( "[%s] %s - %d poin", kode, nama, poin);
  }
}