package App;

import java.util.Scanner;
import Data.*;
import Zamestnanec.*;

public class Menu {
    private final Databaze databaze;
    private final SqlManager sqlManager;
    private final Scanner scanner;

    public Menu(Databaze databaze, SqlManager sqlManager) {
        this.databaze = databaze;
        this.sqlManager = sqlManager;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("Načítám data z SQL databáze...");
        sqlManager.nacistData(databaze);

        boolean running = true;
        while (running) {
            vypisMenu();
            int volba = nactiCislo();

            switch (volba) {
                case 1 -> pridatZamestnance();
                case 2 -> pridatSpolupraci();
                case 3 -> odebratZamestnance();
                case 4 -> vyhledatZamestnance();
                case 5 -> spustitDovednost();
                case 6 -> databaze.vypisAbecedne();
                case 7 -> databaze.vypisStatistiky();
                case 8 -> databaze.vypisPoctySkupin();
                case 0 -> running = false;
                default -> System.out.println("Neplatná volba, zkuste znovu.");
            }
        }

        System.out.println("Ukládám data do SQL databáze...");
        sqlManager.ulozitData(databaze);
        System.out.println("Program ukončen.");
    }

    private void vypisMenu() {
        System.out.println("""
                --- MENU ---
                1. Přidat zaměstnance
                2. Přidat spolupráci
                3. Odebrat zaměstnance
                4. Vyhledat zaměstnance podle ID
                5. Spustit dovednost zaměstnance
                6. Výpis všech zaměstnanců abecedně
                7. Výpis statistik o zaměstnancích
                8. Výpis počtu zaměstnanců v jednotlivých skupinách
                0. Ukončit program
                 """);
    }

    private void pridatZamestnance() {
        System.out.println("\n--- Přidat zaměstnance ---");
        System.out.println("Vyberte skupinu: ");
        System.out.println("1. Datový analytik");
        System.out.println("2. Bezpečnostní specialista");
        System.out.print("Skupina: ");
        int skupina = nactiCislo();

        System.out.print("Jméno: ");
        String jmeno = scanner.nextLine().trim();

        System.out.print("Příjmení: ");
        String prijmeni = scanner.nextLine().trim();

        System.out.print("Rok narození: ");
        int rokNarozeni = nactiCislo();

        if (jmeno.isEmpty() || prijmeni.isEmpty()) {
            System.out.println("Jméno a příjmení nesmí být prázdné.");
            return;
        }

        Zamestnanec novyZamestnanec = Zamestnanec.vytvorZamestnance(skupina, jmeno, prijmeni, rokNarozeni, databaze.getAll());
        databaze.pridatZamestnance(novyZamestnanec);
    }

    private void pridatSpolupraci() {
        System.out.println("\n--- Přidat spolupráci ---");

        System.out.print("ID zaměstnance: ");
        int idZamestnance = nactiCislo();

        System.out.print("ID kolegu: ");
        int idKolegu = nactiCislo();

        System.out.print("Kvalita spolupráce: ");
        System.out.println("1. Špatná");
        System.out.println("2. Průměrná");
        System.out.println("3. Dobrá");
        System.out.print("Volba: ");
        int volbaUrovne = nactiCislo();

        UrovenSpoluprace uroven = switch (volbaUrovne) {
            case 1 -> UrovenSpoluprace.SPATNA;
            case 2 -> UrovenSpoluprace.PRUMERNA;
            case 3 -> UrovenSpoluprace.DOBRA;
            default -> null;
        };

        if (uroven == null) {
            System.out.println("Neplatná volba kvality spolupráce.");
            return;
        }

        databaze.pridatSpolupraci(idZamestnance, idKolegu, uroven);
    }

    private void odebratZamestnance() {
        System.out.println("\n--- Odebrat zaměstnance ---");
        System.out.print("ID zaměstnance: ");
        int id = nactiCislo();
        
        Zamestnanec z = databaze.najdiPodleId(id);
        if (z == null) {
            System.out.println("Zaměstnanec s ID " + id + " neexistuje.");
            return;
        }

        System.out.print("Opravdu chcete odebrat " + z + "? (a/n): ");
        String potvrzeni = scanner.nextLine().trim().toLowerCase();
        if (potvrzeni.equals("a")) {
            databaze.odebratZamestnance(id);
        } else {
            System.out.println("Odebrání zrušeno.");
        }
    }

    private void vyhledatZamestnance() {
        System.out.println("\n--- Vyhledat zaměstnance podle ID ---");
        System.out.print("ID zaměstnance: ");
        int id = nactiCislo();
        databaze.vypisZamestnance(id);
    }

    private void spustitDovednost() {
        System.out.println("\n--- Spustit dovednost zaměstnance ---");
        System.out.print("ID zaměstnance: ");
        int id = nactiCislo();
        databaze.spustitDovednost(id);
    }

    private int nactiCislo() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Neplatný vstup, zadejte číslo: ");
            }
        }
    }
}
