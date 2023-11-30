import java.sql.Connection;import java.sql.DriverManager;import java.sql.PreparedStatement;import java.sql.ResultSet;import java.sql.SQLException;import java.util.Scanner;public class Main {    public static void main(String[] args) {        String url = "jdbc:postgresql://localhost:5432/Lab4";        String user = "postgres";        String password = "123";        try (Connection connection = DriverManager.getConnection(url, user, password)) {            System.out.println("Connected to the database");            // #1            displayTripSchedules(connection, "LA", "SanDiego", "October 23, 2013");            // #2.1            deleteTripOffering(connection, 5, "May 2, 2002", "9a");            // #2.2            //addTripOfferings(connection);            // #2.3            changeDriverForTrip(connection, 3, "May 3, 2045", "3p", "BackToJoshua");            // #2.4            changeBusForTrip(connection, 1, "October 23, 2013", "10p", 3);            // #3            displayTripStops(connection, 1);            // #4            displayWeeklySchedule(connection, "Robert");            // #5            //addDrive(connection, 6, "Feb 2, 2012", "5a", "7p", "Seth", 8);            // #6            //addBus(connection, 13, "mid", 2080);            // #7            deleteBus(connection, 13);            // #8            recordActualTripInfo(connection, 1, "October 23, 2013", "8a", 1, "11a", "8:13a", "11:23a", 14, 14);        } catch (SQLException e) {            System.out.println("Connection failure.");            e.printStackTrace();        }    }    // #1    private static void displayTripSchedules(Connection connection, String startLocation, String destination, String date) {        String query = "SELECT " +                "\"to\".\"Date\", " +                "\"to\".\"TripNumber\", " +                "\"t\".\"StartLocationName\", " +                "\"t\".\"DestinationName\", " +                "\"to\".\"ScheduledStartTime\", " +                "\"to\".\"ScheduledArrivalTime\", " +                "\"to\".\"BusID\", " +                "\"to\".\"DriverName\" " +                "FROM " +                "\"TripOffering\" AS \"to\" " +                "JOIN " +                "\"Trip\" AS \"t\" ON \"to\".\"TripNumber\" = \"t\".\"TripNumber\" " +                "JOIN " +                "\"Bus\" AS \"b\" ON \"to\".\"BusID\" = \"b\".\"BusID\" " +                "JOIN " +                "\"Driver\" AS \"d\" ON \"to\".\"DriverName\" = \"d\".\"DriverName\" " +                "WHERE " +                "\"t\".\"StartLocationName\" = ? AND \"t\".\"DestinationName\" = ? AND \"to\".\"Date\" = ?";        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {            preparedStatement.setString(1, startLocation);            preparedStatement.setString(2, destination);            preparedStatement.setString(3, date);            try (ResultSet resultSet = preparedStatement.executeQuery()) {                while (resultSet.next()) {                    String formattedDate = resultSet.getString("Date");                    int tripNumber = resultSet.getInt("TripNumber");                    String startLoc = resultSet.getString("StartLocationName");                    String dest = resultSet.getString("DestinationName");                    String startTime = resultSet.getString("ScheduledStartTime");                    String arrivalTime = resultSet.getString("ScheduledArrivalTime");                    String driverName = resultSet.getString("DriverName");                    String busID = resultSet.getString("BusID");                    System.out.println("Trip Number: " + tripNumber);                    System.out.println("Date: " + formattedDate);                    System.out.println("Start Location: " + startLoc);                    System.out.println("Destination: " + dest);                    System.out.println("Scheduled Start Time: " + startTime);                    System.out.println("Scheduled Arrival Time: " + arrivalTime);                    System.out.println("Driver Name: " + driverName);                    System.out.println("Bus ID: " + busID);                    System.out.println("-----------------------------");                }            }        } catch (SQLException e) {            System.out.println("Error fetching trip schedules.");            e.printStackTrace();        }    }    // #2.1    private static void deleteTripOffering(Connection connection, int tripNumber, String date, String scheduledStartTime) {        String deleteQuery = "DELETE FROM \"TripOffering\" WHERE \"TripNumber\" = ? AND \"Date\" = ? AND \"ScheduledStartTime\" = ?";        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {            deleteStatement.setInt(1, tripNumber);            deleteStatement.setString(2, date);            deleteStatement.setString(3, scheduledStartTime);            int rowsAffected = deleteStatement.executeUpdate();            if (rowsAffected > 0) {                System.out.println("Trip offering deleted successfully. \n");            } else {                System.out.println("No trip offering found for the specified criteria.");            }        } catch (SQLException e) {            System.out.println("Error deleting trip offering.");            e.printStackTrace();        }    }    // #2.2    private static void addTripOfferings(Connection connection) {        Scanner scanner = new Scanner(System.in);        boolean moreTrips = true;        while (moreTrips) {            System.out.println("Enter Trip Number:");            int tripNumber = scanner.nextInt();            scanner.nextLine();            System.out.println("Enter Date (MMM DD, YYYY):");            String date = scanner.nextLine();            System.out.println("Enter Scheduled Start Time (ex: 8a or 12p):");            String scheduledStartTime = scanner.nextLine();            System.out.println("Enter Scheduled Arrival Time (ex: 3p):");            String scheduledArrivalTime = scanner.nextLine();            System.out.println("Enter Driver Name:");            String driverName = scanner.nextLine();            System.out.println("Enter Bus ID:");            int busID = scanner.nextInt();            String insertQuery = "INSERT INTO \"TripOffering\" VALUES (?, ?, ?, ?, ?, ?)";            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {                insertStatement.setInt(1, tripNumber);                insertStatement.setString(2, date);                insertStatement.setString(3, scheduledStartTime);                insertStatement.setString(4, scheduledArrivalTime);                insertStatement.setString(5, driverName);                insertStatement.setInt(6, busID);                int rowsAffected = insertStatement.executeUpdate();                if (rowsAffected > 0) {                    System.out.println("Trip offering added successfully.");                } else {                    System.out.println("Error adding trip offering.");                }            } catch (SQLException e) {                System.out.println("Error adding trip offering.");                e.printStackTrace();            }            System.out.println("Do you have more trips to enter? (yes/no)");            String moreTripsInput = scanner.nextLine().toLowerCase();            moreTrips = moreTripsInput.equals("yes");        }    }    // #2.3    private static void changeDriverForTrip(Connection connection, int tripNumber, String date, String scheduledStartTime, String newDriverName) {        String updateQuery = "UPDATE \"TripOffering\" SET \"DriverName\" = ? WHERE \"TripNumber\" = ? AND \"Date\" = ? AND \"ScheduledStartTime\" = ?";        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {            updateStatement.setString(1, newDriverName);            updateStatement.setInt(2, tripNumber);            updateStatement.setString(3, date);            updateStatement.setString(4, scheduledStartTime);            int rowsAffected = updateStatement.executeUpdate();            if (rowsAffected > 0) {                System.out.println("Driver for the trip offering updated successfully.");            } else {                System.out.println("No trip offering found for the specified criteria.");            }        } catch (SQLException e) {            System.out.println("Error changing driver for trip offering.");            e.printStackTrace();        }    }    // #2.4    private static void changeBusForTrip(Connection connection, int tripNumber, String date, String scheduledStartTime, int newBusID) {        String updateQuery = "UPDATE \"TripOffering\" SET \"BusID\" = ? WHERE \"TripNumber\" = ? AND \"Date\" = ? AND \"ScheduledStartTime\" = ?";        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {            updateStatement.setInt(1, newBusID);            updateStatement.setInt(2, tripNumber);            updateStatement.setString(3, date);            updateStatement.setString(4, scheduledStartTime);            int rowsAffected = updateStatement.executeUpdate();            if (rowsAffected > 0) {                System.out.println("Bus for the trip offering updated successfully.");            } else {                System.out.println("No trip offering found for the specified criteria.");            }        } catch (SQLException e) {            System.out.println("Error changing bus for trip offering.");            e.printStackTrace();        }    }    // #3    private static void displayTripStops(Connection connection, int tripNumber) {        String query = "SELECT * FROM \"TripStopInfo\" WHERE \"TripNumber\" = ? ORDER BY \"SequenceNumber\"";        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {            preparedStatement.setInt(1, tripNumber);            try (ResultSet resultSet = preparedStatement.executeQuery()) {                while (resultSet.next()) {                    int stopNumber = resultSet.getInt("StopNumber");                    int sequenceNumber = resultSet.getInt("SequenceNumber");                    String drivingTime = resultSet.getString("DrivingTime");                    System.out.println("Stop Number: " + stopNumber);                    System.out.println("Sequence Number: " + sequenceNumber);                    System.out.println("Driving Time: " + drivingTime);                    System.out.println("-----------------------------");                }            }        } catch (SQLException e) {            System.out.println("Error fetching trip stops.");            e.printStackTrace();        }    }    // #4    private static void displayWeeklySchedule(Connection connection, String driverName) {        String query = "SELECT " +                "\"to\".\"Date\", " +                "\"to\".\"TripNumber\", " +                "\"t\".\"StartLocationName\", " +                "\"t\".\"DestinationName\", " +                "\"to\".\"ScheduledStartTime\", " +                "\"to\".\"ScheduledArrivalTime\", " +                "\"to\".\"BusID\" " +                "FROM " +                "\"TripOffering\" AS \"to\" " +                "JOIN " +                "\"Trip\" AS \"t\" ON \"to\".\"TripNumber\" = \"t\".\"TripNumber\" " +                "WHERE " +                "\"to\".\"DriverName\" = ?";        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {            preparedStatement.setString(1, driverName);            try (ResultSet resultSet = preparedStatement.executeQuery()) {                while (resultSet.next()) {                    String formattedDate = resultSet.getString("Date");                    int tripNumber = resultSet.getInt("TripNumber");                    String startLoc = resultSet.getString("StartLocationName");                    String dest = resultSet.getString("DestinationName");                    String startTime = resultSet.getString("ScheduledStartTime");                    String arrivalTime = resultSet.getString("ScheduledArrivalTime");                    String busID = resultSet.getString("BusID");                    System.out.println("Trip Number: " + tripNumber);                    System.out.println("Date: " + formattedDate);                    System.out.println("Start Location: " + startLoc);                    System.out.println("Destination: " + dest);                    System.out.println("Scheduled Start Time: " + startTime);                    System.out.println("Scheduled Arrival Time: " + arrivalTime);                    System.out.println("Bus ID: " + busID);                    System.out.println("-----------------------------");                }            }        } catch (SQLException e) {            System.out.println("Error fetching driver trips.");            e.printStackTrace();        }    }    // #5    private static void addDrive(Connection connection, int tripNumber, String date, String scheduledStartTime, String scheduledArrivalTime, String driverName, int busID) {        String insertQuery = "INSERT INTO \"TripOffering\" VALUES (?, ?, ?, ?, ?, ?)";        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {            insertStatement.setInt(1, tripNumber);            insertStatement.setString(2, date);            insertStatement.setString(3, scheduledStartTime);            insertStatement.setString(4, scheduledArrivalTime);            insertStatement.setString(5, driverName);            insertStatement.setInt(6, busID);            int rowsAffected = insertStatement.executeUpdate();            if (rowsAffected > 0) {                System.out.println("Drive added successfully.");            } else {                System.out.println("Error adding drive.");            }        } catch (SQLException e) {            System.out.println("Error adding drive.");            e.printStackTrace();        }    }    // #6    private static void addBus(Connection connection, int busID, String model, int year) {        String insertQuery = "INSERT INTO \"Bus\" VALUES (?, ?, ?)";        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {            insertStatement.setInt(1, busID);            insertStatement.setString(2, model);            insertStatement.setInt(3, year);            int rowsAffected = insertStatement.executeUpdate();            if (rowsAffected > 0) {                System.out.println("Bus added successfully.");            } else {                System.out.println("Error adding bus.");            }        } catch (SQLException e) {            System.out.println("Error adding bus.");            e.printStackTrace();        }    }    // #7    private static void deleteBus(Connection connection, int busID) {        String deleteQuery = "DELETE FROM \"Bus\" WHERE \"BusID\" = ?";        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {            deleteStatement.setInt(1, busID);            int rowsAffected = deleteStatement.executeUpdate();            if (rowsAffected > 0) {                System.out.println("Bus deleted successfully.");            } else {                System.out.println("Error deleting bus. Bus ID not found.");            }        } catch (SQLException e) {            System.out.println("Error deleting bus.");            e.printStackTrace();        }    }    // #8    private static void recordActualTripInfo(Connection connection, int tripNumber, String date, String scheduledStartTime,                                             int stopNumber, String scheduledArrivalTime, String actualStartTime,                                             String actualArrivalTime, int numberOfPassengerIn, int numberOfPassengerOut) {        String insertQuery = "INSERT INTO \"ActualTripStopInfo\" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {            insertStatement.setInt(1, tripNumber);            insertStatement.setString(2, date);            insertStatement.setString(3, scheduledStartTime);            insertStatement.setInt(4, stopNumber);            insertStatement.setString(5, scheduledArrivalTime);            insertStatement.setString(6, actualStartTime);            insertStatement.setString(7, actualArrivalTime);            insertStatement.setInt(8, numberOfPassengerIn);            insertStatement.setInt(9, numberOfPassengerOut);            int rowsAffected = insertStatement.executeUpdate();            if (rowsAffected > 0) {                System.out.println("Actual trip information recorded successfully.");            } else {                System.out.println("Error recording actual trip information.");            }        } catch (SQLException e) {            System.out.println("Error recording actual trip information.");            e.printStackTrace();        }    }}