import java.io.PrintStream;
import java.util.*;
import java.io.IOException;
import java.io.FileInputStream;

public class Main {
    //this is where we will keep our JFrame

    //for ease of access we may want to have current schedule and search from account in main
    //we’ll make a directory per account with all of the schedules belonging to that account in it

    public static List<Course> allcourses; //set to null to avoid var may not have been initialized
    //public static List<String> allprofessors; this doesn't seem like a necessary variable

    //we will have a directory in which we store all of the account directories
    //within each account directory there will be csv/txt/other files which represent the saved
    //schedules for those accounts

    public static Scanner userin = new Scanner(System.in);

    public static PrintStream autoflush = new PrintStream(System.out,true);

    public static Search search = new Search();

    //associates password hash with String account name
    public static Map<Integer,String> accounts = new HashMap<>();

    public static Account currentaccnt = null;

    //from the user's perspective it will simply appear that the user has made a new blank Schedule
    //or a new custom schedule, but from our perspective we know that the schedule starts as a blank schedule
    //and a custom schedule is simply a matter of changing the attributes of the current schedule
    //a new blank schedule is simply leaving the attributes of the current schedule as they are
    public static Schedule currentsched = new Schedule();

    public static boolean input_verification(List<String> good_inputs, String user_input) {
        boolean is_good = false;
        for (int i = 0; i < good_inputs.size(); i++) {
            if (user_input.equalsIgnoreCase(good_inputs.get(i))) {
                is_good = true;
            }
        }
        return is_good;
    }

    //used for schedule and account names alike.... user must pass this test in order
    //to name something
    /*
    The following reserved characters:
       < (less than)
       > (greater than)
       : (colon)
       " (double quote)
       / (forward slash)
       \ (backslash)
       | (vertical bar or pipe)
       ? (question mark)
       * (asterisk)
    */
    public static boolean is_valid_name(String name) {
        if(name.equalsIgnoreCase("major") || name.equalsIgnoreCase("account") || name.equalsIgnoreCase("accounts")) throw new IllegalArgumentException("Error: you cannot name an account or schedule 'accounts', or 'account', or 'major'");
        if(name.length() > 20) throw new IllegalArgumentException("Error: account and schedule names cannot be longer than 20 characters");
        for(char c : name.toCharArray())
            if(c == '<' || c == '>' || c == ':' || c == '\"' || c == '/' || c == '\\' || c == '|' || c == '?' || c == '*') throw new IllegalArgumentException("Error: account and schedule names cannot contain any of the following characters: '*','?','|','/','\\','>','<',':','\"'");
        return true;
    }

    public static boolean is_valid_password(String password) {
        if(password.length() < 7 || password.length() > 20) throw new IllegalArgumentException("Error: password must be between 7 and 20 (inclusive) characters long");
        return true;
    }

