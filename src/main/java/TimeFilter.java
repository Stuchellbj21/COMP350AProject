import java.util.List;

public class TimeFilter extends Filter {

    private DayTime time; //this DayTime has a day of ‘_’ (there is no day here)

    public TimeFilter(List<Course> courses, DayTime time) {
        super.filteron = FilterType.TIME;
        this.time = time;
        apply(courses); // A filter is applied automatically when it is created
    }

    @Override
    public void apply(List<Course> courses) {
        // first loop runs backwards to avoid concurrent modification exceptions
        for (int i = courses.size()-1; i >= 0 ; i--) {
            for (int j = 0; j < courses.get(i).getTimes().size(); j++) {
                if (!courses.get(i).getTimes().get(j).equals(time)) {
                    courses.remove(courses.get(i));
                }
            }
        }
    }

    //here, apply() will check if Course.times.size() == 1 and if same_time as that time
}
