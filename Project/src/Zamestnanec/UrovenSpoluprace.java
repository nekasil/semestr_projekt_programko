package Zamestnanec;

public enum UrovenSpoluprace {
    SPATNA(1, "Špatná"),
    PRUMERNA(2, "Průměrná"),
    DOBRA(3, "Dobrá");

    private final int hodnota;
    private final String popis;

    UrovenSpoluprace(int hodnota, String popis) {
        this.hodnota = hodnota;
        this.popis = popis;
    }

    public int getHodnota() {
        return hodnota;
    }

    @Override
    public String toString() {
        return popis;
    }
}
