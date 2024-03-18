import java.util.List;

public abstract class Filter {
    protected FilterType filteron;

    public FilterType getFilteron() {return filteron;}

    //will change the list which is a reference type....
    //I think this will be called from the constructor
    public abstract void apply(List<Course> courses);

    public boolean equals(Filter other) {
        return (this.filteron == other.filteron);
        }

    @Override
    public int hashCode() {return super.hashCode();}
}
