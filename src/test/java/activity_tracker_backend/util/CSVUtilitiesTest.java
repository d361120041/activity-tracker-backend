package activity_tracker_backend.util;

import activity_tracker_backend.model.Activity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest
public class CSVUtilitiesTest {

    private final CSVUtilities csvUtilities;

    @Autowired
    public CSVUtilitiesTest(CSVUtilities csvUtilities) {
        this.csvUtilities = csvUtilities;
    }

    private Activity testActivity;

    @BeforeEach
    void setup() {
        testActivity = Activity.builder()
                .activityDate(LocalDate.now())
                .title("test")
                .category("test")
                .startTime(LocalTime.now())
                .endTime(LocalTime.now())
                .notes("test")
                .mood(Byte.valueOf("3"))
                .build();
    }

    @Test
    void testGenerateCSV() throws IOException {
        Path target = Paths.get("C:/Users/江宗翰/Desktop/test.csv");
        String source = "test";
        csvUtilities.generateUTF_8CSV(source, target);
    }

    @Test
    void testParseActivity() {
        String activity = csvUtilities.parseActivity(testActivity);
        System.out.println("activity="+activity);
    }

    @Test
    void parseActivities() {
        String activities = csvUtilities.parseActivities(List.of(testActivity));
        System.out.println("activities="+activities);
    }
}
