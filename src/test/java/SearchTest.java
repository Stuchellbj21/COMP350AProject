import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SearchTest {
    @Test
    public void testSearch() {
        try{SaveLoad.load_allcourses();}
        catch(IOException ioe) {System.out.println(ioe.getMessage() + " " + ioe.getCause());}
        Search s = new Search();
        //System.out.println(s.search(""));
        s.search("comp 220");
        System.out.println(s.to_str(true));
        s.search("comp 222 A");
        System.out.println(s.to_str(true));
        s.search("MWF 2-2:50");
        System.out.println(s.to_str(true));
        s.search("M");
        System.out.println(s.to_str(true));
    }

    @Test
    public void testGetWeight() {
        try{SaveLoad.load_allcourses();}
        catch(IOException ioe) {System.out.println(ioe.getMessage() + " " + ioe.getCause());}
        List<DayTime> dts = new ArrayList<>();
        dts.add(new DayTime("9:00 AM","9:50 AM",'f'));
        dts.add(new DayTime("9:00 AM","9:50 AM",'m'));
        dts.add(new DayTime("9:00 AM","9:50 AM",'w'));
        Course c = new Course("computer architecture/org",'a',Major.COMP,325,3,23,25,"David valentine",2020,"Fall",null,dts);
        System.out.println(c.get_id());
        Search s = new Search("computer architecture/org a comp 325 david valentine 2020 fall mwf 9:00-9:50 AM");
        assertEquals(2+2+1+4+3+2+2+2+2+2+2,s.get_weight(c));
    }

    @Test
    public void testApplyAllFilters() {
        try{SaveLoad.load_allcourses();}
        catch(IOException ioe) {System.out.println(ioe.getMessage() + " " + ioe.getCause());}
        Search s = new Search("writ 101 c");
        System.out.println(s.to_str(true));
        s.activate_new_filter(new DaysFilter(s.get_filtered_results(), Set.of('W','M','F')));
        System.out.println(s.to_str(true));
    }

    @Test
    public void test_partial_matching() throws IOException {
        SaveLoad.load_allcourses();
        //TODO: work with this and see what is going on with weights
        Search s = new Search("no time");
        s.search("astr 207 c no time",10,true);
        Course c = s.get_filtered_results().getFirst();
        System.out.println(c);
        System.out.println(c.toString().length());
        System.out.println(s.get_weight(c));
        /*for(Course c : s.get_filtered_results()) {
            System.out.println(c);
            System.out.println(c.toString().length());
            System.out.println(s.get_weight(c));
        }*/
    }
}