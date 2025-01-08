package java_database_app;

import java.sql.*;
import java.util.Scanner;

public class Databasemm {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/user", // Replace with your database name
                "root", // Replace with your database username
                "root"  // Replace with your database password
            );

            while (true) {
                System.out.println("\n--- Main Menu ---");
                System.out.println("1. Create a New Table");
                System.out.println("2. Insert Data");
                System.out.println("3. Update Data");
                System.out.println("4. Delete Data");
                System.out.println("5. Filter Data (LIKE Query)");
                System.out.println("6. Showcase Table Data");
                System.out.println("7. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        createTable(scanner, con);
                        break;
                    case 2:
                        insertData(scanner, con);
                        break;
                    case 3:
                        updateData(scanner, con);
                        break;
                    case 4:
                        deleteData(scanner, con);
                        break;
                    case 5:
                        filterData(scanner, con);
                        break;
                    case 6:
                        showcaseTableData(scanner, con);
                        break;
                    case 7:
                        System.out.println("Exiting program...");
                        con.close();
                        scanner.close();
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found. Please ensure the MySQL JDBC driver is added to the project.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database error occurred. Please check your credentials and database connection.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("An unexpected error occurred.");
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void createTable(Scanner scanner, Connection con) throws SQLException {
        System.out.print("Enter the table name: ");
        String tableName = scanner.next();

        System.out.print("Enter the number of columns: ");
        int columnCount = scanner.nextInt();

        StringBuilder createTableQuery = new StringBuilder("CREATE TABLE `" + tableName + "` (");

        String primaryKey = null;
        for (int i = 1; i <= columnCount; i++) {
            System.out.print("Enter the name of column " + i + ": ");
            String columnName = scanner.next();

            System.out.print("Enter the data type of " + columnName + " (e.g., INT, VARCHAR(50), BIGINT): ");
            String dataType = scanner.next();

            createTableQuery.append("`" + columnName + "` " + dataType);

            System.out.print("Do you want this column to be NOT NULL? (yes/no): ");
            String notNull = scanner.next();
            if (notNull.equalsIgnoreCase("yes")) {
                createTableQuery.append(" NOT NULL");
            }

            System.out.print("Do you want this column to be UNIQUE? (yes/no): ");
            String unique = scanner.next();
            if (unique.equalsIgnoreCase("yes")) {
                createTableQuery.append(" UNIQUE");
            }

            System.out.print("Is " + columnName + " the primary key? (yes/no): ");
            String isPrimaryKey = scanner.next();
            if (isPrimaryKey.equalsIgnoreCase("yes")) {
                primaryKey = columnName;
            }

            if (i < columnCount) {
                createTableQuery.append(", ");
            }
        }

        if (primaryKey != null) {
            createTableQuery.append(", PRIMARY KEY (`" + primaryKey + "`)");
        }

        createTableQuery.append(");");
        Statement stmt = con.createStatement();
        stmt.execute(createTableQuery.toString());
        System.out.println("Table '" + tableName + "' created successfully!");
    }

    private static void insertData(Scanner scanner, Connection con) throws SQLException {
        System.out.print("Enter the table name to insert data: ");
        String tableName = scanner.next();

        System.out.print("Enter the number of columns to insert data: ");
        int columnCount = scanner.nextInt();

        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for (int i = 1; i <= columnCount; i++) {
            System.out.print("Enter column name " + i + ": ");
            String columnName = scanner.next();

            columns.append("`" + columnName + "`");
            values.append("?");

            if (i < columnCount) {
                columns.append(", ");
                values.append(", ");
            }
        }

        String query = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")";
        PreparedStatement ps = con.prepareStatement(query);

        for (int i = 1; i <= columnCount; i++) {
            System.out.print("Enter value for column " + i + ": ");
            String value = scanner.next();
            ps.setString(i, value);
        }

        int rowsInserted = ps.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println("Data inserted successfully!");
        } else {
            System.out.println("Failed to insert data.");
        }
    }

    private static void updateData(Scanner scanner, Connection con) throws SQLException {
        System.out.print("Enter the table name to update data: ");
        String tableName = scanner.next();

        System.out.print("Enter the column name to update: ");
        String columnToUpdate = scanner.next();

        System.out.print("Enter the new value: ");
        String newValue = scanner.next();

        System.out.print("Enter the condition column: ");
        String conditionColumn = scanner.next();

        System.out.print("Enter the condition value: ");
        String conditionValue = scanner.next();

        String query = "UPDATE " + tableName + " SET `" + columnToUpdate + "` = ? WHERE `" + conditionColumn + "` = ?";
        PreparedStatement ps = con.prepareStatement(query);

        ps.setString(1, newValue);
        ps.setString(2, conditionValue);

        int rowsUpdated = ps.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Data updated successfully!");
        } else {
            System.out.println("Failed to update data or no matching rows found.");
        }
    }

    private static void deleteData(Scanner scanner, Connection con) throws SQLException {
        System.out.print("Enter the table name to delete data from: ");
        String tableName = scanner.next();

        System.out.print("Enter the condition column: ");
        String conditionColumn = scanner.next();

        System.out.print("Enter the condition value: ");
        String conditionValue = scanner.next();

        String query = "DELETE FROM " + tableName + " WHERE `" + conditionColumn + "` = ?";
        PreparedStatement ps = con.prepareStatement(query);

        ps.setString(1, conditionValue);

        int rowsDeleted = ps.executeUpdate();
        if (rowsDeleted > 0) {
            System.out.println("Data deleted successfully!");
        } else {
            System.out.println("Failed to delete data or no matching rows found.");
        }
    }

    private static void filterData(Scanner scanner, Connection con) throws SQLException {
        System.out.print("Enter the table name to filter data: ");
        String tableName = scanner.next();

        System.out.print("Enter the column name to filter: ");
        String columnName = scanner.next();

        System.out.print("Enter the filter value (use % for wildcards): ");
        String filterValue = scanner.next();

        String query = "SELECT * FROM " + tableName + " WHERE `" + columnName + "` LIKE ?";
        PreparedStatement ps = con.prepareStatement(query);

        ps.setString(1, filterValue);

        ResultSet rs = ps.executeQuery();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        System.out.println("\nFiltered Data:");
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + ": " + rs.getString(i) + "\t");
            }
            System.out.println();
        }
    }

    private static void showcaseTableData(Scanner scanner, Connection con) throws SQLException {
        System.out.print("Enter the table name to showcase data: ");
        String tableName = scanner.next();

        String query = "SELECT * FROM " + tableName;
        Statement stmt = con.createStatement();

        ResultSet rs = stmt.executeQuery(query);
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        System.out.println("\nTable Data:");
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + ": " + rs.getString(i) + "\t");
            }
            System.out.println();
        }
    }
}
