import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SearchTest {
    @Test
    public void testSearch() {
        try{Main.populate_allcourses();}
        catch(IOException ioe) {System.out.println(ioe.getMessage() + " " + ioe.getCause());}
        Search s = new Search();
        //System.out.println(s.search(""));
        s.search("comp 220");
        System.out.println(s.to_str(10));
        s.search("comp 222 A");
        System.out.println(s.to_str(10));
        s.search("MWF 2-2:50");
        System.out.println(s.to_str(10));
        s.search("M");
        System.out.println(s.to_str(10));
    }

    @Test
    public void testGetWeight() {
        try{Main.populate_allcourses();}
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
}
