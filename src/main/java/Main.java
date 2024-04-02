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
    public static PrintStream autoflush = new PrintStream(System.out, true);
    public static Search search = new Search();

    //associates password hash with String account name
    public static Map<Integer, String> accounts = new HashMap<>();
    public static Account currentaccnt = null;

    //from the user's perspective it will simply appear that the user has made a new blank Schedule
    //or a new custom schedule, but from our perspective we know that the schedule starts as a blank schedule
    //and a custom schedule is simply a matter of changing the attributes of the current schedule
    //a new blank schedule is simply leaving the attributes of the current schedule as they are
    public static Schedule currentsched = new Schedule();

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
        if (name.equalsIgnoreCase("major") || name.equalsIgnoreCase("account") || name.equalsIgnoreCase("accounts"))
            throw new IllegalArgumentException("Error: you cannot name an account or schedule 'accounts', or 'account', or 'major'");
        if (name.length() > 20)
            throw new IllegalArgumentException("Error: account and schedule names cannot be longer than 20 characters");
        for (char c : name.toCharArray())
            if (c == '<' || c == '>' || c == ':' || c == '\"' || c == '/' || c == '\\' || c == '|' || c == '?' || c == '*')
                throw new IllegalArgumentException("Error: account and schedule names cannot contain any of the following characters: '*','?','|','/','\\','>','<',':','\"'");
        return true;
    }

    public static boolean is_valid_password(String password) {
        if (password.length() < 7 || password.length() > 20)
            throw new IllegalArgumentException("Error: password must be between 7 and 20 (inclusive) characters long");
        return true;
    }

    //account has a schedule instance that is worked on
    public static void populate_allcourses() throws IOException {
        FileInputStream fis = new FileInputStream("2020-2021.csv");
        Scanner csvscn = new Scanner(fis);
        allcourses = new ArrayList<>();
        accounts = new HashMap<Integer, String>();
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
        boolean saved = false;
        while (true) {
            autoflush.println(currentsched.to_str());
            //modify will allow editing of the schedule
            String in = input("Enter (modify) -> modify schedule/(search) -> search for courses/(ac) -> add_course/\n(rc) -> remove course/(save) -> save schedule/(exit) -> exit to schedule selection: ");
            if (in.equalsIgnoreCase("modify")) {
                modify_schedule();
            } else if (in.equalsIgnoreCase("search")) {
                prompt_and_search();
            } else if (in.equalsIgnoreCase("ac")) {
                if (search.get_filteredresults() == null || search.get_filteredresults().isEmpty())
                    autoflush.println("Error: if you wish to add a course, you must add it from search results and you currently have no search results");
                else add_course_to_schedule();
            } else if (in.equalsIgnoreCase("rc")) {
                if (currentsched.get_courses().isEmpty())
                    autoflush.println("Error: the current schedule does not contain any courses for removal");
                else remove_course_from_schedule();
            } else if (in.equalsIgnoreCase("save")) {
                //todo: use saved -> if already saved, say so  |  make sure saved is kept up to date with
                //todo: the rest of in_sched.... saving should check the map to see if the should write the schedule entry ('<hashcode>:<sched_name>')
                //todo: to the Accounts.txt file ()
            } else if (in.equalsIgnoreCase("exit")) break;
            else autoflush.println("Error: '" + in + "' is an invalid response");
        }
    }

    public static void remove_course_from_schedule() {
        boolean first = true;
        while (true) {
            if (first) first = false;
            else if (!want_more('r')) return;
            if (currentsched.get_courses().isEmpty()) {
                System.out.println("Error: the current schedule does not contain any courses for removal");
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
            if (in.equalsIgnoreCase("name")) currentsched.set_name_with_checks();
            else if (in.equalsIgnoreCase("semester")) currentsched.set_semester_with_checks();
            else if (in.equalsIgnoreCase("year")) currentsched.set_year_with_checks();
            else if (in.equalsIgnoreCase("none")) break;
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
            if(!get_semester(sem) || !valid_year(sem) || !valid_semester(sem)) continue;*/
            String toadd = coursecode + " " + section + " - " + currentsched.get_semester() + " " + currentsched.get_year();
            boolean addattempted = false;
            for (Course c : search.get_filteredresults())
                if (toadd.equals(c.short_str(true))) {
                    try {
                        if (currentsched.add_course(c))
                            autoflush.println(toadd + " has been added to the current schedule");
                    } catch (IllegalArgumentException iae) {
                        autoflush.println(iae.getMessage());
                    }
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

    public static boolean get_semester(String[] sem) {
        if (sem.length == 2 && sem[0] != null && sem[0].length() > 1) {
            sem[0] = sem[0].substring(0, 1).toUpperCase() + sem[0].substring(1).toLowerCase();
            return true;
        }
        autoflush.println("Error: invalid semester-year input");
        return false;
    }

    public static boolean valid_year(String[] sem) {
        if (!is_numeric(sem[1]) || sem[1].length() != 4 || Integer.parseInt(sem[1]) < 0 || Integer.parseInt(sem[1]) < 2020) {
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
            if (type == 'a') ans = input("Add course? (y/n) ");
            else if (type == 'r') ans = input("Remove course? (y/n) ");
            else if (type == 's') ans = input("Would you like sorted results? (y/n) ");
            else throw new IllegalArgumentException("Error: didn't correctly specify type");
            if (ans.equalsIgnoreCase("no") || ans.equalsIgnoreCase("n")) return false;
            else if (ans.equalsIgnoreCase("yes") || ans.equalsIgnoreCase("y") || ans.isEmpty()) return true;
            else autoflush.println("Invalid");
        }
    }

    public static void accountMenu() throws IOException {
        while (true) {
            String in = input("Enter (create) -> create new Account/ (login) -> login to existing account\n" +
                    "(close) --> exit program\n");
            if (in.equalsIgnoreCase("create")) {
                if (createAccount()) {
                    break; // account created successfully
                }
            } else if (in.equalsIgnoreCase("login")) {
                if (login()) {
                    break;
                }
            } else if (in.equalsIgnoreCase("close")){
                System.exit(0); // kill the program with no errors
            }
            else {autoflush.println("Error: '" + in + "' is an invalid response");
            }
        }
    }

    public static boolean check_username(String userName) {
        for (int i = 0; i < userName.length(); i++) {
            if (!Character.isLetter(userName.charAt(i)) && !Character.isDigit(userName.charAt(i))) {
                autoflush.println("Error: username should only contains letters and digits");
                return false;
            } else if (userName.length() > 20) {
                autoflush.println("Error: username length cannot exceed 20 characters");
                return false;
            }
        }
        autoflush.println("username set successfully");
        return true;
    }

    public static boolean check_password(String password) {
        for (int i = 0; i < password.length(); i++) {
            if (!Character.isLetter(password.charAt(i)) && !Character.isDigit(password.charAt(i))) {
                autoflush.println("Error: password should only contains letters and digits");
                return false;
            } else if (password.length() > 20) {
                autoflush.println("Error: password length cannot exceed 20 characters");
                return false;
            }
        }
        autoflush.println("password set successfully");
        return true;
    }

    public static String create_username() {
        while (true) { // todo: do I need this 'while'
            String un = input("Enter (<YourUserName>) --> set new username / (back) --> return to Account screen\n");
            if (un.equalsIgnoreCase("back")) {
                return "back";
            } else if (check_username(un)) {
                return un;
            }
        }
    }

    public static String create_password() {
        while (true) { // todo: do I need this 'while'
            String pw = input("Enter (<YourPassword>) --> set new password / (back) --> return to Account screen\n");
            if (pw.equalsIgnoreCase("back")) {
                return "back";
            } else if (check_password(pw)) {
                return pw;
            }
        }
    }

    public static String enter_major() {
        while (true) {
            String in = input("Enter (major) --> set major, e.g. 'COMP' / (back) --> Account menu\n");
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
        String un = input("Enter (username) --> / (back) -> return to Account menu\n");
        if (un.equalsIgnoreCase("back")) {
            return false;
        }
        String pw = input("Enter (password) --> / (back) -> return to Account menu\n");
        if (pw.equalsIgnoreCase("back")) {
            return false;
        }
        if (accounts.containsKey(pw.hashCode()) && accounts.containsValue(un)) {
            currentaccnt = new Account(un, pw, Major.COMP); //  todo: for now, this just makes a new schedule with default major;
            scheduleMenu();
        }
        return true;
    }

    public static boolean createAccount() {

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

        autoflush.println("Account successfully created\n");
        return true;
    }

    public static boolean create_new_Schedule() throws IOException {

        String in = input("Enter <YourScheduleName> --> name schedule / (back) --> return to Schedule Menu\n");
        if (in.equalsIgnoreCase("back")) {
            scheduleMenu();
            return false;
        }
        if (is_valid_name(in)) {
            currentsched = new Schedule();
            currentsched.set_name(in);
        }
        // todo: allow user to set more attributes of new schedule than just the name
        // e.g., allow them to set semester and year
        return true;
    }

    public static void scheduleMenu() throws IOException {
        while (true) {
            String in = input("Enter (newS) --> new blank schedule / (load) --> load schedule / (back) --> return to Account Menu\n");
            if (in.equalsIgnoreCase("back")) {
                accountMenu();
                break;
            } else if (in.equalsIgnoreCase("newS")) {
                create_new_Schedule();
            } else if (in.equalsIgnoreCase("load")) {
                String current = input("Enter (<YourScheduleName>) --> load schedule\n");
                //if (currentaccnt.get_schednames().contains(current)) {
                currentsched = currentaccnt.load_schedule(current);
                //}
            }
            in_schedule();
        }
    }

    public static void run() throws IOException {

        //todo:have to make days given by user to days filter uppercase
        populate_allcourses();
        List<Account> session_accounts = new ArrayList<>();
        autoflush.println("Welcome to SchedulEase!");
        accountMenu();
        scheduleMenu();
        in_schedule();
        input("");
    }

    public static void main(String[] args) {
        try {
            run();
        } catch (IOException ioe) {
            autoflush.println(ioe.getMessage() + "\n" + ioe.getCause());
        }
    }
}