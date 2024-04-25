import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Folder {
    private List<String> scheds;

    private String name;

    public Folder(String folder_name){
        scheds = new ArrayList<>();
        name = folder_name;
    }

    public Folder(){
        scheds = new ArrayList<>();
        name = "New folder";
    }

    public String getName(){
        return name;
    }

    public boolean has_scheds(){
        return scheds != null && !scheds.isEmpty();
    }

    public void save_folder(String accountname) throws IOException {
        File f = new File("Accounts\\" + accountname + '\\' + name);
        f.mkdir();
        //FIXME: KEEPING A TXT FILE DOESN'T SEEM NECESSARY
        FileOutputStream fos = new FileOutputStream("Accounts\\" + accountname + '\\' + name + '\\' + name + ".txt");
        PrintWriter pw = new PrintWriter(fos);
        pw.print(name+"\n");
        for (int i = 0; i < scheds.size(); i++) {
            if (i == scheds.size()-1){
                pw.print(scheds.get(i));
            } else {
                pw.print(scheds.get(i) + ",");
            }
        }
        pw.close();
    }

    public void load_folder(String account_name,String folder_name) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream("Accounts" + '\\' + account_name + '\\' + folder_name + '\\' + folder_name + (folder_name.endsWith(".txt") ? "" : ".txt"));
        Scanner fscn = new Scanner(fis);
        name = fscn.nextLine().replace("\n","");
        if (fscn.hasNextLine()) {
            Scanner scn_scheds = new Scanner(fscn.nextLine());
            scn_scheds.useDelimiter(",");
            while (scn_scheds.hasNext()) {
                scheds.add(scn_scheds.next());
            }
            scn_scheds.close();
        }
        fscn.close();
    }

    //FIXME: CHANGE NAME FROM PRINT -> get_schedules_from_folder or something
    public List<String> print_schedule_list(String username) {
        List<String> schedules = new ArrayList<>();
        File account = new File("Accounts\\" + username);
        //boolean hasSchedule = false; this line seems unnecessary
        if(account.listFiles() != null) {
            for (File f : account.listFiles()) {
                if (f.getName().endsWith(".csv")) {
                    //cut off .csv
                    String sched_name = (f.getName().substring(0, f.getName().length() - 4));
                    scheds.add(sched_name);
                    //if (!hasSchedule) hasSchedule = true;
                }
            }
        }
        return schedules;
    }

    public StringBuilder to_str() {
        StringBuilder folder = new StringBuilder();
        System.out.println("Folder: " + name);
        folder.append("List of schedules: \n");
        if (scheds.isEmpty()) {
            System.out.println("No schedules saved in folder\n");
        } else {
            System.out.println();
            for (int i = 0; i < scheds.size(); i++) {
                String curr_num = String.valueOf(i + 1);
                folder.append(curr_num + ". " + scheds.get(i) + "\n");
            }
        }
        return folder;
    }

    public List<String> get_scheds(){
        return scheds;
    }

    /*public boolean remove_sched(String sched_rem){
        boolean found = false;
        int index_rem = -500;
        for (int i = 0; i < list_of_scheds.size(); i++) {
            if (list_of_scheds.get(i).equals(sched_rem)){
                found = true;
                index_rem = i;
            }
        }
        if (found){
            list_of_scheds.remove(index_rem);
            return true;
        } else {
            return false;
        }
    } CAN JUST USE ArrayList remove*/

    /*public boolean contains_sched(String sched_name){
        boolean found = false;
        for (String schedule : list_of_scheds) {
            if (schedule.equals(sched_name)) {
                found = true;
            }
        }
        return found;
    }CAN JUST USE ArrayList contains*/

    public boolean delete(String accountname) {
        File folder = new File("Accounts\\"+accountname+"\\"+name+".txt");
        return folder.delete();
    }
}
