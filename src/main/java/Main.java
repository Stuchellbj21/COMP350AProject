import java.util.HashSet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import java.util.Scanner;
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
    private List<String> accounts; //list of all account names (which are directory names)

    //account has a schedule instance that is worked on
    public static void run() throws IOException {
        FileInputStream fis = new FileInputStream("2020-2021.csv");
        Scanner csvscn = new Scanner(fis);
        allcourses = new ArrayList<>();
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

        Scanner scnr = new Scanner(System.in)
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
    }

    public static void main(String[] args) {
        try {run();}
        catch(IOException ioe) {System.out.println(ioe.getMessage() + "\n" + ioe.getCause());}
    }
}
