package Zamestnanec;

import java.util.ArrayList;
import java.util.List;

public abstract class Zamestnanec {
    private static int nextId = 1;

    protected final int id;
    protected String jmeno;
    protected String prijmeni;
    protected int rokNarozeni;
    protected List<Spoluprace> spoluprace;

    public Zamestnanec(String jmeno, String prijmeni, int rokNarozeni) {
        this.id = nextId++;
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.rokNarozeni = rokNarozeni;
        this.spoluprace = new ArrayList<>();
    }

    // konstruktor pre nacitanie zo suboru s id
    public Zamestnanec(int id, String jmeno, String prijmeni, int rokNarozeni) {
        this.id = id;
        if (id >= nextId) nextId = id + 1;
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.rokNarozeni = rokNarozeni;
        this.spoluprace = new ArrayList<>();
    }

    // -- Gettery a settery --

    public int getId() {
        return id;
    }
    public String getJmeno() {
        return jmeno;
    }
    public String getPrijmeni() {
        return prijmeni;
    }
    public int getRokNarozeni() {
        return rokNarozeni;
    }

    // Pravdepodobne useless
    /*
    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }
    public void setPrijmeni(String prijmeni) {
        this.prijmeni = prijmeni;
    }
    public void setRokNarozeni(int rokNarozeni) {
        this.rokNarozeni = rokNarozeni;
    }
    */

    // -- Metody skupiny zamestnance --

    public abstract void dovednost();

    public abstract String getSkupina();

    // Metoda na vytvaranie zamestnancov

    public static Zamestnanec vytvorZamestnance(int skupina, String jmeno, String prijmeni, int rokNarozeni, List<Zamestnanec> databaze) {
        return switch (skupina) {
            case 1 -> new DataAnalytik(jmeno, prijmeni, rokNarozeni, databaze);
            case 2 -> new BezpSpecialista(jmeno, prijmeni, rokNarozeni);
            default -> throw new IllegalArgumentException("Neplatná skupina: " + skupina);
        };
    }

    // -- Správa spolupráce --

    public void pridatSpolupraci(Spoluprace spoluprace) {
        for (Spoluprace s : this.spoluprace) {
            if (s.getIdKolegu() == spoluprace.getIdKolegu()) {
                System.out.println("Spoluprace mezi " + this.jmeno + " " + this.prijmeni + " a kolegou s ID " + spoluprace.getIdKolegu() + " již existuje.");
                return;
            }
        }
        this.spoluprace.add(spoluprace);
        
    }

    public void odebratSpolupraci(int idKolegu) {
        this.spoluprace.removeIf(s -> s.getIdKolegu() == idKolegu);
    }

    public List<Spoluprace> getSpoluprace() {
        return this.spoluprace;
    }

    public boolean maSpolupraci(int idKolegu) {
        for (Spoluprace s : this.spoluprace) {
            if (s.getIdKolegu() == idKolegu) {
                return true;
            }
        }
        return false;
    }

    public int getPocetSpolupraci() {
        return this.spoluprace.size();
    }

    // -- Statistiky

    // -- Vypis informacii--
    public void vypisInfo() {
        System.out.println("\n=== Informace o zaměstnanci ===");
        System.out.println("ID: " + id);
        System.out.println("Jméno: " + jmeno);
        System.out.println("Příjmení: " + prijmeni);
        System.out.println("Rok narození: " + rokNarozeni);
        System.out.println("Skupina: " + getSkupina());
        System.out.println("Počet spoluprací: " + spoluprace.size());
        if (!spoluprace.isEmpty()) {
            System.out.println("Spolupráce:");
            for (Spoluprace s : spoluprace) {
                System.out.println("  - ID kolegy: " + s.getIdKolegu() + ", Úroveň: " + s.getUroven());
            }
        }
    }

    @Override
    public String toString() {
        return "[" + id + "] " + jmeno + " " + prijmeni + " (" + rokNarozeni + ") - " + getSkupina();
    }
}
