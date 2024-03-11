import java.util.List;

public class Account {
    //another thing of loading:
    //we make a directory storing all accounts.... each account is a subdirectory of this directory that
    //has schedules in it

    // This is a test for learning how to use pull requests

    // this is another test
    private String username;

    private int passwordhash;

    private CourseCode major;

    private Schedule currentsched;

    private List<String> schednames;

    private Search search;

    //getters + setters not added yet

    //this could be done as a setter for major
    public boolean change_major(CourseCode newmajor) {return false;}

    public boolean change_password(String newpassword) {return false;}

    public boolean change_username(String newname) {return false;}

    public boolean save_schedule() {return false;}

    public boolean load_schedule(String schedname) {return false;} //default is to work with new Schedule

    public boolean delete_schedule(String schedname) {return false;}

    public boolean delete_account() {return false;}

    private boolean verify_password(String passwordattempt) {return false;}
}