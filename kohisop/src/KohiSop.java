import java.util.Scanner;

public class KohiSop {

  private static final Scanner sc = new Scanner(System.in);
  private static final MenuManager menuManager = new MenuManager();

  public static void main(String[] args) {
    printWelcome();

    // 1. Pemesanan
    Order order = doOrderingWithQuantity();
    if (order == null) {
      printCancelled();
      return;
    }

    if (order.isEmpty()) {
      System.out.println("\n[INFO] Tidak ada pesanan. Program selesai.");
      return;
    }

    // 2. Pilih channel pembayaran
    PaymentChannel channel = selectPaymentChannel(order);
    if (channel == null) {
      printCancelled();
      return;
    }

    // 3. Pilih mata uang
    Currency currency = selectCurrency();
    if (currency == null) {
      printCancelled();
      return;
    }

    // 4. Cetak kuitansi (pajak dihitung di dalam Receipt)
    Receipt receipt = new Receipt(order, channel, currency);
    receipt.print();
  }

  // Pemesanan    
  private static Order doOrderingWithQuantity() {
    Order order = new Order();
    menuManager.displayMenu();

    System.out.println();
    System.out.println("  Ketentuan pemesanan:");
    System.out.println("  - Masukkan kode menu, tekan ENTER");
    System.out.println("  - Input kode yang sama untuk menambah kuantitas item tersebut");
    System.out.println("  - Input 'SUBMIT' untuk menyelesaikan pemesanan");
    System.out.println("  - Ketik 'CC' kapan saja untuk membatalkan seluruh pesanan");
    System.out.println("  - Batas: 5 jenis minuman dan 5 jenis makanan per pesanan");
    System.out.println("  - Kuantitas minuman: max 3 porsi | makanan: max 2 porsi");
    System.out.println();

    while (true) {
      if (order.isDrinkFull() && order.isFoodFull()) {
        System.out.println("[INFO] Batas pesanan tercapai (5 minuman + 5 makanan).");
        break;
      }

      printSlotInfo(order);
      System.out.print("Masukkan kode menu (SUBMIT untuk selesai): ");
      String kode = sc.nextLine().trim();

      if (kode.equalsIgnoreCase("CC")) return null;
      if (kode.isEmpty()) {
        System.out.println("[ERROR] Belum menginput pesanan. Masukkan minimal satu kode menu.");
        continue;
      }
      if (kode.equalsIgnoreCase("SUBMIT")) break;

      MenuItem item = menuManager.findByCode(kode);
      if (item == null) {
        System.out.println("[ERROR] Kode '" + kode + "' tidak dikenali. Coba lagi.");
        continue;
      }

      OrderItem existingItem = order.findItemByCode(kode);

      if (existingItem != null) {
        int remaining = item.getMaxQuantity() - existingItem.getQuantity();
        System.out.println();
        System.out.println("  Item dipilih : [" + item.getCode() + "] "
            + item.getName() + " - Rp" + item.getPrice() + ".000/porsi");
        System.out.printf("  Sudah dipesan: %d porsi  |  Maks: %d porsi  |  Sisa: %d porsi%n",
            existingItem.getQuantity(), item.getMaxQuantity(), remaining);

        if (remaining <= 0) {
          System.out.println("[ERROR] '" + item.getName()
              + "' sudah mencapai batas maksimal " + item.getMaxQuantity() + " porsi.");
          System.out.println();
          continue;
        }

        int addQty = readQuantity(item, remaining,
            "  Tambah kuantitas (1-" + remaining + " | Enter=1 | 0/S=batal item | CC=batal pesan): ");
        if (addQty == -1) return null;
        if (addQty == 0) {
          System.out.println("[INFO] Penambahan '" + item.getName() + "' dibatalkan.");
        } else {
          int prev = existingItem.getQuantity();
          existingItem.setQuantity(prev + addQty);
          System.out.printf("[OK] '%s' diperbarui: %d → %d porsi.%n%n",
              item.getName(), prev, existingItem.getQuantity());
        }

      } else {
        if (item instanceof Minuman && order.isDrinkFull()) {
          System.out.println("[ERROR] Batas 5 jenis minuman sudah tercapai.");
          continue;
        }
        if (item instanceof Makanan && order.isFoodFull()) {
          System.out.println("[ERROR] Batas 5 jenis makanan sudah tercapai.");
          continue;
        }

        System.out.println();
        System.out.println("  Item dipilih : [" + item.getCode() + "] "
            + item.getName() + " - Rp" + item.getPrice() + ".000/porsi");
        System.out.println("  Kategori     : " + item.getCategory()
            + "  |  Max kuantitas: " + item.getMaxQuantity() + " porsi");

        int qty = readQuantity(item, item.getMaxQuantity(),
            "  Kuantitas (1-" + item.getMaxQuantity()
                + " | Enter=1 | 0/S=batal item | CC=batal pesan): ");
        if (qty == -1) return null;
        if (qty == 0) {
          System.out.println("[INFO] '" + item.getName() + "' tidak ditambahkan.");
        } else {
          order.addItem(item, qty);
          System.out.println("[OK] '" + item.getName() + "' x" + qty + " berhasil ditambahkan.\n");
        }
      }

      if (!order.isEmpty()) printOrderSummary(order);
    }

    return order;
  }

