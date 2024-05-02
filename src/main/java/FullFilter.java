import java.util.ArrayList;
import java.util.List;

public class FullFilter extends Filter {
    //filter out full courses
    public FullFilter() {
        super(FilterType.FULL);
    }

    @Override
    public void apply_to(List<Course> courses) {
        List<Course> rm = new ArrayList<>();
        for(Course c : courses) if(c.isFull()) rm.add(c);
        courses.removeAll(rm);
    }

    @Override
    public String toString() {return filteron.name() + " filter";}
}
