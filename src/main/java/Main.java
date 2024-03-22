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
    public static List<String> accounts; //list of all account names (which are directory names)

    public static boolean input_verification(List<String> good_inputs, String user_input) {
        boolean is_good = false;
        for (int i = 0; i < good_inputs.size(); i++) {
            if (user_input.equalsIgnoreCase(good_inputs.get(i))) {
                is_good = true;
            }
        }
        return is_good;
    }

    //account has a schedule instance that is worked on
    public static void populate_allcourses() throws IOException {
        FileInputStream fis = new FileInputStream("2020-2021.csv");
        Scanner csvscn = new Scanner(fis);
        allcourses = new ArrayList<>();
        accounts = new ArrayList<>();
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
            allcourses.add(new Course(name, section, major, coursenum, credits, numstudents, capacity, prof, year, sem, requiredby, daytimes));
            inline.close();
        }
        csvscn.close();
    }

    public static void run() throws IOException {
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
            Account curr_account;
            String curr_username;
            String curr_password;
            //Runs if the user wants to sign-in
            if (user_selection.equalsIgnoreCase("s")) {
                if (accounts.isEmpty()) { //if there are no accounts in the accounts list, user is prompted to create an account
                    System.out.println("No accounts exist. Please create account.");
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
                        List<Major> major_list = Arrays.asList(Major.values());
                        //prints out all the major options
                        for (int i = 0; i < major_list.size(); i++) {
                            System.out.print(major_list.get(i) + " ");
                            if (i % 9 == 0){
                                System.out.println();
                            }
                        }
                        System.out.println();
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
                        accounts.add(new_username);
                        System.out.println("Account successfully created!");
                        System.out.println();
                        boolean in_account = true;
                        while (in_account) {
                            System.out.println("ACCOUNT MENU");
                            System.out.println("'o' - Open Schedule | 'm' - Modify Schedule | 'd' - Delete Schedule | 'n' - New Schedule | 'x' - Exit Account");
                            String account_act = scnr.next();
                            good_inputs = Arrays.asList("o", "m", "d", "n", "x", "s");
                            while (!(input_verification(good_inputs,account_act))) {
                                System.out.println("Invalid Input - Only 's', 'c', or 'x' accepted. Please try again.");
                                account_act = scnr.next();
                            }
                            if (account_act.equalsIgnoreCase("o")) {
                                System.out.println("Whats the name of the schedule you'd like to open?");
                                String open_sched = scnr.next();
                                boolean has_file = user_account.has_schedule(open_sched);
                                if (has_file) {
                                    //print out the schedule
                                } else {
                                    System.out.println("Schedule not found.");
                                }
                            } else if (account_act.equalsIgnoreCase("m")) {
                                System.out.println("Whats the name of the schedule you'd like to modify?");
                                String open_sched = scnr.next();
                                boolean has_file = user_account.has_schedule(open_sched);
                                if (has_file) {
                                    //modify out the schedule
                                } else {
                                    System.out.println("Schedule not found.");
                                }
                            } else if (account_act.equalsIgnoreCase("d")) {
                                System.out.println("Whats the name of the schedule you'd like to delete?");
                                String open_sched = scnr.next();
                                boolean has_file = user_account.has_schedule(open_sched);
                                if (has_file) {
                                    user_account.delete_schedule(open_sched);
                                } else {
                                    System.out.println("Schedule not found.");
                                }
                            } else if (account_act.equalsIgnoreCase("n")) {
                                System.out.println("What would you like the schedule to be called?");
                                String sched_name = scnr.next();
                                String file_name = sched_name + ".txt";
                                Schedule newSched = new Schedule(sched_name);
                                System.out.println(newSched);
                            } else {
                                in_account = false;
                            }
                        }
                    } else { //creates an account from user input
                        System.out.println("No major entered.");
                        Account user_account = new Account(new_username, new_password);
                        session_accounts.add(user_account);
                        accounts.add(new_username);
                        System.out.println("Account successfully created!");
                        System.out.println();
                        boolean in_account = true;
                        while (in_account) {
                            System.out.println("ACCOUNT MENU");
                            System.out.println("'o' - Open Schedule | 'm' - Modify Schedule | 'd' - Delete Schedule | 'n' - New Schedule | 'x' - Exit Account");
                            String account_act = scnr.next();
                            good_inputs = Arrays.asList("o", "m", "d", "n", "x", "s");
                            while (!(input_verification(good_inputs,account_act))) {
                                System.out.println("Invalid Input - Only 's', 'c', or 'x' accepted. Please try again.");
                                account_act = scnr.next();
                            }
                            if (account_act.equalsIgnoreCase("o")) {
                                System.out.println("Whats the name of the schedule you'd like to open?");
                                String open_sched = scnr.next();
                                boolean has_file = user_account.has_schedule(open_sched);
                                if (has_file) {
                                    //print out the schedule
                                } else {
                                    System.out.println("Schedule not found.");
                                }
                            } else if (account_act.equalsIgnoreCase("m")) {
                                System.out.println("Whats the name of the schedule you'd like to modify?");
                                String open_sched = scnr.next();
                                boolean has_file = user_account.has_schedule(open_sched);
                                if (has_file) {
                                    //modify out the schedule
                                } else {
                                    System.out.println("Schedule not found.");
                                }
                            } else if (account_act.equalsIgnoreCase("d")) {
                                System.out.println("Whats the name of the schedule you'd like to delete?");
                                String open_sched = scnr.next();
                                boolean has_file = user_account.has_schedule(open_sched);
                                if (has_file) {
                                    user_account.delete_schedule(open_sched);
                                } else {
                                    System.out.println("Schedule not found.");
                                }
                            } else if (account_act.equalsIgnoreCase("n")) {
                                Schedule newSched = new Schedule();
                                System.out.println("What would you like the schedule to be called?");
                                String sched_name = scnr.next();
                                newSched.setName(sched_name);
                                System.out.println("Is the schedule for Spring or Fall?");
                                String semester = scnr.next();
                                newSched.setSemester(semester);
                                System.out.println("What year is this schedule for?");
                                int year = scnr.nextInt();
                                newSched.setYear(year);
                                System.out.println("Your schedule " + sched_name + " has been created!");
                                System.out.println(newSched);
                                //user_account.save_schedule(sched_name);
                            } else {
                                in_account = false;
                            }
                        }
                    }
                } else {
                    System.out.println("Please enter your username.");
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
                        System.out.println("Please enter your password.");
                        curr_password = scnr.next();
                        if (compare.verify_password(curr_password)) {
                            System.out.println("Correct password.");
                        } else {
                            while (!(compare.verify_password(curr_password)) || !(curr_password.equalsIgnoreCase("x"))) {
                                System.out.println("Incorrect password. Please re-enter. Select x if you would like to exit.");
                                curr_password = scnr.next();
                            }
                            if (compare.verify_password(curr_password)) {
                                System.out.println("Successfully logged in.");
                            } else {
                                System.out.println("Goodbye!");
                            }
                        }
                    }
                }
            } else {
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
                if (!(input_verification(good_inputs, user_choice))) {
                    System.out.println("Invalid entry. Please re-enter either y or n.");
                    user_choice = scnr.next();
                } else {
                    if (user_choice.equalsIgnoreCase("y")) {
                        System.out.println("Please type your major from list.");
                        List<Major> major_list = Arrays.asList(Major.values());
                        for (int i = 0; i < major_list.size(); i++) {
                            System.out.print(major_list.get(i) + " ");
                            if (i % 9 == 0){
                                System.out.println();
                            }
                        }
                        System.out.println();
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
                        accounts.add(new_username);
                        System.out.println("Account successfully created!");
                        System.out.println();
                        boolean in_account = true;
                        while (in_account) {
                            System.out.println("ACCOUNT MENU");
                            System.out.println("'o' - Open Schedule | 'm' - Modify Schedule | 'd' - Delete Schedule | 'n' - New Schedule | 'x' - Exit Account");
                            String account_act = scnr.next();
                            good_inputs = Arrays.asList("o", "m", "d", "n", "x", "s");
                            while (!(input_verification(good_inputs,account_act))) {
                                System.out.println("Invalid Input - Only 's', 'c', or 'x' accepted. Please try again.");
                                account_act = scnr.next();
                            }
                            if (account_act.equalsIgnoreCase("o")) {
                                System.out.println("Whats the name of the schedule you'd like to open?");
                                String open_sched = scnr.next();
                                boolean has_file = user_account.has_schedule(open_sched);
                                if (has_file) {
                                    //print out the schedule
                                } else {
                                    System.out.println("Schedule not found.");
                                }
                            } else if (account_act.equalsIgnoreCase("m")) {
                                System.out.println("Whats the name of the schedule you'd like to modify?");
                                String open_sched = scnr.next();
                                boolean has_file = user_account.has_schedule(open_sched);
                                if (has_file) {
                                    //modify out the schedule
                                } else {
                                    System.out.println("Schedule not found.");
                                }
                            } else if (account_act.equalsIgnoreCase("d")) {
                                System.out.println("Whats the name of the schedule you'd like to delete?");
                                String open_sched = scnr.next();
                                boolean has_file = user_account.has_schedule(open_sched);
                                if (has_file) {
                                    user_account.delete_schedule(open_sched);
                                } else {
                                    System.out.println("Schedule not found.");
                                }
                            } else if (account_act.equalsIgnoreCase("n")) {
                                System.out.println("What would you like the schedule to be called?");
                                String sched_name = scnr.next();
                                String file_name = sched_name + ".txt";
                                Schedule newSched = new Schedule(sched_name);
                                System.out.println(newSched);
                            } else {
                                in_account = false;
                            }
                        }
                    } else {
                        System.out.println("No major entered.");
                        Account user_account = new Account(new_username, new_password);
                        session_accounts.add(user_account);
                        accounts.add(new_username);
                        System.out.println("Account successfully created!");
                        System.out.println();
                        boolean in_account = true;
                        while (in_account) {
                            System.out.println("ACCOUNT MENU");
                            System.out.println("'o' - Open Schedule | 'm' - Modify Schedule | 'd' - Delete Schedule | 'n' - New Schedule | 'x' - Exit Account");
                            String account_act = scnr.next();
                            good_inputs = Arrays.asList("o", "m", "d", "n", "x", "s");
                            while (!(input_verification(good_inputs,account_act))) {
                                System.out.println("Invalid Input - Only 's', 'c', or 'x' accepted. Please try again.");
                                account_act = scnr.next();
                            }
                            if (account_act.equalsIgnoreCase("o")) {
                                System.out.println("Whats the name of the schedule you'd like to open?");
                                String open_sched = scnr.next();
                                boolean has_file = user_account.has_schedule(open_sched);
                                if (has_file) {
                                    //print out the schedule
                                } else {
                                    System.out.println("Schedule not found.");
                                }
                            } else if (account_act.equalsIgnoreCase("m")) {
                                System.out.println("Whats the name of the schedule you'd like to modify?");
                                String open_sched = scnr.next();
                                boolean has_file = user_account.has_schedule(open_sched);
                                if (has_file) {
                                    //modify out the schedule
                                } else {
                                    System.out.println("Schedule not found.");
                                }
                            } else if (account_act.equalsIgnoreCase("d")) {
                                System.out.println("Whats the name of the schedule you'd like to delete?");
                                String open_sched = scnr.next();
                                boolean has_file = user_account.has_schedule(open_sched);
                                if (has_file) {
                                    user_account.delete_schedule(open_sched);
                                } else {
                                    System.out.println("Schedule not found.");
                                }
                            } else if (account_act.equalsIgnoreCase("n")) {
                                System.out.println("What would you like the schedule to be called?");
                                String sched_name = scnr.next();
                                String file_name = sched_name + ".txt";
                                Schedule newSched = new Schedule(sched_name);
                                System.out.println(newSched);
                            } else {
                                in_account = false;
                            }
                        }
                    }
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


        for (int i = 0; i < 30; i++) {
            if (i == 0 || i == 29) {
                for (int j = 0; j < 60; j++) {
                    System.out.print("_");
                }
            } else {
                for (int k = 0; k < 60; k++) {
                    if (k % 5 == 0) {
                        System.out.print("|");
                    } else {
                        System.out.print(" ");
                    }
                }
            }
            System.out.println();
        }
    }

    public static void benTest(){
        ArrayList<DayTime> dts1 = new ArrayList<>();
        dts1.add(new DayTime("8:00 AM","8:50 AM",'M'));
        dts1.add(new DayTime("8:00 AM","8:50 AM",'W'));
        dts1.add(new DayTime("8:00 AM","8:50 AM",'F'));
        ArrayList<DayTime> dts2 = new ArrayList<>();
        dts2.add(new DayTime("8:00 AM","9:15 AM",'T'));
        dts2.add(new DayTime("8:00 AM","9:15 AM",'R'));
        ArrayList<DayTime> dts3 = new ArrayList<>();
        dts3.add(new DayTime("9:00 AM","9:50 AM",'M'));
        dts3.add(new DayTime("9:00 AM","9:50 AM",'W'));
        dts3.add(new DayTime("9:00 AM","9:50 AM",'F'));
        ArrayList<DayTime> dts4 = new ArrayList<>();
        dts4.add(new DayTime("9:30 AM","10:45 AM",'T'));
        dts4.add(new DayTime("9:30 AM","10:45 AM",'R'));
        Course c1 = new Course("Enrichment of the Mentality Complex1",'A',Major.ACCT,820,3,10,10,"Greg Bilbod",2050,"Fall",null,dts1);
        Course c2 = new Course("Enrichment of the Mentality Complex2",'B',Major.ACCT,820,3,10,10,"Greg Bilbod",2050,"Fall",null,dts2);
        Course c3 = new Course("Enrichment of the Mentality Complex3",'C',Major.ACCT,820,3,10,10,"Greg Bilbod",2050,"Fall",null,dts3);
        Course c4 = new Course("Enrichment of the Mentality Complex4",'D',Major.ACCT,820,3,10,10,"Greg Bilbod",2050,"Fall",null,dts4);
        Schedule test_Sched = new Schedule();
        test_Sched.add_course(c1);
        test_Sched.add_course(c2);
        test_Sched.add_course(c3);
        test_Sched.add_course(c4);
        test_Sched.printSchedule();
    }

    public static void main(String[] args) {
        benTest();
        try {
            run();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage() + "\n" + ioe.getCause());
        }
    }
}