  // Channel Pembayaran
  private static PaymentChannel selectPaymentChannel(Order order) {
    double subtotal = 0, tax = 0;
    for (OrderItem oi : order.getDrinkItems()) {
      subtotal += TaxCalculator.calculateSubtotal(oi);
      tax += TaxCalculator.calculateTax(oi);
    }
    for (OrderItem oi : order.getFoodItems()) {
      subtotal += TaxCalculator.calculateSubtotal(oi);
      tax += TaxCalculator.calculateTax(oi);
    }
    double totalAfterTax = subtotal + tax;

    System.out.println("=".repeat(60));
    System.out.println("          PILIH CHANNEL PEMBAYARAN");
    System.out.println("=".repeat(60));
    System.out.printf("  Total tagihan (setelah pajak): Rp%.0f.000%n", totalAfterTax);
    System.out.println();
    System.out.println("  1. Tunai    - tidak ada diskon");
    System.out.println("  2. QRIS     - diskon 5%");
    System.out.println("  3. eMoney   - diskon 7%, biaya admin Rp20.000");
    System.out.println();

    PaymentChannel channel = null;

    while (channel == null) {
      System.out.print("Pilih channel (1/2/3 | CC=batal): ");
      String input = sc.nextLine().trim();

      if (input.equalsIgnoreCase("CC")) return null;

      switch (input) {
        case "1" -> channel = new Tunai();
        case "2" -> channel = new QRIS();
        case "3" -> channel = new EMoney();
        default -> {
          System.out.println("[ERROR] Pilihan tidak valid. Masukkan 1, 2, atau 3.");
        }
      }
    }

    double finalAmount = channel.calculateFinal(totalAfterTax);

    System.out.println();
    System.out.printf("  Channel dipilih   : %s%n", channel.getName());
    if (channel.getDiscountRate() > 0)
      System.out.printf("  Diskon            : %.0f%%  (-Rp%.0f.000)%n",
          channel.getDiscountRate() * 100, totalAfterTax * channel.getDiscountRate());
    if (channel.getAdminFee() > 0)
      System.out.printf("  Biaya admin       : +Rp%.0f.000%n", channel.getAdminFee());
    System.out.printf("  Total yang dibayar: Rp%.0f.000%n", finalAmount);

    if (channel.requiresBalanceCheck()) {
      while (true) {
        System.out.printf("%nMasukkan saldo %s (ribuan Rp, CC=batal): ", channel.getName());
        String raw = sc.nextLine().trim();
        if (raw.equalsIgnoreCase("CC")) return null;
        try {
          double saldo = Double.parseDouble(raw);
          if (!channel.isSaldoCukup(saldo, finalAmount)) {
            System.out.printf("[ERROR] Saldo tidak mencukupi. Dibutuhkan Rp%.0f.000, saldo Rp%.0f.000.%n",
                finalAmount, saldo);
            System.out.println("        Masukkan saldo yang cukup atau ketik CC untuk ganti channel.");
          } else {
            System.out.printf("[OK] Saldo mencukupi. Kembalian: Rp%.0f.000%n%n",
                saldo - finalAmount);
            break;
          }
        } catch (NumberFormatException e) {
          System.out.println("[ERROR] Masukkan angka yang valid.");
        }
      }
    }

    return channel;
  }

