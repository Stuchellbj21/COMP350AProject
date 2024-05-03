public class Extracurricular {
    //

    private DayTime time;
    private String name;

    public Extracurricular(DayTime time, String name){
        this.time = time;
        this.name = name;

    }

    @Override
    public String toString(){
        return name + " " + time.get_start_time() + "-" + time.get_end_time() + " on " + time.get_day();
    }

    public void set_time(DayTime dt){ this.time = dt;}
    public DayTime get_time(){ return time;}

    public void set_name(String n){ this.name = n;}
    public String get_name(){ return name;}
}
