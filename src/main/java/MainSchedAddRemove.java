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

    public static void wish_list() {
        Main.currentaccnt.print_wishlist();
        // The Course Code
        String [] cc;
        // Affirmation to adding section or not
        String yon = "";
        // Class period
        String cpd = "";
        //TODO: change System.outs to Main.autoflush
        cc = GeneralUtils.get_course_code(true, "Enter invalid course code to quit");
        if (Validations.valid_course_code(cc)) {
            String coursecode = cc[0] + " " + cc[1];
            yon = GeneralUtils.input("Would you like to specify a class period? (Y / N): ").toUpperCase();
            if (yon.equals("Y")) {
                cpd = GeneralUtils.input("Enter the class section you would like to add to your wishlist: ").toUpperCase();
                for (int i = 0; i < Main.search.get_filtered_results().size(); i++) {
                    String course = Main.search.get_filtered_results().get(i).getMajor() + " " + Main.search.get_filtered_results().get(i).getCourseNum();
                    if (course.equals(coursecode) && Main.search.get_filtered_results().get(i).getSection() == cpd.charAt(0)) {
                        Main.currentaccnt.get_wishlist().add(Main.search.get_filtered_results().get(i));
                        Main.autoflush.println("course added to wishlist");
                        break;
                    }
                }
            }
            else {
                for (int i = 0; i < Main.search.get_filtered_results().size(); i++) {
                    String course = Main.search.get_filtered_results().get(i).getMajor() + " " + Main.search.get_filtered_results().get(i).getCourseNum();
                    if (course.equals(coursecode)) {
                        Main.currentaccnt.get_wishlist().add(Main.search.get_filtered_results().get(i));
                        Main.autoflush.println("course added to wishlist");
                    }
                }
            }
        }
    }

    public static void extracurricular() {
        String name = "";
        name = GeneralUtils.input("Enter the name for the extracurricular activity you choose to add: ");
        char day = GeneralUtils.input("Enter the day the extracurricular activity will occur: ").toUpperCase().charAt(0);
        if (day != 'M' && day != 'T' && day != 'W' && day != 'R' && day != 'F') {
            Main.autoflush.println("Error: invalid day given");
            return;
        }
        DayTime time = MainFilterUtils.get_time_for_filter(false, day);
        Extracurricular e = new Extracurricular(time, name);
        for (Course c : Main.currentsched.get_courses()) {
            if (c.times_overlap_with(time)) {
                Main.autoflush.println("Error: extracurricular overlaps with a course in schedule");
                return;
            }
        }
        //TODO:fix this + extracurricular
        Main.currentsched.get_extracurriculars().add(e);
    }

    public static void revert_change() {
        if (Main.currentsched.get_undocoursestack().isEmpty()) {
            Main.autoflush.println("Error: no course has been added to undo its addition");
        } else {
            Course temp = Main.currentsched.get_undocoursestack().pop();
            Main.currentsched.remove_course(temp);
            System.out.println("Undid last course addition: " + temp.getName());
        }
    }
  
    public static void main(String[] args) throws IOException {
        Main.currentaccnt = new Account();
        MainSaveLoad.load_allcourses();
        Menus.in_schedule_menu();
    }
}
