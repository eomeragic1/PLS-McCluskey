package ba.unsa.etf.rpr;

import java.util.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("Unesite broj literala: ");
        Scanner ulaz = new Scanner(System.in);
        int brojSlogova = ulaz.nextInt();
        ulaz.nextLine();
        System.out.println("Unesite konstituente jedinice, jednu nakon druge, odvojene razmakom: ");
        // Primjer ulaza : 0 1 2 3 4 5 6 7 10 11 14 15
        ArrayList<Integer> konstituente = new ArrayList<>();
        String stringKonstituenti = ulaz.nextLine();
        if (stringKonstituenti.equals("")) {
            System.out.println("0");
            return;
        }
        String[] konstituenteKaoString = stringKonstituenti.split(" ");
        for (int i = 0; i < konstituenteKaoString.length; i++)
            konstituente.add(Integer.parseInt(konstituenteKaoString[i]));
        dajMinimalnuFormu(konstituente, brojSlogova);

        // write your code here
    }

    public static void dajMinimalnuFormu(ArrayList<Integer> konstituente, int brojSlogova) {
        // Minimizacija bazirana na metodi McCluskeya
        // matricaImplikanti - svaki red matrice predstavlja jednu kolonu u tabeli kod McCluskeya, tj. prvi red predstavlja implikante dobijene od konstituenti, drugi implikante dobijene iz prvog reda itd

        ArrayList<ArrayList<Implikanta>> matricaImplikanti = new ArrayList<>();
        int trenutnaIteracija = 0;
        matricaImplikanti.add(new ArrayList<>());

        // Prvo iz ArrayListe konstituenti pravi prvi red matrice implikanti
        for (int i = 0; i < konstituente.size() - 1; i++) {
            for (int j = i + 1; j < konstituente.size(); j++) {
                // Lema 1 Mccluskeya
                if (Implikanta.dajIndeksBroja(konstituente.get(j)) - Implikanta.dajIndeksBroja(konstituente.get(i)) == 1 && Implikanta.daLiJeStepenDvojke(konstituente.get(j) - konstituente.get(i))) {
                    Implikanta implikanta = new Implikanta(konstituente.get(i), konstituente.get(j), brojSlogova);
                    matricaImplikanti.get(0).add(implikanta);
                }
            }
        }
        // Generalizacija za n-tu i n+1 kolonu, tj iz informacija n-te se pravi n+1 kolona.
        // Ako je n+1 kolona prazna, znaci da nema daljeg sazimanja te smo dobili proste implikante
        while (!matricaImplikanti.get(trenutnaIteracija).isEmpty()) {
            matricaImplikanti.add(new ArrayList<>());
            for (int i = 0; i < matricaImplikanti.get(trenutnaIteracija).size() - 1; i++) {
                for (int j = i + 1; j < matricaImplikanti.get(trenutnaIteracija).size(); j++) {
                    Implikanta implikanta1 = matricaImplikanti.get(trenutnaIteracija).get(i);
                    Implikanta implikanta2 = matricaImplikanti.get(trenutnaIteracija).get(j);
                    if (implikanta1.getRazlikeUKonstituentama().equals(implikanta2.getRazlikeUKonstituentama()) && Implikanta.daLiJeStepenDvojke(implikanta2.getSadrzaneKonstituente().get(0) - implikanta1.getSadrzaneKonstituente().get(0)) && implikanta1.getSadrzaneKonstituente().get(implikanta1.getSadrzaneKonstituente().size() - 1) < implikanta2.getSadrzaneKonstituente().get(0) && implikanta1.dajSumuIndexaBrojevaKonstituenti() != implikanta2.dajSumuIndexaBrojevaKonstituenti()) {
                        Implikanta implikanta = new Implikanta(implikanta1, implikanta2);
                        matricaImplikanti.get(trenutnaIteracija + 1).add(implikanta);
                    }
                }
            }
            trenutnaIteracija++;
        }

        ArrayList<Implikanta> prosteImplikante = dajProsteImplikante(matricaImplikanti, konstituente, brojSlogova);
        ArrayList<Implikanta> nesvodljiveProsteImplikante = dajNesvodljivu(prosteImplikante, konstituente, brojSlogova);
        System.out.println("Jedna od minimalnih (a mozda i jedina) disjunktivnih normalnih formi: ");
        for (int i = 0; i < nesvodljiveProsteImplikante.size(); i++) {
            nesvodljiveProsteImplikante.get(i).ispisiImplikantu();
            if (i != nesvodljiveProsteImplikante.size() - 1)
                System.out.print(" V ");
        }

    }

    public static ArrayList<Implikanta> dajProsteImplikante(ArrayList<ArrayList<Implikanta>> matricaImplikanti, ArrayList<Integer> konstituente, int brojSlogova) {
        ArrayList<Implikanta> prosteImplikante = new ArrayList<>();

        Set<Integer> setKonstituenti = new TreeSet<>();

        // Prvo provjeravamo da li su neke konstituente ujedno i proste implikante
        // Uzimamo sve brojeve iz druge kolone tabele (nakon sazimanja konstituenti nekih) da bi mogli provjeriti ima li nekih prostih implikanti medju konstituentama
        for (Implikanta implikanta : matricaImplikanti.get(0)) {
            for (Integer element : implikanta.getSadrzaneKonstituente())
                setKonstituenti.add(element);
        }
        // Ako nadjemo jedan element iz konstituente da nema u skupu brojeva napravljenog u petlji iznad, znaci da je prosta implikanta
        for (Integer element : konstituente) {
            if (!setKonstituenti.contains(element))
                prosteImplikante.add(new Implikanta(element, brojSlogova));
        }
        int trenutnaIteracija = 0;
        // Generalizacija za n-tu i n+1 kolonu
        while (trenutnaIteracija < matricaImplikanti.size() - 2) {
            Set<Integer> noviSetKonstituenti = new TreeSet<>();
            for (Implikanta implikanta : matricaImplikanti.get(trenutnaIteracija + 1)) {
                for (Integer element : implikanta.getSadrzaneKonstituente())
                    noviSetKonstituenti.add(element);
            }
            for (Implikanta implikanta : matricaImplikanti.get(trenutnaIteracija)) {
                for (Integer element : implikanta.getSadrzaneKonstituente()) {
                    if (!noviSetKonstituenti.contains(element)) {
                        prosteImplikante.add(implikanta);
                    }
                }
            }
            trenutnaIteracija++;
        }
        // Elementi zadnje kolone tabele su sigurno proste implikante
        for (Implikanta implikanta : matricaImplikanti.get(matricaImplikanti.size() - 2)) {
            prosteImplikante.add(implikanta);
        }
        return prosteImplikante;
    }

    public static ArrayList<Implikanta> dajNesvodljivu(ArrayList<Implikanta> prosteImplikante, ArrayList<Integer> konstituente, int brojSlogova) {
        ArrayList<Implikanta> nesvodljivi = new ArrayList<>();
        // Nakon dobijanja prostih implikanti, prvo gledamo koje implikante samostalno "drze" jednu konstituentu, i te implikante MORAMO ukljuciti u NDNF
        for (int i = 0; i < prosteImplikante.size(); i++) {
            Set<Integer> set = new TreeSet<>();
            for (int j = 0; j < prosteImplikante.size(); j++) {
                if (i == j) continue;
                else {
                    for (Integer element : prosteImplikante.get(j).getSadrzaneKonstituente())
                        set.add(element);
                }
            }
            for (Integer element : prosteImplikante.get(i).getSadrzaneKonstituente()) {
                if (!set.contains(element)) {
                    nesvodljivi.add(prosteImplikante.get(i));
                    for (Integer element2 : prosteImplikante.get(i).getSadrzaneKonstituente())
                        set.add(element2);
                    break;
                }
            }
        }
        // Nakon toga, mogu postojati neke implikante koje dijele neke konstituente. Prvo u jedan skup stavljamo sve konstituente koje smo obuhvatili
        Set<Integer> set = new TreeSet<>();
        for (Implikanta element : nesvodljivi)
            for (Integer konstituenta : element.getSadrzaneKonstituente()) {
                set.add(konstituenta);
            }
        // Sada cemo dodavati jos prostih implikanti sve dok ne popunimo sve konstituente
        while (!Arrays.equals(set.toArray(), konstituente.toArray())) {
            // Trazimo one proste implikante koje nismo jos iskoristili
            ArrayList<Implikanta> presjek = new ArrayList<>(prosteImplikante);
            presjek.removeAll(nesvodljivi);
            // U svrhu "automatske" minimizacije pri pravljenju nesvodljive forme, prvo cemo sortirati presjek na nacin da implikante koje obuhvataju vise konstituenti idu prve (jer te konstituente imaju manje slova)
            presjek.sort((i1, i2) -> {
                Integer int1 = i1.getSadrzaneKonstituente().size();
                Integer int2 = i2.getSadrzaneKonstituente().size();
                return int1.compareTo(int2);
            });
            // Kada nadjemo prostu implikantu koja sadrzi konstituenti koja jos nije obuhvacena, dodajemo je u nesvodljivu formu i updateujemo obuhvacenost konstituenti
            for (Implikanta implikanta : presjek) {
                for (Integer element : implikanta.getSadrzaneKonstituente()) {
                    if (!set.contains(element)) {
                        nesvodljivi.add(implikanta);
                        set.add(element);
                    }
                }
            }
        }
        return nesvodljivi;
    }

}
