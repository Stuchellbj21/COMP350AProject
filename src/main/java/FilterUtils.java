import java.util.Set;
import java.util.HashSet;

public class FilterUtils {
    public static void edit_filters() {
        while(true) {
            Main.afl.println("Active Filters: " + (Main.search.get_active_filters() != null && !Main.search.get_active_filters().isEmpty() ? Main.search.get_active_filters() : "None"));
            String command = GeneralUtils.input("(a) -> add filter/(m) -> modify filter/(d) -> delete filter/(done) -> end filter editing: ");
            if(command.equalsIgnoreCase("a")) add_filter(false,null);
            else if(command.equalsIgnoreCase("m")) modify_filter();
            else if(command.equalsIgnoreCase("d")) delete_filter();
            else if(command.equalsIgnoreCase("done")) break;
            else Main.afl.println("Error: '" + command + "' not recognized");
        }
    }

    //this method can be used for filter modification instead of pure filter addition.... in the case
    //where it is used for modification, modify arg should be true and tomod should not be null
    public static void add_filter(boolean modify, FilterType tomod) {
        //holds the original size of the active filters for comparison at the end
        int ogsize = Main.search.get_active_filters().size();
        //by default we use the tomod filter type
        FilterType ft = tomod;
        //if we're not modifying then we use a filter type specified by user
        if(!modify) ft = get_filter_type("add");
        //we only want this statement to fire in the addition case, so if not modify
        if(!modify && Main.search.get_active_filters().contains(new Filter(ft))) Main.afl.println("Error: a " + ft.name().toLowerCase() + " filter is already active");
        else {
            switch(ft) {
                case DAYS -> {
                    Set<Character> days = get_days_for_filter(modify);
                    if(days != null) add_or_modify_filter(modify,new DaysFilter(Main.search.get_filtered_results(),days));
                }
                case TIME -> {
                    DayTime time = GeneralUtils.get_time_for_something(modify);
                    if(time != null) add_or_modify_filter(modify,new TimeFilter(Main.search.get_filtered_results(),time));
                }
                case SEMESTER -> {
                    String[] semyear = get_semester_for_filter(modify);
                    if(semyear != null) add_or_modify_filter(modify,new SemesterFilter(Main.search.get_filtered_results(),semyear[0],Integer.parseInt(semyear[1])));
                }
                case NAME -> add_or_modify_filter(modify,new NameFilter(Main.search.get_filtered_results(),GeneralUtils.input("Enter the course name you would like to filter on: ")));
                case MAJOR -> {
                    Major m = get_major_for_filter(modify);
                    if(m != null) add_or_modify_filter(modify,new MajorFilter(Main.search.get_filtered_results(),m));
                }
                case CREDIT -> {
                    Integer i = get_credits_for_filter(modify);
                    if(i != null) add_or_modify_filter(modify,new CreditFilter(Main.search.get_filtered_results(),i));
                }
                case PROFESSOR -> add_or_modify_filter(modify,new ProfessorFilter(Main.search.get_filtered_results(),GeneralUtils.input("Enter the name of the professor (in form '<first_name> <last_name>') you would like to filter on: ")));
                //can only add or remove FULL filter
                case OPEN -> {
                    if(modify) Main.afl.println("Error: cannot modify OPEN filter");
                    else add_or_modify_filter(modify,new OpenFilter(Main.search.get_filtered_results()));
                }
            }
            if(!modify && Main.search.get_active_filters().size() != ogsize) Main.afl.println("Filter addition successful");
        }
    }

    public static void add_or_modify_filter(boolean modify, Filter f) {
        if(!modify) Main.search.activate_new_filter(f);
        else Main.search.modify_filter(f);
    }

    public static Integer get_credits_for_filter(boolean modify) {
        //i for int
        String i;
        boolean first = true;
        do {
            if(first) first = false;
            else Main.afl.println("Error: invalid credit value");
            if(!filter_move_forward(modify,FilterType.CREDIT)) return null;
            //setting i equal to input here
        } while(!GeneralUtils.is_numeric(i = GeneralUtils.input("Enter number of credits to filter on: ")) || Integer.parseInt(i) < 0);
        //now i is a string representing a valid Integer
        return Integer.parseInt(i);
    }

