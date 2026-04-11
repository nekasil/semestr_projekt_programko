package Zamestnanec;

public class Spoluprace {
    private final int idKolegu;
    private UrovenSpoluprace uroven;

    public Spoluprace(int idKolegu, UrovenSpoluprace uroven) {
        this.idKolegu = idKolegu;
        this.uroven = uroven;
    }

    public int getIdKolegu() {
        return idKolegu;
    }

    public UrovenSpoluprace getUroven() {
        return uroven;
    }
    
    public void setUroven(UrovenSpoluprace uroven) {
        this.uroven = uroven;
    }

    @Override
    public String toString() {
        return "ID kolegu: " + idKolegu + " | Úroveň: " + uroven;
    }
}
