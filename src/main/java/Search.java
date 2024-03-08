import java.util.List;
import java.util.Set;

public class Search {
    private List<Course> searchresults;

    private List<Course> filteredresults; //this way don't have to do new search when add filter

    private Set<Filter> activefilters;

    private String searchstr;

    //getters + setters yet to be added

    public List<Course> search(String ss) {return null;} //sets searchstr to ss and performs a new search on searchstr and gives results

    public List<Course> activate_new_filter(Filter f) {return null;}

    /*
    there will only be 1 filter of a given type active at a certain time, so we may want to modify the
    time filter (for example) which will not simply change the filteredresults, but will have to get
    filteredresults from the original results over again
     */
    public List<Course> modify_filter(Filter f) {return null;}

    //removes a filter and does something similar to modify_filter()
    public List<Course> deactivate_filter(Filter f) {return null;}
}
