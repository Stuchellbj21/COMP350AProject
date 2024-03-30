import java.util.Arrays;
import java.util.Objects;

public class DayTime implements Comparable<DayTime> {

    private char day;

    private String starttime;

    private String endtime;

    //4 digit int 1200 = 12:00 pm
    private int militarystart;

    private int militaryend; //could represent 12:45 AM as 0045 and 3:50 PM as 1550.... 11:59 PM = 2359

    public DayTime(String st, String et, char d) {
        starttime = put_in_correct_format(st);
        endtime = put_in_correct_format(et);
        militarystart = to_military_time(starttime);
        militaryend = to_military_time(endtime);
        day = Character.toUpperCase(d); //day is not initialized in this constructor
    }

    public DayTime() {
        starttime = "08:00 AM";
        endtime = "08:50 AM";
        militarystart = 800;
        militaryend = 850;
        day = 'M';
    }

    public DayTime(char d) {
        starttime = "08:00 AM";
        endtime = "08:50 AM";
        militarystart = 800;
        militaryend = 850;
        day = d;
    }

    public DayTime(String st, String et) {
        //start and end time are now in XX:XX PM/AM format
        starttime = put_in_correct_format(st);
        endtime = put_in_correct_format(et);
        militarystart = to_military_time(starttime);
        militaryend = to_military_time(endtime);
        day = '_'; //day is not initialized in this constructor
    }

    public String put_in_correct_format(String t) {
        if(t.length() != 8) return String.format("%8s",t).replaceFirst(" ","0");
        return t;
    }

    public void set_day(char day) {this.day = day;}

    public char get_day() {return day;}

    public int get_militarystart() {return militarystart;}

    public int get_militaryend() {return militaryend;}

    public String get_start_time() {return starttime;}

    public String get_end_time() {return endtime;}

    public void set_start(String s) {
        starttime = put_in_correct_format(s);
        militarystart = to_military_time(starttime);
    }

    public void set_end(String e) {
        endtime = put_in_correct_format(e);
        militaryend = to_military_time(endtime);
    }

    public int to_military_time(String time) {
        //time is something like 9:00 AM
        String[] s = time.split(" ");
        //time = s[0];
        //meridiem = s[1];
        int r = Integer.parseInt(s[0].replace(":",""));
        //assuming there will never be a time of 12:XX AM, the below line should account for that though
        if(r >= 1200 && s[1].equalsIgnoreCase("am")) return r - 1200;
        if(s[1].equalsIgnoreCase("am")) return r;
        //if time is 12:XX PM, don't add 1200
        if(r >= 1200) return r;
        //if time is 1:00 PM or later, add 1200
        return r + 1200;
    }

    public boolean same_time(DayTime other) {return this.militarystart == other.militarystart && this.militaryend == other.militaryend;}

    public boolean same_day(DayTime other) {return this.day == other.day;}

    public boolean same_day(char other) {return this.day == other;} //specify day using char instead of full DayTime

    //should simply check sameday and sametime (want it to work this way so that our Sets can
    //be used effectively)  |  Set contains uses .equals()
    @Override
    public boolean equals(Object other) {
        if(other == null) return false;
        if(!(other instanceof DayTime o)) return false;
        return same_time(o) && same_day(o);
    }

    public boolean overlaps(DayTime other) {
        return day == other.day && ((this.militarystart >= other.militarystart && this.militarystart <= other.militaryend) || (this.militaryend >= other.militarystart && this.militaryend <= other.militaryend));
    }

    @Override
    public int compareTo(DayTime other) {return this.militarystart - other.militarystart;}

    //returns the number of minutes denoted by the military integer given (1250 = 720 + 50 = 770, 1300 = 780 + 0)
    public static int military_to_minutes(int military) {return (military / 100) * 60 + (military % 100);}

    //use military start, military end, and day so that hashcode will match equals
    @Override
    public int hashCode() {return Arrays.hashCode(new int[] {militarystart,militaryend,(int)day});}
}