import java.io.IOException;

public class MainSchedAddRemove {
    public static void add_course_to_schedule() {
        boolean first = true;
        while (true) {
            if (first) first = false;
            else if (!GeneralUtils.want_more('a')) return;
            Main.autoflush.println("Warning: you will only be able to add courses for " + Main.currentsched.get_semester() + " " + Main.currentsched.get_year());
            //display search results to add from to user
            Main.autoflush.println(Main.search.to_str(true));
            /*String how = GeneralUtils.input("Enter 'i' to add a course by index, or enter 'c' to add a course by code and section: ").toLowerCase();
            if(!how.equalsIgnoreCase("i") && !how.equalsIgnoreCase("c")) {
                Main.autoflush.println("Error: invalid input.");
                continue;
            }
            if(how.equalsIgnoreCase("i")) add_course_by_index();
            else add_course_by_code_and_section();*/
            add_course_by_index();
        }
    }

    public static void add_course_by_index() {
        try {
            //attempt to get index and check if it is within the correct bounds
            int idx = Integer.parseInt(GeneralUtils.input("Enter the index of the course you would like to add: "));
            if(idx < 1) throw new IllegalArgumentException("Error: index must be greater than zero.");
            if(idx > Main.search.get_filtered_results().size()) throw new IllegalArgumentException("Error: that index is too large.");
            if(Main.currentsched.add_course(Main.search.get_filtered_results().get(idx-1)))
                Main.autoflush.println(Main.search.get_filtered_results().get(idx-1).short_str(true) + " has been added to the current schedule");
        }
        catch (NumberFormatException nfe) {
            Main.autoflush.println("Error: you did not enter a valid integer.");
        }
        catch (IllegalArgumentException iae) {
            Main.autoflush.println(iae.getMessage());
        }
    }

    //old add
    /*
    public static void add_course_by_code_and_section() {
        String[] cc = GeneralUtils.get_course_code(true);
        if (!Validations.valid_course_code(cc)) return;
        String coursecode = cc[0] + " " + cc[1];
        String section = GeneralUtils.input("Enter the section of the course to add: ").toUpperCase();
        if (!Validations.is_valid_section(section)) return;
        //String[] sem = input("Enter semester and year of the course to add (you will only be able to add courses for " + currentsched.get_semester() + " " + currentsched.get_year() + "): ").strip().split("\\s+");
        //get semester in correct form, then check valid year value, then check valid semester value
        //if(!get_semester_formatted(sem) || !valid_year(sem) || !valid_semester(sem)) continue;
        String toadd = coursecode + " " + section + " - " + Main.currentsched.get_semester() + " " + Main.currentsched.get_year();
        boolean addattempted = false;
        for(Course c : Main.search.get_filtered_results())
            if(toadd.equals(c.short_str(true))) {
                try {if(Main.currentsched.add_course(c)) Main.autoflush.println(toadd + " has been added to the current schedule");}
                catch(IllegalArgumentException iae) {Main.autoflush.println(iae.getMessage());}
                addattempted = true;
            }
        if (!addattempted) Main.autoflush.println("Error: " + toadd + " not found in search results");
    }
    */

    public static void remove_course_from_schedule() {
        boolean first = true;
        while (true) {
            if(first) first = false;
            else if(!GeneralUtils.want_more('r')) return;
            if(Main.currentsched.get_courses().isEmpty()) {
                Main.afl.println("Error: the current schedule does not contain any courses for removal");
                return;
            }
            String[] cc = GeneralUtils.get_course_code("rm");
            if (!Validations.valid_course_code(cc)) continue;
            //we don't have to check the section.... we know only 1 section of a given course code can be added
            //we don't have to check the semester in this case.... we know that the user is only able to add courses
            //for the current schedule's semester
            Course rm = null;
            for (Course c : Main.currentsched.get_courses()) {
                if (c.getMajor() == Major.valueOf(cc[0]) && c.getCourseNum() == Integer.parseInt(cc[1])) {
                    rm = c;
                    break;
                }
            }
            if (rm == null) Main.afl.println("Error: " + cc[0] + " " + cc[1] + " not found in schedule");
            else {
                Main.currentsched.remove_course(rm);
                Main.afl.println(cc[0] + " " + cc[1] + " successfully removed from the schedule");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Main.currentaccnt = new Account();
        MainSaveLoad.load_allcourses();
        Menus.in_schedule_menu();
    }
}
