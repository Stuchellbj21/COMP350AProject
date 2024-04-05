public enum FilterType {
    CREDIT,TIME,DAYS,PROFESSOR,NAME,MAJOR,SEMESTER;

    public static boolean is_filter_type(String s) {
        try {
            FilterType.valueOf(s);
            return true;
        }
        catch(IllegalArgumentException iae) {return false;}
    }
}
