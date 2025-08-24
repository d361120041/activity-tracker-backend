package activity_tracker_backend.service;

import activity_tracker_backend.model.Activity;
import activity_tracker_backend.util.CSVUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class ReportService {

    public static final String applicationRoot = new ApplicationHome().getDir().getPath();

    @Value("${report.dir}")
    public String reportDir;

    private final CSVUtilities csvUtilities;

    public ReportService(CSVUtilities csvUtilities) {
        this.csvUtilities = csvUtilities;
    }

    public String getReportRoot() {
        return applicationRoot + reportDir;
    }

    public String generateReport(List<Activity> data) throws IOException {
        DateTimeFormatter template = DateTimeFormatter.ofPattern("yyyyMMddkkmmss");
        String now = template.format(LocalDateTime.now());

        String simpleClassName = data.getFirst().getClass().getSimpleName();
        String fileName = simpleClassName + "_" + now + ".csv";

        Path reportsRoot = Path.of(getReportRoot());

        if (Files.notExists(reportsRoot)) {
            Files.createDirectory(reportsRoot);
        }
        Path filePath = reportsRoot.resolve(fileName);

        csvUtilities.activitiesToCSV(data, filePath);

        return fileName;
    }
}
