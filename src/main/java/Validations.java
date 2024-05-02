import java.io.File;

public class Validations {

    //used for schedule and account names alike.... user must pass this test in order
    //to name something
    /*
    The following are reserved characters:
       < (less than),  > (greater than),  : (colon),  " (double quote),  / (forward slash)
       \ (backslash),  | (vertical bar or pipe),  ? (question mark),  * (asterisk)
    */
    public static boolean is_valid_name(String name) throws IllegalArgumentException {
        //various checks to ensure valid names
        if(name.isEmpty()) throw new IllegalArgumentException("Error: no blank schedule or account names allowed");
        if(name.equalsIgnoreCase("major") || name.equalsIgnoreCase("account") || name.equalsIgnoreCase("accounts")) throw new IllegalArgumentException("Error: you cannot name an account or schedule 'accounts', or 'account', or 'major'");
        if(name.length() > 20) throw new IllegalArgumentException("Error: account and schedule names cannot be longer than 20 characters");
        //issue if name conflict with schedules
        if(Main.currentaccnt != null && new File("Accounts\\" + Main.currentaccnt.getUsername() + "\\" + name + (name.endsWith(".csv") ? "" : ".csv")).exists()) throw new IllegalArgumentException("Error: a schedule with name '" + name + "' already exists");
        for(char c : name.toCharArray())
            if(c == '<' || c == '>' || c == ':' || c == '\"' || c == '/' || c == '\\' || c == '|' || c == '?' || c == '*') throw new IllegalArgumentException("Error: account and schedule names cannot contain any of the following characters: '*','?','|','/','\\','>','<',':','\"'");
        return true;
    }

    public static boolean is_valid_password(String password) throws IllegalArgumentException {
        //check if input password is valid
        if(Main.accounts.containsKey(password.hashCode())) {
            throw new IllegalArgumentException("Error: that password is already taken");
        }
        if (password.length() < 7 || password.length() > 20) {
            throw new IllegalArgumentException("Error: password must be between 7 and 20 (inclusive) characters long");
        }
        return true;
    }

    public static boolean valid_semester(String[] sem) {
        if (!sem[0].equals("Fall") && !sem[0].equals("Spring")) {
            Main.afl.println("Error: invalid semester value (semester must be either Fall or Spring)");
            return false;
        }
        return true;
    }

    public static boolean valid_year(String[] sem) {
        if(!GeneralUtils.is_numeric(sem[1]) || sem[1].length() != 4 || Integer.parseInt(sem[1]) < 2020) {
            Main.afl.println("Error: invalid year value");
            return false;
        }
        return true;
    }

    public static boolean valid_course_code(String[] coursecode) {
        if (coursecode.length != 2 || !Major.is_major(coursecode[0]) || !GeneralUtils.is_numeric(coursecode[1]) || coursecode[1].length() != 3) {
            Main.afl.println("Error: invalid course code");
            return false;
        }
        return true;
    }

    public static boolean is_valid_section(String section) {
        if (section.length() > 1 || !Character.isAlphabetic(section.charAt(0))) {
            Main.afl.println("Error: invalid section");
            return false;
        }
        return true;
    }

    public static boolean check_username(String userName) {
        try{
            //issue if name conflict with accounts
            if(Main.currentaccnt != null && new File("Accounts\\" + userName + "\\").exists()) throw new IllegalArgumentException("Error: a schedule with name '" + userName + "' already exists");
            is_valid_name(userName);
        }
        catch(IllegalArgumentException iae) {
            Main.afl.println(iae.getMessage());
            return false;
        }
        return true;
    }
}