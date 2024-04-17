import java.util.ArrayList;
import java.util.List;

public class NameFilter extends Filter {
    private String name;
    // Also, does this even make sense to have a name filter? Isn't that what search is for?
    // THis should probably a Filter that returns all of one kind of major, e.g., all COMP classes

    public NameFilter(List<Course> courses, String name) {
        super(FilterType.NAME);
        this.name = name.toUpperCase();
        apply_to(courses); // A filter is applied automatically when it is created
    }

    public NameFilter() {
        super(FilterType.NAME);
        name = null;
    }

    @Override
    public void apply_to(List<Course> courses) {
        String[] namepieces = name.split("\\s+");
        //add all removed courses to toremove to avoid concurrent modification
        ArrayList<Course> toremove = new ArrayList<>();
        for(Course c : courses) {
            String[] coursename = c.getName().split("\\s+");
            //if names aren't equal in length -> remove
            if(coursename.length != namepieces.length) {
                toremove.add(c);
                continue;
            }
            //if not all name pieces match -> remove
            for(int i = 0; i < coursename.length; i++) {
                if(!namepieces[i].equalsIgnoreCase(coursename[i])){
                    toremove.add(c);
                    break;
                }
            }
        }
        courses.removeAll(toremove);

        /*// loop runs backward to avoid concurrent modification error
        for (int i = courses.size()-1; i >= 0; i--) {
            String[] coursename = courses.get(i).getName().split("\\s+");
            // if the course name doesn't equal the filter's 'name' String...
            if (!courses.get(i).getName().equalsIgnoreCase(name)) {
                courses.remove(courses.get(i)); // get rid of it
            }
        }*/
    }

    @Override
    public String toString() {return filteron.name() + " filter: " + name;}
}
