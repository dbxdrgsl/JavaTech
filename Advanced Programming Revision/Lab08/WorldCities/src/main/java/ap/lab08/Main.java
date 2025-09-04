package ap.lab08;

import ap.lab08.dao.*;
import ap.lab08.model.*;
import ap.lab08.util.Haversine;
import ap.lab08.importer.CsvImporter;

public class Main {
    public static void main(String[] args) throws Exception {
        var continents = new ContinentDao();
        var countries  = new CountryDao();
        var cities     = new CityDao();

        // Create or find a continent
        Continent eu = continents.findByName("Europe");
        if (eu == null) eu = continents.create("Europe");

        // Simple inserts + reads
        Country ro = countries.findByName("Romania");
        if (ro == null) ro = countries.create("Romania", "RO", eu.id());

        City iasi = cities.findByName("Iași");
        if (iasi == null) iasi = cities.create(ro.id(), "Iași", false, 47.1585, 27.6014);
        City buch = cities.findByName("Bucharest");
        if (buch == null) buch = cities.create(ro.id(), "Bucharest", true, 44.4268, 26.1025);

        // Distance
        double d = Haversine.km(iasi.latitude(), iasi.longitude(), buch.latitude(), buch.longitude());
        System.out.printf("Distance Iași ↔ Bucharest: %.1f km%n", d);

        // Import dataset if provided as arg[0]
        if (args.length > 0) {
            System.out.println("Importing CSV: " + args[0]);
            CsvImporter.importCities(args[0], countries, cities, eu.id());
            System.out.println("Imported.");
        }

        // List some cities in RO
        System.out.println("Cities in Romania: " + cities.listByCountry(ro.id()).size());
    }
}
