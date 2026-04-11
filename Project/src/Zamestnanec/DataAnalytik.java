package Zamestnanec;

import java.util.ArrayList;
import java.util.List;

public class DataAnalytik extends Zamestnanec {

    private List<Zamestnanec> databaze;
    
    public DataAnalytik(String meno, String prijmeni, int rokNarozeni, List<Zamestnanec> databaze) {
        super(meno, prijmeni, rokNarozeni);
        this.databaze = databaze;
    }

public DataAnalytik(int id, String meno, String prijmeni, int rokNarozeni, List<Zamestnanec> databaze) {
        super(id, meno, prijmeni, rokNarozeni);
        this.databaze = databaze;
    }

    @Override
    public String getSkupina() {
        return "Datový analytik";
    }

    @Override
    public void dovednost() {
        System.out.println("Analyzuji data a vytvářím reporty.");
        // TODO
    }
}
