import java.util.List;

public class SemesterFilter extends Filter{

    private String semester;
    private int year;

    public SemesterFilter(List<Course> courses, String semester, int year) {
        super(FilterType.SEMESTER);
        this.semester = semester;
        this.year = year;
        apply_to(courses); // A filter is applied automatically when it is created
    }

    public SemesterFilter() {
        super(FilterType.SEMESTER);
        year = -1;
        semester = null;
    }

    @Override
    public void apply_to(List<Course> courses) {
        //loop runs backwards to avoid concurrent modification exceptions
        for (int i = courses.size()-1; i >= 0; i--) {
            if(!courses.get(i).getSemester().equalsIgnoreCase(semester) || (courses.get(i).getYear() != year)) {
                courses.remove(courses.get(i));
            }
        }
    }

    @Override
    public String toString() {return filteron.name() + " filter: " + semester + " " + year;}
}
