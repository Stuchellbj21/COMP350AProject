import java.util.ArrayList;
import java.util.List;

public class Folder {
    private List<String> list_of_scheds;

    public Folder(){
        list_of_scheds = new ArrayList<>();
    }

    public void add_schedule(String sched_name) {
        list_of_scheds.add(sched_name);
    }

    public boolean has_scheds(){
        if (list_of_scheds.isEmpty()){
            return false;
        } else {
            return true;
        }
    }
}
