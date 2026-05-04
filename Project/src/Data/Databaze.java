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

    // b) Přidání spolupráce - obousměrné, ale se RŮZNÝMI ÚROVNĚMI
    public void pridatSpolupraci(int idZamestnance, int idKolegu, UrovenSpoluprace uroven1, UrovenSpoluprace uroven2) {
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
        
        // Kontrola, zda spolupráce již neexistuje (obousměrná kontrola)
        if (z.existujeSpolupraceS(idKolegu) || kolega.existujeSpolupraceS(idZamestnance)) {
            System.out.println("Spolupráce mezi těmito zaměstnanci již existuje.");
            return;
        }

        // Přidání spolupráce oběma směry - AUTOMATICKÉ VYTVOŘENÍ VZÁJEMNÉ SPOLUPRÁCE s RŮZNÝMI ÚROVNĚMI
        z.pridatSpolupraci(new Spoluprace(idKolegu, uroven1));
        kolega.pridatSpolupraci(new Spoluprace(idZamestnance, uroven2));
        System.out.println("Přidána vzájemná spolupráce:");
        System.out.println("  " + z.getJmeno() + " -> " + kolega.getJmeno() + " (" + uroven1 + ")");
        System.out.println("  " + kolega.getJmeno() + " -> " + z.getJmeno() + " (" + uroven2 + ")");
    }
    
    // Přetížená metoda pro zpětnou kompatibilitu (stejná úroveň pro oba)
    public void pridatSpolupraci(int idZamestnance, int idKolegu, UrovenSpoluprace uroven) {
        pridatSpolupraci(idZamestnance, idKolegu, uroven, uroven);
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
        
        System.out.println(" INFORMACE O ZAMĚSTNANCI ");
        z.vypisInfo();
        
        System.out.println("\n Statistika spolupráce ");
        List<Spoluprace> spolupraci = z.getSpoluprace();
        
        if (spolupraci.isEmpty()) {
            System.out.println("Zaměstnanec nemá žádné vazby na spolupráci.");
            return;
        }
        
        System.out.println("Počet spoluprací: " + spolupraci.size());
        
        Map<UrovenSpoluprace, Integer> poctyUrovni = new HashMap<>();
        for (UrovenSpoluprace u : UrovenSpoluprace.values()) {
            poctyUrovni.put(u, 0);
        }
        
        for (Spoluprace s : spolupraci) {
            poctyUrovni.merge(s.getUroven(), 1, Integer::sum);
        }
        
        for (UrovenSpoluprace uroven : UrovenSpoluprace.values()) {
            if (poctyUrovni.get(uroven) > 0) {
                System.out.println("  " + uroven + ": " + poctyUrovni.get(uroven));
            }
        }
    }

    // e) Spuštění dovednosti zaměstnance dle jeho skupiny
    public void spustitDovednost(int id) {
        Zamestnanec z = najdiPodleId(id);
        if (z == null) {
            System.out.println("Zaměstnanec nenalezen.");
            return;
        }

        String skupina = z.getSkupina();
        System.out.println("\n=== Spuštění dovednosti pro skupinu: " + skupina + " ===");
        
        int pocet = 0;
        for (Zamestnanec zamestnanec : zamestnanci.values()) {
            if (zamestnanec.getSkupina().equals(skupina)) {
                zamestnanec.dovednost();
                pocet++;
            }
        }
        
        System.out.println("Dovednost spuštěna pro " + pocet + " zaměstnanců.");
    }

    // f) abecedni vypis zamestnancu/
    public void vypisAbecedne() {
        if (zamestnanci.isEmpty()) {
            System.out.println("Databáze je prázdná.");
            return;
        }

        System.out.println("\n=== ABECEDNÍ VÝPIS ZAMĚSTNANCŮ PODLE SKUPINY ===");
        
        Map<String, List<Zamestnanec>> skupiny = new TreeMap<>();
        for (Zamestnanec z : zamestnanci.values()) {
            skupiny.computeIfAbsent(z.getSkupina(), k -> new ArrayList<>()).add(z);
        }

        for (String skupina : skupiny.keySet()) {
            System.out.println("\n--- " + skupina + " ---");
            List<Zamestnanec> seznam = skupiny.get(skupina);
            seznam.sort(Comparator.comparing(Zamestnanec::getPrijmeni).thenComparing(Zamestnanec::getJmeno));
            for (Zamestnanec z : seznam) {
                System.out.println("  " + z.getPrijmeni() + " " + z.getJmeno());
            }
        }
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
        if (zamestnanci.isEmpty()) {
            System.out.println("Databáze je prázdná.");
            return;
        }

        System.out.println("\n=== POČET ZAMĚSTNANCŮ VE SKUPINÁCH ===");
        
        Map<String, Integer> pocty = new HashMap<>();
        for (Zamestnanec z : zamestnanci.values()) {
            pocty.merge(z.getSkupina(), 1, Integer::sum);
        }

        for (String skupina : new TreeSet<>(pocty.keySet())) {
            System.out.println(skupina + ": " + pocty.get(skupina) + " zaměstnanců");
        }
        
        System.out.println("\nCelkem: " + zamestnanci.size() + " zaměstnanců");
    }

    // i) Úprava ohodnocení existující spolupráce - druhý zaměstnanec si změní hodnocení
    public void upravitSpolupraci(int idZamestnance, int idKolegu, UrovenSpoluprace novaUroven) {
        Zamestnanec z = najdiPodleId(idZamestnance);
        
        if (z == null) {
            System.out.println("Zaměstnanec s ID " + idZamestnance + " neexistuje.");
            return;
        }
        
        if (!z.existujeSpolupraceS(idKolegu)) {
            System.out.println("Spolupráce mezi zaměstnanci s ID " + idZamestnance + " a " + idKolegu + " neexistuje.");
            return;
        }
        
        if (z.upravitSpolupraci(idKolegu, novaUroven)) {
            Zamestnanec kolega = najdiPodleId(idKolegu);
            System.out.println("Ohodnocení spolupráce upraveno:");
            System.out.println("  " + z.getJmeno() + " -> " + kolega.getJmeno() + " (" + novaUroven + ")");
        }
    }

    // Pomocne metody
    public List<Zamestnanec> getAll() {
        return new ArrayList<>(zamestnanci.values());
    }

    public boolean jePrazdna() {
        return zamestnanci.isEmpty();
    }
}
