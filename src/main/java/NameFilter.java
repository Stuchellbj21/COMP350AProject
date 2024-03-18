import java.util.List;

public class NameFilter extends Filter {
    private String name; // ToDo: This should really be 'courseEnum'
    // Also, does this even make sense to have a name filter? Isn't that what search is for?
    // THis should probably a Filter that returns all of one kind of major, e.g., all COMP classes

    public NameFilter(List<Course> courses, String name) {
        super.filteron = FilterType.NAME;
        this.name = name;
        apply(courses); // A filter is applied automatically when it is created
    }

    @Override
    public void apply(List<Course> courses) {

        // loop runs backward to avoid concurrent modification error
        for (int i = courses.size()-1; i >= 0; i--) {
            // if the course name doesn't equal the filter's 'name' String...
            if (!courses.get(i).getName().equalsIgnoreCase(name)) {
                courses.remove(courses.get(i)); // get rid of it
            }
        }
    }
}