  // Mata Uang
  private static Currency selectCurrency() {
    System.out.println("=".repeat(60));
    System.out.println("          PILIH MATA UANG PEMBAYARAN");
    System.out.println("=".repeat(60));
    System.out.println("  1. IDR - Rupiah Indonesia  (tidak ada konversi)");
    System.out.println("  2. USD - Dolar Amerika     (1 USD = Rp15.000)");
    System.out.println("  3. JPY - Yen Jepang        (10 JPY = Rp1.000)");
    System.out.println("  4. MYR - Ringgit Malaysia  (1 MYR  = Rp4.000)");
    System.out.println("  5. EUR - Euro              (1 EUR  = Rp14.000)");
    System.out.println();

    while (true) {
      System.out.print("Pilih mata uang (1-5) | CC=batal): ");
      String input = sc.nextLine().trim();

      if (input.equalsIgnoreCase("CC")) return null;

      Currency currency = switch (input) {
        case "1" -> new IDR();
        case "2" -> new USD();
        case "3" -> new JPY();
        case "4" -> new MYR();
        case "5" -> new EUR();
        default -> null;
      };

      if (currency == null) {
        System.out.println("[ERROR] Pilihan tidak valid. Masukkan angka 1 sampai 5.");
      } else {
        System.out.printf("%n  Mata uang dipilih: %s (%s)%n%n",
            currency.getName(), currency.getSymbol());
        return currency;
      }
    }
  }

  private static int readQuantity(MenuItem item, int maxAllowed, String prompt) {
    while (true) {
      System.out.print(prompt);
      String raw = sc.nextLine().trim();

      if (raw.equalsIgnoreCase("CC")) return -1;
      if (raw.equals("0") || raw.equalsIgnoreCase("S")) return 0;
      if (raw.isEmpty()) {
        System.out.println("  [OK] Kuantitas diset ke 1 (default).");
        return 1;
      }
      try {
        int qty = Integer.parseInt(raw);
        if (qty < 1 || qty > maxAllowed) {
          System.out.println("  [ERROR] Kuantitas harus antara 1 dan " + maxAllowed + ".");
          continue;
        }
        return qty;
      } catch (NumberFormatException e) {
        System.out.println("  [ERROR] Input tidak valid. Masukkan angka 1-"
            + maxAllowed + ", '0', 'S', ENTER, atau 'CC'.");
      }
    }
  }

  private static void printOrderSummary(Order order) {
    final int NAME_W = 32;
    final int INNER = 4 + 2 + NAME_W + 2 + 3;
    String dash = "─".repeat(INNER);

    System.out.println("  ┌─ Pesanan Saat Ini " + "─".repeat(INNER - 17) + "┐");

    if (order.hasDrinkItems()) {
      System.out.printf("  │ %-4s  %-" + NAME_W + "s  %s │%n", "Kode", "Minuman", "Qty");
      System.out.println("  │ " + dash + " │");
      for (OrderItem oi : order.getDrinkItems()) {
        String nama = truncate(oi.getMenuItem().getName(), NAME_W);
        System.out.printf("  │ %-4s  %-" + NAME_W + "s  %-3d │%n",
            oi.getMenuItem().getCode(), nama, oi.getQuantity());
      }
    }

    if (order.hasDrinkItems() && order.hasFoodItems()) {
      System.out.println("  │ " + dash + " │");
    }

    if (order.hasFoodItems()) {
      System.out.printf("  │ %-4s  %-" + NAME_W + "s  %s │%n", "Kode", "Makanan", "Qty");
      System.out.println("  │ " + dash + " │");
      for (OrderItem oi : order.getFoodItems()) {
        String nama = truncate(oi.getMenuItem().getName(), NAME_W);
        System.out.printf("  │ %-4s  %-" + NAME_W + "s  %-3d │%n",
            oi.getMenuItem().getCode(), nama, oi.getQuantity());
      }
    }

    System.out.println("  └" + "─".repeat(INNER + 2) + "┘");
    System.out.println();
  }

  private static String truncate(String s, int max) {
    return s.length() <= max ? s : s.substring(0, max - 1) + "…";
  }

  private static void printSlotInfo(Order order) {
    System.out.printf("  [Slot tersisa - Minuman: %d/5 | Makanan: %d/5]%n",
        order.getDrinkItems().size(), order.getFoodItems().size());
  }

  private static void printWelcome() {
    System.out.println("=".repeat(60));
    System.out.println("          Selamat Datang di KohiSop!");
  }

  private static void printCancelled() {
    System.out.println("\n" + "=".repeat(60));
    System.out.println("    Pesanan dibatalkan. Terima kasih sudah berkunjung!");
    System.out.println("=".repeat(60));
  }
}
