package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLConnection {

	private static final String URL = "jdbc:mysql://localhost:3307/finalproject";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Redroses@005";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            throw new SQLException("Error connecting to the database: " + e.getMessage());
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
	/*
	 * public static void main(String[] args) { try { Connection conn =
	 * getConnection(); String sql =
	 * "Select student_name from students where student_id = 1"; PreparedStatement
	 * ptst = conn.prepareStatement(sql); ResultSet rs = ptst.executeQuery();
	 * if(rs.next()) { System.out.println(rs.getString("student_name")); } }catch
	 * (Exception e) { // TODO: handle exception } }
	 */
}
