import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Array;
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
////        //FIXME: KEEPING A TXT FILE DOESN'T SEEM NECESSARY
////        // todo: add file name to info.txt file so it can be loaded by the load_folders method in line 33 of 'Menus'
////        FileOutputStream fos = new FileOutputStream("Accounts\\" + accountname + '\\' + name + '\\' + name + ".txt");
//        PrintWriter pw = new PrintWriter(fos);
//        pw.print(name+"\n");
//        for (int i = 0; i < scheds.size(); i++) {
//            if (i == scheds.size()-1){
//                pw.print(scheds.get(i));
//            } else {
//                pw.print(scheds.get(i) + ",");
//            }
//        }
//        pw.close();
    }

    public void load_folder(String account_name,String folder_name) throws FileNotFoundException {
        name = folder_name;
        scheds = get_folder_scheds(account_name);
        //todo: read from info.txt to load folders upon reopening program
    }

    public List<String> get_schedules(String username) {
        List<String> schedules = new ArrayList<>();
        File account = new File("Accounts\\" + username);
        //boolean hasSchedule = false; this line seems unnecessary
        if(account.listFiles() != null) {
            for (File f : account.listFiles()) {
                if (f.getName().endsWith(".csv")) {
                    //cut off .csv
                    String sched_name = (f.getName().substring(0, f.getName().length() - 4));
                    schedules.add(sched_name);
                    //if (!hasSchedule) hasSchedule = true;
                }
            }
        }
        return schedules;
    }

    public List<String> get_folder_scheds(String username) {
        List<String> schedules = new ArrayList<>();
        File folder = new File("Accounts\\" + username + "\\" + getName());
        //boolean hasSchedule = false; this line seems unnecessary
        if(folder.listFiles() != null) {
            for (File f : folder.listFiles()) {
                if (f.getName().endsWith(".csv")) {
                    //cut off .csv
                    String sched_name = (f.getName().substring(0, f.getName().length() - 4));
                    schedules.add(sched_name);
                    //if (!hasSchedule) hasSchedule = true;
                }
            }
        }
        return schedules;
    }

    public StringBuilder to_str() {
        StringBuilder folder = new StringBuilder();
        Main.afl.println("Folder -- " + name);
        if (scheds.isEmpty()) {
            Main.afl.println("No schedules saved in folder");
        } else {
            folder.append("List of schedules: \n");
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

    public boolean delete(String accountname) throws IOException {
        ArrayList<String> scheds_to_del = new ArrayList<>();
        for (int i = 0; i < scheds.size(); i++) { // remove schedules in Folder before deleting Folder
            scheds_to_del.add(scheds.get(i));
        }
        for (int i = 0; i < scheds_to_del.size(); i++) {
            // I changed this so that the schedules list AND the database are updated
            Main.currentaccnt.delete_schedule(scheds_to_del.get(i));
            //Main.currentaccnt.get_schednames().remove(scheds_to_del.get(i));
        }
        if (new File("Accounts\\" + accountname + "\\" + name).exists()) {
            Files.delete(Path.of("Accounts\\" + accountname + "\\" + name));
        }
        return true;
    }
}
