package Zamestnanec;

public class BezpSpecialista extends Zamestnanec {

    private static final int    HRANICA_MALO_SPOLUPRACI = 2;
    private static final int    HRANICA_VELA_SPOLUPRACI = 10;
    private static final double HRANICA_NIZKA_KVALITA = 1.5;
    private static final double HRANICA_VYSOKA_KVALITA = 2.5;
    
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
        double skore = vypocetRizika();
        String uroven = ohodnotitRiziko(skore);

        System.out.println("Dovednost: Bezpečnostní specialista");
        System.out.printf("Rizikové skóre: %.2f / 100%n", skore);
        System.out.println("Úroveň rizika: " + uroven);
        System.out.println("Počet spolupracovníků: " + getPocetSpolupraci());
        System.out.printf("Průměrná kvalita: %.2f (škála 1–3)%n", getPriemernaKvalita());
    }

    public double vypocetRizika() {
        int pocet = getPocetSpolupraci();
        double prumer = getPriemernaKvalita();

        double rizikoKontaktov;
        if (pocet <= HRANICA_MALO_SPOLUPRACI) {
            rizikoKontaktov = (double) pocet / HRANICA_MALO_SPOLUPRACI * 20.0;
        } else if (pocet <= HRANICA_VELA_SPOLUPRACI) {
            rizikoKontaktov = 20.0 + (double)(pocet - HRANICA_MALO_SPOLUPRACI)
                    / (HRANICA_VELA_SPOLUPRACI - HRANICA_MALO_SPOLUPRACI) * 20.0;
        } else {
            rizikoKontaktov = 40.0 + Math.min(10.0, (pocet - HRANICA_VELA_SPOLUPRACI) * 0.5);
        }

        double rizikoKvality;
        if (pocet == 0) {
            rizikoKvality = 0.0;
        } else if (prumer <= HRANICA_NIZKA_KVALITA) {
            rizikoKvality = 50.0;
        } else if (prumer >= HRANICA_VYSOKA_KVALITA) {
            rizikoKvality = 5.0;
        } else {
            double pomer = (prumer - HRANICA_VYSOKA_KVALITA) 
                    / (HRANICA_NIZKA_KVALITA - HRANICA_VYSOKA_KVALITA);
            rizikoKvality = 5.0 + pomer * 45.0;
        }

        return rizikoKontaktov + rizikoKvality;
    }
    
    private String ohodnotitRiziko(double skore) {
        if (skore < 20.0) return "Nízké";
        if (skore < 50.0) return "Střední";
        if (skore < 75.0) return "Vysoké";
        return                   "Velmi vysoké";
    }
}
