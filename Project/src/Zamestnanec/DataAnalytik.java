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
        if (databaze == null) {
            System.out.println("Chyba: Databáze zaměstnanců není inicializována!");
            return;
        }

        if (getSpoluprace().isEmpty()) {
            System.out.println("Nemám žádné spolupracovníky.");
            return;
        }

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

        if (najlepsi != null) {
            System.out.println(najlepsi.getJmeno() + " " + najlepsi.getPrijmeni() + " - " + maxSpolecnych + " společných spolupracovníků");
        } else {
            System.out.println("Žádný spolupracovník nemá se mnou společné kolegy.");
        }
    }

    private Zamestnanec najdiZamestnancePoId(int id) {
        for (Zamestnanec z : databaze) {
            if (z.getId() == id) return z;
        }
        return null;
    }
}
