package com.kunal.simplecrudapp.helper;


import java.sql.*;
import java.util.*;
import javax.sql.DataSource;

public final class DBUtils {

    private DBUtils() {}

    // ---------- Null safe ----------
    public static String replaceNullWithEmpty(String value) {
        return value == null ? "" : value;
    }

    // ---------- UPDATE / INSERT / DELETE (auto connection close) ----------
    public static int execUpdate(DataSource ds, String sql, Object... params) throws SQLException {
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            bindParams(ps, params);
            int count = ps.executeUpdate();

            return count;

        } catch (SQLException e) {
            throw e; // swallow mat karo, upar handle karne do
        }
    }

    // ---------- INSERT returning generated key (auto connection close) ----------
    public static long execInsertReturnKey(DataSource ds, String sql, Object... params) throws SQLException {
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            bindParams(ps, params);

            int count = ps.executeUpdate();
            if (count == 0) throw new SQLException("Insert affected 0 rows. sql=" + sql);

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
            throw new SQLException("No generated key returned. sql=" + sql);

        } catch (SQLException e) {
            throw e;
        }
    }

    // ---------- SELECT multiple rows (auto connection close) ----------
    public static <T> List<T> execSelect(String sql, RowMapper<T> mapper, Object... params)
            throws SQLException {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                List<T> list = new ArrayList<>();
                int rowNum = 0;
                while (rs.next()) {
                    list.add(mapper.mapRow(rs, rowNum++));
                }
                return list;
            }

        } catch (SQLException e) {
            throw e;
        }
    }

    // ---------- SELECT single row (auto connection close) ----------
    public static <T> Optional<T> execSelectOne(String sql, RowMapper<T> mapper, Object... params)
            throws SQLException {

        List<T> list = execSelect( sql, mapper, params);
        if (list.isEmpty()) return Optional.empty();
        if (list.size() > 1) {
            throw new SQLException("Expected 1 row but got " + list.size() + ". sql=" + sql);
        }
        return Optional.of(list.get(0));
    }

    // ---------- PARAM BINDING (prevents invalid column index) ----------
    private static void bindParams(PreparedStatement ps, Object... params) throws SQLException {
        if (params == null) return;
        for (int i = 0; i < params.length; i++) {
            Object p = params[i];
            if (p == null) {
                ps.setNull(i + 1, Types.VARCHAR); // default; if you know type, pass it explicitly
            } else if (p instanceof java.util.Date) {
                ps.setTimestamp(i + 1, new Timestamp(((java.util.Date) p).getTime()));
            } else {
                ps.setObject(i + 1, p);
            }
        }
    }

    // ---------- Row Mapper ----------
    @FunctionalInterface
    public interface RowMapper<T> {
        T mapRow(ResultSet rs, int rowNum) throws SQLException;
    }
}

