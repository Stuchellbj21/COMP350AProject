import java.io.IOException;
import java.io.File;

public class Menus {
    public static boolean login_menu() throws IOException {
        //Checks to see if any accounts exist. If not, returns false.
        if (Main.accounts.isEmpty()) {
            Main.autoflush.println("No accounts exist -> Please create account or exit");
            System.out.println();
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
        if (un.equals(Main.accounts.get(pw.hashCode()))) { //todo: ensure this is valid way to check password
            Main.currentaccnt = new Account(un, pw, Major.COMP); //  todo: for now, this just makes a new schedule with default major;
            MainSaveLoad.load_schedules();
            return true;
        } else {
            Main.autoflush.println("Incorrect password or username");
            return false;
        }
    }

    public static void accountMenu() throws IOException {
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
                }
            } else if (in.equalsIgnoreCase("close")) {
                if (Main.currentaccnt != null) {
                    MainSaveLoad.account_flush(); // should not be called when no accounts have been made
                }
                MainSaveLoad.close_accounts();
                System.exit(0); // kill the program with no errors
            } else {
                Main.autoflush.println("Error: '" + in + "' is an invalid response");
            }
        }
    }

    public static void schedule_menu() {
        while(true) {
            String in = GeneralUtils.input("(load) -> load a schedule/(new) -> create a new blank schedule/(ls) -> list saved schedules/(b) -> back to account menu: ");
            if(in.equalsIgnoreCase("load")) {
                String schedname = GeneralUtils.input("Enter the name of the schedule to load: ");
                try{
                    if(new File("Accounts\\" + Main.currentaccnt.getUsername() + "\\" + schedname + (schedname.endsWith(".csv") ? "" : ".csv")).exists()) {
                        Main.currentsched.load(Main.currentaccnt.getUsername(), schedname);
                        Main.autoflush.println("Schedule '" + schedname + "' loaded successfully");
                        in_schedule_menu();
                    }
                    else Main.autoflush.println("Error: that schedule does not exist");
                }
                catch(IOException ioe) {Main.autoflush.println("Error: file did not load correctly");}
            }
            else if(in.equalsIgnoreCase("new")) {
                Main.currentsched = new Schedule();
                Main.autoflush.println("New blank schedule created");
                in_schedule_menu();
            }
            else if(in.equalsIgnoreCase("ls")) Main.currentaccnt.print_schedule_list();
                //go back to account menu
            else if(in.equalsIgnoreCase("b")) break;
            else Main.autoflush.println("Error: invalid input");
        }
    }

    public static void modify_schedule_menu() {
        while (true) {
            String in = GeneralUtils.input("What attribute of the current schedule would you like to modify? (name/semester/year/none): ");
            if(in.equalsIgnoreCase("name")) Main.currentsched.set_name_with_checks(Main.currentaccnt.getUsername());
            else if(in.equalsIgnoreCase("semester")) Main.currentsched.set_semester_with_checks();
            else if(in.equalsIgnoreCase("year")) Main.currentsched.set_year_with_checks();
            else if(in.equalsIgnoreCase("none")) break;
            else Main.autoflush.println("Error: '" + in + "' is not a valid attribute to modify");
        }
    }

    public static void in_schedule_menu() {
        while(true) {
            Main.autoflush.println(Main.currentsched.to_str());
            //modify will allow editing of the schedule
            String in = GeneralUtils.input("Enter (modify) -> modify schedule/(search) -> search for courses/(ac) -> add_course/\n(rc) -> remove course/(save) -> save schedule/(filter) -> edit filters/(del) -> delete schedule\n(exit) -> exit to schedule selection: ");
            if(in.equalsIgnoreCase("modify")) modify_schedule_menu();
            else if(in.equalsIgnoreCase("search")) Search.prompt_and_search();
            else if(in.equalsIgnoreCase("ac")) {
                if(Main.search.get_filtered_results() == null || Main.search.get_filtered_results().isEmpty())
                    Main.autoflush.println("Error: if you wish to add a course, you must add it from search results and you currently have no search results");
                else MainSchedAddRemove.add_course_to_schedule();
            } else if (in.equalsIgnoreCase("rc")) {
                if (Main.currentsched.get_courses().isEmpty())
                    Main.autoflush.println("Error: the current schedule does not contain any courses for removal");
                else MainSchedAddRemove.remove_course_from_schedule();
            }
            else if(in.equalsIgnoreCase("save")) {
                try{
                    if(Main.currentsched.get_name().equalsIgnoreCase("blank schedule")) Main.autoflush.println("Error: rename schedule to something other than 'Blank Schedule' before saving");
                    else {
                        Main.currentsched.save(Main.currentaccnt.getUsername());
                        Main.currentaccnt.save_schedule(Main.currentsched.get_name());
                        Main.autoflush.println(Main.currentsched.get_name() + " saved successfully");
                    }
                }
                catch(IOException ioe) {Main.autoflush.println(ioe.getMessage());}
            }
            else if(in.equalsIgnoreCase("del")) {
                Main.currentaccnt.delete_schedule(Main.currentsched.getName()); // todo
                Main.currentsched.delete(Main.currentaccnt.getUsername());
                Main.autoflush.println("schedule deleted successfully");
                break;
            }
            else if(in.equalsIgnoreCase("filter")) MainFilterUtils.edit_filters();
            else if(in.equalsIgnoreCase("exit")) break; //exit to schedule selection menu -> load schedule, new blank schedule, edit schedule (back to account menu)
            else Main.autoflush.println("Error: '" + in + "' is an invalid response");
        }
    }
}
