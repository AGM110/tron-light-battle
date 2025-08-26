import java.sql.*;


public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/tron_game";
    private static final String USER = "root";
    private static final String PASSWORD = "CHIMNEYtrader22@";



    public DatabaseManager() {

        createTable();
    }


    public void createTable() {
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS players (
                    name VARCHAR(50) PRIMARY KEY,
                    wins INT DEFAULT 0
                );
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);
            System.out.println("Table 'players' checked/created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error creating table: " + e.getMessage());
        }
    }

    public void addPlayer(String name) {
        String checkPlayerSQL = "SELECT COUNT(*) FROM players WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(checkPlayerSQL)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {

                System.out.println("Player already exists. No insertion needed.");
            } else {

                String insertSQL = "INSERT INTO players (name, wins) VALUES (?, 0)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                    insertStmt.setString(1, name);
                    insertStmt.executeUpdate();
                    System.out.println("New player added with 0 wins.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateWins(String name) {
        String updateSQL = "UPDATE players SET wins = wins + 1 WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, name);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 1) {
                System.out.println("Win count for " + name + " successfully incremented by 1.");
            } else {
                System.out.println("Player " + name + " not found. Win count not updated.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


 public String getHighScores() {
        StringBuilder scores = new StringBuilder("High Scores:\n");
        String selectSQL = "SELECT name, wins FROM players ORDER BY wins DESC LIMIT 10";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            while (rs.next()) {
                String name = rs.getString("name");
                int wins = rs.getInt("wins");
                scores.append(name).append(": ").append(wins).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return scores.toString();
    }



}