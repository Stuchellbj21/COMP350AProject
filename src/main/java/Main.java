import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    /*
    - The Accounts directory stores all of the user directories, as well as a file containing login
    information for all separate accounts
    - Each user directory contains separate csv files for each schedule saved to the account,
    as well as one info.txt file which stores user information and schedule names.
     */
    public static List<Course> allcourses; //set to null to avoid 'variable may not have been initialized'
    public static Scanner userin = new Scanner(System.in);
    public static PrintStream autoflush = new PrintStream(System.out, true);
    public static Search search = new Search();

    //associates password hash with String account name
    //change default value to null if actually having account selection
    //public static Account currentaccnt = new Account("NateAccount","password1234",Major.COMP);
    public static Map<Integer, String> accounts = new HashMap<>();
    public static Account currentaccnt = null;

    public static Folder current_folder = new Folder();

    /*  from user's perspective it appears that a new blank Schedule or a new custom Schedule
    has been created, but from our perspective we know that the schedule starts as a blank schedule
    and a custom schedule is simply a matter of changing the current schedule's attributes.
    A new blank schedule leaves the attributes of the current schedule as they are. */
    public static Schedule currentsched = new Schedule();


    //used for schedule and account names alike.... user must pass this test in order
    //to name something
    /*
    The following are reserved characters:
       < (less than),  > (greater than),  : (colon),  " (double quote),  / (forward slash)
       \ (backslash),  | (vertical bar or pipe),  ? (question mark),  * (asterisk)
    */

    public static boolean is_valid_name(String name) throws IllegalArgumentException {
        //various checks to ensure valid names
        if(name.isEmpty()) throw new IllegalArgumentException("Error: no blank schedule or account names allowed");
        if(name.equalsIgnoreCase("major") || name.equalsIgnoreCase("account") || name.equalsIgnoreCase("accounts")) throw new IllegalArgumentException("Error: you cannot name an account or schedule 'accounts', or 'account', or 'major'");
        if(name.length() > 20) throw new IllegalArgumentException("Error: account and schedule names cannot be longer than 20 characters");
        //issue if name conflict with schedules
        if(currentaccnt != null && new File("Accounts\\" + currentaccnt.getUsername() + "\\" + name + (name.endsWith(".csv") ? "" : ".csv")).exists()) throw new IllegalArgumentException("Error: a schedule with name '" + name + "' already exists");
        for(char c : name.toCharArray())
            if(c == '<' || c == '>' || c == ':' || c == '\"' || c == '/' || c == '\\' || c == '|' || c == '?' || c == '*') throw new IllegalArgumentException("Error: account and schedule names cannot contain any of the following characters: '*','?','|','/','\\','>','<',':','\"'");
        return true;
    }

    public static boolean is_valid_password(String password) throws IllegalArgumentException {
        //check if input password is valid
        if(accounts.containsKey(password.hashCode())) {
            throw new IllegalArgumentException("Error: that password is already taken");
        }
        if (password.length() < 7 || password.length() > 20) {
            throw new IllegalArgumentException("Error: password must be between 7 and 20 (inclusive) characters long");
        }
        return true;
    }

    //account has a schedule instance that is worked on
    public static void populate_allcourses() throws IOException {
        allcourses = new ArrayList<>();
        FileInputStream fis = new FileInputStream("2020-2021.csv");
        Scanner csvscn = new Scanner(fis);
        //accounts = new HashMap<Integer, String>();
        //skip the descriptors with nextLine()
        csvscn.nextLine();
        while (csvscn.hasNextLine()) {
            Scanner inline = new Scanner(csvscn.nextLine());
            inline.useDelimiter(",");
            String name = "", prof = "", sem = "";

            //define all variables for Course
            char section = '_';
            Major major = Major.COMP;
            int coursenum = -1, credits = 0, numstudents = 0, capacity = 0, year = -1;
            Set<Major> requiredby = new HashSet<>();
            List<Character> days = new ArrayList<>();
            //List<String> times = new ArrayList<>(); it seems that there are only ever classes with a single start and end time
            String[] times = new String[2];
            List<DayTime> daytimes = new ArrayList<>();
            for (int i = 0; inline.hasNext(); i++) {
                String n = inline.next();
                //make cases for i which enumerates the Course items
                //cases for days,times,name,etc.
                switch (i) {
                    case 0:
                        year = Integer.parseInt(n);
                        break;
                    case 1:
                        int s = Integer.parseInt(n);
                        if (s == 10) sem = "Fall"; //assuming 10 -> Fall, 30 -> Spring
                        else sem = "Spring";
                        break;
                    case 2:
                        major = Major.valueOf(n);
                        break;
                    case 3:
                        coursenum = Integer.parseInt(n);
                        break;
                    case 4:
                        if (!n.isBlank()) section = n.charAt(0);
                        break;
                    case 5:
                        name = n;
                        break;
                    case 6:
                        credits = Integer.parseInt(n);
                        break;
                    case 7:
                        if (!n.isBlank()) capacity = Integer.parseInt(n);
                        break;
                    case 8:
                        numstudents = Integer.parseInt(n);
                        break;
                    //add days
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                        if (!n.isBlank()) days.add(n.charAt(0));
                        break;
                    case 14:
                    case 15:
                        //XX:XX:XX AM/PM clip off :XX seconds thing at the end
                        if (!n.isBlank()) times[i - 14] = n.substring(0, n.length() - 6) + n.substring(n.length() - 3);
                        break;
                    case 16:
                    case 17:
                        if (i == 17) prof = " " + prof;
                        prof = n + prof;
                        break;
                }
                if (i > 17) break;
            }
            for (char d : days) daytimes.add(new DayTime(times[0], times[1], d));
            //add courses
            Course add = new Course(name, section, major, coursenum, credits, numstudents, capacity, prof, year, sem, requiredby, daytimes);
            add_course(add);
            inline.close();
        }
        csvscn.close();
    }

    public static void add_course(Course add) {
        //don't have to check null since we make allcourses a new ArrayList at start of populate_allcourses
        if (!allcourses.isEmpty()) {
            Course last = allcourses.getLast();
            //if the course is the same as the one before, just merge the daytimes into the last course
            if (add.getCourseNum() == last.getCourseNum() && add.getMajor() == last.getMajor()
                    && add.getSection() == last.getSection() && add.getYear() == last.getYear()
                    && add.getSemester().equalsIgnoreCase(last.getSemester())) {
                for (DayTime dt : add.getTimes())
                    if (!last.getTimes().contains(dt)) {
                        last.getTimes().add(dt);
                    }
            }
            //otherwise, add the full course
            else allcourses.add(add);
        } else allcourses.add(add);
    }

    public static String input(String prompt) {
        if (prompt != null) {
            autoflush.print(prompt);
        }
        //strip the new line off the end and any starting whitespace
        return userin.nextLine().strip();
    }

    public static void in_schedule() {
        while(true) {
            autoflush.println(currentsched.to_str());
            //modify will allow editing of the schedule
            String in = input("Enter (modify) -> modify schedule/(search) -> search for courses/(ac) -> add_course/\n(rc) -> remove course/(save) -> save schedule/(filter) -> edit filters/(del) -> delete schedule\n(exit) -> exit to schedule selection: ");
            if(in.equalsIgnoreCase("modify")) modify_schedule();
            else if(in.equalsIgnoreCase("search")) prompt_and_search();
            else if(in.equalsIgnoreCase("ac")) {
                if(search.get_filtered_results() == null || search.get_filtered_results().isEmpty())
                    autoflush.println("Error: if you wish to add a course, you must add it from search results and you currently have no search results");
                else add_course_to_schedule();
            } else if (in.equalsIgnoreCase("rc")) {
                if (currentsched.get_courses().isEmpty())
                    autoflush.println("Error: the current schedule does not contain any courses for removal");
                else remove_course_from_schedule();
            }
            else if(in.equalsIgnoreCase("save")) {
                try{
                    if(currentsched.get_name().equalsIgnoreCase("blank schedule")) autoflush.println("Error: rename schedule to something other than 'Blank Schedule' before saving");
                    else {
                        currentsched.save(currentaccnt.getUsername());
                        currentaccnt.save_schedule(currentsched.get_name());
                        autoflush.println(currentsched.get_name() + " saved successfully");
                    }
                }
                catch(IOException ioe) {autoflush.println(ioe.getMessage());}
            }
            else if(in.equalsIgnoreCase("del")) {
                currentaccnt.delete_schedule(currentsched.getName()); // todo
                currentsched.delete(currentaccnt.getUsername());
                autoflush.println("schedule deleted successfully");
                break;
            }
            else if(in.equalsIgnoreCase("filter")) edit_filters();
            else if(in.equalsIgnoreCase("exit")) break; //exit to schedule selection menu -> load schedule, new blank schedule, edit schedule (back to account menu)
            else autoflush.println("Error: '" + in + "' is an invalid response");
        }
    }

    public static void schedule_menu() throws IOException {
        while(true) {
            String in = input("(load) -> load a schedule/(new) -> create a new blank schedule/(ls) -> list saved schedules/(b) -> back to account menu: ");
            if(in.equalsIgnoreCase("load")) {
                String schedname = input("Enter the name of the schedule to load: ");
                try{
                    if(new File("Accounts\\" + currentaccnt.getUsername() + "\\" + schedname + (schedname.endsWith(".csv") ? "" : ".csv")).exists()) {
                        currentsched.load(currentaccnt.getUsername(), schedname);
                        autoflush.println("Schedule '" + schedname + "' loaded successfully");
                        in_schedule();
                    }
                    else autoflush.println("Error: that schedule does not exist");
                }
                catch(IOException ioe) {autoflush.println("Error: file did not load correctly");}
            }
            else if(in.equalsIgnoreCase("new")) {
                currentsched = new Schedule();
                autoflush.println("New blank schedule created");
                in_schedule();
            }
            else if(in.equalsIgnoreCase("ls")) currentaccnt.print_schedule_list();
            //go back to account menu
            else if(in.equalsIgnoreCase("b")) break;
            else autoflush.println("Error: invalid input");
        }
    }

    public static void edit_filters() {
        while(true) {
            autoflush.println("Active Filters: " + (search.get_active_filters() != null && !search.get_active_filters().isEmpty() ? search.get_active_filters() : "None"));
            String command = input("(a) -> add filter/(m) -> modify filter/(d) -> delete filter/(done) -> end filter editing: ");
            if(command.equalsIgnoreCase("a")) add_filter(false,null);
            else if(command.equalsIgnoreCase("m")) modify_filter();
            else if(command.equalsIgnoreCase("d")) delete_filter();
            else if(command.equalsIgnoreCase("done")) break;
            else autoflush.println("Error: '" + command + "' not recognized");
        }
    }

    //this method can be used for filter modification instead of pure filter addition.... in the case
    //where it is used for modification, modify arg should be true and tomod should not be null
    public static void add_filter(boolean modify, FilterType tomod) {
        //holds the original size of the active filters for comparison at the end
        int ogsize = search.get_active_filters().size();
        //by default we use the tomod filter type
        FilterType ft = tomod;
        //if we're not modifying then we use a filter type specified by user
        if(!modify) ft = get_filter_type("add");
        //we only want this statement to fire in the addition case, so if not modify
        if(!modify && search.get_active_filters().contains(new Filter(ft))) autoflush.println("Error: a " + ft.name().toLowerCase() + " filter is already active");
        else {
            switch(ft) {
                case DAYS -> {
                    Set<Character> days = get_days_for_filter(modify);
                    if(days != null) add_or_modify_filter(modify,new DaysFilter(search.get_filtered_results(),days));
                }
                case TIME -> {
                    DayTime time = get_time_for_filter(modify);
                    if(time != null) add_or_modify_filter(modify,new TimeFilter(search.get_filtered_results(),time));
                }
                case SEMESTER -> {
                    String[] semyear = get_semester_for_filter(modify);
                    if(semyear != null) add_or_modify_filter(modify,new SemesterFilter(search.get_filtered_results(),semyear[0],Integer.parseInt(semyear[1])));
                }
                case NAME -> add_or_modify_filter(modify,new NameFilter(search.get_filtered_results(),input("Enter the course name you would like to filter on: ")));
                case MAJOR -> {
                    Major m = get_major_for_filter(modify);
                    if(m != null) add_or_modify_filter(modify,new MajorFilter(search.get_filtered_results(),m));
                }
                case CREDIT -> {
                    Integer i = get_credits_for_filter(modify);
                    if(i != null) add_or_modify_filter(modify,new CreditFilter(search.get_filtered_results(),i));
                }
                case PROFESSOR -> add_or_modify_filter(modify,new ProfessorFilter(search.get_filtered_results(),input("Enter the name of the professor (in form '<first_name> <last_name>') you would like to filter on: ")));
            }
            if(!modify && search.get_active_filters().size() != ogsize) autoflush.println("Filter addition successful");
        }
    }

    public static void add_or_modify_filter(boolean modify, Filter f) {
        if(!modify) search.activate_new_filter(f);
        else search.modify_filter(f);
    }

    public static Integer get_credits_for_filter(boolean modify) {
        //i for int
        String i;
        boolean first = true;
        do {
            if(first) first = false;
            else autoflush.println("Error: invalid credit value");
            if(!filter_move_forward(modify,FilterType.CREDIT)) return null;
            //setting i equal to input here
        } while(!is_numeric(i = input("Enter number of credits to filter on: ")) || Integer.parseInt(i) < 0);
        //now i is a string representing a valid Integer
        return Integer.parseInt(i);
    }

    public static Major get_major_for_filter(boolean modify) {
        String m;
        boolean first = true;
        do {
            if(first) first = false;
            else autoflush.println("Error: invalid major value");
            if(!filter_move_forward(modify,FilterType.MAJOR)) return null;
            //setting m equal to input here
        } while(!Major.is_major(m = input("Enter major to filter on: ").toUpperCase()));
        //now m is a string representing a valid major
        return Major.valueOf(m);
    }

    public static String[] get_semester_for_filter(boolean modify) {
        while(true) {
            if(!filter_move_forward(modify,FilterType.SEMESTER)) return null;
            String[] semyear = input("Enter semester in the form 'Spring/Fall XXXX' where X is a digit: ").split("\\s+");
            if(!get_semester_formatted(semyear) || !valid_semester(semyear) || !valid_year(semyear)) continue;
            return semyear;
        }
    }

    public static boolean filter_move_forward(boolean modify,FilterType filtertype) {
        while(true) {
            String moveforward = input("Would you like to " + (modify ? "modify the " : "add a ") + filtertype.name().toLowerCase() + " filter? (y/n) ");
            if (moveforward.equalsIgnoreCase("n") || moveforward.equalsIgnoreCase("no")) return false;
            //if not yes or no: error
            if (!moveforward.equalsIgnoreCase("") && !moveforward.equalsIgnoreCase("yes") &&
                    !moveforward.equalsIgnoreCase("y")) {
                autoflush.println("Error: invalid input");
                continue;
            }
            //if yes return true
            return true;
        }
    }

    public static DayTime get_time_for_filter(boolean modify) {
        while(true) {
            if(!filter_move_forward(modify,FilterType.TIME)) return null;
            String start = input("Enter start time in the form XX:XX PM/AM (where X is a digit), or none (to only get classes with no times listed): ").toUpperCase();
            if(start.equalsIgnoreCase("none")) return new DayTime(start,"none",'_');
            if(!DayTime.is_valid_time(start)) {
                autoflush.println("Error: '" + start + "' is not a valid time");
                continue;
            }
            String end = input("Enter end time in the form XX:XX PM/AM (where X is a digit): ").toUpperCase();
            if(!DayTime.is_valid_time(end)) {
                autoflush.println("Error: '" + end + "' is not a valid time");
                continue;
            }
            DayTime r = new DayTime(start,end);
            if(DayTime.military_to_minutes(r.get_militarystart()) >= DayTime.military_to_minutes(r.get_militaryend())) {
                autoflush.println("Error: start time must be earlier than end time");
                continue;
            }
            return r;
        }
    }

    public static Set<Character> get_days_for_filter(boolean modify) {
        String[] days;
        while(true) {
            if(!filter_move_forward(modify,FilterType.DAYS)) return null;
            autoflush.println("Valid Days: M -> Monday, T -> Tuesday, W -> Wednesday, R -> Thursday, F -> Friday");
            days = input("Enter whitespace separated characters (see above) for days you would like to filter on: ").toUpperCase().split("\\s+");
            if(days.length < 1) {
                autoflush.println("Error: no days entered");
                continue;
            }
            boolean error = false;
            for(String d : days) {
                //if day is too long or incorrect: error
                if(d.length() > 1 || (!d.equalsIgnoreCase("M") && !d.equalsIgnoreCase("T") &&
                        !d.equalsIgnoreCase("W") && !d.equalsIgnoreCase("R") &&
                        !d.equalsIgnoreCase("F"))) {
                    autoflush.println("Error: '" + d + "' is not a valid day (M,T,W,R,F)");
                    error = true;
                    break;
                }
            }
            if(!error) break;
        }
        //r for return (this is the set containing the days)
        Set<Character> r = new HashSet<>();
        for(String s : days) r.add(s.charAt(0));
        return r;
    }

    public static void modify_filter() {
        if(search.get_active_filters() == null || search.get_active_filters().isEmpty()) {
            autoflush.println("Error: there are no filters to modify");
            return;
        }
        FilterType ft = get_filter_type("modify");
        if(!search.get_active_filters().contains(new Filter(ft))) autoflush.println("Error: there is no " + ft.name().toLowerCase() + " filter active");
        else {
            add_filter(true,ft);
            autoflush.println("Filter modification process successful");
        }
    }

    public static void delete_filter() {
        if(search.get_active_filters() == null || search.get_active_filters().isEmpty()) {
            autoflush.println("Error: there are no filters to delete");
            return;
        }
        FilterType ft = get_filter_type("delete");
        if(!search.get_active_filters().contains(new Filter(ft))) autoflush.println("Error: there is no " + ft.name().toLowerCase() + " filter active");
        else {
            search.deactivate_filter(new Filter(ft));
            autoflush.println("Filter deletion successful");
        }
    }

    public static FilterType get_filter_type(String operation) {
        autoflush.println("Filter types: credit,time,days,professor,name (course name),major,semester");
        boolean first = true;
        String ft = "";
        do {
            if(first) first = false;
            else autoflush.println("Error: '" + ft + "' is not a valid filter type");
            //ft for filter type
            ft = input("Enter filter type to " + operation + ": ").toUpperCase();
        } while(!FilterType.is_filter_type(ft));
        //once we have a valid filter type, return it
        return FilterType.valueOf(ft);
    }

    public static void remove_course_from_schedule() {
        boolean first = true;
        while (true) {
            if(first) first = false;
            else if(!want_more('r')) return;
            if(currentsched.get_courses().isEmpty()) {
                autoflush.println("Error: the current schedule does not contain any courses for removal");
                return;
            }
            String[] cc = get_course_code(false);
            if (!valid_course_code(cc)) continue;
            //we don't have to check the section.... we know only 1 section of a given course code can be added
            //we don't have to check the semester in this case.... we know that the user is only able to add courses
            //for the current schedule's semester
            Course rm = null;
            for (Course c : currentsched.get_courses()) {
                if (c.getMajor() == Major.valueOf(cc[0]) && c.getCourseNum() == Integer.parseInt(cc[1])) {
                    rm = c;
                    break;
                }
            }
            if (rm == null) autoflush.println("Error: " + cc[0] + " " + cc[1] + " not found in schedule");
            else {
                currentsched.remove_course(rm);
                autoflush.println(cc[0] + " " + cc[1] + " successfully removed from the schedule");
            }
        }
    }

    public static void prompt_and_search() {
        int threshold;
        boolean sorted = false;
        while (true) {
            String in = input("Enter maximum number of visible search results: ");
            try {
                threshold = Integer.parseInt(in);
                sorted = want_more('s');
                break;
            } catch (NumberFormatException nfe) {
                autoflush.println("Error: " + in + " is not a valid integer. Enter an integer value.");
            } catch (IllegalArgumentException iae) {
                autoflush.println(iae.getMessage());
            }
        }
        Main.search.search(input("Enter search string: "), sorted);
        autoflush.println(Main.search.to_str(threshold));
    }

    public static boolean is_numeric(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static void modify_schedule() {
        while (true) {
            String in = input("What attribute of the current schedule would you like to modify? (name/semester/year/none): ");
            if(in.equalsIgnoreCase("name")) currentsched.set_name_with_checks(currentaccnt.getUsername());
            else if(in.equalsIgnoreCase("semester")) currentsched.set_semester_with_checks();
            else if(in.equalsIgnoreCase("year")) currentsched.set_year_with_checks();
            else if(in.equalsIgnoreCase("none")) break;
            else autoflush.println("Error: '" + in + "' is not a valid attribute to modify");
        }
    }

    public static void add_course_to_schedule() {
        boolean first = true;
        while (true) {
            if (first) first = false;
            else if (!want_more('a')) return;
            autoflush.println("Warning: you will only be able to add courses for " + currentsched.get_semester() + " " + currentsched.get_year());
            String[] cc = get_course_code(true);
            if (!valid_course_code(cc)) continue;
            String coursecode = cc[0] + " " + cc[1];
            String section = input("Enter the section of the course to add: ").toUpperCase();
            if (!is_valid_section(section)) continue;
            /*String[] sem = input("Enter semester and year of the course to add (you will only be able to add courses for " + currentsched.get_semester() + " " + currentsched.get_year() + "): ").strip().split("\\s+");
            //get semester in correct form, then check valid year value, then check valid semester value
            if(!get_semester_formatted(sem) || !valid_year(sem) || !valid_semester(sem)) continue;*/
            String toadd = coursecode + " " + section + " - " + currentsched.get_semester() + " " + currentsched.get_year();
            boolean addattempted = false;
            for(Course c : search.get_filtered_results())
                if(toadd.equals(c.short_str(true))) {
                    try {if(currentsched.add_course(c)) autoflush.println(toadd + " has been added to the current schedule");}
                    catch(IllegalArgumentException iae) {autoflush.println(iae.getMessage());}
                    addattempted = true;
                }
            if (!addattempted) autoflush.println("Error: " + toadd + " not found in search results");
        }
    }

    public static boolean valid_semester(String[] sem) {
        if (!sem[0].equals("Fall") && !sem[0].equals("Spring")) {
            autoflush.println("Error: invalid semester value (semester must be either Fall or Spring)");
            return false;
        }
        return true;
    }

    public static boolean get_semester_formatted(String[] sem) {
        if(sem.length == 2 && sem[0] != null && sem[0].length() > 1) {
            sem[0] = sem[0].substring(0,1).toUpperCase() + sem[0].substring(1).toLowerCase();
            return true;
        }
        autoflush.println("Error: invalid semester-year input");
        return false;
    }

    public static boolean valid_year(String[] sem) {
        if(!is_numeric(sem[1]) || sem[1].length() != 4 || Integer.parseInt(sem[1]) < 2020) {
            autoflush.println("Error: invalid year value");
            return false;
        }
        return true;
    }

    public static boolean valid_course_code(String[] coursecode) {
        if (coursecode.length != 2 || !Major.is_major(coursecode[0]) || !is_numeric(coursecode[1]) || coursecode[1].length() != 3) {
            autoflush.println("Error: invalid course code");
            return false;
        }
        return true;
    }

    public static boolean is_valid_section(String section) {
        if (section.length() > 1 || !Character.isAlphabetic(section.charAt(0))) {
            autoflush.println("Error: invalid section");
            return false;
        }
        return true;
    }

    public static String[] get_course_code(boolean add) {
        String[] cc;
        if (add) cc = input("Enter the course code of the course to add (major course_number): ").strip().split("\\s+");
        else cc = input("Enter the course code of the course to remove (major course_number): ").strip().split("\\s+");
        cc[0] = cc[0].toUpperCase();
        return cc;
    }

    //method name is a question
    //'a' for add, 'r' for remove, 's' for sorted search
    public static boolean want_more(char type) {
        while (true) {
            String ans;
            if(type == 'a') ans = input("Continue adding courses? (y/n) ");
            else if(type == 'r') ans = input("Continue removing course? (y/n) ");
            else if(type == 's') ans = input("Apply sorting to results? (y/n) ");
            else throw new IllegalArgumentException("Error: didn't correctly specify type");
            if (ans.equalsIgnoreCase("no") || ans.equalsIgnoreCase("n")) return false;
            else if (ans.equalsIgnoreCase("yes") || ans.equalsIgnoreCase("y") || ans.isEmpty()) return true;
            else autoflush.println("Invalid");
        }
    }

    public static void accountMenu() throws IOException {
        while (true) {
            String in = input("Enter (create) -> create new Account/ (login) -> login to existing account\n" +
                    "(close) -> exit program\n");
            if (in.equalsIgnoreCase("create")) {
                if (createAccount()) {
                    //successfully create account and move into schedule menu
                    sched_or_folder();
                }
            } else if (in.equalsIgnoreCase("login")) {
                if (login()) {
                    sched_or_folder();
                }
            } else if (in.equalsIgnoreCase("close")) {
                if (currentaccnt != null) {
                    account_flush(); // should not be called when no accounts have been made
                }
                close_accounts();
                System.exit(0); // kill the program with no errors
            } else {
                autoflush.println("Error: '" + in + "' is an invalid response");
            }
        }
    }

    public static boolean check_username(String username) {
        try{
            //issue if name conflict with accounts
            if(new File("Accounts\\" + username + "\\").exists() || accounts.containsValue(username)) throw new IllegalArgumentException("Error: an account with name '" + username + "' already exists");
            is_valid_name(username);
        }
        catch(IllegalArgumentException iae) {
            autoflush.println(iae.getMessage());
            return false;
        }
        return true;
    }

    public static String create_username() {
        while (true) {
            String un = input("Enter (<YourUserName>) --> set new username / (back) --> return to Account screen\n");
            if (un.equalsIgnoreCase("back")) {
                return "back";
            } else if (check_username(un)) {
                return un;
            }
        }
    }

    public static String create_password() {
        while (true) {
            try {
                String pw = input("Enter (<YourPassword>) -> set new password / (back) -> return to Account screen\n");
                if (pw.equalsIgnoreCase("back")) {
                    return "back";
                } else if (is_valid_password(pw)) {
                    return pw;
                }
            }
            catch(IllegalArgumentException e) {
                autoflush.println(e.getMessage());
            }
        }
    }

    public static String enter_major() {
        while (true) {
            String in = input("Enter (major) -> set major, e.g. 'COMP' / (back) -> Account menu\n");
            if (in.equalsIgnoreCase("back")) {
                return "back";
            } else if (Major.is_major(in.toUpperCase())) {
                return in.toUpperCase();
            } else {
                autoflush.println("Error: '" + in + "' is an invalid major");
            }
        }
    }

    public static boolean login() throws IOException {
        //Checks to see if any accounts exist. If not, returns false.
        if (accounts.isEmpty()) {
            autoflush.println("No accounts exist -> Please create account or exit");
            System.out.println();
            return false;
        }
        String un = input("Enter (username) -> / (back) -> return to Account menu\n");
        if (un.equalsIgnoreCase("back")) {
            return false;
        }
        String pw = input("Enter (password) -> / (back) -> return to Account menu\n");
        if (pw.equalsIgnoreCase("back")) {
            return false;
        }
        if (un.equals(accounts.get(pw.hashCode()))) { //todo: ensure this is valid way to check password
            currentaccnt = new Account(un, pw, Major.COMP); //  todo: for now, this just makes a new schedule with default major;
            load_schedules();
            load_folders();
            return true;
        } else {
            autoflush.println("Incorrect password or username");
            return false;
        }
    }

    public static boolean createAccount() throws IOException {
        String username = create_username();
        if (username.equalsIgnoreCase("back")) {
            return false; // return to Account menu
        }
        String password = create_password();
        if (password.equalsIgnoreCase("back")) {
            return false;  //return to Account menu
        }
        String m = enter_major();
        if (m.equalsIgnoreCase("back")) {
            return false;
        }
        Major major = Major.valueOf(m);
        currentaccnt = new Account(username, password, major);
        accounts.put(password.hashCode(), username); // add account to the map
        File accountDir = new File("Accounts\\" + currentaccnt.getUsername());
        accountDir.mkdir(); // create new folder for each new account that's created

        // store user's password-hash, username, and major in user-specific txt file
        File f = new File("Accounts\\" + currentaccnt.getUsername() + '\\' + "info.txt");
        FileWriter fw = new FileWriter(f, true);
        fw.write(username + ", " + password.hashCode() + ", " + major + "\n");
        fw.close();
        //---------------------------------------------------------
        autoflush.println("Account successfully created\n");
        return true;
    }

    /**
     * Reads from File that stores account identification information and stores it in accounts map
     *
     * @throws FileNotFoundException
     */
    public static void load_accounts() throws FileNotFoundException {
        //Opens a file to the accoutns directory text file
        File accts = new File("Accounts\\account_direc.txt");
        Scanner acct_scnr = new Scanner(accts);
        acct_scnr.useDelimiter(":");
        Scanner line_reader;
        while (acct_scnr.hasNextLine()) {
            line_reader = new Scanner(acct_scnr.nextLine());
            //Gets the line on which each accounts username and password is stored
            line_reader.useDelimiter(":");
            String str_pass_hash = line_reader.next(); //gets the string hash password and converts it to int in order to add it to the accounts Map
            int int_pass_hash = Integer.parseInt(str_pass_hash);
            String account_name = line_reader.next();
            accounts.put(int_pass_hash, account_name); //Adds the accounts username and hash password to the accounts Map
        }
    }


    /**
     * Reads from user's info.txt file and adds all saved schedules to a static list in main
     * @throws IOException
     */
    public static void load_schedules() throws IOException {
        FileInputStream fis = new FileInputStream("Accounts\\" + currentaccnt.getUsername() + '\\' + "info.txt");
        Scanner infoScan = new Scanner(fis);
        infoScan.nextLine(); // skip line that contains account information (password-hash, username, major)
        if (infoScan.hasNextLine()) {
            String schedules = infoScan.nextLine().replace("\n", "");
            Scanner schedScan = new Scanner(schedules);
            schedScan.useDelimiter(",");
            while (schedScan.hasNext()) {
                String temp = schedScan.next();
                currentaccnt.save_schedule(temp);
            }
        }
        infoScan.close();
        fis.close();
    }

    public static void load_folders() throws IOException {
        FileInputStream fis = new FileInputStream("Accounts\\" + currentaccnt.getUsername() + '\\' + "info.txt");
        Scanner infoScan = new Scanner(fis);
        infoScan.nextLine(); // skip line that contains account information (password-hash, username, major)
        if (infoScan.hasNextLine()) {
            infoScan.useDelimiter(",");
            while (infoScan.hasNext()) {
                String temp = infoScan.next();
                if (!(temp.equals("\n"))) {
                    currentaccnt.add_folder(temp);
                }
            }
        }
        infoScan.close();
        fis.close();
    }

    /**
     * Ensures all accounts in the accounts-hashmap are written to a file before the program is closed,
     * allowing program to reboot with saved account information
     *
     * @throws FileNotFoundException
     */
    public static void close_accounts() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter("Accounts\\account_direc.txt");
        //Gets a list of the keys of the accounts Map to loop through the map
        Set<Integer> keys = accounts.keySet();
        for (int key : keys) {
            String hash_password = String.valueOf(key);
            String username = accounts.get(key);
            pw.write(hash_password + ":" + username + "\n"); //Gets the username and password for each account and prints both to the directory file seperated by a colon and ending with a newline
        }
        pw.close();
    }

    /**
     * Completes the info.txt file associated with each account by writing password-hash, username, major
     * and schedule names to file in the user's account directory.
     * @throws IOException
     */
    public static void account_flush() throws IOException {
        FileReader reader = new FileReader(("Accounts\\" + currentaccnt.getUsername() + '\\' + "info.txt"));
        BufferedReader br = new BufferedReader(reader);
        String tempLine = br.readLine(); // save first line of file since it will be overwritten
        br.close();

        File f = new File("Accounts\\" + currentaccnt.getUsername() + '\\' + "info.txt");
        FileWriter fw = new FileWriter(f, false); // rewrite the whole file
        fw.write(tempLine + "\n");
        for (int i = 0; i < currentaccnt.get_schednames().size(); i++) {
            if (i != currentaccnt.get_schednames().size()-1) {
                fw.write(currentaccnt.get_schednames().get(i) + ",");
            } else {
                fw.write(currentaccnt.get_schednames().get(i));
            }
        }
        fw.write("\n");
        for (int i = 0; i < currentaccnt.get_folders().size(); i++) {
            fw.write(currentaccnt.get_folders().get(i) + ",");
        }
        fw.close();
    }

    /**
     * Method creates a new folder in the account and assigns the new folder to the current_folder object
     * @throws IOException
     */
    public static void create_folder() throws IOException {
        autoflush.println("Enter the name for your folder:");
        Scanner scnr = new Scanner(System.in);
        String folder_name = scnr.next();
        currentaccnt.add_folder(folder_name);
        current_folder = new Folder(folder_name);
        current_folder.save_folder(currentaccnt.getUsername());
        autoflush.println("Folder " + folder_name + " created successfully!\n");
    }

    public static void print_folder_list() {
        List<String> folders = currentaccnt.get_folders();
        autoflush.println("List of saved folders:");
        if (folders.isEmpty()){
            autoflush.println("No folders saved in account.");
        } else {
            for (String folder : folders) {
                autoflush.println("   - " + folder);
            }
        }
        autoflush.println();
    }

    public static void folder_menu() throws IOException {
        while(true) {
            String in = input("(load) -> load a folder/(new) -> create a new folder/(lf) -> list of folders/(b) -> back to account menu: \n");
            if(in.equalsIgnoreCase("load")) {
                String folder_name = input("Enter the name of the folder to load: ");
                if(new File("Accounts\\" + currentaccnt.getUsername() + "\\" + folder_name + '\\' + folder_name + (folder_name.endsWith(".txt") ? "" : ".txt")).exists()) {
                    current_folder.load_folder(currentaccnt.getUsername(), folder_name);
                    autoflush.println("Folder '" + folder_name + "' loaded successfully\n");
                    in_folder();
                }
                else autoflush.println("Error: that folder does not exist\n");
            }
            else if(in.equalsIgnoreCase("new")) {
                create_folder();
            }
            else if(in.equalsIgnoreCase("b")) break;
            else if(in.equalsIgnoreCase("lf")) print_folder_list();
            else autoflush.println("Error: invalid input");
            current_folder = new Folder();
        }
    }

    public static void in_folder() throws IOException {
        while(true) {
            autoflush.println(current_folder.to_str());
            //modify will allow editing of the folder
            String in = input("Enter (add) -> add schedule to folder/(remove) -> remove schedule from folder/(open) -> open schedule from folder/(del) -> delete folder/\n(exit) -> exit to menu: \n");
            if(in.equalsIgnoreCase("add")) add_schedule();
            else if(in.equalsIgnoreCase("remove")) remove_sched();
            else if(in.equalsIgnoreCase("open")) {
                String schedname = input("Enter the name of the schedule to load: ");
                try{
                    if(new File("Accounts\\" + currentaccnt.getUsername() + "\\" + current_folder.getF_name() + "\\" + schedname + (schedname.endsWith(".csv") ? "" : ".csv")).exists()) {
                        currentsched.f_load(currentaccnt.getUsername(), current_folder.getF_name(), schedname);
                        autoflush.println("Schedule '" + schedname + "' loaded successfully\n");
                        in_schedule();
                    }
                    else autoflush.println("Error: that schedule does not exist");
                }
                catch(IOException ioe) {autoflush.println("Error: file did not load correctly");}
            }
            else if(in.equalsIgnoreCase("exit")) {
                current_folder.save_folder(currentaccnt.getUsername());
                break; //exit to schedule selection menu -> load schedule, new blank schedule, edit schedule (back to account menu)
            }
            else if(in.equalsIgnoreCase("del")) {
                delete_folder();
                break;
            }
            else autoflush.println("Error: '" + in + "' is an invalid response");
        }
    }

    public static void add_schedule() throws IOException {
        autoflush.println("Enter the name number of the schedule you would like to add to the '" + current_folder.getF_name() + "' folder:");
        List<String> curr_scheds = current_folder.print_schedule_list(currentaccnt.getUsername());
        if (curr_scheds.isEmpty()){
            autoflush.println("No schedules to add.");
        } else {
            for (int i = 0; i < curr_scheds.size(); i++) {
                autoflush.println(i + 1 + ". " + curr_scheds.get(i));
            }
            int num_scheds = curr_scheds.size();
            Scanner input = new Scanner(System.in);
            int choice = Integer.parseInt(input.next());
            while (choice < 1 || choice > num_scheds) {
                autoflush.println("Invalid choice, please try again.");
                choice = Integer.parseInt(input.next());
            }
            choice--;
            String added_sched = curr_scheds.get(choice);
            if (!(current_folder.contains_sched(added_sched))) {
                current_folder.add_schedule(added_sched);
                Path temp = Files.move(Paths.get("Accounts" + '\\' + currentaccnt.getUsername() + '\\' + added_sched + ".csv"), Paths.get("Accounts" + '\\' + currentaccnt.getUsername() + '\\' + current_folder.getF_name() + '\\' + added_sched + ".csv"));
                autoflush.println("Schedule '" + added_sched + "' has been added to folder '" + current_folder.getF_name() + "'");
            } else {
                autoflush.println("Folder already contains schedule '" + added_sched + "'\n");
            }
        }
    }

    public static void remove_sched() throws IOException {
        autoflush.println("Enter the number of the schedule you'd like to remove from folder " + current_folder.getF_name());
        current_folder.print_schedule_list(currentaccnt.getUsername());
        for (int i = 0; i < current_folder.getList_of_scheds().size(); i++) {
            autoflush.println(i+1 + ". " + current_folder.getList_of_scheds().get(i));
        }
        int num_scheds = current_folder.getList_of_scheds().size();
        Scanner input = new Scanner(System.in);
        int choice = Integer.parseInt(input.next());
        if (choice < 1 || choice > num_scheds){
            autoflush.println("Invalid choice.");
        }
        else {
            choice--;
            String added_sched = current_folder.getList_of_scheds().get(choice);
            if (current_folder.contains_sched(added_sched)) {
                if (current_folder.remove_sched(added_sched)) {
                    Path temp = Files.move(Paths.get("Accounts" + '\\' + currentaccnt.getUsername() + '\\' + current_folder.getF_name() + '\\' + added_sched + ".csv"), Paths.get("Accounts" + '\\' + currentaccnt.getUsername() + '\\' + added_sched + ".csv"));
                    autoflush.println("Schedule " + added_sched + " removed from folder " + current_folder.getF_name() +"\n");
                } else {
                    autoflush.println("Removal unsuccessful. Sorry.");
                }
            } else {
                autoflush.println("Removal unsuccessful. Sorry.");
            }
        }
    }

    public static void sched_or_folder() throws IOException {
        while(true) {
            autoflush.println("Enter (sched) -> schedule menu/(folder) -> folder menu/(exit) -> exit to login");
            Scanner input = new Scanner(System.in);
            String in = input.next();
            if (in.equals("sched")) {
                schedule_menu();
            } else if (in.equals("folder")) {
                folder_menu();
            } else if (in.equals("exit")) {
                break;
            } else {
                autoflush.println("Invalid input, please try again.");
            }
        }
    }

    //TODO: Fix so that schedules deleted in a folder are also deleted in an account
    public static void delete_folder(){
        String folder_name = current_folder.getF_name();
        currentaccnt.remove_folder(folder_name);
        current_folder.delete(currentaccnt.getUsername());
    }

    public static void run() throws IOException {
        try {load_accounts();}
        catch (Exception e) {autoflush.println("no accounts to load");}
        populate_allcourses();
        autoflush.println("Welcome to Descartes Favorite Scheduling App.... Enjoy");
        accountMenu();
    }

    public static void main(String[] args) {
        try {
            run();
        } catch (IOException ioe) {
            autoflush.println(ioe.getMessage() + "\n" + ioe.getCause());
        }
    }
}
