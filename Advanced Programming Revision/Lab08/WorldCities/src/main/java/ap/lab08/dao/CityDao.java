package ap.lab08.dao;

import ap.lab08.db.Db;
import ap.lab08.model.City;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class CityDao {
    public City create(long countryId, String name, boolean capital, double lat, double lon) throws SQLException {
        String sql = "insert into cities(country_id,name,capital,latitude,longitude) values (?,?,?,?,?)";
        try (var c = Db.instance().conn();
             var ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, countryId); ps.setString(2, name); ps.setBoolean(3, capital);
            ps.setDouble(4, lat); ps.setDouble(5, lon);
            ps.executeUpdate();
            try (var rs = ps.getGeneratedKeys()) { rs.next(); return new City(rs.getLong(1), countryId, name, capital, lat, lon); }
        }
    }
    public City findByName(String name) throws SQLException {
        String sql = "select id,country_id,name,capital,latitude,longitude from cities where name=?";
        try (var c = Db.instance().conn(); var ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            try (var rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }
    public List<City> listByCountry(long countryId) throws SQLException {
        String sql = "select id,country_id,name,capital,latitude,longitude from cities where country_id=?";
        try (var c = Db.instance().conn(); var ps = c.prepareStatement(sql)) {
            ps.setLong(1, countryId);
            try (var rs = ps.executeQuery()) {
                List<City> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        }
    }
    private City map(ResultSet rs) throws SQLException {
        return new City(rs.getLong(1), rs.getLong(2), rs.getString(3), rs.getBoolean(4), rs.getDouble(5), rs.getDouble(6));
    }
}
