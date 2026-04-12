// Menu.java - úprava
package App;

import java.util.Scanner;
import Data.*;
import Zamestnanec.*;

public class Menu {
    private final Databaze databaze;
    private final DataManager dataManager;
    private final Scanner scanner;

    public Menu(Databaze databaze, DataManager dataManager) {
        this.databaze = databaze;
        this.dataManager = dataManager;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("Načítám data...");
        dataManager.nacistData(databaze);

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
                case 9 -> nacistZamestnanceZeSouboru();
                case 10 -> ulozitZamestnanceDoSouboru();
                case 0 -> running = false;
                default -> System.out.println("Neplatná volba, zkuste znovu.");
            }
        }

        System.out.println("Ukládám data...");
        dataManager.ulozitData(databaze);
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
                9. Načtení zaměstnance ze souboru
                10. Uložení zaměstnance do souboru
                0. Ukončit program
                 """);
    }

    private void pridatZamestnance() {
        System.out.println("\n--- Přidat zaměstnance ---");
        System.out.println("Vyberte skupinu: ");
        System.out.println("1. Datový analytik");
        System.out.println("2. Bezpečnostní specialista");
        System.out.println("3. Načtení zaměstanance ze souboru");
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

        System.out.print("ID kolegy: ");
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
                if (!scanner.hasNextLine()) {
                    return 0; // Konec vstupu
                }
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Neplatný vstup, zadejte číslo: ");
            }
        }
    }

    private void nacistZamestnanceZeSouboru() {
        System.out.println("\n--- Načtení zaměstnance ze souboru ---");
        System.out.print("Zadejte název souboru: ");
        String nazevSouboru = scanner.nextLine().trim();

        if (nazevSouboru.isEmpty()) {
            System.out.println("Název souboru nesmí být prázdný.");
            return;
        }

        Zamestnanec zamestnanec = dataManager.nacistZamestnanceZeSouboru(nazevSouboru, databaze.getAll());
        if (zamestnanec != null) {
            databaze.pridatZamestnance(zamestnanec);
        }
    }

    private void ulozitZamestnanceDoSouboru() {
        System.out.println("\n--- Uložení zaměstnance do souboru ---");
        System.out.print("ID zaměstnance: ");
        int id = nactiCislo();

        Zamestnanec zamestnanec = databaze.najdiPodleId(id);
        if (zamestnanec == null) {
            System.out.println("Zaměstnanec s ID " + id + " neexistuje.");
            return;
        }

        System.out.print("Zadejte název souboru: ");
        String nazevSouboru = scanner.nextLine().trim();

        if (nazevSouboru.isEmpty()) {
            System.out.println("Název souboru nesmí být prázdný.");
            return;
        }

        dataManager.ulozitZamestnanceDoSouboru(zamestnanec, nazevSouboru);
    }
}