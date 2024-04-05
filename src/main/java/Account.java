import java.io.File;
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
    public boolean save_schedule(String sched_name) {
        if (!schednames.contains(sched_name)) { // Changed this to check for sched_name in list
            schednames.add(sched_name);
            return true;
        }
        return false;
    }

    /*public Schedule load_schedule(String schedname) {
        //currentsched = new Schedule(username,schedname);
        //return currentsched;
    } //default is to work with new Schedule*/

    //NOT IMPLEMENTED
    public boolean delete_schedule(String schedname) {
        schednames.remove(schedname);
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
    }

    //NOT IMPLEMENTED
    public int num_scheds(){
        return schednames.size();
    }
}