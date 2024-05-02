import java.io.*;

public class AccountCreation {
    public static String create_username() {
        while (true) {
            String un = GeneralUtils.input("Enter (<YourUserName>) -> set new username / (back) -> return to Account screen\n");
            if (un.equalsIgnoreCase("back")) {
                return "back";
            } else if (Validations.check_username(un)) {
                if (!Main.db.username_exists(un)) {
                    return un;
                }
                else {
                    Main.autoflush.println("username already exists");
                }
            }
        }
    }

    public static String create_password() {
        while (true) {
            try {
                String pw = GeneralUtils.input("Enter (<YourPassword>) -> set new password / (back) -> return to Account screen\n");
                if (pw.equalsIgnoreCase("back")) {
                    return "back";
                } else if (Validations.is_valid_password(pw)) {
                    return pw;
                }
            }
            catch(IllegalArgumentException e) {
                Main.afl.println(e.getMessage());
            }
        }
    }

    public static String enter_major() {
        while (true) {
            String in = GeneralUtils.input("Enter (major) -> set major, e.g. 'COMP' / (back) -> Account menu\n");
            if (in.equalsIgnoreCase("back")) {
                return "back";
            } else if (Major.is_major(in.toUpperCase())) {
                return in.toUpperCase();
            } else {
                Main.afl.println("Error: '" + in + "' is an invalid major");
            }
        }
    }

    public static boolean createAccount() throws IOException {
        String username = create_username();
        if (username.equalsIgnoreCase("back")) {
            return false; // return to Account menu
        }
        String password = create_password();
        if (password.equalsIgnoreCase("back")) {
            return false;  //return to Account menu
        }
        String m = enter_major();
        if (m.equalsIgnoreCase("back")) {
            return false;
        }
        Major major = Major.valueOf(m);

        // ------------------------------------
        // DATABASE
        Main.db.insert_into_users(username, password.hashCode(), major);
        // ------------------------------------

        Main.currentaccnt = new Account(username, password, major);

        File accountDir = new File("Accounts\\" + Main.currentaccnt.getUsername());
        accountDir.mkdir(); // create new folder for each new account that's created
//
        // After adding databasem, this file is not used for username, password, or major.
        File f = new File("Accounts\\" + Main.currentaccnt.getUsername() + '\\' + "info.txt");
        FileWriter fw = new FileWriter(f, true);
        fw.write("Folders: ");
        fw.close();
        //---------------------------------------------------------

        try{Main.currentaccnt.enter_courses_taken();}
        catch(IOException ioe) {Main.afl.println("Error: " + ioe.getMessage());}

        Main.afl.println("Account successfully created\n");
        return true;
    }
}