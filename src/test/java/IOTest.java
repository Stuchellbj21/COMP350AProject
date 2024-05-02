import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
public class IOTest {
    @Test
    public void takeInClassesFileTest() throws IOException {
        SaveLoad.load_allcourses();
        for(Course c : Main.allcourses) System.out.println(c);
    }
}
