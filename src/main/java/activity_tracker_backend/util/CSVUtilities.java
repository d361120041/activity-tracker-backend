package activity_tracker_backend.util;

import activity_tracker_backend.model.Activity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringJoiner;

import static java.nio.file.Files.newOutputStream;

@Component
public class CSVUtilities {

    public void activitiesToCSV(List<Activity> activities, String target) {
        String source = parseActivities(activities);
        generateUTF_8CSV(source, target);
    }

    public String parseActivities(List<Activity> activities) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Activity activity : activities) {
            stringBuilder.append(new StringJoiner(", ")
                    .add(activity.getActivityDate().toString())
                    .add(activity.getTitle())
                    .add(activity.getCategory())
                    .add(activity.getStartTime().toString())
                    .add(activity.getEndTime().toString())
                    .add(activity.getNotes())
                    .add(activity.getMood().toString())
            ).append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    public String parseActivity(Activity activity) {
        return new StringJoiner(", ")
                .add(activity.getActivityDate().toString())
                .add(activity.getTitle())
                .add(activity.getCategory())
                .add(activity.getStartTime().toString())
                .add(activity.getEndTime().toString())
                .add(activity.getNotes())
                .add(activity.getMood().toString())
                .toString();
    }

    public void generateUTF_8CSV(String source, String target) {
        Path filePath = Paths.get(target);
        try (
                OutputStream os = newOutputStream(filePath);
        ) {
            os.write(source.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
