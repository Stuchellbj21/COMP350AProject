import java.util.Set;

public class Course {
    private String name;

    private char section;

    private Major coursecode;

    private int coursenum; //3 digit number representing which course (350 in COMP 350)

    private int credits;

    private int numstudents;

    private int capacity;

    private String professor;

    private Set<Major> requiredby; //a set of all majors that require taking this course

    private Set<DayTime> times;

    //getters + setters yet to be added

    public boolean time_overlaps_with(Course other) {return false;}

    public boolean equals(Course other) {return false;}
}
