public class DayTime {

    private char day;

    private String starttime;

    private String endtime;

    //4 digit int 1200 = 12:00 pm
    private int militarystart;

    private int militaryend; //could represent 12:45 AM as 0045 and 3:50 PM as 1550.... 11:59 PM = 2359

    //getters + setters yet to be added

    //TODO: implement the following functions
    public boolean same_time(DayTime other) {return false;}

    public boolean same_day(DayTime other) {return false;}

    public boolean same_day(char other) {return false;} //specify day using char instead of full DayTime

    //should simply check sameday and sametime (want it to work this way so that our Sets can
    //be used effectively)  |  Set contains uses .equals()
    public boolean equals(DayTime other) {return false;}

    public boolean overlaps(DayTime other) {return false;}

    @Override
    public int hashCode() {return super.hashCode();}
}
