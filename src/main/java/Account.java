import java.util.ArrayList;
import java.util.List;

public class Account {
    //another thing of loading:
    //we make a directory storing all accounts.... each account is a subdirectory of this directory that
    //has schedules in it

    private String username;

    private int passwordhash;

    private Major major;

    private Schedule currentsched;

    private List<String> schednames;

    public Account(){
        this.username = "empty";
    }

    //Constructor with major
    public Account(String username,String passwordhash,Major major){
        this.username = username;
        this.passwordhash = passwordhash.hashCode();
        this.major = major;
        schednames = new ArrayList<String>();
    }

    //getters + setters not added yet

    //this could be done as a setter for major
    public boolean change_major(Major newmajor) {
        this.major = newmajor;
        return true;
    }

    public boolean change_password(String newpassword) {
        passwordhash = newpassword.hashCode();
        return true;
    }

    public boolean change_username(String newname) {
        this.username = newname;
        return true;
    }

    public boolean save_schedule(String sched_name) {
        schednames.add(sched_name);
        return true;
    }

    public Schedule load_schedule(String schedname) {
        currentsched = new Schedule(username,schedname);
        return currentsched;
    } //default is to work with new Schedule

    public boolean delete_schedule(String schedname) {
        schednames.remove(schedname);
        return true;
    }

    public boolean delete_account() {return false;}

    public boolean verify_password(String passwordattempt) {
        int passwordhash = passwordattempt.hashCode();
        return this.passwordhash == passwordhash;
    }

    public boolean has_schedule (String schedname){
        return schednames.contains(schedname);
    }

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

    /**
     This method gets the number of schedules currently saved in an account.
     @return the number of schedules saved in an account
     **/
    public int num_scheds(){
        return schednames.size();
    }

    public void load_schednames(ArrayList<String> schedules){
        schednames.addAll(schedules);
    }
}