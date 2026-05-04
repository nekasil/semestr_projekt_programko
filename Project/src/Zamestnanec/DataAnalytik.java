package Zamestnanec;

import java.util.ArrayList;
import java.util.List;

public class DataAnalytik extends Zamestnanec {

    private List<Zamestnanec> databaze;

    public DataAnalytik(String jmeno, String prijmeni, int rokNarozeni, List<Zamestnanec> databaze) {
        super(jmeno, prijmeni, rokNarozeni);
        this.databaze = databaze;
    }

    public DataAnalytik(int id, String jmeno, String prijmeni, int rokNarozeni, List<Zamestnanec> databaze) {
        super(id, jmeno, prijmeni, rokNarozeni);
        this.databaze = databaze;
    }

    public void nastavitDatabazi(List<Zamestnanec> databaze) { this.databaze = databaze; }

    @Override
    public String getSkupina() { return "Datový analytik"; }

    /**
     * Dovednost: Najde kolegu, se kterým má tento analytik
     * nejvíce společných spolupracovníků.
     */
    @Override
    public void dovednost() {
        int maxSpolecnych = -1;
        Zamestnanec najlepsi = null;

        List<Integer> moeIdentifikatory = new ArrayList<>();
        for (Spoluprace s : getSpoluprace()) {
            moeIdentifikatory.add(s.getIdKolegu());
        }

        for (Spoluprace moje : getSpoluprace()) {
            Zamestnanec kolega = najdiZamestnancePoId(moje.getIdKolegu());
            if (kolega == null) continue;

            int pocetSpolecnych = 0;
            for (Spoluprace kolegova : kolega.getSpoluprace()) {
                if (moeIdentifikatory.contains(kolegova.getIdKolegu())
                        && kolegova.getIdKolegu() != getId()) {
                    pocetSpolecnych++;
                }
            }

            if (pocetSpolecnych > maxSpolecnych) {
                maxSpolecnych = pocetSpolecnych;
                najlepsi = kolega;
            }
        }

        System.out.println("\n--- Dovednost: Datový analytik ---");
        if (najlepsi == null) {
            System.out.println("Nemám žádné kolegy nebo se nenašla shoda.");
        } else {
            System.out.println("Kolega s nejvíce společnými spolupracovníky:");
            System.out.println(najlepsi);
            System.out.println("Počet společných spolupracovníků: " + maxSpolecnych);
        }
    }

    private Zamestnanec najdiZamestnancePoId(int id) {
        for (Zamestnanec z : databaze) {
            if (z.getId() == id) return z;
        }
        return null;
    }
}
