// main.java
package App;

import Data.Databaze;
import Data.DataManager;
import Data.SqlManager;
import Data.FileManager;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Databaze databaze = new Databaze();
        
        int volba = 0;
        DataManager dataManager = null;
        
        while (true) {
            System.out.println("Zvolte režim");
            System.out.println("1. SQL ");
            System.out.println("2. bez SQL");
            System.out.print("Volba: ");
            
            if (!scanner.hasNextLine()) {
                System.out.println("\nVstup skončil.");
                return;
            }
            
            try {
                volba = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Neplatná volba, zkuste znovu.\n");
                continue;
            }
            
            if (volba == 1) {
                dataManager = new SqlManager();
                ((SqlManager) dataManager).inicializace();
                break;
            } else if (volba == 2) {
                dataManager = new FileManager();
                break;
            } else {
                System.out.println("Neplatná volba, zkuste znovu.\n");
            }
        }
        
        Menu menu = new Menu(databaze, dataManager);
        menu.run();
        scanner.close();
    }
}