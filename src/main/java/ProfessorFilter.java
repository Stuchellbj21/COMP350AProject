import java.util.List;

public class ProfessorFilter extends Filter {
    private String professor;

    public ProfessorFilter(List<Course> courses, String professor) {
        super(FilterType.PROFESSOR);
        this.professor = professor;
        apply_to(courses);// A filter is applied automatically when it is created
    }

    public ProfessorFilter() {
        super(FilterType.PROFESSOR);
        professor = null;
    }

    @Override
    public void apply_to(List<Course> courses) {

        for (int i = courses.size()-1; i >= 0 ; i--) {
            if(!courses.get(i).getProfessor().equalsIgnoreCase(professor)) {
                courses.remove(courses.get(i));
            }
        }
    }

    @Override
    public String toString() {return filteron.name() + " filter: " + professor;}
}