import java.util.ArrayList;
import java.util.List;

public class MemberDatabase {

  private final List<Member> members = new ArrayList<>();

  public Member findByNama(String nama) {
    for (Member m : members)
        if (m.getNama().equalsIgnoreCase(nama)) return m;
    return null;
  }

public Member findByKode(String kode) {
    for (Member m : members) {
        if (m.getKode().equalsIgnoreCase(kode)) {
            return m;
        }
    }
    return null;
}

  public Member registerMember(String nama) {
    Member m = new Member(nama);
    members.add(m);
    return m;
  }

  public List<Member> getAll()  { return members; }
  public boolean isEmpty() { return members.isEmpty(); }

}
