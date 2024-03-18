import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilterTest {
    @Test
    public void testCreditFilter() {

        //-----------------------------------------------------
        Set<Major> majors1 = new HashSet<>();
        majors1.add(Major.ACCT);
        Set<Major> majors2 = new HashSet<>();
        majors2.add(Major.PHIL);
        Set<Major> majors3 = new HashSet<>();
        majors3.add(Major.RELI);

        List<DayTime> daytimes1 = new ArrayList<>();
        daytimes1.add(new DayTime("9:00 AM","9:50 AM", 'M'));
        daytimes1.add(new DayTime("9:00 AM","9:50 AM", 'W'));
        daytimes1.add(new DayTime("9:00 AM","9:50 AM", 'F'));
        List<DayTime> daytimes2 = new ArrayList<>();
        daytimes2.add(new DayTime("9:30 AM","10:45 AM", 'T'));
        daytimes2.add(new DayTime("9:30 AM","10:45 AM", 'R'));

        Course c1 = new Course("PRINCIPLES OF ACCOUNTING I", 'A', Major.ACCT, 201, 3, 30, 30,
                "Jennifer", 2020, "Spring", majors1, daytimes2); //TR, ACCT
        Course c2 = new Course("INTRODUCTION TO ETHICS", 'A', Major.PHIL, 103, 2, 25, 30,
                "West", 2020, "Fall", majors2, daytimes1); // MWF, PHIL
        Course c3 = new Course("SHADOWS OF THE ANTICHRIST", 'A', Major.RELI, 301, 3, 15, 15,
                "Truman", 2020, "Spring", majors3, daytimes1); //MWF, RELI
        Course c4 = new Course("METAPHYSICS", 'A', Major.PHIL, 314, 2, 17, 20,
                "Franklin", 2020, "Spring", majors2, daytimes2); //TR, PHIL
        Course c5 = new Course("PRINCIPLES OF ACCOUNTING I", 'B', Major.ACCT, 201, 3, 25, 30,
                "Jennifer", 2020, "Spring", majors1, daytimes1); //MWF, ACCT

        List<Course> courses = new ArrayList<>();
        courses.add(c1);
        courses.add(c2);
        courses.add(c3);
        courses.add(c4);
        courses.add(c5);
        // --------------------------------------------------------

        System.out.println(courses);
        CreditFilter cf = new CreditFilter(courses, 2);
        System.out.println(courses);
    }
    @Test
    public void testDaysFilter() {

        //-----------------------------------------------------
        Set<Major> majors1 = new HashSet<>();
        majors1.add(Major.ACCT);
        Set<Major> majors2 = new HashSet<>();
        majors2.add(Major.PHIL);
        Set<Major> majors3 = new HashSet<>();
        majors3.add(Major.RELI);

        List<DayTime> daytimes1 = new ArrayList<>();
        daytimes1.add(new DayTime("9:00 AM", "9:50 AM", 'M'));
        daytimes1.add(new DayTime("9:00 AM", "9:50 AM", 'W'));
        daytimes1.add(new DayTime("9:00 AM", "9:50 AM", 'F'));
        List<DayTime> daytimes2 = new ArrayList<>();
        daytimes2.add(new DayTime("9:30 AM", "10:45 AM", 'T'));
        daytimes2.add(new DayTime("9:30 AM", "10:45 AM", 'R'));

        Course c1 = new Course("PRINCIPLES OF ACCOUNTING I", 'A', Major.ACCT, 201, 3, 30, 30,
                "Jennifer", 2020, "Spring", majors1, daytimes2); //TR, ACCT
        Course c2 = new Course("INTRODUCTION TO ETHICS", 'A', Major.PHIL, 103, 2, 25, 30,
                "West", 2020, "Fall", majors2, daytimes1); // MWF, PHIL
        Course c3 = new Course("SHADOWS OF THE ANTICHRIST", 'A', Major.RELI, 301, 3, 15, 15,
                "Truman", 2020, "Spring", majors3, daytimes1); //MWF, RELI
        Course c4 = new Course("METAPHYSICS", 'A', Major.PHIL, 314, 2, 17, 20,
                "Franklin", 2020, "Spring", majors2, daytimes2); //TR, PHIL
        Course c5 = new Course("PRINCIPLES OF ACCOUNTING I", 'B', Major.ACCT, 201, 3, 25, 30,
                "Jennifer", 2020, "Spring", majors1, daytimes1); //MWF, ACCT

        List<Course> expected = new ArrayList<>();
        expected.add(c2);
        expected.add(c3); // add these courses in order for test to work
        expected.add(c5); // Three classes on MWF

        List<Course> courses = new ArrayList<>();
        courses.add(c1);
        courses.add(c2);
        courses.add(c3);
        courses.add(c4);
        courses.add(c5);
        // --------------------------------------------------------

        Set<Character> days = new HashSet<>();
        days.add('M');
        days.add('W');
        days.add('F');

        DaysFilter df = new DaysFilter(courses, days);

        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.size(), courses.size());
            assertEquals(expected.get(i), courses.get(i));
        }
    }
    @Test
    public void testMajorFilter() {

        //-----------------------------------------------------
        Set<Major> majors1 = new HashSet<>();
        majors1.add(Major.ACCT);
        Set<Major> majors2 = new HashSet<>();
        majors2.add(Major.PHIL);
        Set<Major> majors3 = new HashSet<>();
        majors3.add(Major.RELI);

        List<DayTime> daytimes1 = new ArrayList<>();
        daytimes1.add(new DayTime("9:00 AM","9:50 AM", 'M'));
        daytimes1.add(new DayTime("9:00 AM","9:50 AM", 'W'));
        daytimes1.add(new DayTime("9:00 AM","9:50 AM", 'F'));
        List<DayTime> daytimes2 = new ArrayList<>();
        daytimes2.add(new DayTime("9:30 AM","10:45 AM", 'T'));
        daytimes2.add(new DayTime("9:30 AM","10:45 AM", 'R'));

        Course c1 = new Course("PRINCIPLES OF ACCOUNTING I", 'A', Major.ACCT, 201, 3, 30, 30,
                "Jennifer", 2020, "Spring", majors1, daytimes2); //TR, ACCT
        Course c2 = new Course("INTRODUCTION TO ETHICS", 'A', Major.PHIL, 103, 2, 25, 30,
                "West", 2020, "Fall", majors2, daytimes1); // MWF, PHIL
        Course c3 = new Course("SHADOWS OF THE ANTICHRIST", 'A', Major.RELI, 301, 3, 15, 15,
                "Truman", 2020, "Spring", majors3, daytimes1); //MWF, RELI
        Course c4 = new Course("METAPHYSICS", 'A', Major.PHIL, 314, 2, 17, 20,
                "Franklin", 2020, "Spring", majors2, daytimes2); //TR, PHIL
        Course c5 = new Course("PRINCIPLES OF ACCOUNTING I", 'B', Major.ACCT, 201, 3, 25, 30,
                "Jennifer", 2020, "Spring", majors1, daytimes1); //MWF, ACCT

        List<Course> courses = new ArrayList<>();
        courses.add(c1);
        courses.add(c2);
        courses.add(c3);
        courses.add(c4);
        courses.add(c5);
        // --------------------------------------------------------

        List<Course> expected = new ArrayList<>();
        expected.add(c1);
        expected.add(c5);

        MajorFilter mf = new MajorFilter(courses, Major.ACCT);

        assertEquals(expected.get(0), courses.get(0));
        assertEquals(expected.get(1), courses.get(1));
        assertEquals(expected.size(), courses.size());

    }
    @Test
    public void testNameFilter() {

        //-----------------------------------------------------
        Set<Major> majors1 = new HashSet<>();
        majors1.add(Major.ACCT);
        Set<Major> majors2 = new HashSet<>();
        majors2.add(Major.PHIL);
        Set<Major> majors3 = new HashSet<>();
        majors3.add(Major.RELI);

        List<DayTime> daytimes1 = new ArrayList<>();
        daytimes1.add(new DayTime("9:00 AM","9:50 AM", 'M'));
        daytimes1.add(new DayTime("9:00 AM","9:50 AM", 'W'));
        daytimes1.add(new DayTime("9:00 AM","9:50 AM", 'F'));
        List<DayTime> daytimes2 = new ArrayList<>();
        daytimes2.add(new DayTime("9:30 AM","10:45 AM", 'T'));
        daytimes2.add(new DayTime("9:30 AM","10:45 AM", 'R'));

        Course c1 = new Course("PRINCIPLES OF ACCOUNTING I", 'A', Major.ACCT, 201, 3, 30, 30,
                "Jennifer", 2020, "Spring", majors1, daytimes2); //TR, ACCT
        Course c2 = new Course("INTRODUCTION TO ETHICS", 'A', Major.PHIL, 103, 2, 25, 30,
                "West", 2020, "Fall", majors2, daytimes1); // MWF, PHIL
        Course c3 = new Course("SHADOWS OF THE ANTICHRIST", 'A', Major.RELI, 301, 3, 15, 15,
                "Truman", 2020, "Spring", majors3, daytimes1); //MWF, RELI
        Course c4 = new Course("METAPHYSICS", 'A', Major.PHIL, 314, 2, 17, 20,
                "Franklin", 2020, "Spring", majors2, daytimes2); //TR, PHIL
        Course c5 = new Course("PRINCIPLES OF ACCOUNTING I", 'B', Major.ACCT, 201, 3, 25, 30,
                "Jennifer", 2020, "Spring", majors1, daytimes1); //MWF, ACCT

        List<Course> courses = new ArrayList<>();
        courses.add(c1);
        courses.add(c2);
        courses.add(c3);
        courses.add(c4);
        courses.add(c5); // add all the courses

        List<Course> expected = new ArrayList<>();
        expected.add(c1);
        expected.add(c5); // add two courses with the same names but different days/imes
        // --------------------------------------------------------

        NameFilter nf = new NameFilter(courses, "PrincIples Of AcCounting I"); // make sure it's not case sensitive

        assertEquals(expected.get(0), courses.get(0));
        assertEquals(expected.get(1), courses.get(1));
        assertEquals(expected.size(), courses.size());

        NameFilter nf2 = new NameFilter(courses, "Metaphysics");

        assertEquals(0, courses.size()); // apply another filter with different name should empty the list
    }
    @Test
    public  void testProfessorFilter() {

        //-----------------------------------------------------
        Set<Major> majors1 = new HashSet<>();
        majors1.add(Major.ACCT);
        Set<Major> majors2 = new HashSet<>();
        majors2.add(Major.PHIL);
        Set<Major> majors3 = new HashSet<>();
        majors3.add(Major.RELI);

        List<DayTime> daytimes1 = new ArrayList<>();
        daytimes1.add(new DayTime("9:00 AM","9:50 AM", 'M'));
        daytimes1.add(new DayTime("9:00 AM","9:50 AM", 'W'));
        daytimes1.add(new DayTime("9:00 AM","9:50 AM", 'F'));
        List<DayTime> daytimes2 = new ArrayList<>();
        daytimes2.add(new DayTime("9:30 AM","10:45 AM", 'T'));
        daytimes2.add(new DayTime("9:30 AM","10:45 AM", 'R'));

        Course c1 = new Course("PRINCIPLES OF ACCOUNTING I", 'A', Major.ACCT, 201, 3, 30, 30,
                "Jennifer", 2020, "Spring", majors1, daytimes2); //TR, ACCT
        Course c2 = new Course("INTRODUCTION TO ETHICS", 'A', Major.PHIL, 103, 2, 25, 30,
                "West", 2020, "Fall", majors2, daytimes1); // MWF, PHIL
        Course c3 = new Course("SHADOWS OF THE ANTICHRIST", 'A', Major.RELI, 301, 3, 15, 15,
                "Truman", 2020, "Spring", majors3, daytimes1); //MWF, RELI
        Course c4 = new Course("METAPHYSICS", 'A', Major.PHIL, 314, 2, 17, 20,
                "Franklin", 2020, "Spring", majors2, daytimes2); //TR, PHIL
        Course c5 = new Course("PRINCIPLES OF ACCOUNTING I", 'B', Major.ACCT, 201, 3, 25, 30,
                "Jennifer", 2020, "Spring", majors1, daytimes1); //MWF, ACCT

        List<Course> courses = new ArrayList<>();
        courses.add(c1);
        courses.add(c2);
        courses.add(c3);
        courses.add(c4);
        courses.add(c5);
        // --------------------------------------------------------

        List<Course> expected = new ArrayList<>();
        expected.add(c1);
        expected.add(c5); // add courses with same professors but different

        ProfessorFilter pf = new ProfessorFilter(courses, "JeNNiFer"); //make sure it's not case-sensitive

        assertEquals(expected.get(0), courses.get(0));
        assertEquals(expected.get(1), courses.get(1));
        assertEquals(expected.size(), courses.size());

        ProfessorFilter pf2 = new ProfessorFilter(courses, "Franklin");
        assertEquals(0, courses.size()); // list should be empty if different professor is filtered
    }
    @Test
    public void testTimeFilter() {

        //-----------------------------------------------------
        Set<Major> majors1 = new HashSet<>();
        majors1.add(Major.ACCT);
        Set<Major> majors2 = new HashSet<>();
        majors2.add(Major.PHIL);
        Set<Major> majors3 = new HashSet<>();
        majors3.add(Major.RELI);

        List<DayTime> daytimes1 = new ArrayList<>();
        daytimes1.add(new DayTime("9:00 AM","9:50 AM", 'M'));
        daytimes1.add(new DayTime("9:00 AM","9:50 AM", 'W'));
        daytimes1.add(new DayTime("9:00 AM","9:50 AM", 'F'));
        List<DayTime> daytimes2 = new ArrayList<>();
        daytimes2.add(new DayTime("9:30 AM","10:45 AM", 'T'));
        daytimes2.add(new DayTime("9:30 AM","10:45 AM", 'R'));

        Course c1 = new Course("PRINCIPLES OF ACCOUNTING I", 'A', Major.ACCT, 201, 3, 30, 30,
                "Jennifer", 2020, "Spring", majors1, daytimes2); //TR, ACCT
        Course c2 = new Course("INTRODUCTION TO ETHICS", 'A', Major.PHIL, 103, 2, 25, 30,
                "West", 2020, "Fall", majors2, daytimes1); // MWF, PHIL
        Course c3 = new Course("SHADOWS OF THE ANTICHRIST", 'A', Major.RELI, 301, 3, 15, 15,
                "Truman", 2020, "Spring", majors3, daytimes1); //MWF, RELI
        Course c4 = new Course("METAPHYSICS", 'A', Major.PHIL, 314, 2, 17, 20,
                "Franklin", 2020, "Spring", majors2, daytimes2); //TR, PHIL
        Course c5 = new Course("PRINCIPLES OF ACCOUNTING I", 'B', Major.ACCT, 201, 3, 25, 30,
                "Jennifer", 2020, "Spring", majors1, daytimes1); //MWF, ACCT

        List<Course> courses = new ArrayList<>();
        courses.add(c1);
        courses.add(c2);
        courses.add(c3);
        courses.add(c4);
        courses.add(c5);
        // --------------------------------------------------------

        // make a daytime with a non-usable day ('S') to ensure filter is checking times, not  days
        DayTime dt = new DayTime("9:00 AM", "9:50 AM", 'S');

        List<Course> expected = new ArrayList<>();
        expected.add(c2);
        expected.add(c3);
        expected.add(c5); // add the three courses that start at 9:00

        System.out.println(expected.get(0).getTimes().getFirst().get_militarystart());
        TimeFilter tf = new TimeFilter(courses, dt);

        System.out.println("Expected: " + expected);
        System.out.println("Courses: " + courses);

        assertEquals(expected.get(0), courses.get(0));
        assertEquals(expected.get(1), courses.get(1));
        assertEquals(expected.get(2), courses.get(2));
        assertEquals(expected.size(), courses.size());

        DayTime dt2 = new DayTime("09:30 AM", "10:45 AM", 'S');

        TimeFilter tf2 = new TimeFilter(courses, dt2);

        assertEquals(0, courses.size()); //applying another filter should empty the course list
    }
    @Test
    public void testComboFilters1() {

        //-----------------------------------------------------
        Set<Major> majors1 = new HashSet<>();
        majors1.add(Major.ACCT);
        Set<Major> majors2 = new HashSet<>();
        majors2.add(Major.PHIL);
        Set<Major> majors3 = new HashSet<>();
        majors3.add(Major.RELI);

        List<DayTime> daytimes1 = new ArrayList<>();
        daytimes1.add(new DayTime("9:00 AM","9:50 AM", 'M'));
        daytimes1.add(new DayTime("9:00 AM","9:50 AM", 'W'));
        daytimes1.add(new DayTime("9:00 AM","9:50 AM", 'F'));
        List<DayTime> daytimes2 = new ArrayList<>();
        daytimes2.add(new DayTime("9:30 AM","10:45 AM", 'T'));
        daytimes2.add(new DayTime("9:30 AM","10:45 AM", 'R'));
        List<DayTime> daytimes3 = new ArrayList<>();
        daytimes3.add(new DayTime("11:00 AM", "12:15", 'T'));
        daytimes3.add(new DayTime("11:00 AM", "12:15", 'R'));
        List<DayTime> daytimes4 = new ArrayList<>();
        daytimes3.add(new DayTime("12:30 AM", "1:45", 'T'));
        daytimes3.add(new DayTime("12:30 AM", "1:45", 'R'));


        Course c1 = new Course("PRINCIPLES OF ACCOUNTING I", 'A', Major.ACCT, 201, 3, 30, 30,
                "Jennifer", 2020, "Spring", majors1, daytimes2); //TR, ACCT
        Course c2 = new Course("INTRODUCTION TO ETHICS", 'A', Major.PHIL, 103, 2, 25, 30,
                "West", 2020, "Fall", majors2, daytimes1); // MWF, PHIL
        Course c3 = new Course("SHADOWS OF THE ANTICHRIST", 'A', Major.RELI, 301, 3, 15, 15,
                "Truman", 2020, "Spring", majors3, daytimes1); //MWF, RELI
        Course c4 = new Course("METAPHYSICS", 'A', Major.PHIL, 314, 2, 17, 20,
                "Franklin", 2020, "Spring", majors2, daytimes2); //TR, PHIL
        Course c5 = new Course("PRINCIPLES OF ACCOUNTING I", 'B', Major.ACCT, 201, 3, 25, 30,
                "Jennifer", 2020, "Spring", majors1, daytimes1); //MWF, ACCT
        Course c6 = new Course("ADVANCED ACCOUNTING", 'A', Major.ACCT, 401, 3, 25, 30,
                "Gates", 2020, "Spring", majors1, daytimes3); //MWF, ACCT
        Course c7 = new Course("ADVANCED ACCOUNTING", 'B', Major.ACCT, 401, 3, 25, 30,
                "Gates", 2020, "Spring", majors1, daytimes4); //MWF, ACCT
        Course c8 = new Course("ADVANCED ACCOUNTING", 'C', Major.ACCT, 401, 3, 25, 30,
                "Hally", 2020, "Spring", majors1, daytimes4); //MWF, ACCT
        Course c9 = new Course("ADVANCED ACCOUNTING", 'C', Major.ACCT, 401, 2, 25, 30,
                "Hally", 2020, "Spring", majors1, daytimes4); //MWF, ACCT


        List<Course> courses = new ArrayList<>();
        courses.add(c1);
        courses.add(c2);
        courses.add(c3);
        courses.add(c4);
        courses.add(c5);
        courses.add(c6);
        courses.add(c7);
        courses.add(c8);
        courses.add(c9);
        // --------------------------------------------------------

        List<Course> expected = new ArrayList<>();
        expected.add(c9);

        //1.
        MajorFilter mf = new MajorFilter(courses, Major.ACCT); // Weeds out non-ACCT majors
        assertEquals(6, courses.size());

        //2.
        Set<Character> days = new HashSet<>();
        days.add('T');
        days.add('R');
        DaysFilter df = new DaysFilter(courses, days);  // Weeds out any courses not on T/R
        assertEquals(5, courses.size());

        //3.
        DayTime dt = new DayTime("12:30 PM", "1:45 PM", 'M');
        TimeFilter tf = new TimeFilter(courses, dt); // Should weed out any course not starting at 12:30
        assertEquals(3, courses.size());

        //4.
        ProfessorFilter pf = new ProfessorFilter(courses, "Hally"); // only courses taught by Hally should remain
        assertEquals(2, courses.size());

        //5.
        CreditFilter cf = new CreditFilter(courses, 2); // only 2 credit courses remain
        assertEquals(expected.getFirst(), courses.getFirst()); // should only be one left
        assertEquals(1, courses.size());
    }

}
