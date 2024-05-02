import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
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
    private List<Course> wishlist = new ArrayList<>(); //TODO: Initialize a wishlist on an account-basis
    // will be added after courses have been searched
    private List<String> preffered_profs;

    private Set<String> majorcourses;

    private Set<String> coursestaken;

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
        coursestaken = new HashSet<>();
        try {
            if(major == Major.COMP) majorcourses = StatusSheetScraper.scrape();
            else majorcourses = new HashSet<>();
        }
        catch(Exception e) {Main.afl.println("Error: " + e.getMessage());}
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


    public Set<String> get_majorcourses() {return majorcourses;}

    public Set<String> get_coursestaken() {return coursestaken;}

    public boolean already_took(Course c) {return coursestaken.contains(c.short_str(false).substring(0,c.short_str(false).lastIndexOf(' ')));}

    public boolean is_major_course(Course c) {return majorcourses.contains(c.short_str(false).substring(0,c.short_str(false).lastIndexOf(' ')));}

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
        Main.afl.println("Your Schedules: " + Main.currentaccnt.get_schednames());
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

    public void print_wishlist(){
        Main.autoflush.println("Your wishlist:");
        if(wishlist == null || wishlist.isEmpty()){
            Main.autoflush.println("\tNone");
            return;
        }
        for(Course c: wishlist)
            Main.autoflush.println("\t- " +c.short_str(true));
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

    public List<Course> get_wishlist(){ return wishlist; }

    public String courses_taken_to_str() {
        StringBuilder sb = new StringBuilder("Courses Taken:");
        if(coursestaken == null || coursestaken.isEmpty()) {
            sb.append("\nNone");
            return sb.toString();
        }
        ArrayList<String> ct = new ArrayList<>(coursestaken);
        Collections.sort(ct);
        for(int i = 0; i < ct.size(); i++) {
            if(i % 5 == 0) sb.append("\n| ");
            sb.append(ct.get(i)).append(" |");
            //if we're not the last one and we're not the last one in a row
            if(i != ct.size()-1 && i % 5 != 4) sb.append(' ');
        }
        return sb.toString();
    }

    public void enter_courses_taken() throws IOException {
        Main.afl.println(courses_taken_to_str());
        Main.afl.println("Enter courses taken in the form '<major> <course_number>' or 'done' to finish account creation");
        while(true) {
            String[] cc = GeneralUtils.get_course_code("tkn");
            if(cc[0].equalsIgnoreCase("done")) break;
            //if not valid input (we already know that it's not "done")
            if(Validations.valid_course_code(cc)) coursestaken.add(String.join(" ",cc[0],cc[1]));
        }
        //by writing to the file at the end and not appending, we ensure that every line written to the file is unique
        //we ensure this because coursestaken is a set and will only have unique elements in it
        FileOutputStream fos = new FileOutputStream("Accounts\\" + username + "\\courses_taken.txt");
        PrintWriter pw = new PrintWriter(fos);
        for(String s : coursestaken) pw.println(s);
        pw.close();
    }

    //Make the generated schedules in here
}
