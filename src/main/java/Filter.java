import java.util.List;

public class Filter {
    private FilterType filteron;

    public FilterType getFilteron() {return filteron;}

    //will change the list which is a reference type....
    //I think this will be called from the constructor
    public void apply(List<Course> courses) {}

    public boolean equals(Filter other) {return false;}

    @Override
    public int hashCode() {return super.hashCode();}
}
