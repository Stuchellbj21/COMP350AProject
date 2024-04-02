import java.util.*;

public class Search {
    private List<Course> searchresults;
    private List<Course> filteredresults; //this way don't have to do new search when add filter

    private Comparator<Course> bymajor = new Comparator<Course>() {
        @Override
        public int compare(Course c1, Course c2) {return c1.getMajor().name().compareTo(c2.getMajor().name());}
    };

    private Comparator<Course> bysection = new Comparator<Course>() {
        @Override
        public int compare(Course c1, Course c2) {return c1.getSection() - (c2.getSection());}
    };

    private Comparator<Course> bycoursenum = new Comparator<Course>() {
        @Override
        public int compare(Course c1, Course c2) {return c1.getCourseNum() - (c2.getCourseNum());}
    };

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
        searchresults = new ArrayList<>();
        search(ss);
        activefilters = new HashSet<>();
    }

    Search(String ss, Set<Filter> filters) {
        searchresults = new ArrayList<>();
        search(ss);
        activefilters = filters;
        apply_all_filters();
    }

    //getters + setters yet to be added

    //threshold specifies the limit on the number of Courses given to search results
    //sets searchstr to ss and performs a new search on searchstr and gives results
    public List<Course> search(String ss,int threshold,boolean sorted) {
        set_search_str(ss);
        HashMap<Course,Integer> coursetoweight = new HashMap<>();
        for(Course c : Main.allcourses) coursetoweight.put(c,get_weight(c)); //now we have weighted courses
        //want ordering by weight, so use treemap
        TreeMap<Integer,List<Course>> weighttocourse = new TreeMap<>();
        for(Course c : coursetoweight.keySet())
            //if weight is greater than 0, add the course to treemap list
            if(coursetoweight.get(c) > 0) {
                if(weighttocourse.get(coursetoweight.get(c)) == null) weighttocourse.put(coursetoweight.get(c),new ArrayList<>());
                weighttocourse.get(coursetoweight.get(c)).add(c);
            }
        searchresults.clear();
        int n = 0;
        for(Integer i : weighttocourse.descendingKeySet()) {
            if(sorted) {
                Collections.sort(weighttocourse.get(i), bysection);
                Collections.sort(weighttocourse.get(i), bycoursenum);
                Collections.sort(weighttocourse.get(i), bymajor);
            }
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

    public List<Course> search(String ss) {return search(ss,Integer.MAX_VALUE,true);}

    public List<Course> search(String ss,boolean sorted) {return search(ss,Integer.MAX_VALUE,sorted);}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Search Results: ");
        if(filteredresults == null || filteredresults.isEmpty()) return sb.append("None").toString();
        for(Course c : filteredresults) sb.append('\n').append(c);
        return sb.toString();
    }

    public String to_str(int threshold) {
        StringBuilder sb = new StringBuilder("Search Results for ").append('\'').append(searchstr).append('\'').append(':');
        if(filteredresults == null || filteredresults.isEmpty()) return sb.append("\nNone").toString();
        for(int i = 0; i < filteredresults.size() && i < threshold;i++)
            sb.append('\n').append(filteredresults.get(i));
        return sb.toString();
    }

    //may want to make different attributes get weighted more than others....
    //thinking 3 digit num + Major strings could get weight 3, everything else aside from
    //section weight 2 and section weight 1.... I don't know
    //think I could work with enum.valueof() and other string stuff to do the above
    public int get_weight(Course c) {
        int w = 0;
        for(String s : search_str_list) {
            if (c.get_id().contains(s)) {
                //section
                if(s.length() == 1 && Character.isAlphabetic(s.charAt(0))) w++;
                //coursenum
                else if(s.length() == 3 && Main.is_numeric(s)) w+=3;
                //major
                else if(s.length() == 4 && Major.is_major(s)) w+=4;
                //everything else
                else w+=2;
            }
        }
        return w;
    }

    public void apply_all_filters() {for(Filter f : activefilters) f.apply_to(filteredresults);}

    public List<Course> activate_new_filter(Filter f) {
        if(activefilters.add(f)) f.apply_to(filteredresults);;
        return filteredresults;
    }

    /*
    there will only be 1 filter of a given type active at a certain time, so we may want to modify the
    time filter (for example) which will not simply change the filteredresults, but will have to get
    filteredresults from the original results over again
     */
    //removes the filter of the same type as f if present, and activates f
    public List<Course> modify_filter(Filter f) {
        //no modification was made.... the requested change was already in place
        if(activefilters.contains(f)) return filteredresults;
        search(searchstr);
        Filter rm = null;
        for(Filter fil : activefilters) {
            //if the filters are not of the same type, apply the filter in the collection of active filters
            if(!(fil.equals(f))) fil.apply_to(filteredresults);
            else rm = fil;
        }
        //if the filter to remove was found, remove it
        if(rm != null) activefilters.remove(rm);
        return activate_new_filter(f);
    }

    //removes a filter and does something similar to modify_filter()
    public List<Course> deactivate_filter(Filter f) {
        //if no change was made, just return og filtertedresults
        if(!activefilters.remove(f)) return filteredresults;
        //otherwise, perform new search, apply all filters, and return
        search(searchstr);
        apply_all_filters();
        return filteredresults;
    }

    public List<Course> get_filteredresults() {return filteredresults;}
}
