package Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Zamestnanec.*;

public class SqlManager {

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

            vymazatVsechno(conn);
            ulozitZamestnance(conn, databaze.getAll());
            ulozitVsechnySpoluprace(conn, databaze.getAll());

            conn.commit();
            System.out.println("Data úspešne uložena do SQL databáze.");
        
        } catch (SQLException e) {
            System.out.println("Chyba při ukládání dat: " + e.getMessage());
        }
    }

    // Nacteni dat pri spusteni
    public void nacistData(Databaze databaze) {
        try (Connection conn = connect()) {

            List<Zamestnanec> zamestnanci = nacteniZamestnancu(conn, databaze.getAll());
            for (Zamestnanec z : zamestnanci) {
                databaze.pridatZamestnance(z);
            }

            nacteniSpolupraci(conn, databaze);
            System.out.println("Data úspešne načtena z SQL databáze.");

        } catch (SQLException e) {
            System.out.println("Chyba při načítání dat: " + e.getMessage());
        }
    }

    // Pomocne - ukladani
    private void ulozitZamestnance(Connection conn, List<Zamestnanec> zamestnanci) throws SQLException {
        String sql = "INSERT INTO zamestnanci (id, jmeno, prijmeni, rokNarozeni, skupina) " + "VALUES (?, ?, ?, ? ,?)";

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
        String sql = "INSERT INTO spoluprace (id_zamestnance, id_kolegu, uroven) " + "VALUES (?, ?, ?)";

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
        conn.createStatement().execute("DELETE FROM spoluprace");
        conn.createStatement().execute("DELETE FROM zamestnanci");
    }

    // Pomocne - nacteni
    private List<Zamestnanec> nacteniZamestnancu(Connection conn, List<Zamestnanec> existujici) throws SQLException {
        List<Zamestnanec> zoznam = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM zamestnanci")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String jmeno = rs.getString("jmeno");
                String prijmeni = rs.getString("prijmeni");
                int rokNarozeni = rs.getInt("rokNarozeni");
                String skupina = rs.getString("skupina");

                Zamestnanec z = switch (skupina) {
                    case "Datový analytik" -> new DataAnalytik(id, jmeno, prijmeni, rokNarozeni, existujici);
                    case "Bezpečnostní specialista" -> new BezpSpecialista(id, jmeno, prijmeni, rokNarozeni);
                    default -> throw new SQLException("Neznámá skupina: " + skupina);
                };

                zoznam.add(z);
            }
        }
        return zoznam;
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
}
