import java.util.List;

public class Filter {
    protected FilterType filteron;

    public FilterType getFilteron() {return filteron;}

    //made non-abstract and added constructor so that we can test
    //if activefilters in search contains certain types of filters
    public Filter(FilterType filteron) {
        this.filteron = filteron;
    }

    //will change the list which is a reference type....
    //I think this will be called from the constructor
    public void apply_to(List<Course> courses) {return;}

    //there should only ever be 1 filter of a given type active at a time
    @Override
    public boolean equals(Object other) {
        if(other == null) return false;
        if(!(other instanceof Filter o)) return false;
        return filteron == o.filteron;
    }

    @Override
    public int hashCode() {return super.hashCode();}
}