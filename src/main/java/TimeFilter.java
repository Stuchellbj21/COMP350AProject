import java.util.ArrayList;
import java.util.List;

public class TimeFilter extends Filter {

    private DayTime time; //this DayTime has a day of ‘_’ (there is no day here)

    public TimeFilter(List<Course> courses, DayTime time) {
        super(FilterType.TIME);
        this.time = time;
        apply_to(courses); // A filter is applied automatically when it is created
    }

    @Override
    public void apply_to(List<Course> courses) {
        //add removed courses to remove instead of removing them right away to avoid concurrent modification exception
        ArrayList<Course> toremove = new ArrayList<>();
        //if we're filtering out courses with times, do so
        if(time.get_start_time().equalsIgnoreCase("none")) {
            for(Course c : courses) {
                //if course has a time, filter it out
                if(!c.has_no_times()) toremove.add(c);
            }
        }
        else {
            for (Course c : courses) {
                //remove courses with no times
                if (c.has_no_times()) {
                    toremove.add(c);
                    continue;
                }
                for (DayTime dt : c.getTimes()) {
                    //if any daytime's time differs from filter input...
                    if (!dt.same_time(time)) {
                        toremove.add(c); // ...get rid of it
                        break;// if a day's time doesn't match the time provided by the filter, no need to keep iterating
                    }
                }
            }
        }
        courses.removeAll(toremove);
    }

    @Override
    public String toString() {
        if(time.get_start_time().equalsIgnoreCase("none")) return filteron.name() + " filter: no times";
        return filteron.name() + " filter: " + time.get_start_time() + " - " + time.get_end_time();
    }

    //here, apply() will check if Course.times.size() == 1 and if same_time as that time
}