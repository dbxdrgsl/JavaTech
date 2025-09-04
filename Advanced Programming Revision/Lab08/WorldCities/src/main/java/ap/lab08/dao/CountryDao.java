package ap.lab08.dao;

import ap.lab08.db.Db;
import ap.lab08.model.Country;

import java.sql.*;

public final class CountryDao {
    public Country create(String name, String code, long continentId) throws SQLException {
        String sql = "insert into countries(name,code,continent_id) values (?,?,?)";
        try (var c = Db.instance().conn();
             var ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name); ps.setString(2, code); ps.setLong(3, continentId);
            ps.executeUpdate();
            try (var rs = ps.getGeneratedKeys()) { rs.next(); return new Country(rs.getLong(1), name, code, continentId); }
        }
    }
    public Country findById(long id) throws SQLException {
        String sql = "select id,name,code,continent_id from countries where id=?";
        try (var c = Db.instance().conn(); var ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (var rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }
    public Country findByName(String name) throws SQLException {
        String sql = "select id,name,code,continent_id from countries where name=?";
        try (var c = Db.instance().conn(); var ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            try (var rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }
    private Country map(ResultSet rs) throws SQLException {
        return new Country(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getLong(4));
    }
}
