import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
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

    public Account() {
        this.username = "empty";
    }

    //Constructor without major
    public Account(String username, String password) {
        this.username = username;
        this.passwordhash = password.hashCode();
        schednames = new ArrayList<String>();

    }

    //Constructor with major
    public Account(String username, String password, Major major) {
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
     *
     * @param newname new username
     * @return true once the username has been changed
     */
    public boolean change_username(String newname) {
        this.username = newname;
        return true;
    }

    /**
     * Saves the name of a created schedule to the list of schedules in an account
     *
     * @param sched_name the name of the new schedule
     * @return true once of the schedule has been added
     */
    public boolean save_schedule(String sched_name) throws SQLException {
        if (!schednames.contains(sched_name)) { // Changed this to check for sched_name in list
            schednames.add(sched_name);

            //-------------------------------------------------------
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

    public boolean delete_account() {
        return false;
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

    public List<String> get_schednames() {
        return schednames;
    }

    public void print_schedule_list() {
//        File account = new File("Accounts\\" + username);
//        Main.afl.println(username + " Schedules:");
//        boolean hasSchedule = false;
//        if (account.listFiles() != null) {
//            for (File f : account.listFiles()) {
//                if (f.getName().endsWith(".csv")) {
//                    //cut off .csv
//                    Main.afl.println("\t- " + f.getName().substring(0, f.getName().length() - 4));
//                    if (!hasSchedule) hasSchedule = true;
//                }
//            }
//        }
//        if (!hasSchedule) Main.afl.println("\tNone");
        //TODO: Database is incorrectly printing out schedules that are in a folder, maybe use old print?
        //----------------------------------------------------
        // DATABASE
        Main.afl.println("Your Schedules: " + Main.currentaccnt.get_schednames());
    }

    public List<String> get_folders() {
        return folders;
    }

    public static List<Schedule> random_gen(int year, String semester) {
        int courses_added = 0;
        Random course_select = new Random();
        List<Schedule> generated_scheds = new ArrayList<>();
        Search non_prof_search = new Search();
        for (int i = 0; i < 5; i++) {
            int number = i + 1;
            String sched_name = "GenSched" + number;
            List<Course> temp = new ArrayList<>();
            Schedule curr_sched = new Schedule(sched_name, semester, year, temp, 0);
            //HUMA addition
            List<Course> humas = gen_sched_research(non_prof_search,"HUMA",semester,year);
            try {
                int selected = course_select.nextInt(humas.size());
                Course huma_select = humas.get(selected);
                while (courses_added == 0) {
                    try {
                        if (curr_sched.add_course(huma_select)) courses_added += 1;
                    } catch (IllegalArgumentException iae) {
                        selected = course_select.nextInt(humas.size());
                        huma_select = humas.get(selected);
                    }
                }
            }
            catch(IllegalArgumentException iae) {
                //there were no huma courses
                Main.afl.println("no more HUMA courses");
            }
            //Addition of two major related courses
            String acct_major = String.valueOf(Main.currentaccnt.getMajor());
            List<Course> maj_courses = gen_sched_research(non_prof_search,acct_major,semester,year);
            try {
                int selected = course_select.nextInt(maj_courses.size());
                int majr_courses = 0;
                if (i % 2 == 0) {
                    majr_courses = 3;
                } else {
                    majr_courses = 4;
                }
                //while we haven't added the desired amount and there are courses to add
                while (courses_added != majr_courses && !maj_courses.isEmpty()) {
                    try {
                        if (curr_sched.add_course(maj_courses.remove(selected))) courses_added += 1;
                    } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                        selected = course_select.nextInt(maj_courses.size());
                    }
                }
            }
            catch(IllegalArgumentException iae) {
                //there were no major courses
                Main.afl.println("no more "+Main.currentaccnt.getMajor().name()+" courses");
            }
            //Addition a general elective class
            /*String add_major = "";
            if (i == 0){
                add_major = String.valueOf(Major.ART);
            } else if (i == 1) {
                add_major = String.valueOf(Major.ENGL);
            } else if (i == 2) {
                add_major = String.valueOf(Major.WRIT);
            } else if (i == 3) {
                add_major = String.valueOf(Major.COMM);
            } else if (i == 4) {
                add_major = String.valueOf(Major.ENTR);
            }*/
            List<Course> electives = gen_sched_research(non_prof_search,"all",semester,year);
            try {
                int selected = course_select.nextInt(electives.size());
                while (courses_added != 5 && !electives.isEmpty()) {
                    try {
                        if (curr_sched.add_course(electives.remove(selected))) courses_added += 1;
                    } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                        selected = course_select.nextInt(electives.size());
                    }
                }
            }
            catch(IllegalArgumentException iae) {
                //there were no elective courses
                Main.afl.println("no more elective courses");
            }
            Main.afl.println(curr_sched.to_str() + "\n");
            generated_scheds.add(curr_sched);
            courses_added = 0;
        }
        return generated_scheds;
    }

    public static List<Course> gen_sched_research(Search s,String searchstr,String sem,int year) {
        s.reset();
        //no filters now
        s.search(searchstr, false);
        List<Course> ret = s.get_filtered_results();
        //usually we're searching on a particular major
        if(!searchstr.equalsIgnoreCase("all")) s.activate_new_filter(new MajorFilter(ret,Major.valueOf(searchstr)));
        else s.deactivate_filter(new MajorFilter());
        s.activate_new_filter(new OpenFilter(ret));
        s.activate_new_filter(new SemesterFilter(ret,sem,year));
        return ret;
    }

    public void print_wishlist(){
        Main.afl.println("Your wishlist:");
        if(wishlist == null || wishlist.isEmpty()){
            Main.afl.println("\tNone");
            return;
        }
        for(Course c: wishlist)
            Main.afl.println("\t- " +c.short_str(true));
    }

    public boolean has_pref_profs(){
        return preffered_profs != null && !preffered_profs.isEmpty();
    }

    //Make the generated schedules in here

    /*public int int_input() {
        int input = 0;
        while (true) {
            Scanner input_scnr = new Scanner(System.in);
            if (input_scnr.hasNextInt()) {
                input = input_scnr.nextInt();
                input_scnr.close();
                break;
            } else {
                Main.afl.println("Invalid input. Please enter an integer.");
            }
            input_scnr.close();
        }
        return input;
    }*/

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
}
