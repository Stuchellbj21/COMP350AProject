import java.util.*;

public class Search {
    private List<Course> searchresults;

    private List<Course> filteredresults; //this way don't have to do new search when add filter

    private Set<Filter> activefilters;

    private String searchstr;

    private List<String> search_str_list;

    Search() {
        searchstr = "";
        search_str_list = new ArrayList<>();
        activefilters = new HashSet<>();
        filteredresults = new ArrayList<>();
        searchresults = new ArrayList<>();
    }

    Search(String ss) {
        set_search_str(ss);
        activefilters = new HashSet<>();
        filteredresults = new ArrayList<>();
        searchresults = new ArrayList<>();
    }

    //getters + setters yet to be added

    //threshold specifies the limit on the number of Courses given to search results
    public List<Course> search(String ss,int threshold) {
        //sets searchstr to ss and performs a new search on searchstr and gives results
        set_search_str(ss);
        HashMap<Course,Integer> coursetoweight = new HashMap<>();
        for(Course c : Main.allcourses) coursetoweight.put(c,get_weight(c)); //now we have weighted courses
        TreeMap<Integer,List<Course>> weighttocourse = new TreeMap<>();
        for(Course c : coursetoweight.keySet())
            if(coursetoweight.get(c) > 0) {
                if(weighttocourse.get(coursetoweight.get(c)) == null) weighttocourse.put(coursetoweight.get(c),new ArrayList<>());
                weighttocourse.get(coursetoweight.get(c)).add(c);
            }
        searchresults.clear();
        int n = 0;
        for(Integer i : weighttocourse.descendingKeySet()) {
            for(Course c : weighttocourse.get(i)) {
                searchresults.add(c);
                if(++n >= threshold) break;
            }
        }
        filteredresults = new ArrayList<>(searchresults);
        return searchresults;
    }

    public void set_search_str(String ss) {
        searchstr = ss.toUpperCase();
        if(search_str_list != null) search_str_list.clear();
        else search_str_list = new ArrayList<>();
        Collections.addAll(search_str_list,searchstr.split("\\s+"));
        search_str_list.remove("");
    }

    public List<Course> search(String ss) {return search(ss,Integer.MAX_VALUE);}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Search Results: ");
        for(Course c : filteredresults) sb.append('\n').append(c);
        return sb.toString();
    }

    public String to_str(int threshold) {
        StringBuilder sb = new StringBuilder("Search Results for ").append('\'').append(searchstr).append('\'').append(':');
        for(int i = 0; i < filteredresults.size() && i < threshold;i++)
            sb.append('\n').append(filteredresults.get(i));
        return sb.toString();
    }

    //may want to make different attributes get weighted more than others....
    //thinking 3 digit num + Major strings could get weight 3, everything else aside from
    //section weight 2 and section weight 1.... I don't know
    //think I could work with enum.valueof() and other string stuff to do the above
    private int get_weight(Course c) {
        int w = 0;
        for(String s : search_str_list) {
            if (c.get_id().contains(s)) {
                //section
                if(s.length() == 1 && Character.isAlphabetic(s.charAt(0))) w++;
                //coursenum
                else if(s.length() == 3 && is_numeric(s)) w+=3;
                //major
                else if(s.length() == 4 && Major.is_major(s)) w+=4;
                //everything else
                else w+=2;
            }
        }
        return w;
    }

    private boolean is_numeric(String s) {
        try {
            Integer.parseInt(s);
            return true;
        }
        catch(NumberFormatException nfe) {return false;}
    }

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
