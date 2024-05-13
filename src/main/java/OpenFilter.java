import java.util.ArrayList;
import java.util.List;

public class OpenFilter extends Filter {
    //filter out full courses
    public OpenFilter() {
        super(FilterType.OPEN);
    }

    public OpenFilter(List<Course> courses) {
        super(FilterType.OPEN);
        apply_to(courses);
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
