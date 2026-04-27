import java.util.LinkedHashMap;
import java.util.Map;

public class MenuManager {

  private Map<String, MenuItem> menuMap;

  public MenuManager() {
    menuMap = new LinkedHashMap<>();
    initMenu();
  }


  private void initMenu() {

    // Minuman
    addItem(new Minuman("A1", "Caffe Latte", 46));
    addItem(new Minuman("A2", "Cappuccino", 46));
    addItem(new Minuman("E1", "Caffe Americano", 37));
    addItem(new Minuman("E2", "Caffe Mocha", 55));
    addItem(new Minuman("E3", "Caramel Macchiato", 59));
    addItem(new Minuman("E4", "Asian Dolce Latte", 55));
    addItem(new Minuman("E5", "Double Shots Iced Shaken Espresso", 50));
    addItem(new Minuman("B1", "Freshly Brewed Coffee", 23));
    addItem(new Minuman("B2", "Vanilla Sweet Cream Cold Brew", 50));
    addItem(new Minuman("B3", "Cold Brew", 44));

    // Makanan
    addItem(new Makanan("M1", "Petemania Pizza", 112));
    addItem(new Makanan("M2", "Mie Rebus Super Mario", 35));
    addItem(new Makanan("M3", "Ayam Bakar Goreng Rebus Spesial", 72));
    addItem(new Makanan("M4", "Soto Kambing Iga Guling", 124));
    addItem(new Makanan("S1", "Singkong Bakar A La Carte", 37));
    addItem(new Makanan("S2", "Ubi Cilembu Bakar Arang", 58));
    addItem(new Makanan("S3", "Tempe Mendoan", 18));
    addItem(new Makanan("S4", "Tahu Bakso Extra Telur", 28));
  }


  private void addItem(MenuItem item) {
    menuMap.put(item.getCode().toUpperCase(), item);
  }


  public MenuItem findByCode(String code) {
    return menuMap.get(code.toUpperCase());
  }

  public void displayMenu() {
  String line = "=".repeat(60);

  System.out.println(line);
  System.out.printf("%-5s | %-35s | %10s%n", "Kode", "Nama Menu", "Harga");
  System.out.println(line);

  for (MenuItem item : menuMap.values()) {
    System.out.printf("%-5s | %-35s | Rp%6d.000%n",
      item.getCode(),
      item.getName(),
      item.getPrice()
    );
  }

  System.out.println(line);
}

}