package ba.unsa.etf.rpr;

import java.util.ArrayList;
import java.util.Objects;

public class Implikanta {

    // Implikanta je klasa koja sadrzi tri privatna atributa:
    //         - sadrzaneKonstituente - Prva zagrada kod implikanti u McCluskey tabeli
    //         - razlikeUKonstituentama - Ostale zagrade kod implikanti u McCluskey tabeli
    //         - brojSlogova - koristi se samo kod ispisa
    public ArrayList<Integer> getSadrzaneKonstituente() {
        return sadrzaneKonstituente;
    }

    public void setSadrzaneKonstituente(ArrayList<Integer> sadrzaneKonstituente) {
        this.sadrzaneKonstituente = sadrzaneKonstituente;
    }

    private ArrayList<Integer> sadrzaneKonstituente;

    public int getBrojSlogova() {
        return brojSlogova;
    }

    private int brojSlogova;

    public ArrayList<Integer> getRazlikeUKonstituentama() {
        return razlikeUKonstituentama;
    }

    public void setRazlikeUKonstituentama(ArrayList<Integer> razlikeUKonstituentama) {
        this.razlikeUKonstituentama = razlikeUKonstituentama;
    }

    private ArrayList<Integer> razlikeUKonstituentama;

    public Implikanta(int prvaKonstituenta, int drugaKonstituenta, int brojSlogova) {
        sadrzaneKonstituente = new ArrayList<>();
        sadrzaneKonstituente.add(prvaKonstituenta);
        sadrzaneKonstituente.add(drugaKonstituenta);

        razlikeUKonstituentama = new ArrayList<>();
        razlikeUKonstituentama.add(drugaKonstituenta - prvaKonstituenta);
        this.brojSlogova = brojSlogova;
    }

    public Implikanta(int konstituenta, int brojSlogova) {
        sadrzaneKonstituente = new ArrayList<>();
        sadrzaneKonstituente.add(konstituenta);
        razlikeUKonstituentama = new ArrayList<>();
        this.brojSlogova = brojSlogova;
    }

    public Implikanta(Implikanta prvaImplikanta, Implikanta drugaImplikanta) {
        sadrzaneKonstituente = new ArrayList<>(prvaImplikanta.getSadrzaneKonstituente());
        for (Integer konstituenta : drugaImplikanta.getSadrzaneKonstituente())
            sadrzaneKonstituente.add(konstituenta);
        razlikeUKonstituentama = new ArrayList<>(prvaImplikanta.getRazlikeUKonstituentama());
        getRazlikeUKonstituentama().add(drugaImplikanta.getSadrzaneKonstituente().get(0) - prvaImplikanta.getSadrzaneKonstituente().get(0));
        this.brojSlogova = prvaImplikanta.getBrojSlogova();
    }

    public static long dajIndeksBroja(int broj) {
        return Integer.toBinaryString(broj).chars().filter(ch -> ch == '1').count();
    }

    public static boolean daLiJeStepenDvojke(int broj) {
        return broj > 0 && ((broj & (broj - 1)) == 0);
    }


    public int dajSumuIndexaBrojevaKonstituenti() {
        int suma = 0;
        for (int konstituenta : sadrzaneKonstituente)
            suma += dajIndeksBroja(konstituenta);
        return suma;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Implikanta that = (Implikanta) o;
        return Objects.equals(sadrzaneKonstituente, that.sadrzaneKonstituente) &&
                Objects.equals(razlikeUKonstituentama, that.razlikeUKonstituentama);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sadrzaneKonstituente, razlikeUKonstituentama);
    }

    public void ispisiImplikantu() {

        // OR/TP finese za ispis
        StringBuffer slova = new StringBuffer();
        Character s = 'A';
        while (slova.length() != brojSlogova) {
            slova.append(s.toString());
            s++;
        }
        String binaryString = Integer.toBinaryString(sadrzaneKonstituente.get(0));
        while (binaryString.length() != brojSlogova) {
            binaryString = "0" + binaryString;
        }
        int j = 0;
        String osnovaSlova = new String(slova.toString());
        for (int i = 0; i < brojSlogova; i++) {
            if (binaryString.charAt(i) == '0') {
                slova.insert(j + 1, "'");
                j++;
            }
            j++;
        }
        for (int element : razlikeUKonstituentama) {
            Double logaritam = Math.log(element) / Math.log(2);
            Character slovo = osnovaSlova.charAt(brojSlogova - 1 - logaritam.intValue());
            int index = slova.indexOf(slovo.toString());
            slova.deleteCharAt(index);
            if (binaryString.charAt(brojSlogova - 1 - logaritam.intValue()) == '0')
                slova.deleteCharAt(index);
        }
        System.out.print(slova);
        if (slova.toString().length() == 0)
            System.out.print("1");
    }
}
