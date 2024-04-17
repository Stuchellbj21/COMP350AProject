import java.util.List;

public class CreditFilter extends Filter {

    private int numcredits;

    public CreditFilter(List<Course> courses,int numcredits) {
        super(FilterType.CREDIT);
        this.numcredits = numcredits;
        apply_to(courses); // A filter is applied automatically when it is created
    }

    //this is here just to be able to remove a credit filter (equals checks only on filter on, so
    //we can just use search.deactivate_filter(new CreditFilter()) to deactivate the credit filter)
    public CreditFilter() {
        super(FilterType.CREDIT);
        numcredits = -1;
    }

    @Override
    public void apply_to(List<Course> courses) {
        // loop runs backward to avoid concurrent modification exception
        for (int i = courses.size()-1; i >= 0; i--) {
            if (courses.get(i).getCredits() != numcredits) {
                courses.remove(courses.get(i));
            }
        }
    }

    @Override
    public String toString() {return filteron.name() + " filter: " + numcredits;}

    /*@Override
    public int hashCode() {
        //I think this will be sufficient here, but maybe not
        return ((Integer)numcredits).hashCode() + filteron.hashCode();
    }*/
}
