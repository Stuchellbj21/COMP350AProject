import java.util.List;

public class TimeFilter extends Filter {

    private DayTime time; //this DayTime has a day of ‘_’ (there is no day here)

    public TimeFilter(List<Course> courses, DayTime time) {
        super.filteron = FilterType.TIME;
        this.time = time;
        apply_to(courses); // A filter is applied automatically when it is created
    }

    @Override
    public void apply_to(List<Course> courses) {
        // first loop runs backwards to avoid concurrent modification exceptions
        for (int i = courses.size()-1; i >= 0; i--) {
            for (int j = 0; j < courses.get(i).getTimes().size(); j++) {
                // if course's daytime object has no times, or if any daytime's time differs from filter input...
                if (courses.get(i).getTimes().isEmpty() ||
                        courses.get(i).getTimes().get(j).get_militarystart() != (time.get_militarystart())) {
                    courses.remove(courses.get(i)); // ...get rid of it
                    break;// if a day's time doesn't match the time provided by the filter, no need to keep iterating
                }
            }
        }
    }

    //here, apply() will check if Course.times.size() == 1 and if same_time as that time
}
