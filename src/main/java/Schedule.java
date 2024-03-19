import java.util.*;
import java.io.FileInputStream;
import java.io.IOException;

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
    public Schedule(String fname) {
        courses = new ArrayList<>();
        try {load(fname);}
        catch(InputMismatchException | IOException error) {System.out.println(error.getMessage() + " " + error.getCause());}
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

    public void save() {

    }

    //fname doesn't have extension
    public void load(String fname) throws IOException {
        FileInputStream fis = new FileInputStream(fname+".csv");
        Scanner fscn = new Scanner(fis);
        //skip descriptor line
        if(!fscn.nextLine().equals("name,semester,year,credits")) throw new InputMismatchException("input file is not in the correct format");
        //don't let user make/save partially filled schedules (all of name, semester, year, and credits must be given)
        //a schedule may have an empty course list however
        String data = fscn.nextLine();
        //if(!data.equals("--")) {
        Scanner parser = new Scanner(data);
        parser.useDelimiter(",");
        //could add if checks to all of the following
        name = parser.next();
        semester = parser.next();
        year = parser.nextInt();
        credits = parser.nextInt();
        parser.close();
        //else return false;
        //get past descriptors
        fscn.nextLine();
        fscn.nextLine();
        //read in courses
        data = fscn.nextLine();
        //'--' will denote that there are no courses
        if(!data.equals("--")) {
            boolean first = true;
            while(fscn.hasNextLine()) {
                //we've already got the new line for the first iteration
                //but on later iterations we need to scan a new line
                if(first) first = false;
                else data = fscn.nextLine();
                parser = new Scanner(data);
                parser.useDelimiter(",");
                //define defaults for all variables for Course
                String cname = "", prof = "", csem = "";
                char section = '_';
                Major major = Major.COMP;
                int coursenum = -1, ccredits = 0, numstudents = 0, capacity = 0, cyear = -1;
                Set<Major> requiredby = new HashSet<>();
                List<DayTime> daytimes = new ArrayList<>();
                for (int i = 0; parser.hasNext(); i++) {
                    data = parser.next();
                    //shouldn't have to deal with blanks here (should have saved correctly)
                    //name,section,major,coursenum,credits,numstudents,capacity,professor,year,semester,times(start-end-day-start-end-day-start-end-day-....),requiredby(m1-m2-m3-m4-....)
                    switch (i) {
                        //name
                        case 0 -> cname = data;
                        //section
                        case 1 -> section = data.charAt(0);
                        //major
                        case 2 -> major = Major.valueOf(data);
                        //coursenum
                        case 3 -> coursenum = Integer.parseInt(data);
                        //credits
                        case 4 -> ccredits = Integer.parseInt(data);
                        //numstudents
                        case 5 -> numstudents = Integer.parseInt(data);
                        //capacity
                        case 6 -> capacity = Integer.parseInt(data);
                        //prof
                        case 7 -> prof = data;
                        //year
                        case 8 -> cyear = Integer.parseInt(data);
                        //semester
                        case 9 -> csem = data;
                        //times
                        case 10 -> load_daytimes(data, daytimes);
                        //requiredby
                        case 11 -> load_required_by(data, requiredby);
                    }
                }
                courses.add(new Course(cname, section, major, coursenum, ccredits, numstudents, capacity, prof, cyear, csem, requiredby, daytimes));
            }
        }
    }

    public void load_daytimes(String data,List<DayTime> daytimes) {
        if(!data.isBlank()) {
            Scanner t = new Scanner(data);
            t.useDelimiter("-");
            while (t.hasNext()) {
                String[] dt = new String[3];
                for (int j = 0; j < dt.length; j++) dt[j] = t.next();
                daytimes.add(new DayTime(dt[0], dt[1], dt[2].charAt(0)));
            }
        }
    }

    public void load_required_by(String data, Set<Major> requiredby) {
        //I know I'm repeating myself a bit.... bad practice, but I don't
        //want to take the time to fix it right now
        if(!data.isBlank()) {
            Scanner rb = new Scanner(data);
            rb.useDelimiter("-");
            while(rb.hasNext()) requiredby.add(Major.valueOf(rb.next()));
        }
    }

    public String show_attributes() {
        StringBuilder sb = new StringBuilder("name: ").append(name).append('\n').append("semester: ");
        sb.append(semester).append(" ").append(year).append("\ncourses:\n");
        if(courses.isEmpty()) sb.append("\tNone");
        for(int i = 0; i < courses.size(); i++) {
            sb.append(i+1).append(".\n\t").append(courses.get(i)).append("\n\tcredits: ");
            sb.append(courses.get(i).getCredits()).append("\n\trequired by: ").append(courses.get(i).getRequiredby());
            //if not on last course, add a '\n'
            if(i < courses.size() - 1) sb.append('\n');
        }
        return sb.toString();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
