import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.*;

public class Course {
    private String name;
    private char section;
    private Major major;
    private int coursenum; //3 digit number representing which course (350 in COMP 350)
    private int credits;

    private int numstudents;

    private int capacity;

    private String professor;

    private int year;

    private String semester;

    private Set<Major> requiredby; //a set of all majors that require taking this course

    private List<DayTime> times;

    public Course(String name,char section,Major major,int coursenum,int credits,int numstudents,int capacity,String professor,int year,String semester,Set<Major> requiredby,List<DayTime> times) {
        this.name = name;
        this.section = section;
        this.major = major;
        this.coursenum = coursenum;
        this.credits = credits;
        this.numstudents = numstudents;
        this.capacity = capacity;
        this.professor = professor;
        this.year = year;
        this.semester = semester;
        this.requiredby = requiredby;
        this.times = times;
    }

    // Constructors
    public Course(){

    }

    public Course(String name, char section, Major major, int courseNum,
                  int credits, int numstudents, int capacity, String professor,
                  int year, String semester){
        this.name = name;     this.section = section;      this.major = major;
        this.coursenum = courseNum;     this.credits = credits;
        this.numstudents = numstudents;     this.capacity = capacity;
        this.professor = professor;      this.year = year;     this.semester = semester;
    }

    //getters + setters yet to be added
    public String getName(){ return name;}
    public void setName(String name){ this.name = name;}
    public char getSection(){ return section;}
    public void setSection(char section){ this.section = section;}
    public Major getMajor(){ return major;}
    public void setMajor(Major major){ this.major = major;}
    public int getCourseNum(){ return coursenum;}
    public void setCourseNum(int coursenum){ this.coursenum = coursenum;}
    public int getCredits(){ return credits;}
    public void setCredits(int credits){ this.credits = credits;}
    public int getNumstudents(){ return numstudents;}
    public void setNumstudents(int numstudents){ this.numstudents = numstudents;}
    public int getCapacity(){ return capacity;}
    public void setCapacity(int capacity){ this.capacity = capacity;}
    public String getProfessor(){ return professor;}
    public void setProfessor(String professor){ this.professor = professor;}

    public int getYear(){ return year;}
    public void setYear(int year){ this.year = year;}
    public String getSemester(){ return semester;}
    public void setSemester(String semester){ this.semester = semester;}

    public Set<Major> getRequiredby() {return requiredby;}

    // this checks two classes to determine if time is the same, later will be accessed in search and schedule to prevent
    public boolean times_overlap_with(Course other) {
        //do I have to do an n^2? It seems like it
        for(DayTime thisdt : this.times) {
            for(DayTime otherdt : other.times) if(thisdt.equals(otherdt) || thisdt.overlaps(otherdt)) return true;
        }
        return false;
    }

    // determines if two courses are the same course
    public boolean equals(Course other) {
        return (other.section == this.section && other.major == this.major && other.coursenum == this.coursenum
        && other.year == this.year && other.semester.equals(this.semester));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(semester);
        HashMap<Character,String> days = new HashMap<>();
        for(DayTime dt : times) days.put(dt.get_day(),String.valueOf(dt.get_day()));
        sb.append(' ').append(year).append(": ").append(major.name()).append(' ').append(coursenum).append(' ').append(section).append(" - ").append(name).append(" - ");
        sb.append(days.getOrDefault('M',"")).append(days.getOrDefault('T',"")).append(days.getOrDefault('W',"")).append(days.getOrDefault('R',"")).append(days.getOrDefault('F',"")).append(" ");
        if(!times.isEmpty()) sb.append(times.get(0).get_start_time()).append(" - ").append(times.get(0).get_end_time());
        else sb.append("(no times listed)");
        return sb.append(' ').append(numstudents).append("/").append(capacity).toString();
    }
}
