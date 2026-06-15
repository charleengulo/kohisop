import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MenuManager {

  private final Map<String, MenuItem> menuMap = new LinkedHashMap<>();
  private final List<Makanan> makananList = new ArrayList<>();
  private final List<Minuman> minumanList = new ArrayList<>();

  public MenuManager() {
    initMenu();
    makananList.sort(Comparator.comparingInt(MenuItem::getPrice));
    minumanList.sort(Comparator.comparingInt(MenuItem::getPrice));
  }

  private void initMenu() {
    addItem(new Makanan("M1", "Petemania Pizza",                112));
    addItem(new Makanan("M2", "Mie Rebus Super Mario",           35));
    addItem(new Makanan("M3", "Ayam Bakar Goreng Rebus Spesial", 72));
    addItem(new Makanan("M4", "Soto Kambing Iga Guling",        124));
    addItem(new Makanan("S1", "Singkong Bakar A La Carte",       37));
    addItem(new Makanan("S2", "Ubi Cilembu Bakar Arang",         58));
    addItem(new Makanan("S3", "Tempe Mendoan",                   18));
    addItem(new Makanan("S4", "Tahu Bakso Extra Telur",          28));

    addItem(new Minuman("A1", "Caffe Latte",                     46));
    addItem(new Minuman("A2", "Cappuccino",                      46));
    addItem(new Minuman("B1", "Freshly Brewed Coffee",           23));
    addItem(new Minuman("B2", "Vanilla Sweet Cream Cold Brew",   50));
    addItem(new Minuman("B3", "Cold Brew",                       44));
    addItem(new Minuman("E1", "Caffe Americano",                 37));
    addItem(new Minuman("E2", "Caffe Mocha",                     55));
    addItem(new Minuman("E3", "Caramel Macchiato",               59));
    addItem(new Minuman("E4", "Asian Dolce Latte",               55));
    addItem(new Minuman("E5", "Double Shots Iced Shaken Espresso", 50));
  }

  private void addItem(MenuItem item) {
    menuMap.put(item.getCode().toUpperCase(), item);
    if (item instanceof Makanan) makananList.add((Makanan) item);
    else minumanList.add((Minuman) item);
  }

  public MenuItem findByCode(String code) {
    if (code == null) return null;
    return menuMap.get(code.toUpperCase());
  }

  public List<MenuItem> getOrderedMenu() {
    List<MenuItem> all = new ArrayList<>();
    all.addAll(makananList);
    all.addAll(minumanList);
    return all;
  }

  public List<Makanan> getMakananList() { return makananList; }
  public List<Minuman> getMinumanList() { return minumanList; }

  public void displayMenu() {
    String sep = "=".repeat(60);
    System.out.println(sep);
    System.out.printf("%-5s | %-35s | %s%n", "Kode", "Nama Menu", "Harga");
    System.out.println(sep);
    System.out.println("[ MAKANAN ]");
    for (Makanan m : makananList)
      System.out.printf("%-5s | %-35s | Rp %d.%n",
          m.getCode(), m.getName(), m.getPrice());
    System.out.println("\n[ MINUMAN ]");
    for (Minuman m : minumanList)
      System.out.printf("%-5s | %-35s | Rp %d.%n",
          m.getCode(), m.getName(), m.getPrice());
    System.out.println(sep);
  }
}