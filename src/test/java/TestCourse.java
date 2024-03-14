import java.util.ArrayList;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestCourse {
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
}
