package Data;
import java.util.*;
import Zamestnanec.*;

public class Databaze {
    private final Map<Integer, Zamestnanec> zamestnanci = new HashMap<>();

    // a) Přidání zaměstnance
    public void pridatZamestnance(Zamestnanec zamestnanec) {
        zamestnanci.put(zamestnanec.getId(), zamestnanec);
        System.out.println("Přidán zaměstnanec: " + zamestnanec);
    }

    // b) Přidání spolupráce
    public void pridatSpolupraci(int idZamestnance, int idKolegu, UrovenSpoluprace uroven) {
        Zamestnanec z = najdiPodleId(idZamestnance);
        Zamestnanec kolega = najdiPodleId(idKolegu);

        if (z == null || kolega == null) {
            System.out.println("Zaměstnanec nebo kolega s daným ID neexistuje.");
            return;
        }
        if (idZamestnance == idKolegu) {
            System.out.println("Nelze přidat spolupráci sám se sebou.");
            return;
        }
        
        // Kontrola, zda spolupráce již neexistuje
        if (z.existujeSpolupraceS(idKolegu)) {
            System.out.println("Spolupráce mezi těmito zaměstnanci již existuje.");
            return;
        }

        // Přidání spolupráce oběma směry
        z.pridatSpolupraci(new Spoluprace(idKolegu, uroven));
        kolega.pridatSpolupraci(new Spoluprace(idZamestnance, uroven));
        System.out.println("Přidána vzájemná spolupráce");
    }

    // c) Odebrani zamestnance
    public void odebratZamestnance(int id) {
        if (!zamestnanci.containsKey(id)) {
            System.out.println("Zaměstnanec s ID " + id + " neexistuje.");
            return;
        }

        for (Zamestnanec z : zamestnanci.values()) {
            z.odebratSpolupraci(id);
        }
        zamestnanci.remove(id);
        System.out.println("Zaměstnanec s ID " + id + " byl odebrán.");
    }

    // d) Vyhledání zaměstnance podle ID
    public Zamestnanec najdiPodleId(int id) {
        return zamestnanci.get(id);
    }

    public void vypisZamestnance(int id) {
        Zamestnanec z = najdiPodleId(id);
        if (z == null) {
            System.out.println("Zaměstnanec nenalezen.");
            return;
        }
        z.vypisInfo(); //TODO
    }

    // e) Spuštení dovednosti zaměstnance
    public void spustitDovednost(int id) {
        Zamestnanec z = najdiPodleId(id);
        if (z == null) {
            System.out.println("Zaměstnanec nenalezen.");
            return;
        }
        z.dovednost(); //TODO
    }

    // f) abecedni vypis zamestnancu
    public void vypisAbecedne() {
        //TODO
    }

    // g) Statistiky
    public void vypisStatistiky() {
        if (zamestnanci.isEmpty()) {
            System.out.println("Databáze je prázdná.");
            return;
        }

        Map<UrovenSpoluprace, Integer> pocty = new HashMap<>();
        for (UrovenSpoluprace u : UrovenSpoluprace.values()) pocty.put(u, 0);

        for (Zamestnanec z : zamestnanci.values()) {
            for (Spoluprace s : z.getSpoluprace()) {
                pocty.merge(s.getUroven(), 1, Integer::sum);
            }
        }

        UrovenSpoluprace prevazujici = Collections.max(
            pocty.entrySet(), Map.Entry.comparingByValue()
        ).getKey();

        Zamestnanec nejvicVazeb = Collections.max(
            zamestnanci.values(),
            Comparator.comparingInt(Zamestnanec::getPocetSpolupraci)
        );

        System.out.println("=== Statistiky ===");
        System.out.println("Převažující kvalita spolupráce: " + prevazujici);
        System.out.println("Zaměstnanec s nejvíce vazbami: " + nejvicVazeb + " (" + nejvicVazeb.getPocetSpolupraci() + " vazeb)");
    }

    // h) Vypis poctu zamestnancu
    public void vypisPoctySkupin(){
        //TODO
    }


    // Pomocne metody
    public List<Zamestnanec> getAll() {
        return new ArrayList<>(zamestnanci.values());
    }

    public boolean jePrazdna() {
        return zamestnanci.isEmpty();
    }
}