    public static Major get_major_for_filter(boolean modify) {
        String m;
        boolean first = true;
        do {
            if(first) first = false;
            else Main.afl.println("Error: invalid major value");
            if(!filter_move_forward(modify,FilterType.MAJOR)) return null;
            //setting m equal to input here
        } while(!Major.is_major(m = GeneralUtils.input("Enter major to filter on: ").toUpperCase()));
        //now m is a string representing a valid major
        return Major.valueOf(m);
    }

    public static String[] get_semester_for_filter(boolean modify) {
        while(true) {
            if(!filter_move_forward(modify,FilterType.SEMESTER)) return null;
            String[] semyear = GeneralUtils.input("Enter semester in the form 'Spring/Fall XXXX' where X is a digit: ").split("\\s+");
            if(!GeneralUtils.get_semester_formatted(semyear) || !Validations.valid_semester(semyear) || !Validations.valid_year(semyear)) continue;
            return semyear;
        }
    }

    public static boolean filter_move_forward(boolean modify,FilterType filtertype) {
        while(true) {
            String moveforward = GeneralUtils.input("Would you like to " + (modify ? "modify the " : "add a ") + filtertype.name().toLowerCase() + " filter? (y/n) ");
            if (moveforward.equalsIgnoreCase("n") || moveforward.equalsIgnoreCase("no")) return false;
            //if not yes or no: error
            if (!moveforward.equalsIgnoreCase("") && !moveforward.equalsIgnoreCase("yes") &&
                    !moveforward.equalsIgnoreCase("y")) {
                Main.afl.println("Error: invalid input");
                continue;
            }
            //if yes return true
            return true;
        }
    }

    public static Set<Character> get_days_for_filter(boolean modify) {
        String[] days;
        while(true) {
            if(!filter_move_forward(modify,FilterType.DAYS)) return null;
            Main.afl.println("Valid Days: M -> Monday, T -> Tuesday, W -> Wednesday, R -> Thursday, F -> Friday");
            days = GeneralUtils.input("Enter whitespace separated characters (see above) for days you would like to filter on: ").toUpperCase().split("\\s+");
            if(days.length < 1) {
                Main.afl.println("Error: no days entered");
                continue;
            }
            boolean error = false;
            for(String d : days) {
                //if day is too long or incorrect: error
                if(d.length() > 1 || (!d.equalsIgnoreCase("M") && !d.equalsIgnoreCase("T") &&
                        !d.equalsIgnoreCase("W") && !d.equalsIgnoreCase("R") &&
                        !d.equalsIgnoreCase("F"))) {
                    Main.afl.println("Error: '" + d + "' is not a valid day (M,T,W,R,F)");
                    error = true;
                    break;
                }
            }
            if(!error) break;
        }
        //r for return (this is the set containing the days)
        Set<Character> r = new HashSet<>();
        for(String s : days) r.add(s.charAt(0));
        return r;
    }

    public static void modify_filter() {
        if(Main.search.get_active_filters() == null || Main.search.get_active_filters().isEmpty()) {
            Main.afl.println("Error: there are no filters to modify");
            return;
        }
        FilterType ft = get_filter_type("modify");
        if(!Main.search.get_active_filters().contains(new Filter(ft))) Main.afl.println("Error: there is no " + ft.name().toLowerCase() + " filter active");
        else {
            add_filter(true,ft);
            Main.afl.println("Filter modification process successful");
        }
    }

    public static void delete_filter() {
        if(Main.search.get_active_filters() == null || Main.search.get_active_filters().isEmpty()) {
            Main.afl.println("Error: there are no filters to delete");
            return;
        }
        FilterType ft = get_filter_type("delete");
        if(!Main.search.get_active_filters().contains(new Filter(ft))) Main.afl.println("Error: there is no " + ft.name().toLowerCase() + " filter active");
        else {
            Main.search.deactivate_filter(new Filter(ft));
            Main.afl.println("Filter deletion successful");
        }
    }

    public static FilterType get_filter_type(String operation) {
        Main.afl.println("Filter types: credit,time,days,professor,name (course name),major,semester,open");
        boolean first = true;
        String ft = "";
        do {
            if(first) first = false;
            else Main.afl.println("Error: '" + ft + "' is not a valid filter type");
            //ft for filter type
            ft = GeneralUtils.input("Enter filter type to " + operation + ": ").toUpperCase();
        } while(!FilterType.is_filter_type(ft));
        //once we have a valid filter type, return it
        return FilterType.valueOf(ft);
    }
}
