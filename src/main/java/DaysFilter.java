import java.util.List;
import java.util.Set;

public class DaysFilter extends Filter {
    private Set<Character> days;

    public DaysFilter(List<Course> courses, Set<Character> days) {
        super.filteron = FilterType.DAYS;
        this.days = days;
        apply(courses); // A filter is applied automatically when it is created
    }

    @Override
    public void apply(List<Course> courses) {
//        for (Course c : courses.reversed()) {
//            for (DayTime dt : c.getTimes())
//                // Todo: By this logic, the filter 'M' will weed out 'MWF' classes. Should we keep it that way?
//                if (!days.contains(dt.get_day())) {
//                    courses.remove(c);
//                }
//        }

        // first loop runs backward to avoid concurrent modification exception
        for (int i = courses.size()-1; i >= 0; i--) {
            for (int j = 0; j < courses.get(i).getTimes().size(); j++) {
                // if no day belonging to a course is found in the filter's set of days...
                if(!days.contains(courses.get(i).getTimes().get(j).get_day())) {
                    courses.remove(courses.get(i)); // get rid of it
                }
            }
        }
    }
}
//here we do for each DayTime in Course.times is the DayTime’s day in days and is
//days.size() == times.size() true Todo: remove this comment

