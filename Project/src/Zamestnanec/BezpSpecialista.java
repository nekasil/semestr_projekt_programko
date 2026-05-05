package Zamestnanec;

import java.util.List;
import java.util.Scanner;

public class BezpSpecialista extends Zamestnanec {

    private static final int    HRANICE_MALO_SPOLUPRACI = 2;
    private static final int    HRANICE_VELA_SPOLUPRACI = 10;
    private static final double HRANICE_NIZKA_KVALITA = 1.5;
    private static final double HRANICE_VYSOKA_KVALITA = 2.5;
    
    private List<Zamestnanec> databaze;
    private Scanner scanner;
    
    public BezpSpecialista(String jmeno, String prijmeni, int rokNarozeni) {
        super(jmeno, prijmeni, rokNarozeni);
        this.scanner = new Scanner(System.in);
    }
    
    public BezpSpecialista(String jmeno, String prijmeni, int rokNarozeni, List<Zamestnanec> databaze) {
        super(jmeno, prijmeni, rokNarozeni);
        this.databaze = databaze;
        this.scanner = new Scanner(System.in);
    }

    public BezpSpecialista(int id, String jmeno, String prijmeni, int rokNarozeni) {
        super(id, jmeno, prijmeni, rokNarozeni);
        this.scanner = new Scanner(System.in);
    }

    public BezpSpecialista(int id, String jmeno, String prijmeni, int rokNarozeni, List<Zamestnanec> databaze) {
        super(id, jmeno, prijmeni, rokNarozeni);
        this.databaze = databaze;
        this.scanner = new Scanner(System.in);
    }
    
    public void nastavitDatabazi(List<Zamestnanec> databaze) { this.databaze = databaze; }

    @Override
    public String getSkupina() {
        return "Bezpečnostní specialista";
    }

    @Override
    public void dovednost() {
        if (databaze == null || databaze.isEmpty()) {
            System.out.println("Chyba: Databáze zaměstnanců není inicializována!");
            return;
        }
        
        System.out.println("\nDostupní zaměstnanci k vyhodnocení:");
        for (Zamestnanec z : databaze) {
            System.out.println("ID: " + z.getId() + " - " + z.getJmeno() + " " + z.getPrijmeni());
        }
        
        System.out.print("Zadejte ID zaměstnance, kterého chcete vyhodnotit: ");
        int idVybr = scanner.nextInt();
        scanner.nextLine();
        
        Zamestnanec vybrany = null;
        for (Zamestnanec z : databaze) {
            if (z.getId() == idVybr) {
                vybrany = z;
                break;
            }
        }
        
        if (vybrany == null) {
            System.out.println("Zaměstnanec s ID " + idVybr + " nebyl nalezen.");
            return;
        }
        
        System.out.println("\n=== Vyhodnocení rizikovosti: " + vybrany.getJmeno() + " " + vybrany.getPrijmeni() + " ===");
        
        double skore = vypocetRizikaProZamestnance(vybrany);
        String uroven = ohodnotitRiziko(skore);
        System.out.printf("Rizikové skóre: %.2f / 100 - %s%n", skore, uroven);
    }

    public double vypocetRizikaProZamestnance(Zamestnanec zamestnanec) {
        int pocet = zamestnanec.getPocetSpolupraci();
        double prumer = zamestnanec.getPriemernaKvalita();

        double rizikoKontaktu = vypocetRizikaKontaktu(pocet);
        double rizikoKvality = vypocetRizikaKvality(pocet, prumer);

        return rizikoKontaktu + rizikoKvality;
    }

    private double vypocetRizikaKontaktu(int pocet) {
        if (pocet <= HRANICE_MALO_SPOLUPRACI) {
            return (double) pocet / HRANICE_MALO_SPOLUPRACI * 20.0;
        } else if (pocet <= HRANICE_VELA_SPOLUPRACI) {
            return 20.0 + (double)(pocet - HRANICE_MALO_SPOLUPRACI)
                    / (HRANICE_VELA_SPOLUPRACI - HRANICE_MALO_SPOLUPRACI) * 20.0;
        } else {
            return 40.0 + Math.min(10.0, (pocet - HRANICE_VELA_SPOLUPRACI) * 0.5);
        }
    }

    private double vypocetRizikaKvality(int pocet, double prumer) {
        if (pocet == 0) {
            return 0.0;
        } else if (prumer <= HRANICE_NIZKA_KVALITA) {
            return 50.0;
        } else if (prumer >= HRANICE_VYSOKA_KVALITA) {
            return 5.0;
        } else {
            double pomer = (prumer - HRANICE_VYSOKA_KVALITA) 
                    / (HRANICE_NIZKA_KVALITA - HRANICE_VYSOKA_KVALITA);
            return 5.0 + pomer * 45.0;
        }
    }
    
    private String ohodnotitRiziko(double skore) {
        if (skore < 20.0) return "Nízké";
        if (skore < 50.0) return "Střední";
        if (skore < 75.0) return "Vysoké";
        return                   "Velmi vysoké";
    }
}
