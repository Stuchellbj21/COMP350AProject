import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

public class AccountCreation {
    public static String create_username() {
        while (true) {
            String un = GeneralUtils.input("Enter (<YourUserName>) --> set new username / (back) --> return to Account screen\n");
            if (un.equalsIgnoreCase("back")) {
                return "back";
            } else if (Validations.check_username(un)) {
                return un;
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
                Main.autoflush.println(e.getMessage());
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
                Main.autoflush.println("Error: '" + in + "' is an invalid major");
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
        Main.currentaccnt = new Account(username, password, major);
        Main.accounts.put(password.hashCode(), username); // add account to the map
        File accountDir = new File("Accounts\\" + Main.currentaccnt.getUsername());
        accountDir.mkdir(); // create new folder for each new account that's created

        // store user's password-hash, username, and major in user-specific txt file
        File f = new File("Accounts\\" + Main.currentaccnt.getUsername() + '\\' + "info.txt");
        FileWriter fw = new FileWriter(f, true);
        fw.write(username + ", " + password.hashCode() + ", " + major + "\n");
        fw.close();
        //---------------------------------------------------------
        Main.autoflush.println("Account successfully created\n");
        return true;
    }
}