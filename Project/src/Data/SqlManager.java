package Data;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Zamestnanec.BezpSpecialista;
import Zamestnanec.DataAnalytik;
import Zamestnanec.Spoluprace;
import Zamestnanec.UrovenSpoluprace;
import Zamestnanec.Zamestnanec;

public class SqlManager implements DataManager {

    private static final String DB_URL = "jdbc:sqlite:zamestnanci.db";

    //Inicializace databáze
    public void inicializace() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS zamestnanci (
                 id          INTEGER PRIMARY KEY,
                 jmeno       TEXT    NOT NULL,
                 prijmeni    TEXT    NOT NULL,
                 rokNarozeni INTEGER NOT NULL,
                 skupina     TEXT    NOT NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS spoluprace (
                 id_zamestnance INTEGER NOT NULL,
                 id_kolegu      INTEGER NOT NULL,
                 uroven         TEXT    NOT NULL,
                 PRIMARY KEY (id_zamestnance, id_kolegu),
                 FOREIGN KEY (id_zamestnance) REFERENCES zamestnanci(id),
                 FOREIGN KEY (id_kolegu) REFERENCES zamestnanci(id)
                )
            """);

            System.out.println("SQL databáze inicializovaná.");
        } catch (SQLException e) {
            System.out.println("Chyba při inicializaci databáze: " + e.getMessage());
        }
    }

    // Ulozeni dat pri ukonceni
    public void ulozitData(Databaze databaze) {
        try (Connection conn = connect()) {
            conn.setAutoCommit(false);

            try {
                vymazatVsechno(conn);
                ulozitZamestnance(conn, databaze.getAll());
                ulozitVsechnySpoluprace(conn, databaze.getAll());
                conn.commit();
                System.out.println("Data úspešne uložena do SQL databáze.");
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Chyba při ukládání dat, zmeny boli vrátené: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.out.println("Chyba při připojení k databázi: " + e.getMessage());
        }
    }

    // Nacteni dat pri spusteni
    public void nacistData(Databaze databaze) {
        try (Connection conn = connect()) {

            nacteniZamestnancu(conn, databaze);
            nacteniSpolupraci(conn, databaze);
            System.out.println("Data úspešne načtena z SQL databáze.");

        } catch (SQLException e) {
            System.out.println("Chyba při načítání dat: " + e.getMessage());
        }
    }

    // Pomocne - ukladani
    private void ulozitZamestnance(Connection conn, List<Zamestnanec> zamestnanci) throws SQLException {
        String sql = "INSERT INTO zamestnanci (id, jmeno, prijmeni, rokNarozeni, skupina) VALUES (?, ?, ?, ? ,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Zamestnanec z : zamestnanci) {
                ps.setInt(1, z.getId());
                ps.setString(2, z.getJmeno());
                ps.setString(3, z.getPrijmeni());
                ps.setInt(4, z.getRokNarozeni());
                ps.setString(5, z.getSkupina());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void ulozitVsechnySpoluprace(Connection conn, List<Zamestnanec> zamestnanci) throws SQLException {
        String sql = "INSERT INTO spoluprace (id_zamestnance, id_kolegu, uroven) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Zamestnanec z : zamestnanci) {
                for (Spoluprace s : z.getSpoluprace()) {
                    ps.setInt(1, z.getId());
                    ps.setInt(2, s.getIdKolegu());
                    ps.setString(3, s.getUroven().name());
                    ps.addBatch();
                }
            }
            ps.executeBatch();
        }
    }

    private void vymazatVsechno(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM spoluprace");
            stmt.execute("DELETE FROM zamestnanci");
        }
    }

    // Pomocne - nacteni
    private void nacteniZamestnancu(Connection conn, Databaze databaze) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM zamestnanci")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String jmeno = rs.getString("jmeno");
                String prijmeni = rs.getString("prijmeni");
                int rokNarozeni = rs.getInt("rokNarozeni");
                String skupina = rs.getString("skupina");
                
                Zamestnanec z;
                switch (skupina) {
                    case "Datový analytik":
                        z = new DataAnalytik(id, jmeno, prijmeni, rokNarozeni, databaze.getAll());
                        break;
                    case "Bezpečnostní specialista":
                        z = new BezpSpecialista(id, jmeno, prijmeni, rokNarozeni, databaze.getAll());
                        break;
                    default:
                        throw new SQLException("Neznámá skupina: " + skupina);
                }
                databaze.pridatZamestnance(z);
            }
        }
    }
    
    private void nacteniSpolupraci(Connection conn, Databaze databaze) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM spoluprace")) {

            while (rs.next()) {
                int idZ = rs.getInt("id_zamestnance");
                int idK = rs.getInt("id_kolegu");
                UrovenSpoluprace uroven = UrovenSpoluprace.valueOf(rs.getString("uroven"));
                databaze.pridatSpolupraci(idZ, idK, uroven);
            }
        }
    }

    // Pomocne - spojeni
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Načtení jednotlivého zaměstnance ze souboru
    @Override
    public List<Zamestnanec> nacistZamestnanceZeSouboru(String nazevSouboru, List<Zamestnanec> existujici) {
        List<Zamestnanec> vysledok = new ArrayList<>();
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(nazevSouboru))) {
            String line;
            boolean prazdny = true;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                prazdny = false;

                String[] parts = line.split("\\|");
                if (parts.length < 5) {
                    System.out.println("Neplatný formát souboru na riadku: " + line);
                    continue;
                }

                int id = Integer.parseInt(parts[0].trim());
                String jmeno = parts[1].trim();
                String prijmeni = parts[2].trim();
                int rokNarozeni = Integer.parseInt(parts[3].trim());
                String skupina = parts[4].trim();

                Zamestnanec z = null;
                switch (skupina) {
                    case "Datový analytik":
                        z = new DataAnalytik(id, jmeno, prijmeni, rokNarozeni, existujici);
                        break;
                    case "Bezpečnostní specialista":
                        z = new BezpSpecialista(id, jmeno, prijmeni, rokNarozeni);
                        break;
                    default:
                        System.out.println("Neznámá skupina: " + skupina);
                }

                if (z != null) {
                    vysledok.add(z);
                    System.out.println("Zaměstnanec úspěšně načten z \"" + nazevSouboru + "\".");
                }
            }

            if (prazdny) {
                System.out.println("Soubor je prázdný.");
            }
            
        } catch (java.io.IOException e) {
            System.out.println("Chyba při načítání ze souboru: " + e.getMessage());
        }
        return vysledok;
    }

    // Uložení jednotlivého zaměstnance do souboru
    @Override
    public void ulozitZamestnanceDoSouboru(Zamestnanec zamestnanec, String nazevSouboru) {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(nazevSouboru, true))) {
            writer.println(zamestnanec.getId() + "|" + zamestnanec.getJmeno() + "|" 
                + zamestnanec.getPrijmeni() + "|" + zamestnanec.getRokNarozeni() + "|" + zamestnanec.getSkupina());
            System.out.println("Zaměstnanec úspěšně uložen do \"" + nazevSouboru + "\".");
        } catch (java.io.IOException e) {
            System.out.println("Chyba při ukládání do souboru: " + e.getMessage());
        }
    }
}
