import java.util.List;

public class CreditFilter extends Filter {

    private int numcredits;

    public CreditFilter(List<Course> courses,int numcredits) {
        super.filteron = FilterType.CREDIT;
        this.numcredits = numcredits;
        apply(courses); // A filter is applied automatically when it is created
    }

    @Override
    public void apply(List<Course> courses) {
        // loop runs backward to avoid concurrent modification exception
        for (int i = courses.size()-1; i >= 0; i--) {
            if (courses.get(i).getCredits() != numcredits) {
                courses.remove(courses.get(i));
            }
        }
    }
}
