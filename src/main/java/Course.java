import java.util.Set;

public class Course {
    private String name;

    private char section;

    private CourseCode coursecode;

    private int credits;

    private int numstudents;

    private int capacity;

    private String professor;

    private Set<CourseCode> requiredby; //a set of all majors that require taking this course

    private Set<DayTime> times;

    //getters + setters yet to be added

    public boolean time_overlaps_with(Course other) {return false;}

    public boolean equals(Course other) {return false;}
}
