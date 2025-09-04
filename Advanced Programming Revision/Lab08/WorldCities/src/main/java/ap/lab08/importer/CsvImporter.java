package ap.lab08.importer;

import ap.lab08.dao.CityDao;
import ap.lab08.dao.CountryDao;
import ap.lab08.model.Country;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.sql.SQLException;

public final class CsvImporter {
    /**
     * CSV columns expected: country,city,is_capital(0/1 or true/false),latitude,longitude,code(optional)
     */
    public static void importCities(String csvPath, CountryDao ctryDao, CityDao cityDao, long continentId) throws Exception {
        try (var rdr = new CSVReader(new FileReader(csvPath))) {
            String[] row;
            // optionally skip header
            rdr.peek(); // OpenCSV 5.x keeps it simple; assume first line is header if not numeric in lat
            while ((row = rdr.readNext()) != null) {
                if (row.length < 5) continue;
                String country = row[0].trim();
                String city    = row[1].trim();
                boolean cap    = row[2].trim().equalsIgnoreCase("1") || row[2].trim().equalsIgnoreCase("true");
                double lat     = Double.parseDouble(row[3].trim());
                double lon     = Double.parseDouble(row[4].trim());
                String code    = row.length>=6 ? row[5].trim() : null;

                Country c = getOrCreateCountry(ctryDao, country, code, continentId);
                cityDao.create(c.id(), city, cap, lat, lon);
            }
        }
    }

    private static Country getOrCreateCountry(CountryDao dao, String name, String code, long contId) throws SQLException {
        Country x = dao.findByName(name);
        if (x != null) return x;
        return dao.create(name, code, contId);
    }
}
