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

    //account has a schedule instance that is worked on
    public static void run() throws IOException {
        FileInputStream fis = new FileInputStream("2020-2021.csv");
        Scanner csvscn = new Scanner(fis);
        allcourses = new ArrayList<>();
        accounts = new ArrayList<>();
        List<Account> session_accounts = new ArrayList<>();
        //skip the descriptors with nextLine()
        csvscn.nextLine();
        while(csvscn.hasNextLine()) {
            Scanner inline = new Scanner(csvscn.nextLine());
            inline.useDelimiter(",");
            String name = "", prof = "", sem = "";

            //define all variables for Course
            char section = '_';
            Major major = Major.COMP;
            int coursenum = -1, credits = 0,numstudents = 0,capacity = 0,year = -1;
            Set<Major> requiredby = new HashSet<>();
            List<Character> days = new ArrayList<>();
            //List<String> times = new ArrayList<>(); it seems that there are only ever classes with a single start and end time
            String[] times = new String[2];
            List<DayTime> daytimes = new ArrayList<>();
            for(int i = 0; inline.hasNext(); i++) {
                String n = inline.next();
                //make cases for i which enumerates the Course items
                //cases for days,times,name,etc.
                switch(i) {
                    case 0:
                        year = Integer.parseInt(n);
                        break;
                    case 1:
                        int s = Integer.parseInt(n);
                        if(s == 10) sem = "Fall"; //assuming 10 -> Fall, 30 -> Spring
                        else sem = "Spring";
                        break;
                    case 2:
                        major = Major.valueOf(n);
                        break;
                    case 3:
                        coursenum = Integer.parseInt(n);
                        break;
                    case 4:
                        if(!n.isBlank()) section = n.charAt(0);
                        break;
                    case 5:
                        name = n;
                        break;
                    case 6:
                        credits = Integer.parseInt(n);
                        break;
                    case 7:
                        if(!n.isBlank()) capacity = Integer.parseInt(n);
                        break;
                    case 8:
                        numstudents = Integer.parseInt(n);
                        break;
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                        if(!n.isBlank()) days.add(n.charAt(0));
                        break;
                    case 14:
                    case 15:
                        //XX:XX:XX AM/PM clip off :XX seconds thing at the end
                        if(!n.isBlank())times[i - 14] = n.substring(0,n.length()-6) + n.substring(n.length() - 3);
                        break;
                    case 16:
                    case 17:
                        if(i == 17) prof = " " + prof;
                        prof = n + prof;
                        break;
                }
                if(i > 17) break;
            }
            for(char d : days) daytimes.add(new DayTime(times[0],times[1],d));
            allcourses.add(new Course(name,section,major,coursenum,credits,numstudents,capacity,prof,year,sem,requiredby,daytimes));
            inline.close();
        }
        csvscn.close();

        Scanner scnr = new Scanner(System.in);
        Account curr_account;
        System.out.println("Do you have an account? y or no.");
        String user_choice = scnr.next();
        String curr_username;
        String curr_password;
        if ((!(user_choice.equalsIgnoreCase("y"))) && (!(user_choice.equalsIgnoreCase("n")))){
            System.out.println("Invalid entry. Please re-enter either y or n.");
            user_choice = scnr.next();
        }
        else {
            if (user_choice.equalsIgnoreCase("y")){
                System.out.println("Please enter your username.");
                curr_username = scnr.next();
                boolean hasAccount = false;
                int acct_indx = 0;
                for (int i = 0; i < accounts.size(); i++){
                    if (accounts.get(i).equals(curr_username)){
                        acct_indx = i;
                        hasAccount = true;
                    }
                }
                if (hasAccount) {
                    Account compare = new Account();
                    for (int i = 0; i < session_accounts.size(); i++){
                        if (session_accounts.get(i).getUsername() == curr_username){
                            compare = session_accounts.get(i);
                        }
                    }
                    System.out.println("Please enter your password.");
                    curr_password = scnr.next();
                    if(compare.verify_password(curr_password)){
                        System.out.println("Correct password.");
                    }
                    else {
                        while(!(compare.verify_password(curr_password)) || !(curr_password.equalsIgnoreCase("x"))){
                            System.out.println("Incorrect password. Please re-enter. Select x if you would like to exit.");
                            curr_password = scnr.next();
                        }
                        if (compare.verify_password(curr_password)){
                            System.out.println("Successfully logged in.");
                        }
                        else {
                            System.out.println("Goodbye!");
                        }
                    }
                }
            }
            else {
                String new_username;
                String new_password;
                Major new_major = Major.NULL;
                System.out.println("Would you like to create an account? Answer y or n");
                user_choice = scnr.next();
                String user_name;
                if ((!(user_choice.equalsIgnoreCase("y"))) && (!(user_choice.equalsIgnoreCase("n")))) {
                    System.out.println("Invalid entry. Please re-enter either y or n.");
                    user_choice = scnr.next();
                }
                else {
                    if (user_choice.equalsIgnoreCase("y")) {
                        System.out.println("What would you like to set as your username?");
                        new_username = scnr.next();
                        System.out.println("What would you like to set as your password?");
                        new_password = scnr.next();
                        System.out.println("Would you like to add a major to your account? Enter y or n.");
                        user_choice = scnr.next();
                        if ((!(user_choice.equalsIgnoreCase("y"))) && (!(user_choice.equalsIgnoreCase("n")))) {
                            System.out.println("Invalid entry. Please re-enter either y or n.");
                            user_choice = scnr.next();
                        }
                        else {
                            if (user_choice.equalsIgnoreCase("y")) {
                                System.out.println("Please type your major from list.");
                                List<Major> major_list = Arrays.asList(Major.values());
                                for (int i = 0; i < major_list.size(); i++) {
                                    System.out.print(major_list.get(i) + " ");
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
                                ;
                                Account user_account = new Account(new_username, new_password, new_major);
                                session_accounts.add(user_account);
                                System.out.println("Account successfully created!");
                                System.out.println();
                                user_account.printAcct();
                                System.out.println();
                            } else {
                                System.out.println("No major entered.");
                                Account user_account = new Account(new_username, new_password);
                                session_accounts.add(user_account);
                                System.out.println("Account successfully created!");
                                System.out.println();
                                user_account.printAcct();
                                System.out.println();
                            }
                        }
                    }
                    else {
                        System.out.println("No account created.");
                    }
                }
            }
        }


        System.out.println("Would you like to create a new schedule? Enter 'y' for yes or 'n' for no.");
        String user_input = (String) scnr.next();
        String yes_choice = "y";
        String no_choice = "n";
        while (!(user_input.equalsIgnoreCase(yes_choice)) && !(user_input.equalsIgnoreCase(no_choice))) {
            System.out.println("Invalid input. Please try again.");
            user_input = (String) scnr.next();
        }
        if (user_input.equals("y")) {
            System.out.println("What would you like the schedule to be called?");
            String sched_name = scnr.next();
            String file_name = sched_name + ".txt";
            System.out.println(file_name);
            Schedule newSched = new Schedule(sched_name);
            System.out.println(newSched);
        }
        else {
            System.out.println("No class created.");
        }

        for (int i = 0; i < 30; i++) {
            if(i == 0 || i == 29) {
                for (int j = 0; j < 60; j++){
                    System.out.print("_");
                }
            }
            else {
                for (int k = 0; k < 60; k++){
                    if (k % 5 == 0){
                        System.out.print("|");
                    }
                    else {
                        System.out.print(" ");
                    }
                }
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        try {run();}
        catch(IOException ioe) {System.out.println(ioe.getMessage() + "\n" + ioe.getCause());}
    }
}
