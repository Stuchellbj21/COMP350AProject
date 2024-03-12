import java.util.List;

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
    public void run() {}

    public static void main(String[] args) {}
}
