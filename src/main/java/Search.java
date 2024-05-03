import java.io.IOException;
import java.util.*;

public class Search {
    private List<Course> searchresults;
    private List<Course> filteredresults; //this way don't have to do new search when add filter

    //comparators for certain sorting techniques
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

    private List<Filter> activefilters;

    private String searchstr;

    private List<String> search_str_list;

    Search() {
        searchstr = "";
        search_str_list = new ArrayList<>();
        activefilters = new ArrayList<>();
        filteredresults = new ArrayList<>();
        searchresults = new ArrayList<>();
    }

    Search(String ss) {
        searchresults = new ArrayList<>();
        activefilters = new ArrayList<>();
        search(ss);
    }

    Search(String ss, List<Filter> filters) {
        searchresults = new ArrayList<>();
        activefilters = filters;
        search(ss);
    }

    //getters + setters yet to be added

    //threshold specifies the limit on the number of Courses given to search results
    //sets searchstr to ss and performs a new search on searchstr and gives results
    public List<Course> search(String ss,int threshold,boolean sorted) {
        set_search_str(ss);
        //if user wants to see all classes, show them all classes  |  if user enters all, the
        //results will always be sorted
        if(searchstr.equalsIgnoreCase("all")) return search_all(threshold,sorted);
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
            //sort bin if wanted
            if(sorted) {
                Collections.sort(weighttocourse.get(i), bysection);
                Collections.sort(weighttocourse.get(i), bycoursenum);
                Collections.sort(weighttocourse.get(i), bymajor);
            }
            //add all courses up to threshold to results
            for(Course c : weighttocourse.get(i)) {
                //only add the course to search results if the course wasn't already taken by
                //the current user
                if(!Main.currentaccnt.already_took(c)) {
                    if(n++ >= threshold) break;
                    searchresults.add(c);
                }
            }
        }
        //filter results and return
        filteredresults = new ArrayList<>(searchresults);
        apply_all_filters();
        return searchresults;
    }

    public List<Course> search_all(int threshold,boolean sorted) {
        searchresults = new ArrayList<>(Main.allcourses);
        filteredresults = new ArrayList<>();
        for(int i = 0; i < threshold && i < searchresults.size(); i++) filteredresults.add(searchresults.get(i));
        apply_all_filters();
        //if(!sorted) Main.afl.println("Searching for 'ALL' gives classes in the order they were loaded.");
        return filteredresults;
    }

    public void set_search_str(String ss) {
        searchstr = ss.toUpperCase();
        if(search_str_list != null) search_str_list.clear();
        else search_str_list = new ArrayList<>();
        Collections.addAll(search_str_list,searchstr.split("\\s+"));
        search_str_list.remove("");
    }

    public List<Course> search(String ss) {return search(ss,Integer.MAX_VALUE,true);}

    public List<Course> search(String ss,int threshold) {return search(ss,threshold,true);}

    public List<Course> search(String ss,boolean sorted) {return search(ss,Integer.MAX_VALUE,sorted);}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Search Results: ");
        if(filteredresults == null || filteredresults.isEmpty()) return sb.append("None").toString();
        for(Course c : filteredresults)
            if(Main.currentaccnt.get_wishlist().contains(c)) {
                sb.append('\n').append(c + "*");
            } else{
                    sb.append('\n').append(c);
        }
        return sb.toString();
    }

    public String to_str(boolean numbered) {
        StringBuilder sb = new StringBuilder("Active Filters: ").append(activefilters != null && !activefilters.isEmpty() ? activefilters : "None").append('\n');
        sb.append("Search Results for ").append('\'').append(searchstr).append('\'').append(':');
        if(filteredresults == null || filteredresults.isEmpty()) return sb.append("\nNone").toString();
        //give results up to threshold
        for(int i = 0; i < filteredresults.size();i++) {
            sb.append('\n');
            if(numbered) sb.append(i+1).append(". ");
            sb.append(filteredresults.get(i));
            if(Main.currentaccnt.get_wishlist().contains(filteredresults.get(i))) {
                sb.append("☆");
            }
        }
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
                if(s.length() == 1 && Character.isAlphabetic(s.charAt(0)) && c.getSection() == Character.toUpperCase(s.charAt(0))) w+=2;
                //coursenum
                else if(s.length() == 3 && GeneralUtils.is_numeric(s)) w+=4;
                //major
                else if(s.length() == 4 && Major.is_major(s)) w+=5;
                //everything else
                else w+=3;
            }
            w += partial_match_weight(c,s);
        }
        return w;
    }

    public int partial_match_weight(Course c, String s) {
        if(c.toString().toUpperCase().contains(s.toUpperCase())) return 1;
        return 0;
        //if there's a partial match within the course's string, increase the weight slightly
        //initialize p (for present) along with i so we have it at loop scope
        /*for(int i = 0,p = c.toString().toUpperCase().indexOf(s.toUpperCase(),i); i < c.toString().length();) {
            //Main.afl.println("i = " + i + " p = " + p);
            //if the string was found in the remainder of the string, increment weight
            if(p >= 0) w++;
            //if p wasn't found return the current weight value
            else return w;
            //move past place where we found it and see if we find it again
            i = p+1;
            p = c.toString().toUpperCase().indexOf(s.toUpperCase(),i);
        }
        return w;*/
    }

    public void apply_all_filters() {for(Filter f : activefilters) f.apply_to(filteredresults);}

    public List<Course> activate_new_filter(Filter f) {
        if(activefilters.add(f)) f.apply_to(filteredresults);
        return filteredresults;
    }

    /*
    there will only be 1 filter of a given type active at a certain time, so we may want to modify the
    time filter (for example) which will not simply change the filteredresults, but will have to get
    filteredresults from the original results over again
     */
    //removes the filter of the same type as f if present, and activates f
    public List<Course> modify_filter(Filter f) {
        search(searchstr);
        Filter rm = null;
        for(Filter fil : activefilters) {
            //if the filters are not of the same type, apply the filter in the collection of active filters
            if(!(fil.equals(f))) fil.apply_to(filteredresults);
            else rm = fil;
        }
        //if the filter to remove was found, remove it and activate its replacement.... if it wasn't found, activate
        //the new filter
        if(rm != null) activefilters.remove(rm);
        return activate_new_filter(f);
    }

    //removes a filter and does something similar to modify_filter()
    public List<Course> deactivate_filter(Filter f) {
        //if no change was made, just return og filteredresults
        if(!activefilters.remove(f)) return filteredresults;
        //otherwise, perform new search, apply all filters, and return
        search(searchstr);
        apply_all_filters();
        return filteredresults;
    }

    public void reset() {
        activefilters.clear();
        search("",0);
    }

    public List<Course> get_filtered_results() {return filteredresults;}

    public List<Filter> get_active_filters() {return activefilters;}

    /*public String active_filters_to_str() {
        if(activefilters == null || activefilters.isEmpty()) return "No Active Filters";
        StringBuilder sb = new StringBuilder();
        sb.append("Active Filters:\n");
        sb.append(activefilters.getFirst());
        for(int i = 1; i < activefilters.size(); i++)
            sb.append('\n').append(activefilters.get(i));
        return sb.toString();
    }*/

    public static void prompt_and_search() {
        int threshold;
        boolean sorted = false;
        while (true) {
            String in = GeneralUtils.input("Enter maximum number search results: ");
            try {
                threshold = Integer.parseInt(in);
                if(threshold < 1) throw new IllegalArgumentException("Error: maximum number of search results should be greater than zero.");
                sorted = GeneralUtils.want_more('s');
                break;
            } catch (NumberFormatException nfe) {
                Main.afl.println("Error: '" + in + "' is not a valid integer. Enter an integer value.");
            } catch (IllegalArgumentException iae) {
                Main.afl.println(iae.getMessage());
            }
        }
        String ss = GeneralUtils.input("Enter search string: ");
        if(ss.equalsIgnoreCase("all") && !sorted) Main.afl.println("Searching for 'ALL' gives classes in the order they were loaded.");
        Main.search.search(ss, threshold, sorted);
        Main.afl.println(Main.search.to_str(true));
    }

    public static void main(String[] args) throws IOException {
        SaveLoad.load_allcourses();
        do prompt_and_search();
        while(!GeneralUtils.input("q to quit: ").equalsIgnoreCase("q"));
    }
}