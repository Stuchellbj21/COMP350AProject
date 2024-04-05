import java.util.List;

public class MajorFilter extends Filter {
    private Major major;

    public MajorFilter(List<Course> courses, Major major) {
        super(FilterType.MAJOR);
        this.major = major;
        apply_to(courses);// A filter is applied automatically when it is created
    }

    public MajorFilter() {
        super(FilterType.MAJOR);
        major = null;
    }

    @Override
    public void apply_to(List<Course> courses) {
        // loop runs backward to avoid concurrent modification exception
        for (int i = courses.size()-1; i >= 0; i--) {
            if (!courses.get(i).getMajor().equals(major)) {
                courses.remove(courses.get(i));
            }
        }
    }

    @Override
    public String toString() {return filteron.name() + " filter: " + major.name();}
}
