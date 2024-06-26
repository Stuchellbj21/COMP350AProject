import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    public static void main(String[] args) {

        try {
            Database db = new Database();
            db.connect();
            db.clear_all();  // reset everything
            db.disconnectDB();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection conn = null;

    void clear_all() {
        clear_schedules();
        clear_users();
    }

    boolean connect() {
        try {
            // create variable that allows us to pass username and password to DB
//            Properties info = new Properties();
//            String username = "XXXXXXX"; // replace w/user input
//            String pass = "XXXXXX"; // replace w/user input
//            String schema = ""; //todo: fix
//            // set username and password
//            info.put("user", username);
//            info.put("password", pass);
            // connect to SQLite database
            conn = DriverManager.getConnection("jdbc:sqlite:C://SQLite/scrumptious.db");
            return true;

        } catch (SQLException e) {
            Main.afl.println("connection to database unsuccessful");
            return false;
        }
    }

    void disconnectDB() throws SQLException {
        if (conn != null) {
            conn.close();
            Main.afl.println("connection to \"jdbc:sqlite3:C://SQLite/scrumptious.db\" closed");
        }
    }


    // Prepared Statements for 'users'
    boolean authenticate(String username, String password) {
        try {
            PreparedStatement ps = conn.prepareStatement("" +
                    "SELECT passwordHash FROM users WHERE username = ?");
            ps.setString(1, username);
            ResultSet rst = ps.executeQuery();
            if (rst.next()) {
                int temp = rst.getInt(1); // grab passwordHash
                if (temp == password.hashCode()) {
                    Main.afl.println("Login successful");
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    boolean username_exists(String username) {
        try {
            PreparedStatement ps = conn.prepareStatement("" +
                    "SELECT * FROM users WHERE username = ?");
            ps.setString(1, username);
            ResultSet rst = ps.executeQuery();
            int cntr = 0;
            if (rst.next()) {
                cntr += 1;
            }
            return cntr > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    String get_major(String username) {
        try {
            PreparedStatement ps = conn.prepareStatement("" +
                    "SELECT major FROM users WHERE username = ?");
            ps.setString(1, username);
            ResultSet rst = ps.executeQuery();
            if (rst.next()) {
                return rst.getString(1);
            }
            Main.afl.println("get major didn't work");
            return "COMP";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    boolean insert_into_users(String username, int passwordHash, Major major) {
        try {
            // generate prepared statement
            PreparedStatement ps = conn.prepareStatement("" +
                    "INSERT INTO users VALUES (?, ?, ?)");

            ps.setString(1, username);
            ps.setInt(2, passwordHash);
            ps.setString(3, String.valueOf(major));

            // launch statement
            int rows = ps.executeUpdate();
            if (rows > 0) {
                return true;
            } else {
                Main.afl.println("Insert into 'users' failed");
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    boolean delete_from_users(String username) {
        try {
            PreparedStatement ps = conn.prepareStatement("" +
                    "DELETE FROM users WHERE userID = ?");
            ps.setString(1, username);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                Main.afl.println("Deletion from 'users' was successful");
                return true;
            }
            Main.afl.println("Deletion from 'users' failed");
            return false;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is primarily for testing purposes
     *
     * @return true if all tuples of 'users' table are deleted
     * @throws SQLException
     */
    boolean clear_users() {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM users");
            int rows = ps.executeUpdate();
            if (rows > 0) {
                Main.afl.println("All tuples deleted from 'users'");
                return true;
            }
            Main.afl.println("'users' table was already empty or was not cleared");
            return false;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // why can this not be private?
    boolean users_is_empty() throws SQLException {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users");
            ResultSet rst = ps.executeQuery();
            if (rst.next()) {
                return false;
            }
            return true;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // Prepared statements for 'schedules'
    boolean insert_into_schedules(String schedName, String username) throws SQLException {

        try {
            PreparedStatement ps = conn.prepareStatement("" +
                    "INSERT INTO schedules values (?, ?)");
            ps.setString(1, schedName);
            ps.setString(2, username);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                Main.afl.println("Successful insert into 'schedules' ");
                return true;
            }
            Main.afl.println("Insert into 'schedules' failed");
            return false; // probably wrong spot for this
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    boolean delete_from_schedules(String schedName, String username) {

        try {
            PreparedStatement ps = conn.prepareStatement("" +
                    "DELETE FROM schedules WHERE name = ? and userID = ?");
            ps.setString(1, schedName);
            ps.setString(2, username);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Primarily for testing purposes
     *
     * @return
     */
    private boolean clear_schedules() {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM schedules");
            int rows = ps.executeUpdate();
            if (rows > 0) {
                Main.afl.println("All tuples deleted from 'schedules'");
                return true;
            }
            Main.afl.println("'schedules' table was not cleared ");
            return false;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    boolean update_schedules(String newSchedName, String schedName, String username) {
        try {
            PreparedStatement ps = conn.prepareStatement("" +
                    "UPDATE schedules SET name = ? WHERE name = ? and userID = ?");
            ps.setString(1, newSchedName);
            ps.setString(2, schedName);
            ps.setString(3, username);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                Main.afl.println("Updated 'schedules' successfully");
                return true;
            }
            Main.afl.println("Update to 'schedules' failed");
            return false;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    boolean schedule_exists(String schedName, String username) {
        try {
            PreparedStatement ps = conn.prepareStatement("" +
                    "SELECT * FROM schedules WHERE name = ? and userID = ?");
            ps.setString(1, schedName);
            ps.setString(2, username);
            ResultSet rst = ps.executeQuery();
            int cntr = 0;
            if (rst.next()) {
                cntr += 1;
            }
            return cntr > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    List<String> get_schedules(String userID) {
        try {
            ArrayList<String> scheds = new ArrayList();
            PreparedStatement ps = conn.prepareStatement("" +
                    "SELECT * FROM schedules WHERE userID = ?");
            ps.setString(1, userID);
            ResultSet rst = ps.executeQuery();
            while (rst.next()) {
                scheds.add(rst.getString("name"));
            }
            return scheds;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

