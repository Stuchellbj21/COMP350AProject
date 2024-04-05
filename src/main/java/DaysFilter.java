import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DaysFilter extends Filter {
    private Set<Character> days;

    public DaysFilter(List<Course> courses, Set<Character> days) {
        super(FilterType.DAYS);
        this.days = days;
        apply_to(courses); // A filter is applied automatically when it is created
    }

    public DaysFilter() {
        super(FilterType.DAYS);
        days = null;
    }

    @Override
    public void apply_to(List<Course> courses) {
//        for (Course c : courses.reversed()) {
//            for (DayTime dt : c.getTimes())
//                // Todo: By this logic, the filter 'M' will weed out 'MWF' classes. Should we keep it that way?
//                if (!days.contains(dt.get_day())) {
//                    courses.remove(c);
//                }
//        }

        // add removed to remove list and remove all at once to avoid concurrent modification error
        ArrayList<Course> toremove = new ArrayList<>();
        for (Course c : courses) {
            if(c.getTimes() == null || c.getTimes().isEmpty()) {
                toremove.add(c);
                continue;
            }
            for (DayTime dt : c.getTimes()) {
                // if the number of days in the filter is different from the number of days in the course
                // or the course's day isn't in the filter -> remove the course
                if(c.getTimes().size() != days.size() || !days.contains(dt.get_day())) {
                    toremove.add(c); // get rid of it
                    break;
                }
            }
        }
        courses.removeAll(toremove);
    }

    @Override
    public String toString() {return filteron.name() + " filter: " + days;}
}
//here we do for each DayTime in Course.times is the DayTime’s day in days and is
//days.size() == times.size() true Todo: remove this comment

