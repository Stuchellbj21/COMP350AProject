import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DayTimeTest {
    @Test
    public void test_str_format() {
        assertEquals(String.format("%8s","9:00 AM").replaceFirst(" ","0"),"09:00 AM");
    }

    @Test
    public void test_str_replace() {
        assertEquals("09:00".replace(":",""),"0900");
    }

    @Test
    public void test_put_in_correct_format() {
        DayTime d = new DayTime();
        assertEquals(d.put_in_correct_format("8:00 AM"),"08:00 AM");
        assertEquals(d.put_in_correct_format("10:00 PM"),"10:00 PM");
        assertEquals(d.put_in_correct_format("7:45 PM"),"07:45 PM");
        assertEquals(d.put_in_correct_format("6:17 AM"),"06:17 AM");
    }

    @Test
    public void test_to_military_time() {
        DayTime d = new DayTime();
        assertEquals(d.to_military_time("9:00 AM"),900);
        assertEquals(d.to_military_time("10:00 PM"),2200);
        assertEquals(d.to_military_time("11:16 AM"),1116);
        assertEquals(d.to_military_time("5:17 PM"),1717);
    }
}
