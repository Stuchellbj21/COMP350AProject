import java.io.*;
import java.util.*;

public class Main {
    /*
    - The Accounts directory stores all of the user directories, as well as a file containing login
    information for all separate accounts
    - Each user directory contains separate csv files for each schedule saved to the account,
    as well as one info.txt file which stores user information and schedule names.
     */
    public static List<Course> allcourses; //set to null to avoid 'variable may not have been initialized'
    public static PrintStream autoflush = new PrintStream(System.out, true);
    public static Search search = new Search();

    //associates password hash with String account name
    //change default value to null if actually having account selection
    //public static Account currentaccnt = new Account("NateAccount","password1234",Major.COMP);
    public static Map<Integer, String> accounts = new HashMap<>();
    public static Account currentaccnt = null;

    public static Folder current_folder = new Folder();

    /*  from user's perspective it appears that a new blank Schedule or a new custom Schedule
    has been created, but from our perspective we know that the schedule starts as a blank schedule
    and a custom schedule is simply a matter of changing the current schedule's attributes.
    A new blank schedule leaves the attributes of the current schedule as they are. */
    public static Schedule currentsched = new Schedule();
  
    public static void run() throws IOException {
        try {MainSaveLoad.load_accounts();}
        catch (Exception e) {autoflush.println("no accounts to load");}
        MainSaveLoad.load_allcourses();
        autoflush.println("Welcome to Descartes Favorite Scheduling App.... Enjoy");
        Menus.accountMenu();
    }

    public static void main(String[] args) {
        try {
            run();
        } catch (IOException ioe) {
            autoflush.println(ioe.getMessage() + "\n" + ioe.getCause());
        }
    }
}