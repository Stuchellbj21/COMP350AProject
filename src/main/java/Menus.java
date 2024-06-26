import java.io.IOException;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class Menus {
    public static boolean login_menu() throws IOException, SQLException {
        //Checks to see if any accounts exist. If not, returns false.

//        if (Main.accounts.isEmpty()) {
//            Main.afl.println();
//            return false;
//        }
        //-----------------------------
        // DATABASE isEmpty
        if (Main.db.users_is_empty()) {
            Main.afl.println("No accounts exist -> Please create account or exit\n");
            return false;
        }

        String un = GeneralUtils.input("Enter (username) -> / (back) -> return to Account menu\n");
        if (un.equalsIgnoreCase("back")) {
            return false;
        }
        String pw = GeneralUtils.input("Enter (password) -> / (back) -> return to Account menu\n");
        if (pw.equalsIgnoreCase("back")) {
            return false;
        }
        // if (un.equals(Main.accounts.get(pw.hashCode()))) {
        if (Main.db.authenticate(un, pw)) {
            String major = Main.db.get_major(un); // get major from database
            Main.currentaccnt = new Account(un, pw, Major.valueOf(major));
            try {
                SaveLoad.load_courses_taken();
            } catch (IOException ioe) {
                Main.afl.println("Error: " + ioe.getMessage());
            }
            SaveLoad.load_schedules();
            SaveLoad.load_acct_info(); // todo: Rework load folders with info.txt file
            return true;
        } else {
            Main.afl.println("Incorrect password or username");
            return false;
        }
    }

    public static void accountMenu() throws IOException, SQLException {
        while (true) {
            String in = GeneralUtils.input("Enter (create) -> create new Account/ (login) -> login to existing account\n" +
                    "(close) -> exit program\n");
            if (in.equalsIgnoreCase("create")) {
                if (AccountCreation.createAccount()) {
                    //successfully create account and move into schedule menu
                    schedule_menu();
                }
            } else if (in.equalsIgnoreCase("login")) {
                if (login_menu()) {
                    schedule_menu();
                    SaveLoad.account_flush();
                }
            } else if (in.equalsIgnoreCase("close")) {
                if (Main.currentaccnt != null) {
                    SaveLoad.account_flush(); // should not be called when no accounts have been made
                }
                //SaveLoad.close_accounts();
                System.exit(0); // kill the program with no errors
            } else {
                Main.afl.println("Error: '" + in + "' is an invalid response");
            }
        }
    }

    public static void gen_sched_menu() throws IOException, SQLException {
        String semester;
        String choice = GeneralUtils.input("What semester would you like a schedule generated for?\n");
        while (!(choice.equalsIgnoreCase("Fall")) && !(choice.equalsIgnoreCase("Spring"))) {
            choice = GeneralUtils.input("Invalid semester. Please enter 'Fall' or 'Spring'\n");
        }
        if (choice.equalsIgnoreCase("Fall")) {
            semester = "Fall";
        } else {
            semester = "Spring";
        }

        //Provide space inbetween
        Main.afl.println();
        List<Schedule> potential_scheds = Account.random_gen(2020, semester);
        Main.afl.println();
        String sched_choice = GeneralUtils.input("Would you like to add any of the following schedules (y/n)?\n");
        while (true) {
            while (!(sched_choice.equalsIgnoreCase("n")) && !(sched_choice.equalsIgnoreCase("y"))) {
                sched_choice = GeneralUtils.input("Invalid choice. Please re-enter.\n");
            }
            if (sched_choice.equalsIgnoreCase("y")) {
                for (int i = 0; i < potential_scheds.size(); i++) {
                    int number = i + 1;
                    Main.afl.println("   " + number + ". " + potential_scheds.get(i).get_name());
                }
                int sched_num = GeneralUtils.get_int_from_user("Which schedule would you like to add?\n");
                boolean invalid_choice = true;
                while (invalid_choice) {
                    if (!(sched_num >= 1 && sched_num <= potential_scheds.size())) {
                        sched_num = GeneralUtils.int_input("Choice is out of range. Please re-enter.\n");
                    } else {
                        invalid_choice = false;
                    }
                }
                sched_num--;
                Main.currentsched = potential_scheds.get(sched_num);
                while (true) {
                    try {
                        String sched_name = GeneralUtils.input("Please enter a name for your schedule:\n");
                        Validations.is_valid_name(sched_name);
                        Main.currentsched.setName(sched_name);
                        //Main.afl.println("New schedule name successfully set!");
                        break;
                    } catch (IllegalArgumentException iae) {
                        Main.afl.println(iae.getMessage());
                    }
                }
                potential_scheds.remove(sched_num);
                Main.currentsched.save(Main.currentaccnt.getUsername()); // save to file
                Main.currentaccnt.save_schedule(Main.currentsched.get_name()); // save to list
                Main.afl.println(Main.currentsched.get_name() + " saved successfully");
                sched_choice = GeneralUtils.input("\nWould you like to add any additional schedules (y/n)?\n");
            } else {
                break;
            }
        }
    }

    public static void schedule_menu() throws SQLException, IOException {
        while (true) {
            String in = GeneralUtils.input("(load) -> load a schedule/(new) -> create a new blank schedule/(ls) -> list saved schedules/\n(f) -> access folders/(gen) -> generate schedules/(ct) -> add courses already taken/\n(b) -> back to account menu: ");
            if (in.equalsIgnoreCase("load")) {
                String schedname = GeneralUtils.input("Enter the name of the schedule to load: ");
                try {
                    if (Main.currentaccnt.get_schednames().contains(schedname)) {
                        Main.currentsched.load(Main.currentaccnt.getUsername(), schedname);
                        Main.afl.println("Schedule '" + schedname + "' loaded successfully"); //todo: go to sched menu?
                        in_schedule_menu();
                    } else Main.afl.println("Error: that schedule does not exist");
                } catch (IOException ioe) {
                    Main.afl.println("Error: file did not load correctly");
                }
            } else if (in.equalsIgnoreCase("new")) {
                Main.currentsched = new Schedule();
                Main.afl.println("New blank schedule created");
                in_schedule_menu();
            } else if (in.equalsIgnoreCase("f")) folder_menu();
            else if (in.equalsIgnoreCase("gen")) gen_sched_menu();
            else if (in.equalsIgnoreCase("ls")) Main.currentaccnt.print_schedule_list();
            else if (in.equalsIgnoreCase("ct")) {
                try {
                    Main.currentaccnt.enter_courses_taken();
                } catch (IOException ioe) {
                    Main.afl.println("Error: " + ioe.getMessage());
                }
            }
            //go back to account menu
            else if (in.equalsIgnoreCase("b")) {
                Main.search.reset();
                break;
            } else Main.afl.println("Error: invalid input");
        }
    }

    public static void modify_schedule_menu() {
        while (true) {
            String in = GeneralUtils.input("What attribute of the current schedule would you like to modify? (name/semester/year/none): ");
            if (in.equalsIgnoreCase("name")) Main.currentsched.set_name_with_checks(Main.currentaccnt.getUsername());
            else if (in.equalsIgnoreCase("semester")) Main.currentsched.set_semester_with_checks();
            else if (in.equalsIgnoreCase("year")) Main.currentsched.set_year_with_checks();
            else if (in.equalsIgnoreCase("none")) break;
            else Main.afl.println("Error: '" + in + "' is not a valid attribute to modify");
        }
    }

    public static void in_schedule_menu() throws SQLException, IOException {
        while (true) {
            Main.afl.println(Main.currentsched.to_str());
            //modify will allow editing of the schedule
            String in = GeneralUtils.input("Enter (modify) -> modify schedule/(search) -> search for courses/(ac) -> add_course/\n(rc) -> remove course/(save) -> save schedule/(filter) -> edit filters/(del) -> delete schedule/\n(extra) -> add extracurricular/(ws) -> add to wishlist/(undo) -> revert last course addition/\n(exit) -> exit to schedule selection: ");
            if (in.equalsIgnoreCase("modify")) modify_schedule_menu();
            else if (in.equalsIgnoreCase("search")) Search.prompt_and_search();
            else if (in.equalsIgnoreCase("ac")) {
                if (Main.search.get_filtered_results() == null || Main.search.get_filtered_results().isEmpty())
                    Main.afl.println("Error: if you wish to add a course, you must add it from search results and you currently have no search results");
                else SchedAddRemove.add_course_to_schedule();
            } else if (in.equalsIgnoreCase("rc")) {
                if (Main.currentsched.get_courses().isEmpty())
                    Main.afl.println("Error: the current schedule does not contain any courses for removal");
                else SchedAddRemove.remove_course_from_schedule();
            } else if (in.equalsIgnoreCase("extra")) {
                SchedAddRemove.extracurricular();
            } else if (in.equalsIgnoreCase("ws")) {
                if (Main.search.get_filtered_results() == null || Main.search.get_filtered_results().isEmpty())
                    Main.afl.println("Error: If you wish to add a course to wishlist, you must add it from search results and you currently have no search results.");
                else {
                    SchedAddRemove.wish_list();
                }
            } else if (in.equalsIgnoreCase("undo")) {
                SchedAddRemove.revert_change();
            } else if (in.equalsIgnoreCase("save")) {
//                try {
                if (Main.currentsched.get_name().equalsIgnoreCase("blank schedule"))
                    Main.afl.println("Error: rename schedule to something other than 'Blank Schedule' before saving");
                else {
                    if(new File("Accounts\\" + Main.currentaccnt.getUsername() + "\\" + Main.current_folder.getName() + "\\" + Main.currentsched.get_name() + ".csv").exists()){
                        Main.currentsched.f_save(Main.currentaccnt.getUsername(),Main.current_folder.getName()); // save to file
                        Main.currentaccnt.save_schedule(Main.currentsched.get_name()); // save to list
                        Main.afl.println(Main.currentsched.get_name() + " saved successfully");
                    } else {
                        Main.currentsched.save(Main.currentaccnt.getUsername()); // save to file
                        Main.currentaccnt.save_schedule(Main.currentsched.get_name()); // save to list
                        Main.afl.println(Main.currentsched.get_name() + " saved successfully");
                    }
                }
            } else if (in.equalsIgnoreCase("del")) {
                Main.currentaccnt.delete_schedule(Main.currentsched.getName()); // todo
                Main.currentsched.delete(Main.currentaccnt.getUsername());
                Main.afl.println("schedule deleted successfully");
                break;
            } else if (in.equalsIgnoreCase("filter")) FilterUtils.edit_filters();
            else if (in.equalsIgnoreCase("exit"))
                break; //exit to schedule selection menu -> load schedule, new blank schedule, edit schedule (back to account menu)
            else Main.afl.println("Error: '" + in + "' is an invalid response");
        }
    }

    public static void folder_menu() throws IOException {
        while (true) {
            String in = GeneralUtils.input("(load) -> load a folder/(new) -> create a new folder/(lf) -> list of folders/(b) -> back to account menu: \n");
            if (in.equalsIgnoreCase("load")) {
                if (!Main.currentaccnt.get_folders().isEmpty()) {
                    String folder_name = GeneralUtils.input("Enter the name of the folder to load: ");
                    if (new File("Accounts\\" + Main.currentaccnt.getUsername() + "\\" + folder_name).exists()) {
                        Main.current_folder.load_folder(Main.currentaccnt.getUsername(), folder_name);
                        Main.afl.println("Folder '" + folder_name + "' loaded successfully\n");
                        in_folder_menu();
                    } else Main.afl.println("Error: that folder does not exist\n");
                } else {
                    Main.afl.println("No folders saved in account.\n");
                }
            } else if (in.equalsIgnoreCase("new")) {
                FolderOps.create_folder();
            } else if (in.equalsIgnoreCase("b")) {
                //SaveLoad.load_acct_info(); //
                break;
            } else if (in.equalsIgnoreCase("lf")) FolderOps.print_folder_list();
            else Main.afl.println("Error: invalid input");
            Main.current_folder = new Folder();
        }
    }

    public static void in_folder_menu() throws IOException {
        while (true) {
            Main.afl.println(Main.current_folder.to_str());
            //modify will allow editing of the folder
            String in = GeneralUtils.input("Enter (add) -> add schedule to folder/(remove) -> remove schedule from folder/(open) -> open schedule from folder/(del) -> delete folder/\n(exit) -> exit to menu: \n");
            if (in.equalsIgnoreCase("add")) FolderOps.add_sched();
            else if (in.equalsIgnoreCase("remove")) FolderOps.remove_sched();
            else if (in.equalsIgnoreCase("open")) {
                String schedname = GeneralUtils.input("Enter the name of the schedule to load: ");
                try {
                    if (new File("Accounts\\" + Main.currentaccnt.getUsername() + "\\" + Main.current_folder.getName() + "\\" + schedname + (schedname.endsWith(".csv") ? "" : ".csv")).exists()) {
                        Main.currentsched.f_load(Main.currentaccnt.getUsername(), Main.current_folder.getName(), schedname);
                        Main.afl.println("Schedule '" + schedname + "' loaded successfully\n");
                        in_schedule_menu();
                        Main.currentsched.f_save(Main.currentaccnt.getUsername(), Main.current_folder.getName());
                    } else Main.afl.println("Error: that schedule does not exist");
                } catch (IOException | SQLException ioe) {
                    Main.afl.println("Error: file did not load correctly");
                }
            } else if (in.equalsIgnoreCase("exit")) {
                Main.current_folder.save_folder(Main.currentaccnt.getUsername());
                break; //exit to schedule selection menu -> load schedule, new blank schedule, edit schedule (back to account menu)
            } else if (in.equalsIgnoreCase("del")) {
                FolderOps.delete_folder();
                Main.afl.println("Folder successfully deleted");
                break;
            } else Main.afl.println("Error: '" + in + "' is an invalid response");
        }
    }
}