import java.util.Scanner;

public class GeneralUtils {
    public static Scanner userin = new Scanner(System.in);
    public static String input(String prompt) {
        if (prompt != null) {
            Main.autoflush.print(prompt);
        }
        //strip the new line off the end and any starting whitespace
        return userin.nextLine().strip();
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
            else Main.autoflush.println("Invalid");
        }
    }

    public static String[] get_course_code(boolean add) {
        String[] cc;
        if (add) cc = input("Enter the course code of the course to add (major course_number): ").strip().split("\\s+");
        else cc = input("Enter the course code of the course to remove (major course_number): ").strip().split("\\s+");
        cc[0] = cc[0].toUpperCase();
        return cc;
    }
    public static String[] get_course_code(boolean add, String exit_condition) {
        String[] cc;
        Main.autoflush.println(exit_condition);
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
        Main.autoflush.println("Error: invalid semester-year input");
        return false;
    }
}
