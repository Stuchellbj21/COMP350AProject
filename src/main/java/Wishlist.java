import java.sql.SQLOutput;
import java.util.*;
import java.io.*;
public class Wishlist {
    /*
    - Will utilize a list to store class items selected by users to better visualize and track desired courses
    - Will be able to be called after logging in, likely on the screen containing "schedule"
     */

    public static List<Course> wishedCourses; //Will contain the courses a user adds to Wishlist

    /*
    - Adds course to wishlist
     */
    private void Add(Course W){
        wishedCourses.add(W);
    }
    /*
    - Removes course from wishlist
     */
    private void Remove(Course W){
        wishedCourses.remove(W);
    }

    /*
    - Displays all courses within wishlist
     */
    private void View(){
        for(int i = 0; i < wishedCourses.size(); i++){
            System.out.println(wishedCourses.get(i));
        }
    }
}
