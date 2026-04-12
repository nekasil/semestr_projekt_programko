// DataManager.java
package Data;

import Zamestnanec.*;
import java.util.List;

public interface DataManager {
    void nacistData(Databaze databaze);
    void ulozitData(Databaze databaze);
    Zamestnanec nacistZamestnanceZeSouboru(String nazevSouboru, List<Zamestnanec> existujici);
    void ulozitZamestnanceDoSouboru(Zamestnanec zamestnanec, String nazevSouboru);
}