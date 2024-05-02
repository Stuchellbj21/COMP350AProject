import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Account {
    //another thing of loading:
    //we make a directory storing all accounts.... each account is a subdirectory of this directory that
    //has schedules in it

    private String username;
    private int passwordhash;
    private Major major;
    private List<String> schednames;
    private List<String> preffered_profs;

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
//        File account = new File("Accounts\\" + username);
//        Main.autoflush.println(username+" Schedules:");
//        boolean hasSchedule = false;
//        if(account.listFiles() != null) {
//            for (File f : account.listFiles()) {
//                if (f.getName().endsWith(".csv")) {
//                    //cut off .csv
//                    Main.autoflush.println("\t- " + f.getName().substring(0, f.getName().length() - 4));
//                    if (!hasSchedule) hasSchedule = true;
//                }
//            }
//        }
//        if(!hasSchedule) Main.autoflush.println("\tNone");

        //----------------------------------------------------
        // DATABASE
        Main.autoflush.println("Your Schedules: " + Main.currentaccnt.get_schednames());

    }

    public void print_pref_profs(){
        System.out.print("List of Preferred Professors: ");
        for (int i = 0; i < preffered_profs.size(); i++){
            if (i == preffered_profs.size()-1){
                System.out.print(preffered_profs.get(i));
            } else {
                System.out.print(preffered_profs.get(i) + ", ");
            }
        }
        System.out.println();
    }

    public boolean has_pref_profs(){
        return preffered_profs != null && !preffered_profs.isEmpty();
    }

    public List<String> get_pref_profs(){
        return preffered_profs;
    }

    //NOT IMPLEMENTED
    public int num_scheds(){
        return schednames.size();
    }

    public List<String> get_folders(){
        return folders;
    }

    //Make the generated schedules in here
}
