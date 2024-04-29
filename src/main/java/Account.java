import java.io.File;
import java.sql.SQLException;
import java.util.*;

public class Account {
    //another thing of loading:
    //we make a directory storing all accounts.... each account is a subdirectory of this directory that
    //has schedules in it

    private String username;
    private int passwordhash;
    private Major major;
    private List<String> schednames;

    private List<String> preffered_profs;

    public static Set<String> prof_list = new HashSet<>();

    private List<String> folders;

    public Account(){
        this.username = "empty";
    }

    //Constructor without major
    public Account(String username,String password){
        this.username = username;
        this.passwordhash = password.hashCode();
        schednames = new ArrayList<String>();
    }

    //Constructor with major
    public Account(String username,String password,Major major){
        this.username = username;
        this.passwordhash = password.hashCode();
        this.major = major;
        schednames = new ArrayList<String>();
        folders = new ArrayList<>();
        preffered_profs = new ArrayList<>();
    }

    //getters + setters not added yet

    //this could be done as a setter for major
    public boolean change_major(Major newmajor) {
        this.major = newmajor;
        return true;
    }

    //NOT IMPLEMENTED
    public boolean change_password(String newpassword) {
        passwordhash = newpassword.hashCode();
        return true;
    }

    /**
     * Allows the user to change their username
     * @param newname new username
     * @return true once the username has been changed
     */
    public boolean change_username(String newname) {
        this.username = newname;
        return true;
    }

    /**
     * Saves the name of a created schedule to the list of schedules in an account
     * @param sched_name the name of the new schedule
     * @return true once of the schedule has been added
     */
    public boolean save_schedule(String sched_name) throws SQLException {
        if (!schednames.contains(sched_name)) { // Changed this to check for sched_name in list
            schednames.add(sched_name);

            //-------------------------------------------------------
            System.out.println("got here");
            // DATABASE save
            Main.db.insert_into_schedules(sched_name, Main.currentaccnt.getUsername());
            //--------------------------------------------------------
            return true;
        }
        return false;
    }

    public boolean delete_schedule(String sched_name) {
        schednames.remove(sched_name);
        //-----------------------------------------------
        // DATABASE delete
        Main.db.delete_from_schedules(sched_name, Main.currentaccnt.getUsername());
        //-----------------------------------------------
        return true;
    }

    public boolean delete_account() {return false;}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Major getMajor() {
        return major;
    }

    public void setMajor(Major major) {
        this.major = major;
    }

    public List<String> get_schednames() {
        return schednames;
    }

    public void print_schedule_list() {
        File account = new File("Accounts\\" + username);
        Main.autoflush.println(username+" Schedules:");
        boolean hasSchedule = false;
        if(account.listFiles() != null) {
            for (File f : account.listFiles()) {
                if (f.getName().endsWith(".csv")) {
                    //cut off .csv
                    Main.autoflush.println("\t- " + f.getName().substring(0, f.getName().length() - 4));
                    if (!hasSchedule) hasSchedule = true;
                }
            }
        }
        if(!hasSchedule) Main.autoflush.println("\tNone");
        //TODO: Database is incorrectly printing out schedules that are in a folder, maybe use old print?
        //----------------------------------------------------
        // DATABASE
        Main.autoflush.println("Your Schedules: " + Main.currentaccnt.get_schednames());

    }

    public List<String> get_folders(){
        return folders;
    }

    public void pref_prof_menu(){
        Scanner scn = new Scanner(System.in);
        System.out.println("(add) -> add professor | (rem) -> remove professor | (lp) -> print list of professors | (back) -> exit preferred prof menu");
        String input = scn.next();
        while (true) {
            if (input.equalsIgnoreCase("add")){
                System.out.println("Enter the last name of the professor you'd like to add\n");
                String professor = scn.next();
                if (prof_list.contains(professor)){
                    preffered_profs.add(professor);
                }
                else {
                    System.out.println("Invalid entry\n");
                }
            }
            else if (input.equalsIgnoreCase("rem")){
                System.out.println("Enter the name of the professor you'd like to remove");
                String professor = scn.next();
                if (preffered_profs.contains(professor)){
                    preffered_profs.remove(professor);
                }
                else {
                    System.out.println("Invalid entry\n");
                }
            }
            else if (input.equalsIgnoreCase("lp")){
                if (preffered_profs.isEmpty()){
                    System.out.println("No professors saved.\n");
                }
                else {
                    System.out.println("Saved list of preferred professors:");
                    for (int i = 0; i < preffered_profs.size(); i++) {
                        System.out.println("  " + i + ". " + preffered_profs.get(i));
                    }
                }
            }
            else if (input.equalsIgnoreCase("back")){
                break;
            }
            else {
                System.out.println("Invalid input\n");
            }
            System.out.print("(add) -> add professor | (rem) -> remove professor | (lp) -> print list of professors | (back) -> exit preferred prof menu");
            input = scn.next();
        }
    }

    public void add_prof(String professor){
        preffered_profs.add(professor);
    }

    //Make the generated schedules in here
    public static void main(String[] arg){
        Account test = new Account("benjam","1234567",Major.COMP);
        test.pref_prof_menu();
    }
}
