import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class Schedule {
    private String name;
    private String semester; // Spring or Fall
    private int year;
    private List<Course> courses;
    private int credits;

    //associates days to sorted lists of times that are on that day
    private Map<Character,List<DayTime>> timesperday;

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
        semester = "Spring";
        year = 2020;
        courses = new ArrayList<>();
        credits = 0;
        init_timesperday();
    }
    // constructor w/one parameter
    public Schedule(String accountname, String name) {
        courses = new ArrayList<>();
        init_timesperday();
        //name, semeseter, year, and credits are set in load
        try {load(accountname, name);}
        catch(InputMismatchException | IOException error) {Main.autoflush.println(error.getMessage() + ' ' + error.getCause());}
        update_times_per_day();
    }
    // constructor with name and list of courses
    public Schedule(String name, ArrayList<Course> courses) {
        this.name = name;
        this.courses = courses;
    }
    // full constructor
    public Schedule(String name, String semester, int year, List<Course> courses, int credits) {
        this.name = name;
        this.semester = semester;
        this.year = year;
        this.courses = courses;
        update_times_per_day();
        this.credits = credits;
    }

    // equals method
    @Override
    public boolean equals(Object other) {
        if(other == null) return false;
        if(!(other instanceof Schedule o)) return false;
        //there should only be 1 schedule of a given name
        return name.equals(o.name);
    }

    //toString
    @Override
    public String toString() {
        return name + ", " + semester + ", " + year;
    }

    public void set_name_with_checks(String accountname) {
        while(true) {
            String newname = Main.input("Enter new schedule name: ");
            try{
                if(Main.is_valid_name(newname)) {
                    //don't have to replace existing because is_valid_name already checks to make sure the target doesn't exist
                    File oldf = new File("Accounts\\" + accountname + "\\" + name +(name.endsWith(".csv") ? "" : ".csv"));
                    File newf = new File("Accounts\\" + accountname + "\\" + newname + (newname.endsWith(".csv") ? "" : ".csv"));
                    //only need to rename if old file exists
                    if(oldf.exists()) Files.move(oldf.toPath(),newf.toPath());
                    name = newname;
                    Main.autoflush.println("Schedule name successfully changed to '" + newname + "'");
                    break;
                }
                else Main.autoflush.println("Error: '" + newname + "' is not a valid schedule name");
            }
            catch(IOException ioe) {Main.autoflush.println("Error: renaming of file failed: " + ioe.getMessage());}
            catch(IllegalArgumentException iae) {Main.autoflush.println(iae.getMessage());}
        }
    }
    public void set_semester_with_checks() {
        while(true) {
            String newsem = Main.input("Enter new semester value (Warning: changing the schedule's semester will remove all courses from the schedule): ").toLowerCase();
            //ensure either Fall or Spring
            if(newsem.isEmpty()) {
                Main.autoflush.println("Error: '" + newsem + "' is not a valid semester value");
                continue;
            }
            newsem = newsem.substring(0,1).toUpperCase() + newsem.substring(1);
            if(newsem.equals("Fall") || newsem.equals("Spring")) {
                if(!newsem.equalsIgnoreCase(semester)) {
                    //remove all courses if semester changed
                    for(Iterator<Course> cit = courses.iterator();cit.hasNext();cit.remove()) removal_update(cit.next());
                }
                semester = newsem;
                Main.autoflush.println("Warning: you will now only be able to add courses for the " + semester + " to this schedule");
                return;
            }
            else Main.autoflush.println("Error: '" + newsem + "' is not a valid semester value");
        }
    }
    public void set_year_with_checks() {
        while(true) {
            //ensure either Fall or Spring
            int newyear;
            try{
                String in = Main.input("Enter new year value (Warning: changing the schedule's year will remove all courses from the schedule): ");
                newyear = Integer.parseInt(in);
                if(in.length() != 4) Main.autoflush.println("Error: valid year values are only 4 digits long");
                else if(newyear < 0) Main.autoflush.println("Error: the year value must be positive");
                else if(2020 > newyear) Main.autoflush.println("Error: " + newyear + " is too far in the past, schedules must be for 2020 or later");
                else {
                    if(newyear != year) {
                        //remove all courses if year changed
                        for(Iterator<Course> cit = courses.iterator();cit.hasNext();cit.remove()) removal_update(cit.next());
                    }
                    year = newyear;
                    Main.autoflush.println("Warning: you will now only be able to add courses for " + year + " to this schedule");
                    break;
                }
            }
            catch(NumberFormatException nfe) {Main.autoflush.println("Error: you did not enter a valid integer value");}
        }
    }

    public String to_str() {
        StringBuilder sb = new StringBuilder("Name: ").append(name).append(" - ").append(semester).append(' ');
        sb.append(year).append(" - ").append(credits).append(" credits\n");
        sb.append("-------------------------------------\n");
        add_classes_for_day_to_str('M',sb);
        add_classes_for_day_to_str('T',sb);
        add_classes_for_day_to_str('W',sb);
        add_classes_for_day_to_str('R',sb);
        add_classes_for_day_to_str('F',sb);
        sb.append("-------------------------------------");
        add_no_times(sb);
        return sb.toString();
    }

    private void add_no_times(StringBuilder sb) {
        int count = 0;
        for(Course c : courses) {
            if(c.has_no_times()) {
                String coursestr = new StringBuilder(c.getMajor().name()).append(' ').append(c.getCourseNum()).append(' ').append(c.getSection()).toString();
                if(count++ == 0) sb.append("\nNo Times Listed For: ").append(coursestr);
                else sb.append(", ").append(coursestr);
            }
        }
        if(count > 0) sb.append("\n-------------------------------------");
    }

    private void add_classes_for_day_to_str(char day, StringBuilder sb) {
        sb.append(day).append(":(free time)");
        for(int i = 0; i < timesperday.get(day).size(); i++) {
            DayTime dt = timesperday.get(day).get(i);
            Course current = null;
            for(Course c : courses)
                if(c.has_time(dt)) current = c;
            //now we have the correct course to add, so we add it
            sb.append('(').append(current.getMajor()).append(' ').append(current.getCourseNum());
            sb.append(' ').append(current.getSection()).append(' ').append(dt.get_start_time()).append('-');
            sb.append(dt.get_end_time()).append(')');
            //if we're on last time, or there are more than 15 minutes between this course and the next,
            //add (free time)
            if(i == timesperday.get(day).size() - 1 || (DayTime.military_to_minutes(timesperday.get(day).get(i + 1).get_militarystart()) - DayTime.military_to_minutes(dt.get_militaryend()) > 15))
                sb.append("(free time)");
        }
        sb.append('\n');
    }

    /**
     * Adds a course to a Schedule
     *
     * @param course, The specific course to be added
     * @return true if course is added, false if not
     */
    public boolean add_course(Course course) {
        if(course.isFull()) throw new IllegalArgumentException("Error: " + course.short_str(true) + " is full already");
        //if(course.has_no_times()) throw new IllegalArgumentException("Error: courses with no times listed cannot be added to schedules");
        if(!course.getSemester().equalsIgnoreCase(semester) || course.getYear() != year) throw new IllegalArgumentException("Error: " + course.short_str(true) + " cannot be added as it is for a different semester than this schedule is");
        for(Course c : courses) {
            //if the times overlap or they are the same class as something already in the schedule or the course is full
            //there's an issue
            if(c.getCourseNum() == course.getCourseNum() && c.getMajor() == course.getMajor()) throw new IllegalArgumentException("Error: " + course.getMajor() + " " + course.getCourseNum() + " is already in your schedule");
            if(c.times_overlap_with(course)) throw new IllegalArgumentException("Error: the course you attempted to add has a time overlap with " + c.short_str(true) + " in your schedule");
        }
        courses.add(course);
        //not going to change numstudents of course because don't want to have to update the csv file
        credits += course.getCredits();
        update_times_per_day();
        return true;
    }

    /**
     * Removes course from a Schedule
     *
     * @param course The specific course to be removed
     * @return true if course is removed, false if not
     */
    public boolean remove_course(Course course) {
        if(courses.isEmpty()) throw new InputMismatchException("Error: cannot remove a course from an empty schedule");
        if(!courses.contains(course)) throw new InputMismatchException("Error: your schedule does not contain " + course.short_str(false) + " course");
        courses.remove(course);
        removal_update(course);
        return true;
    }

    private void removal_update(Course course) {
        credits -= course.getCredits();
        //not updating number of students in course cause don't want to update all course csv
        remove_from_times_per_day(course);
    }

    private void remove_from_times_per_day(Course c) {
        for(DayTime dt : c.getTimes()) timesperday.get(dt.get_day()).remove(dt);
    }

    public void save(String accountname) throws IOException {
        //will save to <schedule name>.csv (may have to remove some punctuation or something)
        //to get things to work right
        FileOutputStream fos = new FileOutputStream("Accounts\\" + accountname + '\\' + name + ".csv");
        PrintWriter pw = new PrintWriter(fos);
        pw.println("name,semester,year,credits");
        pw.println(name + ',' + semester + ',' + year + ',' + credits + '\n');
        pw.println("name,section,major,coursenum,credits,numstudents,capacity,professor,year,semester,times(start-end-day-start-end-day-start-end-day-....),requiredby(m1-m2-m3-m4-....)");
        if(courses.isEmpty()) pw.println("--");
        else {
            //name,section,major,coursenum,credits,numstudents,capacity,professor,year,semester,times(start-end-day-start-end-day-start-end-day-....),requiredby(m1-m2-m3-m4-....)
            for(Course c : courses) {
                StringBuilder course = new StringBuilder(c.getName()).append(',').append(c.getSection()).append(',').append(c.getMajor().name());
                course.append(',').append(c.getCourseNum()).append(',').append(c.getCredits()).append(',').append(c.getNumstudents()).append(',');
                course.append(c.getCapacity()).append(',').append(c.getProfessor()).append(',').append(c.getYear()).append(',').append(c.getSemester());
                course.append(',');
                for(DayTime dt : c.getTimes()) {
                    course.append(dt.get_start_time()).append('-').append(dt.get_end_time()).append('-').append(dt.get_day());
                    //think we actually want a compare by reference here
                    if(dt != c.getTimes().getLast()) course.append('-');
                }
                course.append(',');
                for(Major m : c.getRequiredby()) course.append(m.name()).append('-');
                //don't include last hyphon of course set or last comma if no requiredby
                pw.println(course.substring(0,course.length()-1));
            }
        }
        pw.close();
    }

    //timesperday should always be initialized via this method.... timesperday should always be null if
    //init hasn't been called
    void init_timesperday() {
        if(timesperday == null) {
            timesperday = new HashMap<>();
            timesperday.put('M',new ArrayList<>());
            timesperday.put('T',new ArrayList<>());
            timesperday.put('W',new ArrayList<>());
            timesperday.put('R',new ArrayList<>());
            timesperday.put('F',new ArrayList<>());
        }
    }

    //makes sure all courses in courses are represented in timesperday
    private void update_times_per_day() {
        init_timesperday();
        for(Course c : courses) {
            for(DayTime dt : c.getTimes()) {
                //if no list present for a given day, add a new list
                if(timesperday.get(dt.get_day()) == null) timesperday.put(dt.get_day(),new ArrayList<>());
                add_if_unique(dt);
            }
        }
        //ensure that the lists of times are in sorted order
        for(List<DayTime> times : timesperday.values()) Collections.sort(times);
    }

    //expects timesperday to not be null
    private boolean add_if_unique(DayTime dt) {
        if(timesperday.get(dt.get_day()) == null || !timesperday.get(dt.get_day()).contains(dt)) {
            timesperday.get(dt.get_day()).add(dt);
            return true;
        }
        //the list exists and contains the element already
        return false;
    }

    public void load(String accountname,String fname) throws IOException {
        FileInputStream fis = new FileInputStream("Accounts" + '\\' + accountname + '\\' + fname + (fname.endsWith(".csv") ? "" : ".csv"));
        Scanner fscn = new Scanner(fis);
        //skip descriptor line
        if (!fscn.nextLine().equals("name,semester,year,credits"))
            throw new InputMismatchException("input file is not in the correct format");
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
        courses.clear();
        parser.close();
        //else return false;
        //get past descriptors
        fscn.nextLine();
        fscn.nextLine();
        //read in courses
        data = fscn.nextLine();
        //'--' will denote that there are no courses
        if (!data.equals("--")) {
            boolean first = true;
            while (first || fscn.hasNextLine()) {
                //we've already got the new line for the first iteration
                //but on later iterations we need to scan a new line
                if (first) first = false;
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
                update_times_per_day();
            }
            System.out.println(courses);
        }
        fscn.close();
    }

    public void f_load(String accountname,String fname,String sname) throws IOException {
        FileInputStream fis = new FileInputStream("Accounts" + '\\' + accountname + '\\' + fname + '\\' + sname + (sname.endsWith(".csv") ? "" : ".csv"));
        Scanner fscn = new Scanner(fis);
        //skip descriptor line
        if (!fscn.nextLine().equals("name,semester,year,credits"))
            throw new InputMismatchException("input file is not in the correct format");
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
        courses.clear();
        parser.close();
        //else return false;
        //get past descriptors
        fscn.nextLine();
        fscn.nextLine();
        //read in courses
        data = fscn.nextLine();
        //'--' will denote that there are no courses
        if (!data.equals("--")) {
            boolean first = true;
            while (first || fscn.hasNextLine()) {
                //we've already got the new line for the first iteration
                //but on later iterations we need to scan a new line
                if (first) first = false;
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
                update_times_per_day();
            }
            System.out.println(courses);
        }
        fscn.close();
    }

    public void load_daytimes(String data, List<DayTime> daytimes) {
        if (!data.isBlank()) {
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
        if (!data.isBlank()) {
            Scanner rb = new Scanner(data);
            rb.useDelimiter("-");
            while (rb.hasNext()) requiredby.add(Major.valueOf(rb.next()));
        }
    }

    public String show_attributes() {
        StringBuilder sb = new StringBuilder("name: ").append(name).append('\n').append("semester: ");
        sb.append(semester).append(" ").append(year).append("\ncredits: ").append(credits).append("\ncourses:\n");
        if(courses.isEmpty()) sb.append("\tNone");
        for(int i = 0; i < courses.size(); i++) {
            sb.append(i+1).append(".\n\t").append(courses.get(i)).append("\n\tcredits: ");
            sb.append(courses.get(i).getCredits()).append("\n\trequired by: ").append(courses.get(i).getRequiredby());
            //if not on last course, add a '\n'
            if (i < courses.size() - 1) sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Removes all courses from a schedule
     *
     * @return true if all courses are cleared, false if schedule is already empty
     */
    public boolean clear_schedule() {
        if (courses.isEmpty()) {
            Main.autoflush.println("Cannot clear empty schedule");
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

    /**
     * Method which prints out a visual representation of a schedule similar to Microsoft Outlook
     * NOTE: Method wasn't handling edge case course times well, so we decided to use a simpler print method for the MVP
     */
    public void printSchedule() {
        //Prints out the header for the schedule
        System.out.printf("%-13s", "");
        System.out.printf("%-13s", " Monday");
        System.out.printf("%-13s", " Tuesday");
        System.out.printf("%-13s", " Wednesday");
        System.out.printf("%-13s", " Thursday");
        System.out.printf("%-13s", " Friday");
        System.out.println();
        //Loops through each hour in a school day
        double hour = 8.0;
        for (int k = 0; k < 14; k++) {
            String time_of_day = "A.M.";
            int curr_day = 0;
            while (curr_day < 5) {
                double converted_hour = hour;
                if (hour >= 12) {
                    time_of_day = "P.M.";
                }
                if (converted_hour > 12) {
                    converted_hour -= 12;
                }
                if (curr_day == 0) {
                    System.out.printf("%12s", (int) converted_hour + ":00 " + time_of_day + "|");
                }
                //Loops through every course in the list of courses to check for printing
                String print_for_day = "";
                boolean printed = false;
                for (int i = 0; i < courses.size(); i++) {
                    //Creates loop to get all end times for T and R to evaluate for correct printing
                    Course curr = courses.get(i);
                    List<DayTime> current_times = curr.getTimes();
                    //Loops through all the times for each class to check the current position to print at
                    for (int j = 0; j < current_times.size(); j++) {
                        DayTime curr_dt = current_times.get(j);
                        char day = curr_dt.get_day();
                        double start_time = curr_dt.get_militarystart();
                        double end_time = curr_dt.get_militaryend();
                        String AM_PM = curr_dt.get_start_time();
                        String[] t = AM_PM.split(" ");
                        if (t[1].equals("AM")){
                            t[1] = "A.M.";
                        } else {
                            t[1] = "P.M.";
                        }
                        //Checks to see if the class time is in the morning or evening
                        boolean morning;
                        if (t[1].equals("A.M.")){
                            morning = true;
                        } else {
                            morning = false;
                        }
                        //Converts start_time from military time to decimal hour number
                        if (start_time >= 1300) {
                            start_time = start_time - 1200;
                        }
                        start_time = start_time / 100;
                        //Converts end_time from military time to decimal hour number
                        if (end_time >= 1300) {
                            end_time = end_time - 1200;
                        }
                        end_time = end_time / 100;
                        //Goes through each day for that hour
                        if (curr_day == 0) {
                            if (day == 'M' && (start_time == converted_hour)) {
                                if (morning && time_of_day.equals("A.M.")) {
                                    print_for_day += "| " + curr.getMajor() + " " + curr.getCourseNum() + " " + curr.getSection();
                                    printed = true;
                                } else if (!morning && time_of_day.equals("P.M.")) {
                                    print_for_day += "| " + curr.getMajor() + " " + curr.getCourseNum() + " " + curr.getSection();
                                    printed = true;
                                }
                            }
                        } else if (curr_day == 1) {
                            if (day == 'T' && (start_time == converted_hour || end_time-0.45 == converted_hour || (start_time - 0.05 == converted_hour)))  {
                                if (morning && time_of_day.equals("A.M.")) {
                                    print_for_day += "| " + curr.getMajor() + " " + curr.getCourseNum() + " " + curr.getSection()+" ";
                                    printed = true;
                                } else if (!morning && time_of_day.equals("P.M.")) {
                                    print_for_day += "| " + curr.getMajor() + " " + curr.getCourseNum() + " " + curr.getSection()+" ";
                                    printed = true;
                                }
                            }
                            else if (day == 'T' && (end_time-0.15 == converted_hour || start_time-0.3 == converted_hour || end_time - 0.2 == converted_hour)){
                                if (morning && time_of_day.equals("A.M.")) {
                                    print_for_day += "|------------";
                                    printed = true;
                                } else if (!morning && time_of_day.equals("P.M.")) {
                                    print_for_day += "|------------";
                                    printed = true;
                                }
                            }
                        } else if (curr_day == 2) {
                            if (day == 'W' && (start_time == converted_hour)) {
                                if (morning && time_of_day.equals("A.M.")) {
                                    print_for_day += "| " + curr.getMajor() + " " + curr.getCourseNum() + " " + curr.getSection();
                                    printed = true;
                                } else if (!morning && time_of_day.equals("P.M.")) {
                                    print_for_day += "| " + curr.getMajor() + " " + curr.getCourseNum() + " " + curr.getSection();
                                    printed = true;
                                }
                            }
                        } else if (curr_day == 3) {
                            if (day == 'R' && (start_time == converted_hour || end_time-0.45 == converted_hour || start_time - 0.05 == converted_hour)) {
                                if (morning && time_of_day.equals("A.M.")) {
                                    print_for_day += "| " + curr.getMajor() + " " + curr.getCourseNum() + " " + curr.getSection()+" ";
                                    printed = true;
                                } else if (!morning && time_of_day.equals("P.M.")) {
                                    print_for_day += "| " + curr.getMajor() + " " + curr.getCourseNum() + " " + curr.getSection()+" ";
                                    printed = true;
                                }
                            }
                            else if (day == 'R' && (end_time-0.15 == converted_hour || start_time-0.3 == converted_hour || end_time - 0.2 == converted_hour)){
                                if (morning && time_of_day.equals("A.M.")) {
                                    print_for_day += "|------------";
                                    printed = true;
                                } else if (!morning && time_of_day.equals("P.M.")) {
                                    print_for_day += "|------------";
                                    printed = true;
                                }
                            }
                        } else if (curr_day == 4) {
                            if (day == 'F' && (start_time == converted_hour)) {
                                if (morning && time_of_day.equals("A.M.")) {
                                    print_for_day += "| " + curr.getMajor() + " " + curr.getCourseNum() + " " + curr.getSection();
                                    printed = true;
                                } else if (!morning && time_of_day.equals("P.M.")) {
                                    print_for_day += "| " + curr.getMajor() + " " + curr.getCourseNum() + " " + curr.getSection();
                                    printed = true;
                                }
                            }
                        }
                    }
                }
                if (printed) {
                    System.out.printf("%-13s",print_for_day);
                }
                else {
                    System.out.print("|");
                    for (int i = 0; i < 12; i++){
                        System.out.print(" ");
                    }
                }
                curr_day++;
            }
            System.out.print("||");
            System.out.println();
            hour++;
        }
        System.out.println();
    }

    public boolean delete(String accountname) {
        File sched = new File("Accounts\\"+accountname+"\\"+name+".csv");
        return sched.delete();
    }
}
