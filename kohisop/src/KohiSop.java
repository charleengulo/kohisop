import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class KohiSop {

  private static final Scanner sc = new Scanner(System.in);
  private static final MenuManager menuManager = new MenuManager();
  private static final MemberDatabase memberDB = new MemberDatabase();
  private static final KitchenQueue kitchenQueue = new KitchenQueue();
  private static int customerCount = 0;

  public static void main(String[] args) {
    printBanner();

    while (true) {
      System.out.println(line('=', 60));
      System.out.printf("   Selamat Datang di KohiSop!  (Pelanggan ke-%d)%n",
          customerCount + 1);
      System.out.println(line('=', 60));

      // 1. Pemesanan
      Order order = doOrdering();
      if (order == null) { printCancelled(); break; }
      if (order.isEmpty()) { System.out.println("[INFO] Tidak ada pesanan."); break; }

      // 2. Membership
      Member member = handleMembership();

      // 3. Channel pembayaran
      PaymentChannel channel = selectChannel(order, member);
      if (channel == null) { printCancelled(); break; }

      // 4. Mata uang
      Currency currency = selectCurrency();
      if (currency == null) { printCancelled(); break; }

      // 5. Kuitansi
      Receipt receipt = new Receipt(order, channel, currency, member);
      receipt.print();

      // 6. Kitchen queue — tampil setelah 3 pelanggan
      boolean batchReady = kitchenQueue.addOrder(order);
      customerCount++;

      if (batchReady) {
        kitchenQueue.printKitchenOrders();
      } else {
        int sisa = 3 - (customerCount % 3);
        if (sisa == 3) sisa = 0;
        System.out.println("[INFO] Tim dapur menunggu " + sisa
            + " pelanggan lagi sebelum memproses pesanan.");
      }

      // 7. Lanjut?
      System.out.print("\nLanjut ke pelanggan berikutnya? (Y/N): ");
      if (!sc.nextLine().trim().equalsIgnoreCase("Y")) break;
    }

    System.out.println("\n" + line('=', 60));
    System.out.println("   Terima kasih! Program ditutup.");
    System.out.println(line('=', 60));
  }

  private static Order doOrdering() {
    Order order = new Order();
    printMenu();

    System.out.println();
    System.out.println("  Ketentuan pemesanan:");
    System.out.println("  - Masukkan kode menu, tekan ENTER");
    System.out.println("  - Input kode yang sama untuk menambah kuantitas");
    System.out.println("  - SUBMIT  : selesai memesan");
    System.out.println("  - CC      : batalkan seluruh pesanan");
    System.out.println("  - Batas   : 5 jenis minuman & 5 jenis makanan per pesanan");
    System.out.println("  - Max qty : minuman 3 porsi | makanan 2 porsi");
    System.out.println();

    while (true) {
      if (order.isDrinkFull() && order.isFoodFull()) {
        System.out.println("[INFO] Batas pesanan tercapai.");
        break;
      }

      System.out.printf("  [Slot - Makanan: %d/5 | Minuman: %d/5]%n",
          order.getFoodItems().size(), order.getDrinkItems().size());
      System.out.print("Kode menu (SUBMIT/CC): ");
      String kode = sc.nextLine().trim().toUpperCase();

      if (kode.equals("CC")) return null;
      if (kode.isEmpty()) { System.out.println("[ERROR] Input tidak boleh kosong."); continue; }
      if (kode.equals("SUBMIT")) {
        if (order.isEmpty()) { System.out.println("[ERROR] Belum ada item."); continue; }
        break;
      }

      MenuItem item = menuManager.findByCode(kode);
      if (item == null) { System.out.println("[ERROR] Kode '" + kode + "' tidak dikenali."); continue; }

      if (item instanceof Minuman && order.isDrinkFull()) {
        System.out.println("[ERROR] Batas 5 jenis minuman sudah tercapai."); continue;
      }
      if (item instanceof Makanan && order.isFoodFull()) {
        System.out.println("[ERROR] Batas 5 jenis makanan sudah tercapai."); continue;
      }

      OrderItem existing = order.findItemByCode(kode);

      System.out.println();
      System.out.printf("  Item    : [%s] %s  -  %d.000/porsi%n",
          item.getCode(), item.getName(), item.getPrice());
      System.out.printf("  Kategori: %s  |  Max: %d porsi%n",
          item.getCategory(), item.getMaxQuantity());

      if (existing != null) {
        int remaining = item.getMaxQuantity() - existing.getQuantity();
        System.out.printf("  Sudah dipesan: %d  |  Sisa: %d porsi%n",
            existing.getQuantity(), remaining);
        if (remaining <= 0) {
          System.out.println("[ERROR] Sudah mencapai batas maksimal.");
          System.out.println();
          continue;
        }
        int add = readQty(remaining);
        if (add == -1) return null;
        if (add == 0) {
          System.out.println("[INFO] Penambahan dibatalkan.");
        } else {
          int prev = existing.getQuantity();
          existing.setQuantity(prev + add);
          System.out.printf("[OK] '%s' diperbarui: %d -> %d porsi.%n",
              item.getName(), prev, existing.getQuantity());
        }
      } else {
        int qty = readQty(item.getMaxQuantity());
        if (qty == -1) return null;
        if (qty == 0) {
          System.out.println("[INFO] Item tidak ditambahkan.");
        } else {
          order.addItem(item, qty);
          System.out.printf("[OK] '%s' x%d ditambahkan.%n", item.getName(), qty);
        }
      }

      System.out.println();
      if (!order.isEmpty()) printOrderTable(order);
    }

    return order;
  }

  private static int readQty(int maxAllowed) {
    while (true) {
      System.out.printf("  Qty (1-%d | Enter=1 | 0/S=batal | CC=batal pesan): ", maxAllowed);
      String raw = sc.nextLine().trim();
      if (raw.equalsIgnoreCase("CC")) return -1;
      if (raw.equals("0") || raw.equalsIgnoreCase("S")) return 0;
      if (raw.isEmpty()) { System.out.println("  [OK] Qty default: 1"); return 1; }
      try {
        int q = Integer.parseInt(raw);
        if (q < 1 || q > maxAllowed) {
          System.out.printf("  [ERROR] Qty harus 1-%d.%n", maxAllowed);
          continue;
        }
        return q;
      } catch (NumberFormatException e) {
        System.out.println("  [ERROR] Input tidak valid.");
      }
    }
  }

  private static Member handleMembership() {
    System.out.println(line('=', 60));
    System.out.println("                    MEMBERSHIP");
    System.out.println(line('=', 60));

    System.out.print("Masukkan kode member (ENTER jika belum punya): ");
    String kode = sc.nextLine().trim().toUpperCase();

    if (!kode.isEmpty()) {
      while (true) {
        Member member = memberDB.findByKode(kode);
        if (member != null) {
          System.out.println("\n[OK] Member ditemukan!");
          System.out.println("  Nama : " + member.getNama());
          System.out.println("  Kode : " + member.getKode());
          System.out.println("  Poin : " + member.getPoin());
          if (member.isTaxExempt())  System.out.println("  Benefit : Bebas Pajak");
          if (member.hasBonusPoin()) System.out.println("  Benefit : Bonus Poin x2");
          return member;
        }
        System.out.println("[ERROR] Kode member tidak ditemukan.");
        System.out.print("Masukkan kode lagi (ENTER untuk buat member baru): ");
        kode = sc.nextLine().trim().toUpperCase();
        if (kode.isEmpty()) break;
      }
    }

    System.out.print("Nama pelanggan: ");
    String nama = sc.nextLine().trim();
    if (nama.isEmpty()) nama = "Pelanggan";

    Member baru = memberDB.registerMember(nama);
    System.out.println("\n[OK] Member baru berhasil dibuat!");
    System.out.println("  Nama : " + baru.getNama());
    System.out.println("  Kode : " + baru.getKode());
    System.out.println("  Poin : " + baru.getPoin());
    if (baru.isTaxExempt())  System.out.println("  Benefit : Bebas Pajak");
    if (baru.hasBonusPoin()) System.out.println("  Benefit : Bonus Poin x2");
    return baru;
  }

  private static PaymentChannel selectChannel(Order order, Member member) {
    double subtotal = 0, tax = 0;
    for (OrderItem oi : order.getFoodItems()) {
      subtotal += TaxCalculator.calculateSubtotal(oi);
      tax      += TaxCalculator.calculateTax(oi, member);
    }
    for (OrderItem oi : order.getDrinkItems()) {
      subtotal += TaxCalculator.calculateSubtotal(oi);
      tax      += TaxCalculator.calculateTax(oi, member);
    }
    double totalPajak = subtotal + tax;

    System.out.println(line('=', 60));
    System.out.println("                  PILIH CHANNEL PEMBAYARAN");
    System.out.println(line('=', 60));
    System.out.printf("  Total tagihan (setelah pajak): Rp %,.0f.000%n", totalPajak);
    System.out.println();
    System.out.println("  1. Tunai   - tidak ada diskon");
    System.out.println("  2. QRIS    - diskon 5%");
    System.out.println("  3. eMoney  - diskon 7%, biaya admin Rp 20.000");
    System.out.println();

    while (true) {
      System.out.print("Pilih (1/2/3 | CC=batal): ");
      String in = sc.nextLine().trim();
      if (in.equalsIgnoreCase("CC")) return null;

      PaymentChannel ch = switch (in) {
        case "1" -> new Tunai();
        case "2" -> new QRIS();
        case "3" -> new EMoney();
        default  -> null;
      };

      if (ch == null) { System.out.println("[ERROR] Pilihan tidak valid."); continue; }

      double finalAmt = ch.calculateFinal(totalPajak);
      System.out.printf("%n  Channel     : %s%n", ch.getName());
      if (ch.getDiscountRate() > 0)
        System.out.printf("  Diskon      : %.0f%%  (-Rp %,.0f.000)%n",
            ch.getDiscountRate() * 100, totalPajak * ch.getDiscountRate());
      if (ch.getAdminFee() > 0)
        System.out.printf("  Biaya admin : +Rp %,.0f.000%n", ch.getAdminFee());
      System.out.printf("  Total bayar : Rp %,.0f.000%n", finalAmt);

      if (ch.requiresBalanceCheck()) {
        while (true) {
          System.out.printf("%nSaldo %s (ribuan Rp, CC=batal): ", ch.getName());
          String raw = sc.nextLine().trim();
          if (raw.equalsIgnoreCase("CC")) return null;
          try {
            double saldo = Double.parseDouble(raw);
            if (!ch.isSaldoCukup(saldo, finalAmt)) {
              System.out.printf("[ERROR] Saldo kurang. Butuh Rp %,.0f.000, saldo Rp %,.0f.000.%n",
                  finalAmt, saldo);
            } else {
              System.out.printf("[OK] Kembalian: Rp %,.0f.000%n%n", saldo - finalAmt);
              break;
            }
          } catch (NumberFormatException e) {
            System.out.println("[ERROR] Masukkan angka.");
          }
        }
      }
      return ch;
    }
  }

  private static Currency selectCurrency() {
    System.out.println(line('=', 60));
    System.out.println("                PILIH MATA UANG PEMBAYARAN");
    System.out.println(line('=', 60));
    System.out.println("  1. IDR - Rupiah Indonesia  (tidak ada konversi)");
    System.out.println("  2. USD - Dolar Amerika     (1 USD  = Rp 15.000)");
    System.out.println("  3. JPY - Yen Jepang        (10 JPY = Rp 1.000)");
    System.out.println("  4. MYR - Ringgit Malaysia  (1 MYR  = Rp 4.000)");
    System.out.println("  5. EUR - Euro              (1 EUR  = Rp 14.000)");
    System.out.println();

    while (true) {
      System.out.print("Pilih (1-5 | CC=batal): ");
      String in = sc.nextLine().trim();
      if (in.equalsIgnoreCase("CC")) return null;

      Currency cur = switch (in) {
        case "1" -> new IDR();
        case "2" -> new USD();
        case "3" -> new JPY();
        case "4" -> new MYR();
        case "5" -> new EUR();
        default  -> null;
      };

      if (cur == null) { System.out.println("[ERROR] Pilihan tidak valid."); continue; }
      System.out.printf("%n  Mata uang: %s (%s)%n%n", cur.getName(), cur.getSymbol());
      return cur;
    }
  }

  private static void printMenu() {
    final int W = 60;
    System.out.println(line('=', W));
    System.out.printf("%-5s | %-34s | %s%n", "Kode", "Nama Menu", "Harga (Rp)");
    System.out.println(line('-', W));

    System.out.println("[ MAKANAN ]");
    List<Makanan> makanan = new ArrayList<>(menuManager.getMakananList());
    makanan.sort(Comparator.comparing(MenuItem::getCode));
    for (Makanan m : makanan)
      System.out.printf("%-5s | %-34s | %d%n",
          m.getCode(), m.getName(), m.getPrice());

    System.out.println(line('-', W));
    System.out.println("[ MINUMAN ]");
    List<Minuman> minuman = new ArrayList<>(menuManager.getMinumanList());
    minuman.sort(Comparator.comparing(MenuItem::getCode));
    for (Minuman m : minuman)
      System.out.printf("%-5s | %-34s | %d%n",
          m.getCode(), m.getName(), m.getPrice());

    System.out.println(line('=', W));
  }

private static void printOrderTable(Order order) {
  final int CW = 5;
  final int NW = 34;
  final int HW = 10;
  final int QW = 9;
  final int TW = CW + 3 + NW + 3 + HW + 3 + QW + 2;
  List<OrderItem> sortedFood  = new ArrayList<>(order.getFoodItems());
  List<OrderItem> sortedDrink = new ArrayList<>(order.getDrinkItems());
  sortedFood .sort(Comparator.comparingInt(oi -> oi.getMenuItem().getPrice()));
  sortedDrink.sort(Comparator.comparingInt(oi -> oi.getMenuItem().getPrice()));

  String lineEq   = "=".repeat(TW);
  String lineDash = "-".repeat(TW);

  System.out.println(lineEq);
  System.out.println("  PESANAN SAAT INI");
  System.out.println(lineEq);
  System.out.printf("%-" + CW + "s | %-" + NW + "s | %-" + HW + "s | %s%n",
      "Kode", "Nama Menu", "Harga (Rp)", "Kuantitas");
  System.out.println(lineDash);

  if (!sortedFood.isEmpty()) {
    System.out.println("[ MAKANAN ]");
    for (OrderItem oi : sortedFood) {
      String nama = trunc(oi.getMenuItem().getName(), NW);
      System.out.printf("%-" + CW + "s | %-" + NW + "s | %-" + HW + "d | %d%n",
          oi.getMenuItem().getCode(), nama,
          oi.getMenuItem().getPrice(), oi.getQuantity());
    }
  }

  if (!sortedFood.isEmpty() && !sortedDrink.isEmpty())
    System.out.println(lineDash);

  if (!sortedDrink.isEmpty()) {
    System.out.println("[ MINUMAN ]");
    for (OrderItem oi : sortedDrink) {
      String nama = trunc(oi.getMenuItem().getName(), NW);
      System.out.printf("%-" + CW + "s | %-" + NW + "s | %-" + HW   + "d | %d%n",
          oi.getMenuItem().getCode(), nama,
          oi.getMenuItem().getPrice(), oi.getQuantity());
    }
  }

  System.out.println(lineEq);
  System.out.println();
}

  private static void printBanner() {
    System.out.println("\n" + line('=', 60));
    System.out.println("           SISTEM KASIR KOHISOP");
    System.out.println(line('=', 60) + "\n");
  }

  private static void printCancelled() {
    System.out.println("\n" + line('=', 60));
    System.out.println("   Pesanan dibatalkan. Terima kasih sudah berkunjung!");
    System.out.println(line('=', 60));
  }

  private static String line(char ch, int n) { return String.valueOf(ch).repeat(n); }

  private static String trunc(String s, int max) {
    return s.length() <= max ? s : s.substring(0, max - 1) + "...";
  }
}