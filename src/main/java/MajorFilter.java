import java.util.List;

public class MajorFilter extends Filter {
    private Major major;

    public MajorFilter(List<Course> courses, Major major) {
        super.filteron = FilterType.MAJOR;
        this.major = major;
        apply(courses);// A filter is applied automatically when it is created
    }

    @Override
    public void apply(List<Course> courses) {
        // loop runs backward to avoid concurrent modification exception
        for (int i = courses.size()-1; i >= 0; i--) {
            if (!courses.get(i).getMajor().equals(major)) {
                courses.remove(courses.get(i));
            }
        }
    }
}
