import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Set;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.util.List;
import java.util.HashSet;


public class SaveLoad {
    /**
     * Completes the info.txt file associated with each account by writing password-hash, username, major
     * and schedule names to file in the user's account directory.
     * @throws IOException
     */
    public static void account_flush() throws IOException {
        FileReader reader = new FileReader(("Accounts\\" + Main.currentaccnt.getUsername() + '\\' + "info.txt"));
        BufferedReader br = new BufferedReader(reader);
        String tempLine = br.readLine(); // save first line of file since it will be overwritten
        br.close();

        File f = new File("Accounts\\" + Main.currentaccnt.getUsername() + '\\' + "info.txt");
        FileWriter fw = new FileWriter(f, false); // rewrite the whole file
        fw.write("Folders:\n");
        for (int i = 0; i < Main.currentaccnt.get_folders().size(); i++) {
            if (i == Main.currentaccnt.get_folders().size()-1){
                fw.write(Main.currentaccnt.get_folders().get(i));
            } else {
                fw.write(Main.currentaccnt.get_folders().get(i) + ",");
            }
        }
        fw.close();
    }


    /**
     * Reads from user's info.txt file and adds all saved schedules to a static list in main
     * @throws IOException
     */
    public static void load_schedules() throws IOException, SQLException {
//        FileInputStream fis = new FileInputStream("Accounts\\" + Main.currentaccnt.getUsername() + '\\' + "info.txt");
//        Scanner infoScan = new Scanner(fis);
//        infoScan.nextLine(); // skip line that contains account information (password-hash, username, major)
//        if (infoScan.hasNextLine()) {
//            String schedules = infoScan.nextLine().replace("\n", "");
//            Scanner schedScan = new Scanner(schedules);
//            schedScan.useDelimiter(",");
//            while (schedScan.hasNext()) {
//                String temp = schedScan.next();
//                Main.currentaccnt.save_schedule(temp);
//            }
//        }
        // DATABASE version
        List<String> tempList = Main.db.get_schedules(Main.currentaccnt.getUsername());
        Main.currentaccnt.get_schednames().addAll(tempList);
        //--------------------------------------------------

//        infoScan.close();
//        fis.close();
    }

    public static void load_allcourses() throws IOException {
        Main.allcourses = new ArrayList<>();
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
                        String last_name = prof;
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
        if (!Main.allcourses.isEmpty()) {
            Course last = Main.allcourses.getLast();
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
            else Main.allcourses.add(add);
        } else Main.allcourses.add(add);
    }

    public static void load_acct_info() throws IOException {
        FileInputStream fis = new FileInputStream("Accounts\\" + Main.currentaccnt.getUsername() + '\\' + "info.txt");
        Scanner infoScan = new Scanner(fis);
        infoScan.nextLine(); // skip line that contains 'Folders' header
        if (infoScan.hasNextLine()) {
            infoScan.useDelimiter(",");
            while (infoScan.hasNext()) {
                String temp = infoScan.next();
                if (!(temp.equals("\n"))) {
                    Main.currentaccnt.get_folders().add(temp);
                }
            }
        }
        infoScan.close();
        fis.close();
    }

    //load the courses already in the 'courses_taken.txt' file into accounts courses taken.... this will ONLY be called on login
    public static void load_courses_taken() throws IOException {
        FileInputStream fis = new FileInputStream("Accounts\\" + Main.currentaccnt.getUsername() + "\\courses_taken.txt");
        Scanner fscn = new Scanner(fis);
        while(fscn.hasNextLine()) Main.currentaccnt.get_coursestaken().add(fscn.nextLine().strip());
        fscn.close();
    }
}
