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
        Course = new Course("Software Engineering", 'A', Major.COMP, 350, 3, 30, 30, "John Hutchins", 2024, "Spring");
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
        Course = new Course("Software Engineering", 'A', Major.COMP, 350, 3, 30, 30, "John Hutchins", 2024, "Spring");
        Course Blank = new Course();
        Blank.setName(Course.getName());    Blank.setSection(Course.getSection());    Blank.setMajor(Course.getMajor());
        Blank.setCourseNum(Course.getCourseNum());    Blank.setCredits(Course.getCredits());    Blank.setNumstudents(Course.getNumstudents());
        Blank.setCapacity(Course.getCapacity());    Blank.setProfessor(Course.getProfessor());    Blank.setYear(Course.getYear());   Blank.setSemester(Course.getSemester());
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
        Course other = new Course("Software Engineering", 'A', Major.COMP, 350, 3, 30, 30, "John Hutchins", 2024, "Spring");
        Course course = new Course("Software Engineering", 'A', Major.COMP, 350, 3, 30, 30, "John Hutchins", 2024, "Spring");
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

}
