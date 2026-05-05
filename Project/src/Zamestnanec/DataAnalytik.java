package Zamestnanec;

import java.util.ArrayList;
import java.util.List;

public class DataAnalytik extends Zamestnanec {

    private List<Zamestnanec> databaze;

    public DataAnalytik(String jmeno, String prijmeni, int rokNarozeni) {
        super(jmeno, prijmeni, rokNarozeni);
    }

    public DataAnalytik(String jmeno, String prijmeni, int rokNarozeni, List<Zamestnanec> databaze) {
        super(jmeno, prijmeni, rokNarozeni);
        this.databaze = databaze;
    }

    public DataAnalytik(int id, String jmeno, String prijmeni, int rokNarozeni) {
        super(id, jmeno, prijmeni, rokNarozeni);
    }

    public DataAnalytik(int id, String jmeno, String prijmeni, int rokNarozeni, List<Zamestnanec> databaze) {
        super(id, jmeno, prijmeni, rokNarozeni);
        this.databaze = databaze;
    }

    public void nastavitDatabazi(List<Zamestnanec> databaze) { this.databaze = databaze; }

    @Override
    public String getSkupina() { return "Datový analytik"; }

    @Override
    public void dovednost() {
        if (databaze == null) {
            System.out.println("Chyba: Databáze není inicializována!");
            return;
        }

        if (getSpoluprace().isEmpty()) {
            System.out.println("Nemám žádné spolupracovníky.");
            return;
        }

        Zamestnanec nejlepsi = najdiKolegaSNejviceSpolecnych();
        
        if (nejlepsi != null) {
            int pocet = spocitajSpolecne(nejlepsi);
            System.out.println(nejlepsi.getJmeno() + " " + nejlepsi.getPrijmeni() + 
                             " - " + pocet + " společných spolupracovníků");
        } else {
            System.out.println("Žádný spolupracovník nemá se mnou společné kolegy.");
        }
    }

    private Zamestnanec najdiKolegaSNejviceSpolecnych() {
        int maxSpolecnych = -1;
        Zamestnanec nejlepsi = null;
        List<Integer> mojeIds = ziskejMojeIdentifikatory();

        for (Spoluprace moje : getSpoluprace()) {
            Zamestnanec kolega = najdiZamestnancePoId(moje.getIdKolegu());
            if (kolega == null) continue;

            int pocetSpolecnych = 0;
            for (Spoluprace kolegova : kolega.getSpoluprace()) {
                if (mojeIds.contains(kolegova.getIdKolegu()) && kolegova.getIdKolegu() != getId()) {
                    pocetSpolecnych++;
                }
            }

            if (pocetSpolecnych > maxSpolecnych) {
                maxSpolecnych = pocetSpolecnych;
                nejlepsi = kolega;
            }
        }

        return nejlepsi;
    }

    private List<Integer> ziskejMojeIdentifikatory() {
        List<Integer> ids = new ArrayList<>();
        for (Spoluprace s : getSpoluprace()) {
            ids.add(s.getIdKolegu());
        }
        return ids;
    }

    private int spocitajSpolecne(Zamestnanec kolega) {
        List<Integer> mojeIds = ziskejMojeIdentifikatory();
        int pocet = 0;
        for (Spoluprace s : kolega.getSpoluprace()) {
            if (mojeIds.contains(s.getIdKolegu()) && s.getIdKolegu() != getId()) {
                pocet++;
            }
        }
        return pocet;
    }

    private Zamestnanec najdiZamestnancePoId(int id) {
        for (Zamestnanec z : databaze) {
            if (z.getId() == id) return z;
        }
        return null;
    }
}
