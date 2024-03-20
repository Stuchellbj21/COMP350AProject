import jdk.jshell.spi.ExecutionControlProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.EmptyStackException;
import java.util.List;

class AccountTest {

    Schedule currentSchedule;
    String sched_name;
    List<String> schednames;
    // Create Account
    Account myAccount = new Account("Stuchellbj21", "53444", Major.COMP );
    @Test
    public void  ConstructorTest() throws Exception{
        // Test gets for Account variables
        if(myAccount.getUsername().equals("Stuchellbj21")) { }else{throw new Exception("failed name get");}
        // if(myAccount.verify_password(53444)) { throw new Exception("failed passwordhash get");}
        if(myAccount.getMajor()!= Major.COMP) { throw new Exception("failed major get");}
    }

    @Test
    public void settersTest() throws Exception{
        Account newAcc = new Account();
        newAcc.setUsername("MyNewUsername");
        newAcc.setMajor(Major.ACCT);
        if(newAcc.getUsername().equals("MyNewUsername")){ } else {throw new Exception("failed username get"); }
        if(newAcc.getMajor() != Major.ACCT){ throw new Exception("failed major set");}
    }

    @Test
    void changeMajor() throws Exception {
        myAccount.change_major(Major.COMP);
        if (myAccount.getMajor() != Major.COMP) {
            throw new Exception("failed major change");
        }
    }
    @Test
    void change_username() throws Exception {
        myAccount.change_username("Bstuchell15");
        if (myAccount.getUsername().equals("Bstuchell15")) {
            throw new Exception("failed username change");
        }
    }
    @Test
    void testSaveSchedule() throws Exception {
        currentSchedule.save_schedule(sched_name);

    }
    @Test
    void testLoadSchedule() throws Exception {
        schednames.load_schedule(sched_name);
    }

    @Test
    void testDeleteSchedule() throws Exception{
        schednames.delete_schedule(sched_name);
    }

    @Test
    void testDeleteAccount() throws Exception{
        schednames.delete_account(myAccount);
        if(myAccount != null){
            throw new Exception("myAccount still exists");
        }
    }

    @Test
    void testHasSchedule() throws Exception {
        schednames.save_schedule(myAccount);
        if(schednames.has_schedule("myAccount")){ } else { throw new Exception("Schedule not found"); }
    }
}