    //account has a schedule instance that is worked on
    public static void populate_allcourses() throws IOException {
        FileInputStream fis = new FileInputStream("2020-2021.csv");
        Scanner csvscn = new Scanner(fis);
        allcourses = new ArrayList<>();
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
            Course add = new Course(name, section, major, coursenum, credits, numstudents, capacity, prof, year, sem, requiredby, daytimes);
            add_course(add);
            inline.close();
        }
        csvscn.close();
    }

    public static void add_course(Course add) {
        if(!allcourses.isEmpty()) {
            Course last = allcourses.getLast();
            //if the course is the same as the one before, just merge the daytimes into the last course
            if (add.getCourseNum() == last.getCourseNum() && add.getMajor() == last.getMajor()
                    && add.getSection() == last.getSection() && add.getYear() == last.getYear()
                    && add.getSemester().equalsIgnoreCase(last.getSemester())) {
                for(DayTime dt : add.getTimes())
                    if(!last.getTimes().contains(dt)) {
                        last.getTimes().add(dt);
                    }
            }
                //otherwise, add the full course
            else allcourses.add(add);
        }
        else allcourses.add(add);
    }

    public static String input(String prompt) {
        if(prompt != null) {autoflush.print(prompt);}
        //strip the new line off the end and any starting whitespace
        return userin.nextLine().strip();
    }

    public static void in_schedule() {
        boolean saved = false;
        while(true) {
            autoflush.println(currentsched.to_str());
            //modify will allow editing of the schedule
            String in = input("Enter (modify) -> modify schedule/(search) -> search for courses/(ac) -> add_course/\n(rc) -> remove course/(save) -> save schedule/(exit) -> exit to schedule selection: ");
            if(in.equalsIgnoreCase("modify")) {modify_schedule();}
            else if(in.equalsIgnoreCase("search")) {prompt_and_search();}
            else if(in.equalsIgnoreCase("ac")) {
                if(search.get_filteredresults() == null || search.get_filteredresults().isEmpty())
                    autoflush.println("Error: if you wish to add a course, you must add it from search results and you currently have no search results");
                else add_course_to_schedule();
            }
            else if(in.equalsIgnoreCase("rc")) {
                if(currentsched.get_courses().isEmpty()) autoflush.println("Error: the current schedule does not contain any courses for removal");
                else remove_course_from_schedule();
            }
            else if(in.equalsIgnoreCase("save")) {
                //todo: use saved -> if already saved, say so  |  make sure saved is kept up to date with
                //todo: the rest of in_sched.... saving should check the map to see if the should write the schedule entry ('<hashcode>:<sched_name>')
                //todo: to the Accounts.txt file ()
            }
            else if(in.equalsIgnoreCase("exit")) break;
            else autoflush.println("Error: '" + in + "' is an invalid response");
        }
    }

    public static void remove_course_from_schedule() {
        boolean first = true;
        while (true) {
            if(first) first = false;
            else if(!want_more('r')) return;
            if(currentsched.get_courses().isEmpty()) {
                System.out.println("Error: the current schedule does not contain any courses for removal");
                return;
            }
            String[] cc = get_course_code(false);
            if(!valid_course_code(cc)) continue;
            //we don't have to check the section.... we know only 1 section of a given course code can be added
            //we don't have to check the semester in this case.... we know that the user is only able to add courses
            //for the current schedule's semester
            Course rm = null;
            for(Course c : currentsched.get_courses()) {
                if(c.getMajor() == Major.valueOf(cc[0]) && c.getCourseNum() == Integer.parseInt(cc[1])) {
                    rm = c;
                    break;
                }
            }
            if(rm == null) autoflush.println("Error: " + cc[0] + " " + cc[1] + " not found in schedule");
            else {
                currentsched.remove_course(rm);
                autoflush.println(cc[0] + " " + cc[1] + " successfully removed from the schedule");
            }
        }
    }

    public static void prompt_and_search() {
        int threshold;
        boolean sorted = false;
        while(true) {
            String in = input("Enter maximum number of visible search results: ");
            try {
                threshold = Integer.parseInt(in);
                sorted = want_more('s');
                break;
            }
            catch(NumberFormatException nfe) {autoflush.println("Error: " + in + " is not a valid integer. Enter an integer value.");}
            catch(IllegalArgumentException iae) {autoflush.println(iae.getMessage());}
        }
        Main.search.search(input("Enter search string: "),sorted);
        autoflush.println(Main.search.to_str(threshold));
    }

    public static boolean is_numeric(String s) {
        try {
            Integer.parseInt(s);
            return true;
        }
        catch(NumberFormatException nfe) {return false;}
    }

    public static void modify_schedule() {
        while(true) {
            String in = input("What attribute of the current schedule would you like to modify? (name/semester/year/none): ");
            if(in.equalsIgnoreCase("name")) currentsched.set_name_with_checks();
            else if(in.equalsIgnoreCase("semester")) currentsched.set_semester_with_checks();
            else if(in.equalsIgnoreCase("year")) currentsched.set_year_with_checks();
            else if(in.equalsIgnoreCase("none")) break;
            else autoflush.println("Error: '" + in + "' is not a valid attribute to modify");
        }
    }

    public static void add_course_to_schedule() {
        boolean first = true;
        while(true) {
            if(first) first = false;
            else if(!want_more('a')) return;
            autoflush.println("Warning: you will only be able to add courses for " + currentsched.get_semester() + " " + currentsched.get_year());
            String[] cc = get_course_code(true);
            if(!valid_course_code(cc)) continue;
            String coursecode = cc[0] + " " + cc[1];
            String section = input("Enter the section of the course to add: ").toUpperCase();
            if(!is_valid_section(section)) continue;
            /*String[] sem = input("Enter semester and year of the course to add (you will only be able to add courses for " + currentsched.get_semester() + " " + currentsched.get_year() + "): ").strip().split("\\s+");
            //get semester in correct form, then check valid year value, then check valid semester value
            if(!get_semester(sem) || !valid_year(sem) || !valid_semester(sem)) continue;*/
            String toadd = coursecode + " " + section + " - " + currentsched.get_semester() + " " + currentsched.get_year();
            boolean addattempted = false;
            for(Course c : search.get_filteredresults())
                if(toadd.equals(c.short_str(true))) {
                    try {if(currentsched.add_course(c)) autoflush.println(toadd + " has been added to the current schedule");}
                    catch(IllegalArgumentException iae) {autoflush.println(iae.getMessage());}
                    addattempted = true;
                }
            if(!addattempted) autoflush.println("Error: " + toadd + " not found in search results");
        }
    }

    public static boolean valid_semester(String[] sem) {
        if(!sem[0].equals("Fall") && !sem[0].equals("Spring")) {
            autoflush.println("Error: invalid semester value (semester must be either Fall or Spring)");
            return false;
        }
        return true;
    }

    public static boolean get_semester(String[] sem) {
        if(sem.length == 2 && sem[0] != null && sem[0].length() > 1) {
            sem[0] = sem[0].substring(0,1).toUpperCase() + sem[0].substring(1).toLowerCase();
            return true;
        }
        autoflush.println("Error: invalid semester-year input");
        return false;
    }

    public static boolean valid_year(String[] sem) {
        if(!is_numeric(sem[1]) || sem[1].length() != 4 || Integer.parseInt(sem[1]) < 0 || Integer.parseInt(sem[1]) < 2020) {
            autoflush.println("Error: invalid year value");
            return false;
        }
        return true;
    }

    public static boolean valid_course_code(String[] coursecode) {
        if(coursecode.length != 2 || !Major.is_major(coursecode[0]) || !is_numeric(coursecode[1]) || coursecode[1].length() != 3) {
            autoflush.println("Error: invalid course code");
            return false;
        }
        return true;
    }

    public static boolean is_valid_section(String section) {
        if(section.length() > 1 || !Character.isAlphabetic(section.charAt(0))) {
            autoflush.println("Error: invalid section");
            return false;
        }
        return true;
    }

    public static String[] get_course_code(boolean add) {
        String[] cc;
        if(add) cc = input("Enter the course code of the course to add (major course_number): ").strip().split("\\s+");
        else cc = input("Enter the course code of the course to remove (major course_number): ").strip().split("\\s+");
        cc[0] = cc[0].toUpperCase();
        return cc;
    }

    //method name is a question
    //'a' for add, 'r' for remove, 's' for sorted search
    public static boolean want_more(char type) {
        while(true) {
            String ans;
            if(type == 'a') ans = input("Add course? (y/n) ");
            else if(type == 'r') ans = input("Remove course? (y/n) ");
            else if(type == 's') ans = input("Would you like sorted results? (y/n) ");
            else throw new IllegalArgumentException("Error: didn't correctly specify type");
            if (ans.equalsIgnoreCase("no") || ans.equalsIgnoreCase("n")) return false;
            else if(ans.equalsIgnoreCase("yes") || ans.equalsIgnoreCase("y") || ans.isEmpty()) return true;
            else autoflush.println("Invalid");
        }
    }

    public static void run() throws IOException {
        //todo:have to make days given by user to days filter uppercase
        populate_allcourses();
        in_schedule();
        input("");
        List<Account> session_accounts = new ArrayList<>();
        autoflush.println("Welcome to SchedulEase!");
        boolean run = true;
        autoflush.println("Input 's' for Sign-in | Input 'c' for Create Account | Input 'x' for Exit Application");
        Scanner scnr = new Scanner(System.in);
        String user_selection = scnr.next();
        List<String> good_inputs = Arrays.asList("s", "c", "x");
        //Runs until user gives correct input
        while (!(input_verification(good_inputs, user_selection))) {
            autoflush.println("Invalid Input - Only 's', 'c', or 'x' accepted. Please try again.");
            user_selection = scnr.next();
        }
        //Registers if user wants to exit
        if (user_selection.equalsIgnoreCase("x")) {
            run = false;
        }
        while (run) { //Runs an individual journey until the user asks to exit the program
            Account curr_account;
            String curr_username;
            String curr_password;
            //Runs if the user wants to sign-in
            if (user_selection.equalsIgnoreCase("s")) {
                if (accounts.isEmpty()) { //if there are no accounts in the accounts list, user is prompted to create an account
                    autoflush.println("No accounts exist. Please create account.");
                    String user_choice;
                    String new_username;
                    String new_password;
                    Major new_major = null;
                    autoflush.println("What would you like to set as your username?");
                    new_username = scnr.next();
                    autoflush.println("What would you like to set as your password?");
                    new_password = scnr.next();
                    autoflush.println("Would you like to add a major to your account? Enter y or n.");
                    user_choice = scnr.next();
                    good_inputs = Arrays.asList("y", "n");
                    //Runs till user enters correct input
                    while (!(input_verification(good_inputs, user_choice))) {
                        autoflush.println("Invalid entry. Please re-enter either y or n.");
                        user_choice = scnr.next();
                    }
                    //Gets user input for major choice
                    if (user_choice.equalsIgnoreCase("y")) {
                        autoflush.println("Please type your major from list.");
                        List<Major> major_list = Arrays.asList(Major.values());
                        //prints out all the major options
                        for (int i = 0; i < major_list.size(); i++) {
                            autoflush.print(major_list.get(i) + " ");
                            if (i % 9 == 0){
                                autoflush.println();
                            }
                        }
                        autoflush.println();
                        String major_choice = scnr.next().toUpperCase();
                        boolean major_found = false;
                        while (!major_found) {
                            switch (major_choice) {
                                case "ACCT":
                                    new_major = Major.ACCT;
                                    major_found = true;
                                    break;
                                case "ART":
                                    new_major = Major.ART;
                                    major_found = true;
                                    break;
                                case "ASTR":
                                    new_major = Major.ASTR;
                                    major_found = true;
                                    break;
                                case "BOIL":
                                    new_major = Major.BIOL;
                                    major_found = true;
                                    break;
                                case "CHEM":
                                    new_major = Major.CHEM;
                                    major_found = true;
                                    break;
                                case "CMIN":
                                    new_major = Major.CMIN;
                                    major_found = true;
                                    break;
                                case "COMM":
                                    new_major = Major.COMM;
                                    major_found = true;
                                    break;
                                case "COMP":
                                    new_major = Major.COMP;
                                    major_found = true;
                                    break;
                                case "DESI":
                                    new_major = Major.DESI;
                                    major_found = true;
                                    break;
                                case "ECON":
                                    new_major = Major.ECON;
                                    major_found = true;
                                    break;
                                case "EDUC":
                                    new_major = Major.EDUC;
                                    major_found = true;
                                    break;
                                case "ELEE":
                                    new_major = Major.ELEE;
                                    major_found = true;
                                    break;
                                case "ENGL":
                                    new_major = Major.ENGL;
                                    major_found = true;
                                    break;
                                case "ENGR":
                                    new_major = Major.ENGR;
                                    major_found = true;
                                    break;
                                case "ENTR":
                                    new_major = Major.ENTR;
                                    major_found = true;
                                    break;
                                case "EXER":
                                    new_major = Major.EXER;
                                    major_found = true;
                                    break;
                                case "FNCE":
                                    new_major = Major.FNCE;
                                    major_found = true;
                                    break;
                                case "FREN":
                                    new_major = Major.FREN;
                                    major_found = true;
                                    break;
                                case "GEOL":
                                    new_major = Major.GEOL;
                                    major_found = true;
                                    break;
                                case "GREK":
                                    new_major = Major.GREK;
                                    major_found = true;
                                    break;
                                case "HEBR":
                                    new_major = Major.HEBR;
                                    major_found = true;
                                    break;
                                case "HIST":
                                    new_major = Major.HIST;
                                    major_found = true;
                                    break;
                                case "HUMA":
                                    new_major = Major.HUMA;
                                    major_found = true;
                                    break;
                                case "INBS":
                                    new_major = Major.INBS;
                                    major_found = true;
                                    break;
                                case "MARK":
                                    new_major = Major.MARK;
                                    major_found = true;
                                    break;
                                case "MECE":
                                    new_major = Major.MECE;
                                    major_found = true;
                                    break;
                                case "MNGT":
                                    new_major = Major.MNGT;
                                    major_found = true;
                                    break;
                                case "MUSI":
                                    new_major = Major.MUSI;
                                    major_found = true;
                                    break;
                                case "NURS":
                                    new_major = Major.NURS;
                                    major_found = true;
                                    break;
                                case "PHIL":
                                    new_major = Major.PHIL;
                                    major_found = true;
                                    break;
                                case "PHYE":
                                    new_major = Major.PHYE;
                                    major_found = true;
                                    break;
                                case "PHYS":
                                    new_major = Major.PHYS;
                                    major_found = true;
                                    break;
                                case "POLS":
                                    new_major = Major.POLS;
                                    major_found = true;
                                    break;
                                case "PSYC":
                                    new_major = Major.PSYC;
                                    major_found = true;
                                    break;
                                case "RELI":
                                    new_major = Major.RELI;
                                    major_found = true;
                                    break;
                                case "ROBO":
                                    new_major = Major.ROBO;
                                    major_found = true;
                                    break;
                                case "SCIC":
                                    new_major = Major.SCIC;
                                    major_found = true;
                                    break;
                                case "SEDU":
                                    new_major = Major.SEDU;
                                    major_found = true;
                                    break;
                                case "SOCI":
                                    new_major = Major.SOCI;
                                    major_found = true;
                                    break;
                                case "SOCW":
                                    new_major = Major.SOCW;
                                    major_found = true;
                                    break;
                                case "SPAN":
                                    new_major = Major.SPAN;
                                    major_found = true;
                                    break;
                                case "SSFT":
                                    new_major = Major.SSFT;
                                    major_found = true;
                                    break;
                                case "THEA":
                                    new_major = Major.THEA;
                                    major_found = true;
                                    break;
                                case "WRIT":
                                    new_major = Major.WRIT;
                                    major_found = true;
                                    break;
                                case "LATN":
                                    new_major = Major.LATN;
                                    major_found = true;
                                    break;
                            }
                        }
                        //creates an account from user input
                        Account user_account = new Account(new_username, new_password, new_major);
                        session_accounts.add(user_account);
                        //accounts.add(new_username);
                        autoflush.println("Account successfully created!");
                        autoflush.println();
                        boolean in_account = true;
                        while (in_account) {
                            autoflush.println("ACCOUNT MENU");
                            autoflush.println("'o' - Open Schedule | 'm' - Modify Schedule | 'd' - Delete Schedule | 'n' - New Schedule | 'x' - Exit Account");
                            String account_act = scnr.next();
                            good_inputs = Arrays.asList("o", "m", "d", "n", "x", "s");
                            while (!(input_verification(good_inputs,account_act))) {
                                autoflush.println("Invalid Input - Only 's', 'c', or 'x' accepted. Please try again.");
                                account_act = scnr.next();
                            }
                            if (account_act.equalsIgnoreCase("o")) {
                                autoflush.println("Whats the name of the schedule you'd like to open?");
                                String open_sched = scnr.next();
                                boolean has_file = user_account.has_schedule(open_sched);
                                if (has_file) {
                                    //print out the schedule
                                } else {
                                    autoflush.println("Schedule not found.");
                                }
                            } else if (account_act.equalsIgnoreCase("m")) {
                                autoflush.println("Whats the name of the schedule you'd like to modify?");
                                String open_sched = scnr.next();
                                boolean has_file = user_account.has_schedule(open_sched);
                                if (has_file) {
                                    //modify out the schedule
                                } else {
                                    autoflush.println("Schedule not found.");
                                }
                            } else if (account_act.equalsIgnoreCase("d")) {
                                autoflush.println("Whats the name of the schedule you'd like to delete?");
                                String open_sched = scnr.next();
                                boolean has_file = user_account.has_schedule(open_sched);
                                if (has_file) {
                                    user_account.delete_schedule(open_sched);
                                } else {
                                    autoflush.println("Schedule not found.");
                                }
                            } else if (account_act.equalsIgnoreCase("n")) {
                                autoflush.println("What would you like the schedule to be called?");
                                String sched_name = scnr.next();
                                String file_name = sched_name + ".csv";
                                Schedule newSched = new Schedule(user_account.getUsername(),sched_name);
                                autoflush.println(newSched);
                            } else {
                                in_account = false;
                            }
                        }
                    } else { //creates an account from user input
                        autoflush.println("No major entered.");
                        Account user_account = new Account(new_username, new_password);
                        session_accounts.add(user_account);
                        //accounts.add(new_username);
                        autoflush.println("Account successfully created!");
                        autoflush.println();
                        boolean in_account = true;
                        while (in_account) {
                            autoflush.println("ACCOUNT MENU");
                            autoflush.println("'o' - Open Schedule | 'm' - Modify Schedule | 'd' - Delete Schedule | 'n' - New Schedule | 'x' - Exit Account");
                            String account_act = scnr.next();
                            good_inputs = Arrays.asList("o", "m", "d", "n", "x", "s");
                            while (!(input_verification(good_inputs,account_act))) {
                                autoflush.println("Invalid Input - Only 's', 'c', or 'x' accepted. Please try again.");
                                account_act = scnr.next();
                            }
                            if (account_act.equalsIgnoreCase("o")) {
                                autoflush.println("Whats the name of the schedule you'd like to open?");
                                String open_sched = scnr.next();
                                boolean has_file = user_account.has_schedule(open_sched);
                                if (has_file) {
                                    //print out the schedule
                                } else {
                                    autoflush.println("Schedule not found.");
                                }
                            } else if (account_act.equalsIgnoreCase("m")) {
                                autoflush.println("Whats the name of the schedule you'd like to modify?");
                                String open_sched = scnr.next();
                                boolean has_file = user_account.has_schedule(open_sched);
                                if (has_file) {
                                    //modify out the schedule
                                } else {
                                    autoflush.println("Schedule not found.");
                                }
                            } else if (account_act.equalsIgnoreCase("d")) {
                                autoflush.println("Whats the name of the schedule you'd like to delete?");
                                String open_sched = scnr.next();
                                boolean has_file = user_account.has_schedule(open_sched);
                                if (has_file) {
                                    user_account.delete_schedule(open_sched);
                                } else {
                                    autoflush.println("Schedule not found.");
                                }
                            } else if (account_act.equalsIgnoreCase("n")) {
                                Schedule newSched = new Schedule();
                                autoflush.println("What would you like the schedule to be called?");
                                String sched_name = scnr.next();
                                newSched.setName(sched_name);
                                autoflush.println("Is the schedule for Spring or Fall?");
                                String semester = scnr.next();
                                newSched.setSemester(semester);
                                autoflush.println("What year is this schedule for?");
                                int year = scnr.nextInt();
                                newSched.setYear(year);
                                autoflush.println("Your schedule " + sched_name + " has been created!");
                                autoflush.println(newSched);
                                //user_account.save_schedule(sched_name);
                            } else {
                                in_account = false;
                            }
                        }
                    }
                } else {
                    autoflush.println("Please enter your username.");
                    curr_username = scnr.next();
                    boolean hasAccount = false;
                    int acct_indx = 0;
                    for (int i = 0; i < accounts.size(); i++) {
                        if (accounts.get(i).equals(curr_username)) {
                            acct_indx = i;
                            hasAccount = true;
                        }
                    }
                    if (hasAccount) {
                        Account compare = new Account();
                        for (int i = 0; i < session_accounts.size(); i++) {
                            Account current_acct = session_accounts.get(i);
                            if (current_acct.getUsername().equals(curr_username)) {
                                compare = session_accounts.get(i);
                            }
                        }
                        autoflush.println("Please enter your password.");
                        curr_password = scnr.next();
                        if (compare.verify_password(curr_password)) {
                            autoflush.println("Correct password.");
                        } else {
                            while (!(compare.verify_password(curr_password)) || !(curr_password.equalsIgnoreCase("x"))) {
                                autoflush.println("Incorrect password. Please re-enter. Select x if you would like to exit.");
                                curr_password = scnr.next();
                            }
                            if (compare.verify_password(curr_password)) {
                                autoflush.println("Successfully logged in.");
                            } else {
                                autoflush.println("Goodbye!");
                            }
                        }
                    }
                }
            } else {
                String user_choice;
                String new_username;
                String new_password;
                Major new_major = null;
                autoflush.println("What would you like to set as your username?");
                new_username = scnr.next();
                autoflush.println("What would you like to set as your password?");
                new_password = scnr.next();
                autoflush.println("Would you like to add a major to your account? Enter y or n.");
                user_choice = scnr.next();
                good_inputs = Arrays.asList("y", "n");
                if (!(input_verification(good_inputs, user_choice))) {
                    autoflush.println("Invalid entry. Please re-enter either y or n.");
                    user_choice = scnr.next();
                } else {
                    if (user_choice.equalsIgnoreCase("y")) {
                        autoflush.println("Please type your major from list.");
                        List<Major> major_list = Arrays.asList(Major.values());
                        for (int i = 0; i < major_list.size(); i++) {
                            autoflush.print(major_list.get(i) + " ");
                            if (i % 9 == 0){
                                autoflush.println();
                            }
                        }
                        autoflush.println();
                        String major_choice = scnr.next().toUpperCase();
                        boolean major_found = false;
                        while (!major_found) {
                            switch (major_choice) {
                                case "ACCT":
                                    new_major = Major.ACCT;
                                    major_found = true;
                                    break;
                                case "ART":
                                    new_major = Major.ART;
                                    major_found = true;
                                    break;
                                case "ASTR":
                                    new_major = Major.ASTR;
                                    major_found = true;
                                    break;
                                case "BOIL":
                                    new_major = Major.BIOL;
                                    major_found = true;
                                    break;
                                case "CHEM":
                                    new_major = Major.CHEM;
                                    major_found = true;
                                    break;
                                case "CMIN":
                                    new_major = Major.CMIN;
                                    major_found = true;
                                    break;
                                case "COMM":
                                    new_major = Major.COMM;
                                    major_found = true;
                                    break;
                                case "COMP":
                                    new_major = Major.COMP;
                                    major_found = true;
                                    break;
                                case "DESI":
                                    new_major = Major.DESI;
                                    major_found = true;
                                    break;
                                case "ECON":
                                    new_major = Major.ECON;
                                    major_found = true;
                                    break;
                                case "EDUC":
                                    new_major = Major.EDUC;
                                    major_found = true;
                                    break;
                                case "ELEE":
                                    new_major = Major.ELEE;
                                    major_found = true;
                                    break;
                                case "ENGL":
                                    new_major = Major.ENGL;
                                    major_found = true;
                                    break;
                                case "ENGR":
                                    new_major = Major.ENGR;
                                    major_found = true;
                                    break;
                                case "ENTR":
                                    new_major = Major.ENTR;
                                    major_found = true;
                                    break;
                                case "EXER":
                                    new_major = Major.EXER;
                                    major_found = true;
                                    break;
                                case "FNCE":
                                    new_major = Major.FNCE;
                                    major_found = true;
                                    break;
                                case "FREN":
                                    new_major = Major.FREN;
                                    major_found = true;
                                    break;
                                case "GEOL":
                                    new_major = Major.GEOL;
                                    major_found = true;
                                    break;
                                case "GREK":
                                    new_major = Major.GREK;
                                    major_found = true;
                                    break;
                                case "HEBR":
                                    new_major = Major.HEBR;
                                    major_found = true;
                                    break;
                                case "HIST":
                                    new_major = Major.HIST;
                                    major_found = true;
                                    break;
                                case "HUMA":
                                    new_major = Major.HUMA;
                                    major_found = true;
                                    break;
                                case "INBS":
                                    new_major = Major.INBS;
                                    major_found = true;
                                    break;
                                case "MARK":
                                    new_major = Major.MARK;
                                    major_found = true;
                                    break;
                                case "MECE":
                                    new_major = Major.MECE;
                                    major_found = true;
                                    break;
                                case "MNGT":
                                    new_major = Major.MNGT;
                                    major_found = true;
                                    break;
                                case "MUSI":
                                    new_major = Major.MUSI;
                                    major_found = true;
                                    break;
                                case "NURS":
                                    new_major = Major.NURS;
                                    major_found = true;
                                    break;
                                case "PHIL":
                                    new_major = Major.PHIL;
                                    major_found = true;
                                    break;
                                case "PHYE":
                                    new_major = Major.PHYE;
                                    major_found = true;
                                    break;
                                case "PHYS":
                                    new_major = Major.PHYS;
                                    major_found = true;
                                    break;
                                case "POLS":
                                    new_major = Major.POLS;
                                    major_found = true;
                                    break;
                                case "PSYC":
                                    new_major = Major.PSYC;
                                    major_found = true;
                                    break;
                                case "RELI":
                                    new_major = Major.RELI;
                                    major_found = true;
                                    break;
                                case "ROBO":
                                    new_major = Major.ROBO;
                                    major_found = true;
                                    break;
                                case "SCIC":
                                    new_major = Major.SCIC;
                                    major_found = true;
                                    break;
                                case "SEDU":
                                    new_major = Major.SEDU;
                                    major_found = true;
                                    break;
                                case "SOCI":
                                    new_major = Major.SOCI;
                                    major_found = true;
                                    break;
                                case "SOCW":
                                    new_major = Major.SOCW;
                                    major_found = true;
                                    break;
                                case "SPAN":
                                    new_major = Major.SPAN;
                                    major_found = true;
                                    break;
                                case "SSFT":
                                    new_major = Major.SSFT;
                                    major_found = true;
                                    break;
                                case "THEA":
                                    new_major = Major.THEA;
                                    major_found = true;
                                    break;
                                case "WRIT":
                                    new_major = Major.WRIT;
                                    major_found = true;
                                    break;
                                case "LATN":
                                    new_major = Major.LATN;
                                    major_found = true;
                                    break;
                            }
                        }
                        Account user_account = new Account(new_username, new_password, new_major);
                        session_accounts.add(user_account);
                        //accounts.add(new_username);
                        autoflush.println("Account successfully created!");
                        autoflush.println();
                        boolean in_account = true;
                        while (in_account) {
                            autoflush.println("ACCOUNT MENU");
                            autoflush.println("'o' - Open Schedule | 'm' - Modify Schedule | 'd' - Delete Schedule | 'n' - New Schedule | 'x' - Exit Account");
                            String account_act = scnr.next();
                            good_inputs = Arrays.asList("o", "m", "d", "n", "x", "s");
                            while (!(input_verification(good_inputs,account_act))) {
                                autoflush.println("Invalid Input - Only 's', 'c', or 'x' accepted. Please try again.");
                                account_act = scnr.next();
                            }
                            if (account_act.equalsIgnoreCase("o")) {
                                autoflush.println("Whats the name of the schedule you'd like to open?");
                                String open_sched = scnr.next();
                                boolean has_file = user_account.has_schedule(open_sched);
                                if (has_file) {
                                    //print out the schedule
                                } else {
                                    autoflush.println("Schedule not found.");
                                }
                            } else if (account_act.equalsIgnoreCase("m")) {
                                autoflush.println("Whats the name of the schedule you'd like to modify?");
                                String open_sched = scnr.next();
                                boolean has_file = user_account.has_schedule(open_sched);
                                if (has_file) {
                                    //modify out the schedule
                                } else {
                                    autoflush.println("Schedule not found.");
                                }
                            } else if (account_act.equalsIgnoreCase("d")) {
                                autoflush.println("Whats the name of the schedule you'd like to delete?");
                                String open_sched = scnr.next();
                                boolean has_file = user_account.has_schedule(open_sched);
                                if (has_file) {
                                    user_account.delete_schedule(open_sched);
                                } else {
                                    autoflush.println("Schedule not found.");
                                }
                            } else if (account_act.equalsIgnoreCase("n")) {
                                autoflush.println("What would you like the schedule to be called?");
                                String sched_name = scnr.next();
                                String file_name = sched_name + ".txt";
                                Schedule newSched = new Schedule("",sched_name);
                                autoflush.println(newSched);
                            } else {
                                in_account = false;
                            }
                        }
                    } else {
                        autoflush.println("No major entered.");
                        Account user_account = new Account(new_username, new_password);
                        session_accounts.add(user_account);
                        //accounts.add(new_username);
                        autoflush.println("Account successfully created!");
                        autoflush.println();
                        boolean in_account = true;
                        while (in_account) {
                            autoflush.println("ACCOUNT MENU");
                            autoflush.println("'o' - Open Schedule | 'm' - Modify Schedule | 'd' - Delete Schedule | 'n' - New Schedule | 'x' - Exit Account");
                            String account_act = scnr.next();
                            good_inputs = Arrays.asList("o", "m", "d", "n", "x", "s");
                            while (!(input_verification(good_inputs,account_act))) {
                                autoflush.println("Invalid Input - Only 's', 'c', or 'x' accepted. Please try again.");
                                account_act = scnr.next();
                            }
                            if (account_act.equalsIgnoreCase("o")) {
                                autoflush.println("Whats the name of the schedule you'd like to open?");
                                String open_sched = scnr.next();
                                boolean has_file = user_account.has_schedule(open_sched);
                                if (has_file) {
                                    //print out the schedule
                                } else {
                                    autoflush.println("Schedule not found.");
                                }
                            } else if (account_act.equalsIgnoreCase("m")) {
                                autoflush.println("Whats the name of the schedule you'd like to modify?");
                                String open_sched = scnr.next();
                                boolean has_file = user_account.has_schedule(open_sched);
                                if (has_file) {
                                    //modify out the schedule
                                } else {
                                    autoflush.println("Schedule not found.");
                                }
                            } else if (account_act.equalsIgnoreCase("d")) {
                                autoflush.println("Whats the name of the schedule you'd like to delete?");
                                String open_sched = scnr.next();
                                boolean has_file = user_account.has_schedule(open_sched);
                                if (has_file) {
                                    user_account.delete_schedule(open_sched);
                                } else {
                                    autoflush.println("Schedule not found.");
                                }
                            } else if (account_act.equalsIgnoreCase("n")) {
                                autoflush.println("What would you like the schedule to be called?");
                                String sched_name = scnr.next();
                                String file_name = sched_name + ".csv";
                                Schedule newSched = new Schedule(user_account.getUsername(),sched_name);
                                autoflush.println(newSched);
                            } else {
                                in_account = false;
                            }
                        }
                    }
                }
            }
            autoflush.println("Input 's' for Sign-in | Input 'c' for Create Account | Input 'x' for Exit Application");
            user_selection = scnr.next();
            good_inputs = Arrays.asList("s", "c", "x");
            while (!(input_verification(good_inputs,user_selection))) {
                autoflush.println("Invalid Input - Only 's', 'c', or 'x' accepted. Please try again.");
                user_selection = scnr.next();
            }
            if (user_selection.equalsIgnoreCase("x")) {
                run = false;
            }
        }
        autoflush.println("Thank you for using SchedulEase!");


        for (int i = 0; i < 30; i++) {
            if (i == 0 || i == 29) {
                for (int j = 0; j < 60; j++) {
                    autoflush.print("_");
                }
            } else {
                for (int k = 0; k < 60; k++) {
                    if (k % 5 == 0) {
                        autoflush.print("|");
                    } else {
                        autoflush.print(" ");
                    }
                }
            }
            autoflush.println();
        }
    }

    public static void main(String[] args) {
        try {
            run();
        } catch (IOException ioe) {
            autoflush.println(ioe.getMessage() + "\n" + ioe.getCause());
        }
    }
}
