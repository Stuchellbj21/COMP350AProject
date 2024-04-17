import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
public class TestCourse {

    Course Course;
    Course Blank;
    @Test
    public void  gettersTest() throws Exception{
        // Create filled Course
        Course = new Course("Software Engineering", 'A', Major.COMP, 350, 3, 30, 30, "John Hutchins", 2024, "Spring");
        // Test gets for Course variables
        if(Course.getName().equals("Software Engineering")) { }else{throw new Exception("failed name get");}
        if(Course.getSection()!= 'A') { throw new Exception("failed section get");}
        if(Course.getMajor()!= Major.COMP) { throw new Exception("failed major get");}
        if(Course.getCourseNum()!= 350) { throw new Exception("failed courseNum get");}
        if(Course.getCredits()!= 3){ throw new Exception("failed credits get");}
        if(Course.getNumstudents()!= 30) { throw new Exception("failed numStudent get");}
        if(Course.getCapacity()!= 30) { throw new Exception("failed capacity get");}
        if(Course.getProfessor().equals("John Hutchins")){ }else{ throw new Exception ("failed professor get");}
        if(Course.getYear() != 2024){ throw new Exception ("failed year");}
        if(Course.getSemester().equals("Spring")){ }else{ throw new Exception("failed semester");}
    }
    @Test
    public void  settersTest() throws Exception{
        // Create filled Course
        Course = new Course("Software Engineering", 'A', Major.COMP, 350, 3, 30, 30, "John Hutchins", 2024, "Spring");
        // Create empty Course
        Course Blank = new Course();
        // Set Blank's variables to equal
        Blank.setName(Course.getName());    Blank.setSection(Course.getSection());    Blank.setMajor(Course.getMajor());
        Blank.setCourseNum(Course.getCourseNum());    Blank.setCredits(Course.getCredits());    Blank.setNumstudents(Course.getNumstudents());
        Blank.setCapacity(Course.getCapacity());    Blank.setProfessor(Course.getProfessor());    Blank.setYear(Course.getYear());   Blank.setSemester(Course.getSemester());
        // Compare now filled Blank to Course
        if(Blank.getName().equals("Software Engineering")) { }else{throw new Exception("failed name get");}
        if(Blank.getSection()!= 'A') { throw new Exception("failed section get");}
        if(Blank.getMajor()!= Major.COMP) { throw new Exception("failed major get");}
        if(Blank.getCourseNum()!= 350) { throw new Exception("failed courseNum get");}
        if(Blank.getCredits()!= 3){ throw new Exception("failed credits get");}
        if(Blank.getNumstudents()!= 30) { throw new Exception("failed numStudent get");}
        if(Blank.getCapacity()!= 30) { throw new Exception("failed capacity get");}
        if(Blank.getProfessor().equals("John Hutchins")){ }else{ throw new Exception ("failed professor get");}
        if(Blank.getYear() != 2024){ throw new Exception ("failed year");}
        if(Blank.getSemester().equals("Spring")){ }else{ throw new Exception("failed semester");}
    }

    @Test
    public void  equalsTest() throws Exception{
        // Create Equal Course
        Course other = new Course("Software Engineering", 'A', Major.COMP, 350, 3, 30, 30, "John Hutchins", 2024, "Spring");
        // Create Equal Course
        Course course = new Course("Software Engineering", 'A', Major.COMP, 350, 3, 30, 30, "John Hutchins", 2024, "Spring");
        // Compare get statements on two classes
        if(other.getMajor() != course.getMajor()){ throw new Exception("failed major comparison");}
        if(other.getCourseNum() != course.getCourseNum()){ throw new Exception("failed courseNum comparison");}
        if(other.getSection() != course.getSection()){ throw new Exception("failed Section comparison");}
        if(other.getSemester().equals(course.getSemester())){}else{ throw new Exception("failed Semester comparison");}
        if(other.getYear() != course.getYear()) { throw new Exception("failed Year Comparison");}
        }

    // To be completed after Nate touches up this method later this week
    @Test
    public void  overlapTest() throws Exception {
        Course = new Course();
    }
  
    @Test
    public void toStringTest() {
        ArrayList<DayTime> dts = new ArrayList<>();
        dts.add(new DayTime("8:50 AM","9:40 AM",'M'));
        dts.add(new DayTime("8:50 AM","9:40 AM",'W'));
        dts.add(new DayTime("8:50 AM","9:40 AM",'R'));
        dts.add(new DayTime("8:50 AM","9:40 AM",'F'));
        Course c = new Course("Imitation Game",'A',Major.COMP,477,3,3,20,"Willard Wongleton",2077,"Fall",null,dts);
        System.out.println(c);
    }

    @Test
    public void idTest() {
        ArrayList<DayTime> times = new ArrayList<>();
        times.add(new DayTime("09:00 AM","12:00 PM",'M'));
        times.add(new DayTime("09:00 AM","12:00 PM",'W'));
        times.add(new DayTime("09:00 AM","12:00 PM",'F'));
        times.add(new DayTime("03:00 PM","06:00 PM",'R'));
        Course c = new Course("Enrichment of the Mentality Complex",'B',Major.ACCT,820,3,10,10,"Greg Bilbod",2050,"Fall",null,times);
        System.out.println(c.get_id());
    }
}
