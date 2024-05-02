import java.io.File;
import java.io.IOException;
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
        File account = new File("Accounts\\" + username);
        Main.autoflush.println(username + " Schedules:");
        boolean hasSchedule = false;
        if (account.listFiles() != null) {
            for (File f : account.listFiles()) {
                if (f.getName().endsWith(".csv")) {
                    //cut off .csv
                    Main.autoflush.println("\t- " + f.getName().substring(0, f.getName().length() - 4));
                    if (!hasSchedule) hasSchedule = true;
                }
            }
        }
        if (!hasSchedule) Main.autoflush.println("\tNone");
        //TODO: Database is incorrectly printing out schedules that are in a folder, maybe use old print?
        //----------------------------------------------------
        // DATABASE
        Main.autoflush.println("Your Schedules: " + Main.currentaccnt.get_schednames());

    }

    public List<String> get_folders() {
        return folders;
    }

    public List<Schedule> random_gen(int year, String semester) {
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
            List<Course> humas = non_prof_search.search("all", 500, false);
            MajorFilter humas_mf = new MajorFilter(humas,Major.HUMA);
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
            //Addition of two major related courses
            String acct_major = String.valueOf(Main.currentaccnt.getMajor());
            List<Course> maj_courses = non_prof_search.search(acct_major, 500, false);
            MajorFilter maj_crs_mf = new MajorFilter(maj_courses,Major.valueOf(acct_major));
            selected = course_select.nextInt(maj_courses.size());
            Course maj_select = maj_courses.get(selected);
            int majr_courses = 0;
            if (i % 2 == 0) {
                majr_courses = 3;
            } else {
                majr_courses = 4;
            }
            while (courses_added != majr_courses) {
                try {
                    if (curr_sched.add_course(maj_select)) courses_added += 1;
                } catch (IllegalArgumentException iae) {
                    selected = course_select.nextInt(maj_courses.size());
                    maj_select = maj_courses.get(selected);
                }
            }
            //Addition a general elective class
            String add_major = "";
            Random elective_sel = new Random();
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
            }
            List<Course> electives = non_prof_search.search(add_major, 500, false);
            MajorFilter elec_mf = new MajorFilter(electives,Major.valueOf(add_major));
            selected = course_select.nextInt(electives.size());
            Course found_select = electives.get(selected);
            int elec_courses = 5 - majr_courses;
            while (courses_added != 5) {
                try {
                    if (curr_sched.add_course(found_select)) courses_added += 1;
                } catch (IllegalArgumentException iae) {
                    selected = course_select.nextInt(electives.size());
                    found_select = electives.get(selected);
                }
            }
            System.out.println(curr_sched.to_str() + "\n");
            generated_scheds.add(curr_sched);
            courses_added = 0;
        }
        return generated_scheds;
    }

    //Make the generated schedules in here
    public void gen_sched_menu() throws IOException, SQLException {
        String semester;
        System.out.println("What semester would you like a schedule generated for?");
        Scanner scn = new Scanner(System.in);
        String choice = scn.next();
        while (!(choice.equalsIgnoreCase("Fall")) && !(choice.equalsIgnoreCase("Spring"))) {
            System.out.println("Invalid semester. Please enter 'Fall' or 'Spring'");
            choice = scn.next();
        }
        if (choice.equalsIgnoreCase("Fall")) {
            semester = "Fall";
        } else {
            semester = "Spring";
        }

        //Provide space inbetween
        System.out.println();
        List<Schedule> potential_scheds = random_gen(2020, semester);
        System.out.println();
        System.out.println("Would you like to add any of the following schedules (y/n)?");
        while(true) {
            Scanner scnr = new Scanner(System.in);
            String sched_choice = scnr.next();
            while (!(sched_choice.equalsIgnoreCase("n")) && !(sched_choice.equalsIgnoreCase("y"))) {
                System.out.println("Invalid choice. Please re-enter.");
                sched_choice = scnr.next();
            }
            if (sched_choice.equalsIgnoreCase("y")) {
                System.out.println("Which schedule would you like to add?");
                for (int i = 0; i < potential_scheds.size(); i++) {
                    int number = i + 1;
                    System.out.println("   " + number + ". " + potential_scheds.get(i).get_name());
                }
                int sched_num = int_input();
                boolean invalid_choice = true;
                while (invalid_choice) {
                    if (!(sched_num >= 1 && sched_num <= potential_scheds.size())) {
                        System.out.println("Choice is out of range. Please re-enter.");
                        sched_num = int_input();
                    } else {
                        invalid_choice = false;
                    }
                }
                sched_num--;
                Main.currentsched = potential_scheds.get(sched_num);
                System.out.println("Please enter a new name for your schedule:");
                Scanner naming = new Scanner(System.in);
                String sched_name = naming.next();
                if (sched_name.length() > 15) {
                    System.out.println("Schedule name is too long. Please try again.");
                    boolean invalid = true;
                    while (invalid) {
                        sched_name = naming.next();
                        if (sched_name.length() <= 15){
                            invalid = false;
                            Main.currentsched.setName(sched_name);
                            System.out.println("New schedule name successfully set!");
                        } else {
                            System.out.println("Schedule name is too long. Please try again.");
                        }
                    }
                }
                potential_scheds.remove(sched_num);
                Main.currentsched.save(Main.currentaccnt.getUsername()); // save to file
                Main.currentaccnt.save_schedule(Main.currentsched.get_name()); // save to list
                Main.autoflush.println(Main.currentsched.get_name() + " saved successfully");
                System.out.println("\nWould you like to add any additional scheules (y/n)?");
            } else {
                break;
            }
        }
    }

    public int int_input() {
        int input = 0;
        while (true) {
            Scanner input_scnr = new Scanner(System.in);
            if (input_scnr.hasNextInt()) {
                input = input_scnr.nextInt();
                break;
            } else {
                System.out.println("Invalid input. Please enter an integer.");
            }
        }
        return input;
    }
}
