import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.util.List;
import java.util.HashSet;


public class MainSaveLoad {
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
        fw.write(tempLine + "\n");
        for (int i = 0; i < Main.currentaccnt.get_schednames().size(); i++) {
            if (i != Main.currentaccnt.get_schednames().size()-1) {
                fw.write(Main.currentaccnt.get_schednames().get(i) + ",");
            } else {
                fw.write(Main.currentaccnt.get_schednames().get(i));
            }
        }
        fw.write("\n");
        for (int i = 0; i < Main.currentaccnt.get_folders().size(); i++) {
            fw.write(Main.currentaccnt.get_folders().get(i) + ",");
        }
        fw.close();
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
        Set<Integer> keys = Main.accounts.keySet();
        for (int key : keys) {
            String hash_password = String.valueOf(key);
            String username = Main.accounts.get(key);
            pw.write(hash_password + ":" + username + "\n"); //Gets the username and password for each account and prints both to the directory file seperated by a colon and ending with a newline
        }
        pw.close();
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
            Main.accounts.put(int_pass_hash, account_name); //Adds the accounts username and hash password to the accounts Map
        }
    }

    /**
     * Reads from user's info.txt file and adds all saved schedules to a static list in main
     * @throws IOException
     */
    public static void load_schedules() throws IOException {
        FileInputStream fis = new FileInputStream("Accounts\\" + Main.currentaccnt.getUsername() + '\\' + "info.txt");
        Scanner infoScan = new Scanner(fis);
        infoScan.nextLine(); // skip line that contains account information (password-hash, username, major)
        if (infoScan.hasNextLine()) {
            String schedules = infoScan.nextLine().replace("\n", "");
            Scanner schedScan = new Scanner(schedules);
            schedScan.useDelimiter(",");
            while (schedScan.hasNext()) {
                String temp = schedScan.next();
                Main.currentaccnt.save_schedule(temp);
            }
        }
        infoScan.close();
        fis.close();
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

    public static void load_folders() throws IOException {
        FileInputStream fis = new FileInputStream("Accounts\\" + Main.currentaccnt.getUsername() + '\\' + "info.txt");
        Scanner infoScan = new Scanner(fis);
        infoScan.nextLine(); // skip line that contains account information (password-hash, username, major)
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
}
