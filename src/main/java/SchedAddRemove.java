import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SchedAddRemove {
    public static void add_course_to_schedule() {
        boolean first = true;
        while (true) {
            if (first) first = false;
            else if (!GeneralUtils.want_more('a')) return;
            Main.afl.println("Warning: you will only be able to add courses for " + Main.currentsched.get_semester() + " " + Main.currentsched.get_year());
            //display search results to add from to user
            Main.afl.println(Main.search.to_str(true));
            add_course_by_index();
        }
    }

    public static void add_course_by_index() {
        try {
            //attempt to get index and check if it is within the correct bounds
            int idx = Integer.parseInt(GeneralUtils.input("Enter the index of the course you would like to add: "));
            if(idx < 1) throw new IllegalArgumentException("Error: index must be greater than zero.");
            if(idx > Main.search.get_filtered_results().size()) throw new IllegalArgumentException("Error: that index is too large.");
            if(Main.currentsched.add_course(Main.search.get_filtered_results().get(idx-1))) {
                //make sure the thing is added to the undo stack
                Main.currentsched.get_undocoursestack().push(Main.search.get_filtered_results().get(idx-1));
                //if wishlist contains items and the added course is in wishlist, remove it from there
                if(Main.currentaccnt.get_wishlist() != null && !Main.currentaccnt.get_wishlist().isEmpty())
                    remove_all_from_wishlist(Main.search.get_filtered_results().get(idx-1));
                Main.afl.println(Main.search.get_filtered_results().get(idx - 1).short_str(true) + " has been added to the current schedule");
            }
        }
        catch (NumberFormatException nfe) {
            Main.afl.println("Error: you did not enter a valid integer.");
        }
        catch (IllegalArgumentException iae) {
            Main.afl.println(iae.getMessage());
        }
    }

    public static void remove_all_from_wishlist(Course add) {
        List<Course> rm = new ArrayList<>();
        //every course that matches the added course must be removed from the wishlist, we don't care about
        //semester or section
        for(Course course : Main.currentaccnt.get_wishlist()) {
            //chop off section, don't care about it (-2)
            String coursestr = course.short_str(false).substring(0, course.short_str(false).length() - 2);
            String addstr = add.short_str(false).substring(0, add.short_str(false).length() - 2);
            if(coursestr.equalsIgnoreCase(addstr)) rm.add(course);
        }
        Main.currentaccnt.get_wishlist().removeAll(rm);
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
                try {if(Main.currentsched.add_course(c)) Main.afl.println(toadd + " has been added to the current schedule");}
                catch(IllegalArgumentException iae) {Main.afl.println(iae.getMessage());}
                addattempted = true;
            }
        if (!addattempted) Main.afl.println("Error: " + toadd + " not found in search results");
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

    public static void wish_list() {
        Main.currentaccnt.print_wishlist();
        // The Course Code
        String [] cc;
        // Affirmation to adding section or not
        String yon = "";
        // Class period
        String cpd = "";
        cc = GeneralUtils.get_course_code(true, "Enter invalid course code to quit");
        if (Validations.valid_course_code(cc)) {
            String coursecode = cc[0] + " " + cc[1];
            //search for that course
            Search findcourse = new Search();
            yon = GeneralUtils.input("Would you like to specify a section? (y/n): ").toUpperCase();
            if (yon.equals("Y")) {
                cpd = GeneralUtils.input("Enter the class section you would like to add to your wishlist: ").toUpperCase();
                //assuming there will be no courses with over 15 sections/15 variants
                findcourse.search(coursecode + " " + cpd,15);
                for (int i = 0; i < findcourse.get_filtered_results().size(); i++) {
                    String course = findcourse.get_filtered_results().get(i).getMajor() + " " + findcourse.get_filtered_results().get(i).getCourseNum();
                    if (course.equals(coursecode) && findcourse.get_filtered_results().get(i).getSection() == cpd.charAt(0)) {
                        Main.currentaccnt.get_wishlist().add(findcourse.get_filtered_results().get(i));
                        Main.afl.println("course added to wishlist");
                        //break; remove break so that all courses with same section and
                        //course code will be added to wishlist
                    }
                }
            }
            else if(yon.equals("N")){
                findcourse.search(coursecode,15);
                for (int i = 0; i < findcourse.get_filtered_results().size(); i++) {
                    String course = findcourse.get_filtered_results().get(i).getMajor() + " " + findcourse.get_filtered_results().get(i).getCourseNum();
                    if (course.equals(coursecode)) {
                        Main.currentaccnt.get_wishlist().add(findcourse.get_filtered_results().get(i));
                        Main.afl.println("course added to wishlist");
                    }
                }
            }
            else Main.afl.println("Error: invalid input");
        }
        else Main.afl.println("Error: invalid input");
    }

    public static void extracurricular() {
        String name = "";
        name = GeneralUtils.input("Enter the name for the extracurricular activity you choose to add: ");
        //make string first since want to check if they put in to long a string (str should only be 1 char long)
        String d = GeneralUtils.input("Enter the day the extracurricular activity will occur: ").toUpperCase();
        if(d.length() != 1) {
            Main.afl.println("Error: input should be 1 character long ('M', 'T', 'W', 'R', or 'F')");
            return;
        }
        char day = d.charAt(0);
        if (day != 'M' && day != 'T' && day != 'W' && day != 'R' && day != 'F') {
            Main.afl.println("Error: invalid day given");
            return;
        }
        DayTime time = GeneralUtils.get_time_for_something(day);
        Extracurricular e = new Extracurricular(time, name);

        for (Extracurricular schedex : Main.currentsched.get_extracurriculars()) {
            //can't have 2 extracurriculars that have the same name and overlap in time
            if (schedex.get_name().equalsIgnoreCase(e.get_name()) && schedex.get_time().get_day() == e.get_time().get_day() && schedex.get_time().overlaps(e.get_time())) {
                Main.afl.println("Error: there can't be multiple instances of extracurricular " + e.get_name() + " that overlap in time");
                return;
            }
        }

        for (Course c : Main.currentsched.get_courses()) {
            if (c.times_overlap_with(time)) {
                Main.afl.println("Error: extracurricular overlaps with a course in schedule");
                return;
            }
        }
        //TODO:fix this + extracurricular
        Main.currentsched.get_extracurriculars().add(e);
    }

    public static void revert_change() {
        if (Main.currentsched.get_undocoursestack().isEmpty()) {
            Main.afl.println("Error: no course additions recorded");
        } else {
            Course temp = Main.currentsched.get_undocoursestack().pop();
            Main.currentsched.remove_course(temp);
            Main.afl.println("Undid last course addition: " + temp.getName());
        }
    }
  
    public static void main(String[] args) throws IOException, SQLException {
        Main.currentaccnt = new Account();
        SaveLoad.load_allcourses();
        Menus.in_schedule_menu();
    }
}
