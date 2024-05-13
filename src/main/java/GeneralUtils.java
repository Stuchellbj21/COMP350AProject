import java.util.Scanner;

public class GeneralUtils {
    public static Scanner userin = new Scanner(System.in);

    public static String input(String prompt) {
        while(true) {
            if (prompt != null) Main.afl.print(prompt);
            //strip the new line off the end and any starting whitespace
            String resp = userin.nextLine().strip();
            if(resp.length() <= 20) return resp;
            else Main.afl.println("Error: input was too long (valid input is 20 characters or less)");
        }
    }

    public static int int_input(String prompt) throws NumberFormatException {
        String intstr = input(prompt);
        return Integer.parseInt(intstr);
    }

    public static int get_int_from_user(String prompt) {
        while(true) {
            try{return int_input(prompt);}
            catch(NumberFormatException nfe){Main.afl.println("Invalid input. Please enter an integer.");}
        }
    }

    public static boolean is_numeric(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
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
            else Main.afl.println("Invalid");
        }
    }

    public static String[] get_course_code(String type) {
        String[] cc;
        if (type.equalsIgnoreCase("add")) cc = input("Enter the course code of the course to add (major course_number): ").strip().split("\\s+");
        else if(type.equalsIgnoreCase("rm")) cc = input("Enter the course code of the course to remove (major course_number): ").strip().split("\\s+");
        //tkn for taken
        else if(type.equalsIgnoreCase("tkn")) cc = input("Enter course taken (<major> <course_number>) or 'done': ").strip().split("\\s+");
        else {
            Main.afl.println("Error: wrong arg(s) given to get_course_code");
            cc = null;
        }
        cc[0] = cc[0].toUpperCase();
        return cc;
    }
    public static String[] get_course_code(boolean add, String exit_condition) {
        String[] cc;
        Main.afl.println(exit_condition);
        if (add) cc = input("Enter the course code of the course to add (major course_number): ").strip().split("\\s+");
        else cc = input("Enter the course code of the course to remove (major course_number): ").strip().split("\\s+");
        cc[0] = cc[0].toUpperCase();
        return cc;
    }

    public static boolean get_semester_formatted(String[] sem) {
        if(sem.length == 2 && sem[0] != null && sem[0].length() > 1) {
            sem[0] = sem[0].substring(0,1).toUpperCase() + sem[0].substring(1).toLowerCase();
            return true;
        }
        Main.afl.println("Error: invalid semester-year input");
        return false;
    }

    public static DayTime get_time_for_something(boolean modify) {
        while(true) {
            if(!FilterUtils.filter_move_forward(modify,FilterType.TIME)) return null;
            String start = GeneralUtils.input("Enter start time in the form XX:XX PM/AM (where X is a digit): ").toUpperCase();
            if(!DayTime.is_valid_time(start)) {
                Main.afl.println("Error: '" + start + "' is not a valid time");
                continue;
            }
            String end = GeneralUtils.input("Enter end time in the form XX:XX PM/AM (where X is a digit): ").toUpperCase();
            if(!DayTime.is_valid_time(end)) {
                Main.afl.println("Error: '" + end + "' is not a valid time");
                continue;
            }
            DayTime r = new DayTime(start,end);
            if(DayTime.military_to_minutes(r.get_militarystart()) >= DayTime.military_to_minutes(r.get_militaryend())) {
                Main.afl.println("Error: start time must be earlier than end time");
                continue;
            }
            return r;
        }
    }

    public static DayTime get_time_for_something(char extracurday) {
        while(true) {
            String start = GeneralUtils.input("Enter start time in the form XX:XX PM/AM (where X is a digit): ").toUpperCase();
            if(!DayTime.is_valid_time(start)) {
                Main.afl.println("Error: '" + start + "' is not a valid time");
                continue;
            }
            String end = GeneralUtils.input("Enter end time in the form XX:XX PM/AM (where X is a digit): ").toUpperCase();
            if(!DayTime.is_valid_time(end)) {
                Main.afl.println("Error: '" + end + "' is not a valid time");
                continue;
            }
            DayTime r = new DayTime(start,end,extracurday);
            if(DayTime.military_to_minutes(r.get_militarystart()) >= DayTime.military_to_minutes(r.get_militaryend())) {
                Main.afl.println("Error: start time must be earlier than end time");
                continue;
            }
            return r;
        }
    }
}
