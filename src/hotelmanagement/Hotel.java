package hotelmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class Hotel {

    static Scanner scanner = new Scanner(System.in);

    static final double BRONZE_PRICE = 1000;
    static final double SILVER_PRICE = 2000;
    static final double GOLD_PRICE = 3000;

    static final double SPA_PRICE = 500;
    static final double LAUNDRY_PRICE = 200;
    static final double MEAL_PRICE = 300;

    public static void main(String[] args) {
        System.out.println("Welcome to our Hotel!");
        showRoomTypes();

        double totalPrice = 0;

        System.out.println("How many days would you like to book?");
        int days = scanner.nextInt();
        scanner.nextLine(); // Consume newline left-over

        int[] roomCount = new int[3]; // Array to store count of each room type
        for (int i = 0; i < days; i++) {
            System.out.println("Choose room type for day " + (i + 1) + " (bronze, silver, gold): ");
            String roomType = scanner.nextLine().toLowerCase();
            roomCount[getIndexFromRoomType(roomType)]++;

            System.out.println("Would you like to add meals? (yes/no)");
            if (scanner.nextLine().equalsIgnoreCase("yes")) {
                totalPrice += MEAL_PRICE;
            }

            System.out.println("Would you like to add spa service? (yes/no)");
            if (scanner.nextLine().equalsIgnoreCase("yes")) {
                totalPrice += SPA_PRICE;
            }

            System.out.println("Would you like to add laundry service? (yes/no)");
            if (scanner.nextLine().equalsIgnoreCase("yes")) {
                totalPrice += LAUNDRY_PRICE;
            }
        }

        totalPrice += calculateTotalPrice(roomCount, days);

        System.out.println("Total price for the stay: " + totalPrice);

        Customer customer = collectPersonalDetails();
        try {
            customer.saveToDatabase();
            int[] daysPerRoom = new int[3]; // Array to store number of days booked for each room type
            for (int i = 0; i < 3; i++) {
                daysPerRoom[i] = roomCount[i] > 0 ? days : 0;
            }
            saveBookingDetails(customer.getId(), days, totalPrice, daysPerRoom, new String[]{"bronze", "silver", "gold"}, roomCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Thank you for booking with us, " + customer.getName() + "!");
    }

    static void showRoomTypes() {
        System.out.println("We have the following types of rooms:");
        System.out.println("1. Bronze: $" + BRONZE_PRICE + " per day");
        System.out.println("2. Silver: $" + SILVER_PRICE + " per day");
        System.out.println("3. Gold: $" + GOLD_PRICE + " per day");
    }

    static int getIndexFromRoomType(String roomType) {
        switch (roomType) {
            case "bronze":
                return 0;
            case "silver":
                return 1;
            case "gold":
                return 2;
            default:
                System.out.println("Invalid room type. Defaulting to Bronze.");
                return 0;
        }
    }

    static double calculateTotalPrice(int[] roomCount, int days) {
        return (roomCount[0] * BRONZE_PRICE + roomCount[1] * SILVER_PRICE + roomCount[2] * GOLD_PRICE) * days;
    }

    static Customer collectPersonalDetails() {
        System.out.println("Please enter your personal details.");

        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Aadhar Number: ");
        String aadhar = scanner.nextLine();

        System.out.print("Phone Number: ");
        String phone = scanner.nextLine();

        System.out.println("Details collected: Name - " + name + ", Aadhar - " + aadhar + ", Phone - " + phone);

        return new Customer(name, aadhar, phone);
    }

    static void saveBookingDetails(int customerId, int days, double totalPrice, int[] daysPerRoom, String[] roomTypes, int[] roomCount) throws SQLException {
        String sql = "INSERT INTO bookings (customer_id, room_type, days, spa, laundry, meal, total_price) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int index = 0; // Index to track room types and days per room
            for (int i = 0; i < roomCount.length; i++) {
                for (int j = 0; j < roomCount[i]; j++) {
                    pstmt.setInt(1, customerId);
                    pstmt.setString(2, roomTypes[i]);
                    pstmt.setInt(3, daysPerRoom[index]); // Number of days for this booking
                    pstmt.setBoolean(4, true); // Example value, update based on actual user input
                    pstmt.setBoolean(5, true); // Example value, update based on actual user input
                    pstmt.setBoolean(6, true); // Example value, update based on actual user input
                    pstmt.setDouble(7, totalPrice);
                    pstmt.executeUpdate();
                    index++; // Move to the next day for the next room type
                }
            }
        }
    }
}
