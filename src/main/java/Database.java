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
            System.out.println("connection to database unsuccessful");
            return false;
        }
    }

    void disconnectDB() throws SQLException {
        if (conn != null) {
            conn.close();
            System.out.println("connection to \"jdbc:sqlite3:C://SQLite/scrumptious.db\" closed");
        }
    }


    // Prepared Statements for 'users'
    boolean authenticate(String username, String password) {
        try {
            PreparedStatement ps = conn.prepareStatement("" +
                    "SELECT username FROM users WHERE passwordHash = ?");
            ps.setInt(1, password.hashCode());
            ResultSet rst = ps.executeQuery();
            if (rst.next()) {
                String temp = rst.getString(1); // grab username
                if (temp.equals(username)) {
                    Main.autoflush.println("Login successful");
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
            System.out.println("get major didn't work");
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
                Main.autoflush.println("Insert into 'users' failed");
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
                Main.autoflush.println("Deletion from 'users' was successful");
                return true;
            }
            Main.autoflush.println("Deletion from 'users' failed");
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
                System.out.println("All tuples deleted from 'users'");
                return true;
            }
            Main.autoflush.println("'users' table was not cleared");
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
                Main.autoflush.println("Successful insert into 'schedules' ");
                return true;
            }
            Main.autoflush.println("Insert into 'schedules' failed");
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
                Main.autoflush.println("Succesfully deleted from 'schedules'");
                return true;
            }
            Main.autoflush.println("Deletion from 'schedules' failed ");
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
                Main.autoflush.println("All tuples deleted from 'schedules'");
                return true;
            }
            Main.autoflush.println("'schedules' table was not cleared ");
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
                Main.autoflush.println("Updated 'schedules' successfully");
                return true;
            }
            Main.autoflush.println("Update to 'schedules' failed");
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

    // Prepared statements for 'courses'
    // have to figure out how to work in the daytimes and required by list
    private boolean insert_into_courses(String name, char section, String major, int coursenum, int credits, int numstudents, int capacity, String prof, int year, String sem) {
        try {
            // generate prepared statement
            PreparedStatement ps = conn.prepareStatement("" +
                    "INSERT INTO users VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            ps.setString(1, name);
            ps.setString(2, String.valueOf(section));
            ps.setString(3, major); // check this
            ps.setInt(4, coursenum);
            ps.setInt(5, credits);
            ps.setInt(6, numstudents);
            ps.setInt(7, capacity);
            ps.setString(8, prof);
            ps.setInt(9, year);
            ps.setString(10, sem);

            // launch statement
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Insert into 'courses' was successful");
                return true;
            } else {
                System.out.println("Insert into 'courses' failed");
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

