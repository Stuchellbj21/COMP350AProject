import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private String name;
    private String semester; // Spring or Fall
    private int year;
    private List<Course> courses;
    private int credits;

    //getters + setters
    public String get_name() {
        return name;
    }

    public void set_name(String name) {
        this.name = name;
    }

    public String get_semester() {
        return semester;
    }

    public void set_semester(String semester) {
        this.semester = semester;
    }

    public int get_year() {
        return year;
    }

    public void set_year(int year) {
        this.year = year;
    }

    public List<Course> get_courses() {
        return courses;
    }

    public void set_courses(List<Course> courses) {
        this.courses = courses;
    }

    public int get_credits() {
        return credits;
    }

    public void set_credits(int credits) {
        this.credits = credits;
    }

    // default constructor
    public Schedule() {
        name = "Blank Schedule";
        courses = new ArrayList<>();
    }

    // constructor w/one parameter
    public Schedule(String name) {
        this.name = name;
        courses = new ArrayList<>(); //create empty course list
        // credits will be added as courses are added
    }

    // constructor with name and list of courses
    public Schedule(String name, ArrayList<Course> courses) {
        this.name = name;
        this.courses = courses;
    }

    // full constructor
    public Schedule(String name, String semester, int year, int credits) {
        this.name = name;
        this.semester = semester;
        this.year = year;
        courses = new ArrayList<>();
        this.credits = credits;
    }

    // equals method
    public boolean equals(Schedule sched) {
        return (this.name.equals(sched.name) && this.semester.equals(sched.name)
                && this.year == sched.year && this.courses.equals(sched.courses) &&
                this.credits == sched.credits);
        // May not need ALL of these comparisons, consider which ones are needed
    }

    //toString
    @Override
    public String toString() {
        return name + ", " + semester + ", year";
        // could also be ' semester + ", " + year + ", " + name '
    }


    /**
     * Adds a course to a Schedule
     *
     * @param course, The specific course to be added
     * @return true if course is added, false if not
     */
    public boolean add_course(Course course) {
        if (course == null) {
            System.out.println("Cannot add null course");
            return false;
        }
        courses.add(course);
        return true;
    }

    /**
     * Removes course from a Schedule
     *
     * @param course The specific course to be removed
     * @return true if course is removed, false if not
     */
    public boolean remove_course(Course course) {
        if (courses.isEmpty()) {
            System.out.println("Schedule is already empty");
            return false;
        }
        courses.remove(course);
        return true;
    }

    /**
     * Removes all courses from a schedule
     * @return true if all courses are cleared, false if schedule is already empty
     */
    public boolean clear_schedule() {
        if (courses.isEmpty()) {
            System.out.println("Cannot clear empty schedule");
            return false;
        }
        courses.clear();
        return true;
    }
}
