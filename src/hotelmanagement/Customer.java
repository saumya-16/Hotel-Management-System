package hotelmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Customer {
    private String name;
    private String aadhar;
    private String phone;
    private int id;

    public Customer(String name, String aadhar, String phone) {
        this.name = name;
        this.aadhar = aadhar;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getAadhar() {
        return aadhar;
    }

    public String getPhone() {
        return phone;
    }

    public int getId() {
        return id;
    }

    public void saveToDatabase() throws SQLException {
        String sql = "INSERT INTO customers (name, aadhar, phone) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setString(2, aadhar);
            pstmt.setString(3, phone);
            pstmt.executeUpdate();

            var rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        }
    }
}
