import java.util.ArrayList;
import java.util.List;

public class TimeFilter extends Filter {

    private DayTime time; //this DayTime has a day of ‘_’ (there is no day here)

    public TimeFilter(List<Course> courses, DayTime time) {
        super(FilterType.TIME);
        this.time = time;
        apply_to(courses); // A filter is applied automatically when it is created
    }

    //used for filter removal
    public TimeFilter() {
        super(FilterType.TIME);
        time = null;
    }

    @Override
    public void apply_to(List<Course> courses) {
        //add removed courses to remove instead of removing them right away to avoid concurrent modification exception
        ArrayList<Course> toremove = new ArrayList<>();
        for (Course c : courses) {
            //if we remove the course, we'll want to [c1,c2,c3,c5,c6]
            if(c.getTimes() == null || c.getTimes().isEmpty()) {
                toremove.add(c);
                continue;
            }
            for (DayTime dt : c.getTimes()) {
                // if course's daytime object has no times, or if any daytime's time differs from filter input...
                if (!dt.same_time(time)) {
                    toremove.add(c); // ...get rid of it
                    break;// if a day's time doesn't match the time provided by the filter, no need to keep iterating
                }
            }
        }
        courses.removeAll(toremove);
    }

    @Override
    public String toString() {return filteron.name() + " filter: " + time.get_start_time() + " - " + time.get_end_time();}

    //here, apply() will check if Course.times.size() == 1 and if same_time as that time
}