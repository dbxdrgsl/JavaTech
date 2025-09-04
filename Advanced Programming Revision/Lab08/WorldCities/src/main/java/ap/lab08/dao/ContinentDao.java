package ap.lab08.dao;

import ap.lab08.db.Db;
import ap.lab08.model.Continent;

import java.sql.*;

public final class ContinentDao {
    public Continent create(String name) throws SQLException {
        String sql = "insert into continents(name) values (?)";
        try (var c = Db.instance().conn();
             var ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();
            try (var rs = ps.getGeneratedKeys()) { rs.next(); return new Continent(rs.getLong(1), name); }
        }
    }
    public Continent findById(long id) throws SQLException {
        String sql = "select id,name from continents where id=?";
        try (var c = Db.instance().conn(); var ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (var rs = ps.executeQuery()) { return rs.next() ? new Continent(rs.getLong(1), rs.getString(2)) : null; }
        }
    }
    public Continent findByName(String name) throws SQLException {
        String sql = "select id,name from continents where name=?";
        try (var c = Db.instance().conn(); var ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            try (var rs = ps.executeQuery()) { return rs.next() ? new Continent(rs.getLong(1), rs.getString(2)) : null; }
        }
    }
}
