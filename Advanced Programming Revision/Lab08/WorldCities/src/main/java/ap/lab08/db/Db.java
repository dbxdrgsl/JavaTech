package ap.lab08.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

public final class Db {
    private static Db INSTANCE;
    private final HikariDataSource ds;

    private Db() {
        try {
            Properties p = new Properties();
            try (var in = Db.class.getClassLoader().getResourceAsStream("application.properties")) {
                p.load(in);
            }
            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl(p.getProperty("jdbc.url"));
            cfg.setUsername(p.getProperty("jdbc.user"));
            cfg.setPassword(p.getProperty("jdbc.pass"));
            cfg.setMaximumPoolSize(Integer.parseInt(p.getProperty("pool.max", "5")));
            ds = new HikariDataSource(cfg);
            runSchema();
        } catch (Exception e) {
            throw new RuntimeException("DB init failed", e);
        }
    }

    public static synchronized Db instance() {
        if (INSTANCE == null) INSTANCE = new Db();
        return INSTANCE;
    }

    public Connection conn() throws java.sql.SQLException { return ds.getConnection(); }

    private void runSchema() throws Exception {
        try (Connection c = conn()) {
            String sql;
            try (var in = Db.class.getClassLoader().getResourceAsStream("schema.sql");
                 var br = new BufferedReader(new InputStreamReader(in))) {
                sql = br.lines().reduce("", (a,b)-> a + b + "\n");
            }
            for (String stmt : sql.split(";")) {
                String s = stmt.trim();
                if (s.isEmpty()) continue;
                try (Statement st = c.createStatement()) { st.execute(s); }
            }
        }
    }
}
