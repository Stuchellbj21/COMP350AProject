import java.util.Scanner;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class FolderOps {
    //TODO: Fix so that schedules deleted in a folder are also deleted in an account
    public static void delete_folder() throws IOException {
        String folder_name = Main.current_folder.getName();
        Main.currentaccnt.get_folders().remove(folder_name);
        Main.current_folder.delete(Main.currentaccnt.getUsername());
    }

    public static void remove_sched() throws IOException {
        if (Main.current_folder.get_scheds().isEmpty()){
            Main.afl.println("No schedules to remove\n");
        }
        else {
            Main.afl.println("Enter the number of the schedule you'd like to remove from folder " + Main.current_folder.getName());
            Main.current_folder.get_schedules(Main.currentaccnt.getUsername());
            for (int i = 0; i < Main.current_folder.get_scheds().size(); i++) {
                Main.afl.println(i + 1 + ". " + Main.current_folder.get_scheds().get(i));
            }
            int num_scheds = Main.current_folder.get_scheds().size();
            Scanner input = new Scanner(System.in);
            int choice = Integer.parseInt(input.next());
            if (choice < 1 || choice > num_scheds) {
                Main.afl.println("Invalid choice.");
            } else {
                choice--;
                String added_sched = Main.current_folder.get_scheds().get(choice);
                if (Main.current_folder.get_scheds().contains(added_sched)) {
                    if (Main.current_folder.get_scheds().remove(added_sched)) {
                        Path temp = Files.move(Paths.get("Accounts" + "\\" + Main.currentaccnt.getUsername() + "\\" + Main.current_folder.getName() + "\\" + added_sched + ".csv"), Paths.get("Accounts" + "\\" + Main.currentaccnt.getUsername() + "\\" + added_sched + ".csv"));
                        Main.afl.println("Schedule " + added_sched + " removed from folder " + Main.current_folder.getName() + "\n");
                    } else {
                        Main.afl.println("Removal unsuccessful. Sorry.");
                    }
                } else {
                    Main.afl.println("Removal unsuccessful. Sorry.");
                }
            }
        }
    }

    public static void add_sched() throws IOException {
        Main.afl.println("Enter the name number of the schedule you would like to add to the '" + Main.current_folder.getName() + "' folder:");
        List<String> curr_scheds = Main.current_folder.get_schedules(Main.currentaccnt.getUsername());
        if (curr_scheds.isEmpty()){
            Main.afl.println("No schedules to add.\n");
        } else {
            for (int i = 0; i < curr_scheds.size(); i++) {
                Main.afl.println(i + 1 + ". " + curr_scheds.get(i));
            }
            int num_scheds = curr_scheds.size();
            Scanner input = new Scanner(System.in);
            int choice = Integer.parseInt(input.next());
            while (choice < 1 || choice > num_scheds) {
                Main.afl.println("Invalid choice, please try again.");
                choice = Integer.parseInt(input.next());
            }
            choice--;
            String added_sched = curr_scheds.get(choice);
            if (!(Main.current_folder.get_scheds().contains(added_sched))) {
                Main.current_folder.get_scheds().add(added_sched);
                Path temp = Files.move(Paths.get("Accounts" + "\\" + Main.currentaccnt.getUsername() + "\\" + added_sched + ".csv"), Paths.get("Accounts" + "\\" + Main.currentaccnt.getUsername() + "\\" + Main.current_folder.getName() + "\\" + added_sched + ".csv"));
                Main.afl.println("Schedule '" + added_sched + "' has been added to folder '" + Main.current_folder.getName() + "'\n");
            } else {
                Main.afl.println("Folder already contains schedule '" + added_sched + "'\n");
            }
        }
    }

    public static void print_folder_list() {
        List<String> folders = Main.currentaccnt.get_folders();
        Main.afl.println("List of saved folders:");
        if (folders.isEmpty()){
            Main.afl.println("No folders saved in account.");
        } else {
            for (String folder : folders) {
                Main.afl.println("   - " + folder);
            }
        }
        Main.afl.println();
    }

    /**
     * Method creates a new folder in the account and assigns the new folder to the current_folder object
     * @throws IOException
     */
    public static void create_folder() throws IOException {
        Main.afl.println("Enter the name for your folder:");
        Scanner scnr = new Scanner(System.in);
        String folder_name = scnr.next();
        if (Main.currentaccnt.get_folders().contains(folder_name)){
            Main.afl.println("Cannot have duplicate folder names.\n");
            Main.afl.println("Unsuccessful folder creation.\n");
        } else {
            Main.currentaccnt.get_folders().add(folder_name);
            Main.current_folder = new Folder(folder_name);
            Main.current_folder.save_folder(Main.currentaccnt.getUsername());
            Main.afl.println("Folder " + folder_name + " created successfully!\n");
        }
    }
}
