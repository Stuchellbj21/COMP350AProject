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

    private Search search;

    //Constructor without major
    public Account(String username,int passwordhash){
        this.username = username;
        this.passwordhash = passwordhash;
    }

    //Constructor with major
    public Account(String username,int passwordhash,Major major){
        this.username = username;
        this.passwordhash = passwordhash;
        this.major = major;
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

    public boolean load_schedule(String schedname) {
        currentsched = new Schedule(schedname);
        return true;
    } //default is to work with new Schedule

    public boolean delete_schedule(String schedname) {
        schednames.remove(schedname);
        return true;
    }

    public boolean delete_account() {return false;}

    private boolean verify_password(String passwordattempt) {
        int passwordhash = passwordattempt.hashCode();
        return this.passwordhash == passwordhash;
    }

    private boolean has_schedule (String schedname){
        return schednames.contains(schedname);
    }
}