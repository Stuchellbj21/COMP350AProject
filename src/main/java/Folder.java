import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Folder {
    private List<String> list_of_scheds;

    private String f_name;

    public Folder(String folder_name){
        list_of_scheds = new ArrayList<>();
        f_name = folder_name;
    }

    public Folder(){
        list_of_scheds = new ArrayList<>();
        f_name = "New folder";
    }

    public String getF_name(){
        return f_name;
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

    public void save_folder(String accountname) throws IOException {
        File f = new File("Accounts\\" + accountname + '\\' + f_name);
        f.mkdir();
        FileOutputStream fos = new FileOutputStream("Accounts\\" + accountname + '\\' + f_name + '\\' + f_name + ".txt");
        PrintWriter pw = new PrintWriter(fos);
        pw.print(f_name+"\n");
        for (int i = 0; i < list_of_scheds.size(); i++) {
            if (i == list_of_scheds.size()-1){
                pw.print(list_of_scheds.get(i));
            } else {
                pw.print(list_of_scheds.get(i) + ",");
            }
        }
        pw.close();
    }

    public void load_folder(String account_name,String folder_name) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream("Accounts" + '\\' + account_name + '\\' + folder_name + '\\' + folder_name + (folder_name.endsWith(".txt") ? "" : ".txt"));
        Scanner fscn = new Scanner(fis);
        String name = fscn.nextLine().replace("\n","");
        f_name = name;
        if (fscn.hasNextLine()) {
            Scanner scheds = new Scanner(fscn.nextLine());
            scheds.useDelimiter(",");
            while (scheds.hasNext()) {
                list_of_scheds.add(scheds.next());
            }
            scheds.close();
        }
        fscn.close();
    }

    public List<String> print_schedule_list(String username) {
        List<String> scheds = new ArrayList<>();
        File account = new File("Accounts\\" + username);
        boolean hasSchedule = false;
        if(account.listFiles() != null) {
            for (File f : account.listFiles()) {
                if (f.getName().endsWith(".csv")) {
                    //cut off .csv
                    String name = (f.getName().substring(0, f.getName().length() - 4));
                    scheds.add(name);
                    if (!hasSchedule) hasSchedule = true;
                }
            }
        }
        return scheds;
    }

    public StringBuilder to_str() {
        StringBuilder folder = new StringBuilder();
        System.out.println("Folder: " + f_name);
        folder.append("List of schedules: \n");
        if (list_of_scheds.isEmpty()) {
            System.out.println("No schedules saved in folder\n");
        } else {
            System.out.println();
            for (int i = 0; i < list_of_scheds.size(); i++) {
                String curr_num = String.valueOf(i + 1);
                folder.append(curr_num + ". " + list_of_scheds.get(i) + "\n");
            }
        }
        return folder;
    }

    public List<String> getList_of_scheds(){
        return list_of_scheds;
    }

    public boolean remove_sched(String sched_rem){
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
    }

    public boolean contains_sched(String sched_name){
        boolean found = false;
        for (String schedule : list_of_scheds) {
            if (schedule.equals(sched_name)) {
                found = true;
            }
        }
        return found;
    }

    public boolean delete(String accountname) {
        File folder = new File("Accounts\\"+accountname+"\\"+f_name+".txt");
        return folder.delete();
    }
}
