public class DayTime {

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

    //TODO: put getters + setters if necessary

    public int to_military_time(String time) {
        //time is something like 9:00 AM
        String[] s = time.split(" ");
        //time = s[0];
        //meridiem = s[1];
        int r = Integer.parseInt(s[0].replace(":",""));
        //assuming there will never be a time of 12:00 AM
        if(s[1].equalsIgnoreCase("am")) return r;
        return r + 1200;
    }

    public boolean same_time(DayTime other) {return this.militarystart == other.militarystart && this.militaryend == other.militaryend;}

    public boolean same_day(DayTime other) {return this.day == other.day;}

    public boolean same_day(char other) {return this.day == other;} //specify day using char instead of full DayTime

    //should simply check sameday and sametime (want it to work this way so that our Sets can
    //be used effectively)  |  Set contains uses .equals()
    public boolean equals(DayTime other) {
        return same_time(other) && same_day(other);
    }

    public boolean overlaps(DayTime other) {
        return day == other.day && ((this.militarystart >= other.militarystart && this.militarystart <= other.militaryend) || (this.militaryend >= other.militarystart && this.militaryend <= other.militaryend));
    }

    @Override
    public int hashCode() {return super.hashCode();}
}