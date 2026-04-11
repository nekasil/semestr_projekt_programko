package Zamestnanec;

public class BezpSpecialista extends Zamestnanec {
    
    public BezpSpecialista(String jmeno, String prijmeni, int rokNarozeni) {
        super(jmeno, prijmeni, rokNarozeni);
    }

    public BezpSpecialista(int id, String jmeno, String prijmeni, int rokNarozeni) {
        super(id, jmeno, prijmeni, rokNarozeni);
    }

    @Override
    public String getSkupina() {
        return "Bezpečnostní specialista";
    }

    @Override
    public void dovednost() {
        System.out.println("TODO");
        // TODO
    }
    
}
