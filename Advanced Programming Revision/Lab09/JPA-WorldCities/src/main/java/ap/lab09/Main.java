package ap.lab09;

import ap.lab09.model.*;
import ap.lab09.repo.*;
import ap.lab09.util.Haversine;

public class Main {
    public static void main(String[] args) {
        var contRepo = new ContinentRepository();
        var countryRepo = new CountryRepository();
        var cityRepo = new CityRepository();

        // upsert Europe
        Continent eu = contRepo.findByName("Europe").stream().findFirst().orElse(null);
        if (eu == null) { eu = new Continent("Europe"); contRepo.create(eu); }

        // upsert Romania
        Country ro = countryRepo.findByName("Romania").stream().findFirst().orElse(null);
        if (ro == null) { ro = new Country("Romania","RO", eu); countryRepo.create(ro); }

        // upsert cities
        City iasi = cityRepo.findByName("Iași").stream().findFirst().orElse(null);
        if (iasi == null) { iasi = new City(ro,"Iași",false,47.1585,27.6014); cityRepo.create(iasi); }
        City buch = cityRepo.findByName("Bucharest").stream().findFirst().orElse(null);
        if (buch == null) { buch = new City(ro,"Bucharest",true,44.4268,26.1025); cityRepo.create(buch); }

        double d = Haversine.km(iasi.getLatitude(), iasi.getLongitude(), buch.getLatitude(), buch.getLongitude());
        System.out.printf("Iași ↔ Bucharest: %.1f km%n", d);

        System.out.println("Continents like 'Eu': " + contRepo.findByName("Eu").size());
        System.out.println("Countries like 'Ro': " + countryRepo.findByName("Ro").size());
        System.out.println("Cities like 'aș': " + cityRepo.findByName("aș").size());
    }
}
