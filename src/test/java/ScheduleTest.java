import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
public class ScheduleTest {
    @Test
    public void test_defaultConstructor() {
        // lol, do I even need this?
        Schedule s1 = new Schedule();
        assertNotNull(s1);
    }
    @Test
    public void test_equals() {
        Schedule s1 = new Schedule("Test", "Spring", 2024, 15);
        Schedule s2 = new Schedule("Test", "Spring", 2024, 15);

        assertEquals(s1.get_name(), s2.get_name());
        assertEquals(s1.get_semester(), s2.get_semester());
        assertEquals(s1.get_year(), s2.get_year());
        assertEquals(s1.get_courses(), s2.get_courses());
        assertEquals(s1.get_credits(), s2.get_credits());
        assertEquals(s1, s2);
        // does assertEquals use my overwritten equals() method?
        // if so, this test can be erased
    }
    @Test
    public void test_addCourse() {
        Schedule s = new Schedule();
        for (int i = 0; i < 5; i++) {
            s.add_course(new Course());
        }
        // make sure list has correct number of courses
        assertEquals(5, s.get_courses().size());
    }
    @Test
    public void test_removeCourse() {
        Schedule s = new Schedule();
        for (int i = 0; i < 3; i++) { // add three courses
            s.add_course(new Course()); // for now, generic courses are added
        }

        for (int i = 0; i < 2; i++) { // remove two courses
            s.remove_course(s.get_courses().getFirst());
        }
        // only two have been removed so far
        assertNotEquals(3, s.get_courses().size());

        // make sure all elements are removed
        s.remove_course(s.get_courses().getFirst());
        assertEquals(0, s.get_courses().size());
    }

    @Test
    public void test_load() {
        Schedule s = new Schedule("NateDog(0_0)","Schedule1");
        System.out.println(s.show_attributes());
        Schedule s2 = new Schedule("NateDog(0_0)","Schedule2");
        System.out.println(s2.show_attributes());
    }

    @Test
    public void test_save() {
        try{Main.populate_allcourses();}
        catch(IOException ioe) {System.out.println(ioe.getMessage() + " " + ioe.getCause());}
        Schedule s = new Schedule("NateDog(0_0)","Schedule1");
        s.remove_course(s.get_courses().getFirst());
        s.remove_course(s.get_courses().getFirst());
        s.add_course(Main.allcourses.getFirst());
        s.add_course(Main.allcourses.getLast());
        try{s.save("NateDog(0_0)");}
        catch(IOException ioe) {System.out.println(ioe.getMessage() + " " + ioe.getCause());}
    }
}
