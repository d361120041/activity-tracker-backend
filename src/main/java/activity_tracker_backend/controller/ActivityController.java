package activity_tracker_backend.controller;

import activity_tracker_backend.controller.dto.ActivityDto;
import activity_tracker_backend.controller.dto.CSVDto;
import activity_tracker_backend.model.Activity;
import activity_tracker_backend.model.User;
import activity_tracker_backend.service.ActivityService;
import activity_tracker_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final UserService userService;
    private final ActivityService activityService;

    public ActivityController(UserService userService, ActivityService activityService) {
        this.userService = userService;
        this.activityService = activityService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllActivities() {
        List<Activity> activities = activityService.findAll();
        return ResponseEntity.ok().body(activities);
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

    @PostMapping("/csv")
    public ResponseEntity<?> activitiesToCSV(@RequestBody CSVDto dto) {
        activityService.generateActivityCSV(dto.getIds());
        return ResponseEntity.ok("CSV 產生成功");
    }

}
