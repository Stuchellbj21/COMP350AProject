import java.lang.StringBuilder;
import java.util.HashMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Course {
    private String name;

    private Set<String> id;

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
        id = new HashSet<>();
        populate_id();
    }

    private void populate_id() {
        add_time_strs_to_id();
        //make sure everything is uppercase so works with search
        Collections.addAll(id,days_to_str().toUpperCase(),String.valueOf(section).toUpperCase(),
                major.name().toUpperCase(),String.valueOf(coursenum).toUpperCase(),
                professor.split("\\s+")[1].toUpperCase(),String.valueOf(year).toUpperCase(),
                semester.toUpperCase());
        String[] names = name.split("\\s+");
        for (int i = 0; i < names.length; i++) names[i] = names[i].toUpperCase();
        Collections.addAll(id,names);
    }

    // Constructors
    public Course(){}

    public Set<String> get_id() {return id;}

    private void add_time_strs_to_id() {
        //first 2/1 char of starttime + whole starttime + starttime with no meridiem
        if(!times.isEmpty()) {
            String st = times.get(0).get_start_time();
            String et = times.get(0).get_end_time();
            //09:00 AM-12:00 PM MWF and 03:00 PM-06:00 PM R -> 9,9:00,9:00-12:00,9-12:00,9-12,9:00-12,AM
            add_times_and_ranges(st,et);
        }
    }

    private int get_start_idx(String time) {
        if(time.charAt(0) == '0') return 1;
        return 0;
    }

    private void add_short_strt_strs(String st, int startst, String et, int startet, String shortet) {
        if(st.startsWith("00",3)) {
            String shortst = st.substring(startst,2);
            id.add(shortst); // + '9'
            id.add(shortst + '-' + et.substring(startet,et.indexOf(' '))); // + '9-12:00
            if(shortet != null) id.add(shortst + '-' + shortet); // + '9-12'
        }
    }

    private void add_times_and_ranges(String st,String et) {
        //start for start index
        int startst = get_start_idx(st),startet = get_start_idx(et);
        String stwithoutm = get_without_meridiem(st,startst), etwithoutm = get_without_meridiem(et,startet);
        //time identifiers for 09:00 AM-12:00 PM MWF and 03:00 PM-06:00 PM R -> 9,9:00,9:00-12:00,9-12:00,9-12,9:00-12,AM
        id.add(stwithoutm); //add 9:00
        id.add(st.substring(st.length()-2).toUpperCase()); //add AM (uppercase for search)
        String shortet = null;
        if(et.startsWith("00",3)) shortet = et.substring(startet,2);
        //add '9' and range permutations that go with it as start
        add_short_strt_strs(st,startst,et,startet,shortet); // + '9', '9-12:00', '9-12'
        id.add(stwithoutm + '-' + etwithoutm); // + '9:00-12:00'
        if(shortet != null) id.add(stwithoutm + '-' + shortet); // + '9:00-12'
    }

    private String get_without_meridiem(String time,int start) {return time.substring(start,time.indexOf(' '));}

    private String days_to_str() {
        HashMap<Character,String> days = new HashMap<>();
        for(DayTime dt : times) days.put(dt.get_day(),String.valueOf(dt.get_day()).toUpperCase());
        StringBuilder sb = new StringBuilder();
        return sb.append(days.getOrDefault('M',"")).append(days.getOrDefault('T',"")).append(days.getOrDefault('W',"")).append(days.getOrDefault('R',"")).append(days.getOrDefault('F',"")).toString();
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
  
    public List<DayTime> getTimes() {
        return times;
    }
    
    public void setTimes(List<DayTime> times) {
        this.times = times;
    }

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
        sb.append(' ').append(year).append(": ").append(professor).append(" - ").append(major.name());
        sb.append(' ').append(coursenum).append(' ').append(section).append(" - ");
        sb.append(name).append(" - ").append(days_to_str()).append(" ");
        if(!times.isEmpty()) sb.append(times.get(0).get_start_time()).append(" - ").append(times.get(0).get_end_time());
        else sb.append("(no times listed)");
        return sb.append(" (").append(numstudents).append("/").append(capacity).append(')').toString();
    }
}