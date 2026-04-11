package App;
//hlavní část programu = spuštění apod

import Data.Databaze;
import Data.SqlManager;

public class main {
    public static void main(String[] args) {
        Databaze databaze = new Databaze();
        SqlManager sqlManager = new SqlManager();
        sqlManager.inicializace();

        Menu menu = new Menu(databaze, sqlManager);
        menu.run();
    }
}