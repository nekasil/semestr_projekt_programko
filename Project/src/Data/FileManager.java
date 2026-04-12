// FileManager.java
package Data;

import java.io.*;
import java.util.*;
import Zamestnanec.*;

public class FileManager implements DataManager {
    private static final String ZAMESTNANCI_FILE = "zamestnanci.txt";
    private static final String SPOLUPRACE_FILE = "spoluprace.txt";

    @Override
    public void nacistData(Databaze databaze) {
        try {
            nacistZamestnance(databaze);
            nacistSpolupraci(databaze);
            System.out.println("Data úspěšně načtena ze souborů.");
        } catch (IOException e) {
            System.out.println("Chyba při načítání dat: " + e.getMessage());
        }
    }

    @Override
    public void ulozitData(Databaze databaze) {
        try {
            ulozitZamestnance(databaze.getAll());
            ulozitSpolupraci(databaze.getAll());
            System.out.println("Data úspěšně uložena do souborů.");
        } catch (IOException e) {
            System.out.println("Chyba při ukládání dat: " + e.getMessage());
        }
    }

    private void nacistZamestnance(Databaze databaze) throws IOException {
        File file = new File(ZAMESTNANCI_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts.length < 5) continue;

                int id = Integer.parseInt(parts[0].trim());
                String jmeno = parts[1].trim();
                String prijmeni = parts[2].trim();
                int rokNarozeni = Integer.parseInt(parts[3].trim());
                String skupina = parts[4].trim();

                Zamestnanec z = switch (skupina) {
                    case "Datový analytik" -> new DataAnalytik(id, jmeno, prijmeni, rokNarozeni, databaze.getAll());
                    case "Bezpečnostní specialista" -> new BezpSpecialista(id, jmeno, prijmeni, rokNarozeni);
                    default -> null;
                };

                if (z != null) databaze.pridatZamestnance(z);
            }
        }
    }

    private void nacistSpolupraci(Databaze databaze) throws IOException {
        File file = new File(SPOLUPRACE_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts.length < 3) continue;

                int idZamestnance = Integer.parseInt(parts[0].trim());
                int idKolegu = Integer.parseInt(parts[1].trim());
                UrovenSpoluprace uroven = UrovenSpoluprace.valueOf(parts[2].trim());
                databaze.pridatSpolupraci(idZamestnance, idKolegu, uroven);
            }
        }
    }

    private void ulozitZamestnance(List<Zamestnanec> zamestnanci) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ZAMESTNANCI_FILE))) {
            for (Zamestnanec z : zamestnanci) {
                writer.println(z.getId() + "|" + z.getJmeno() + "|" + z.getPrijmeni() + "|" + z.getRokNarozeni() + "|" + z.getSkupina());
            }
        }
    }

    private void ulozitSpolupraci(List<Zamestnanec> zamestnanci) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SPOLUPRACE_FILE))) {
            for (Zamestnanec z : zamestnanci) {
                for (Spoluprace s : z.getSpoluprace()) {
                    writer.println(z.getId() + "|" + s.getIdKolegu() + "|" + s.getUroven().name());
                }
            }
        }
    }

    // Načtení jednotlivého zaměstnance ze souboru
    public Zamestnanec nacistZamestnanceZeSouboru(String nazevSouboru, List<Zamestnanec> existujici) {
        try (BufferedReader reader = new BufferedReader(new FileReader(nazevSouboru))) {
            String line = reader.readLine();
            if (line == null || line.trim().isEmpty()) {
                System.out.println("Soubor je prázdný.");
                return null;
            }

            String[] parts = line.split("\\|");
            if (parts.length < 5) {
                System.out.println("Neplatný formát souboru.");
                return null;
            }

            int id = Integer.parseInt(parts[0].trim());
            String jmeno = parts[1].trim();
            String prijmeni = parts[2].trim();
            int rokNarozeni = Integer.parseInt(parts[3].trim());
            String skupina = parts[4].trim();

            Zamestnanec z = switch (skupina) {
                case "Datový analytik" -> new DataAnalytik(id, jmeno, prijmeni, rokNarozeni, existujici);
                case "Bezpečnostní specialista" -> new BezpSpecialista(id, jmeno, prijmeni, rokNarozeni);
                default -> null;
            };

            if (z != null) {
                System.out.println("Zaměstnanec úspěšně načten z \"" + nazevSouboru + "\".");
            }
            return z;
        } catch (IOException e) {
            System.out.println("Chyba při načítání ze souboru: " + e.getMessage());
            return null;
        }
    }

    // Uložení jednotlivého zaměstnance do souboru
    public void ulozitZamestnanceDoSouboru(Zamestnanec zamestnanec, String nazevSouboru) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(nazevSouboru))) {
            writer.println(zamestnanec.getId() + "|" + zamestnanec.getJmeno() + "|" 
                + zamestnanec.getPrijmeni() + "|" + zamestnanec.getRokNarozeni() + "|" + zamestnanec.getSkupina());
            System.out.println("Zaměstnanec úspěšně uložen do \"" + nazevSouboru + "\".");
        } catch (IOException e) {
            System.out.println("Chyba při ukládání do souboru: " + e.getMessage());
        }
    }
}