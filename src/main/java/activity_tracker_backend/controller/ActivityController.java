package activity_tracker_backend.controller;

import activity_tracker_backend.controller.dto.ActivityDto;
import activity_tracker_backend.controller.dto.CSVDto;
import activity_tracker_backend.controller.response.activity.ActivitiesResponse;
import activity_tracker_backend.enums.Status;
import activity_tracker_backend.model.Activity;
import activity_tracker_backend.model.User;
import activity_tracker_backend.service.ActivityService;
import activity_tracker_backend.service.ReportService;
import activity_tracker_backend.service.UserService;
import activity_tracker_backend.service.dto.ActivityReturn;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final UserService userService;
    private final ActivityService activityService;
    private final ReportService reportService;

    public ActivityController(UserService userService, ActivityService activityService, ReportService reportService) {
        this.userService = userService;
        this.activityService = activityService;
        this.reportService = reportService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllActivities() {
        ActivitiesResponse response = new ActivitiesResponse();
        List<ActivityReturn> activities = activityService.findAll();
        response.setStatusCode(Status.OK.getStatusCode());
        response.setMessage(Status.OK.getMessage());
        response.setActivities(activities);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/byDate")
    public ResponseEntity<?> findActivitiesByDate(UUID userId, LocalDate activityDate) {
        Optional<User> userOpt = userService.findById(userId);
        if(userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("找不到 user");
        }
        User user = userOpt.get();

        List<Activity> activities = activityService.findActivitiesByActivityDate(user, activityDate);
        return ResponseEntity.ok(activities);
    }

    @PostMapping("/create")
    public ResponseEntity<?> addAnActivity(@Valid @RequestBody ActivityDto dto) {
        Optional<User> userFound = userService.findById(dto.getUserId());
        if(userFound.isEmpty()) {
            return ResponseEntity.status(404).body("找不到 user");
        }
        User user = userFound.get();

        Activity activity = Activity.builder()
                .activityDate(dto.getActivityDate())
                .title(dto.getTitle())
                .category(dto.getCategory())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .notes(dto.getNotes())
                .mood(dto.getMood())
                .build();

        Activity activitySaved = activityService.addAnActivity(user, activity);
        return ResponseEntity.ok(activitySaved);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAnActivity(@PathVariable UUID id, @RequestBody Activity payload) {
        Optional<Activity> activityOpt = activityService.findActivityById(id);
        if(activityOpt.isEmpty()) {
            return ResponseEntity.status(404).body("找不到 activity");
        }
        Activity activityExisting = activityOpt.get();

        if(!activityExisting.getUser().getId().equals(payload.getUser().getId())) {
            return ResponseEntity.status(403).body("無權限編輯此活動");
        }

        activityExisting.setActivityDate(payload.getActivityDate());
        activityExisting.setTitle(payload.getTitle());
        activityExisting.setCategory(payload.getCategory());
        activityExisting.setStartTime(payload.getStartTime());
        activityExisting.setEndTime(payload.getEndTime());
        activityExisting.setNotes(payload.getNotes());
        activityExisting.setMood(payload.getMood());

        Activity activity = activityService.updateAnActivity(activityExisting);
        return ResponseEntity.ok(activity);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAnActivityById(@PathVariable UUID id, UUID userId) {
        Optional<Activity> activityOpt = activityService.findActivityById(id);
        if(activityOpt.isEmpty()) {
            return ResponseEntity.status(404).body("找不到 activity");
        }

        Activity activity = activityOpt.get();
        if(!activity.getUser().getId().equals(userId)) {
            return ResponseEntity.status(403).body("無權限刪除此活動");
        }

        activityService.deleteAnActivityById(id);
        return ResponseEntity.ok("刪除成功");
    }

    @PostMapping("/generateReport")
    public ResponseEntity<?> generateReport(@RequestBody CSVDto dto) {
        String fileName = activityService.generateReport(dto.getIds());
        return ResponseEntity.ok(fileName);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<?> downloadReport(@PathVariable String fileName) throws MalformedURLException {
        Path reportRoot = Path.of(reportService.getReportRoot());
        Path filePath = reportRoot.resolve(fileName);
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists()) {
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        }
        return ResponseEntity.badRequest().body("無此檔案");
    }
}
