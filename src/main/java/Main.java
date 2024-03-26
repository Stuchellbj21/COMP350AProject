import java.util.*;
import java.util.HashMap;
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
    public static HashMap<Integer, String> accounts; //HashMap of all account numbers and names (which are directory names)
    // Number associated with each user at account creation
    public static int userNum = 0;

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
        for(char c : name.toCharArray())
            if(c == '<' || c == '>' || c == ':' || c == '\"' || c == '/' || c == '\\' || c == '|' || c == '?' || c == '*') return false;
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
        if(!allcourses.isEmpty()) {
            Course last = allcourses.getLast();
            //if the course is the same as the one before, just merge the daytimes into the last course
            if (add.getCourseNum() == last.getCourseNum() && add.getMajor() == last.getMajor()
                    && add.getSection() == last.getSection() && add.getYear() == last.getYear()
                    && add.getSemester().equalsIgnoreCase(last.getSemester())) last.getTimes().addAll(add.getTimes());
                //otherwise, add the full course
            else allcourses.add(add);
        }
        else allcourses.add(add);
    }

    public static Major major_set(String major_name){
        Scanner maj = new Scanner(System.in);
        while (!Major.is_major(major_name)){
            System.out.println("Invalid major, please try again.");
            major_name = maj.next();
        }
        Major corr = Major.valueOf(major_name);
        return corr;
    }

    public static void print_majs(){
        List<Major> major_list = Arrays.asList(Major.values());
        for (int i = 0; i < major_list.size(); i++) {
            System.out.print(major_list.get(i) + " ");
            if (i % 9 == 0){
                System.out.println();
            }
        }
    }

    public static void run() throws IOException {
        //todo:have to make days given by user to days filter uppercase
        populate_allcourses();
        List<Account> session_accounts = new ArrayList<>();
        System.out.println("Welcome to SchedulEase!");
        boolean run = true;
        System.out.println("Input 's' for Sign-in | Input 'c' for Create Account | Input 'x' for Exit Application");
        Scanner scnr = new Scanner(System.in);
        String user_selection = scnr.next();
        List<String> good_inputs = Arrays.asList("s", "c", "x");
        //Runs until user gives correct input
        while (!(input_verification(good_inputs, user_selection))) {
            System.out.println("Invalid Input - Only 's', 'c', or 'x' accepted. Please try again.");
            user_selection = scnr.next();
        }
        //Registers if user wants to exit
        if (user_selection.equalsIgnoreCase("x")) {
            run = false;
        }
        while (run) { //Runs an individual journey until the user asks to exit the program
            boolean enter_acct = false;
            Account curr_account = null;
            String curr_username;
            String curr_password;
            //Runs if the user wants to sign-in
            if (user_selection.equalsIgnoreCase("s") && !accounts.isEmpty()) {
                System.out.println("Please enter your username.");
                curr_username = scnr.next();
                Account compare = new Account();
                Boolean exists = false;
                for (Account current_acct : session_accounts) {
                    if (current_acct.getUsername().equals(curr_username)) {
                        compare = current_acct;
                        exists = true;
                    }
                }
                if (exists) {
                    System.out.println("Please enter your password.");
                    curr_password = scnr.next();
                    if (compare.verify_password(curr_password)) {
                        System.out.println("Correct password.");
                        curr_account = compare;
                        enter_acct = true;
                    } else {
                        while (!(compare.verify_password(curr_password))) {
                            System.out.println("Incorrect password. Please re-enter.");
                            curr_password = scnr.next();
                        }
                        System.out.println("Successfully logged in.");
                        curr_account = compare;
                        enter_acct = true;
                    }
                } else {
                    System.out.println("Account does not exist. Please create account.");
                }
            }
            else {
                if (user_selection.equalsIgnoreCase("s")){
                    System.out.println("No accounts exist. Please create account");
                }
                String user_choice;
                String new_username;
                String new_password;
                Major new_major = null;
                System.out.println("What would you like to set as your username?");
                new_username = scnr.next();
                System.out.println("What would you like to set as your password?");
                new_password = scnr.next();
                System.out.println("Would you like to add a major to your account? Enter y or n.");
                user_choice = scnr.next();
                good_inputs = Arrays.asList("y", "n");
                //Runs till user enters correct input
                while (!(input_verification(good_inputs, user_choice))) {
                    System.out.println("Invalid entry. Please re-enter either y or n.");
                    user_choice = scnr.next();
                }
                //Gets user input for major choice
                if (user_choice.equalsIgnoreCase("y")) {
                    System.out.println("Please type your major from list.");
                    //SELECTION OF MAJOR
                    print_majs();
                    String major_choice = scnr.next().toUpperCase();
                    Major off_major = major_set(major_choice);
                    //creates an account from user input
                    curr_account = new Account(new_username, new_password, off_major);
                    userNum = 1+userNum;
                    session_accounts.add(curr_account);
                    accounts.put(userNum, new_username);
                    System.out.println("Account successfully created!");
                    System.out.println();
                    enter_acct = true;
                }
                else {
                    curr_account = new Account(new_username, new_password);
                    userNum = 1+userNum;
                    session_accounts.add(curr_account);
                    accounts.put(userNum, new_username);
                    System.out.println("Account successfully created!");
                    System.out.println();
                    enter_acct = true;
                }
            }
            boolean in_account = true;
            System.out.println("ACCOUNT MENU");
            while (in_account && enter_acct) {
                System.out.println("'o' - Open Schedule | 'm' - Modify Schedule | 'd' - Delete Schedule | 'n' - New Schedule | 'x' - Exit Account");
                String account_act = scnr.next();
                good_inputs = Arrays.asList("o", "m", "d", "n", "x", "s");
                while (!(input_verification(good_inputs,account_act))) {
                    System.out.println("Invalid Input - Only 's', 'c', or 'x' accepted. Please try again.");
                    account_act = scnr.next();
                }
                if (account_act.equalsIgnoreCase("o")) {
                    if (curr_account.num_scheds() == 0){
                        System.out.println("No schedules saved in account.");
                        System.out.println();
                    }
                    else {
                        System.out.println("Whats the name of the schedule you'd like to open?");
                        String open_sched = scnr.next();
                        boolean has_file = curr_account.has_schedule(open_sched);
                        if (has_file) {
                            Schedule opened_sched = curr_account.load_schedule(open_sched);
                            opened_sched.printSchedule();
                        } else {
                            System.out.println("Schedule not found.");
                            System.out.println();
                        }
                    }
                } else if (account_act.equalsIgnoreCase("m")) {
                    if (curr_account.num_scheds() == 0){
                        System.out.println("No schedules saved in account.");
                        System.out.println();
                    }
                    else {
                        System.out.println("Whats the name of the schedule you'd like to modify?");
                        String open_sched = scnr.next();
                        boolean has_file = curr_account.has_schedule(open_sched);
                        if (has_file) {
                            //modify out the schedule
                        } else {
                            System.out.println("Schedule not found.");
                            System.out.println();
                        }
                    }
                } else if (account_act.equalsIgnoreCase("d")) {
                    if (curr_account.num_scheds() == 0){
                        System.out.println("No schedules saved in account.");
                        System.out.println();
                    }
                    else {
                        System.out.println("Whats the name of the schedule you'd like to delete?");
                        String open_sched = scnr.next();
                        boolean has_file = curr_account.has_schedule(open_sched);
                        if (has_file) {
                            curr_account.delete_schedule(open_sched);
                            System.out.println("Schedule successfully deleted.");
                            System.out.println();
                        } else {
                            System.out.println("Schedule not found.");
                            System.out.println();
                        }
                    }
                } else if (account_act.equalsIgnoreCase("n")) {
                    System.out.println("What would you like the schedule to be called?");
                    String sched_name = scnr.next();
                    curr_account.save_schedule(sched_name);
                    String file_name = sched_name + ".txt";
                    Schedule newSched = new Schedule("",sched_name);
                    newSched.printSchedule();
                } else {
                    in_account = false;
                }
            }
            System.out.println("Input 's' for Sign-in | Input 'c' for Create Account | Input 'x' for Exit Application");
            user_selection = scnr.next();
            good_inputs = Arrays.asList("s", "c", "x");
            while (!(input_verification(good_inputs,user_selection))) {
                System.out.println("Invalid Input - Only 's', 'c', or 'x' accepted. Please try again.");
                user_selection = scnr.next();
            }
            if (user_selection.equalsIgnoreCase("x")) {
                run = false;
            }
        }
        System.out.println("Thank you for using SchedulEase!");
    }

    public static void printScheduleTest(){
        ArrayList<DayTime> dts1 = new ArrayList<>();
        dts1.add(new DayTime("8:00 AM","8:50 AM",'M'));
        dts1.add(new DayTime("8:00 AM","8:50 AM",'W'));
        dts1.add(new DayTime("8:00 AM","8:50 AM",'F'));
        ArrayList<DayTime> dts2 = new ArrayList<>();
        dts2.add(new DayTime("8:00 AM","9:15 AM",'T'));
        dts2.add(new DayTime("8:00 AM","9:15 AM",'R'));
        ArrayList<DayTime> dts3 = new ArrayList<>();
        dts3.add(new DayTime("3:00 PM","3:50 PM",'M'));
        dts3.add(new DayTime("3:00 PM","3:50 PM",'W'));
        dts3.add(new DayTime("3:00 PM","3:50 PM",'F'));
        ArrayList<DayTime> dts4 = new ArrayList<>();
        dts4.add(new DayTime("12:30 PM","1:45 PM",'T'));
        dts4.add(new DayTime("12:30 PM","1:45 PM",'R'));
        Course c1 = new Course("Enrichment of the Mentality Complex1",'A',Major.ACCT,820,3,9,10,"Greg Bilbod",2050,"Fall",null,dts1);
        Course c2 = new Course("Enrichment of the Mentality Complex2",'B',Major.ACCT,830,3,9,10,"Greg Bilbod",2050,"Fall",null,dts2);
        Course c3 = new Course("Enrichment of the Mentality Complex3",'C',Major.ACCT,850,3,9,10,"Greg Bilbod",2050,"Fall",null,dts3);
        Course c4 = new Course("Enrichment of the Mentality Complex4",'D',Major.ACCT,860,3,9,10,"Greg Bilbod",2050,"Fall",null,dts4);
        Schedule test_Sched = new Schedule();
        test_Sched.add_course(c1);
        test_Sched.add_course(c2);
        test_Sched.add_course(c3);
        test_Sched.add_course(c4);
        test_Sched.printSchedule();
    }

    public static void main(String[] args) {
        try {
            printScheduleTest();
            run();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage() + "\n" + ioe.getCause());
        }
    }
}
