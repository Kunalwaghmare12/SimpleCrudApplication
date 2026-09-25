package com.kunal.simplecrudapp.helper;
import java.sql.*;
import java.util.*;
import javax.sql.DataSource;
public class Helper {

    public static List<Map<String, Object>> execSelectAsMap(String sql, Object... params) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> rows = new ArrayList<>();

                ResultSetMetaData md = rs.getMetaData();
                int colCount = md.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>(); // maintains column order
                    for (int c = 1; c <= colCount; c++) {
                        String key = md.getColumnLabel(c); // respects alias, else column name
                        Object val = rs.getObject(c);
                        row.put(key, val);
                    }
                    rows.add(row);
                }
                return rows;
            }
        }
    }

    private static void bindParams(PreparedStatement ps, Object... params) throws SQLException {
        if (params == null) return;
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }



    public static int execInsert(String tableName, Map<String, Object> data)
            throws SQLException {

        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Insert data cannot be empty");
        }

        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        for (String col : data.keySet()) {
            columns.append(col).append(",");
            placeholders.append("?,");
        }

        // remove last comma
        columns.setLength(columns.length() - 1);
        placeholders.setLength(placeholders.length() - 1);

        String sql = "INSERT INTO " + tableName +
                " (" + columns + ") VALUES (" + placeholders + ")";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int index = 1;
            for (Object value : data.values()) {
                ps.setObject(index++, value);
            }

            return ps.executeUpdate();
        }
    }
}
