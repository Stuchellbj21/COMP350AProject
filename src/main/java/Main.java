import java.util.List;
import java.util.Scanner;

public class Main {
    //this is where we will keep our JFrame

    //for ease of access we may want to have current schedule and search from account in main
    //we’ll make a directory per account with all of the schedules belonging to that account in it

    public final List<Course> allcourses = null; //set to null to avoid var may not have been initialized
    public final List<String> allprofessors = null; //same reasoning here for null

    //we will have a directory in which we store all of the account directories
    //within each account directory there will be csv/txt/other files which represent the saved
    //schedules for those accounts
    private List<String> accounts; //list of all account names (which are directory names)

    //account has a schedule instance that is worked on
    public static void run() {
        Scanner scnr = new Scanner(System.in);
//        System.out.println("What is your account name?");
//        String account_name = scnr.next();
//        if(accounts.contains(account_name)){
//            System.out.println("What is your password?");
//            String password = scnr.next();
//        }
//        else {
//            System.out.println("Account does not exist. Would you like to make an account? Choose 'y' or 'n'.");
//            String user_choice = scnr.next();
//            user_choice.toLowerCase();
//            if (user_choice.equals("y") || user_choice.equals("n")){
//                //create account process
//            }
//        }
        System.out.println("Would you like to create a new schedule? Enter 'y' for yes or 'n' for no.");
        String user_input = (String) scnr.next();
        String yes_choice = "y";
        String no_choice = "n";
        while (!(user_input.equalsIgnoreCase(yes_choice)) && !(user_input.equalsIgnoreCase(no_choice))) {
            System.out.println("Invalid input. Please try again.");
            user_input = (String) scnr.next();
        }
        if (user_input.equals("y")) {
            //create new schedule
        }
        else {
            System.out.println("No class created.");
        }
    }

    public static void main(String[] args) {
        run();
    }
}
