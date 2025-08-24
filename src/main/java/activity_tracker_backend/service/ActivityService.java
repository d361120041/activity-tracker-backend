package activity_tracker_backend.service;

import activity_tracker_backend.model.Activity;
import activity_tracker_backend.model.User;
import activity_tracker_backend.repository.ActivityRepository;
import activity_tracker_backend.service.dto.ActivityResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ReportService reportService;

    public ActivityService(ActivityRepository activityRepository, ReportService reportService) {
        this.activityRepository = activityRepository;
        this.reportService = reportService;
    }

    public List<Activity> findActivitiesByActivityDate(User user, LocalDate activityDate) {
        return activityRepository.findByUserAndActivityDate(user, activityDate);
    }

    public List<Activity> findActivitiesByActivityDateBetween(User user, LocalDate startDate, LocalDate endDate) {
        return activityRepository.findByUserAndActivityDateBetween(user, startDate, endDate);
    }

    public Optional<Activity> findActivityById(UUID id) {
        return activityRepository.findById(id);
    }

    public List<ActivityResponse> findAll() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        return activityRepository.findAll().stream()
                .map(activity -> {
                    ActivityResponse dto = new ActivityResponse();
                    dto.setId(activity.getId());
                    dto.setActivityDate(activity.getActivityDate());
                    dto.setTitle(activity.getTitle());
                    dto.setCategory(activity.getCategory());
                    dto.setStartTime(activity.getStartTime());
                    dto.setEndTime(activity.getEndTime());
                    dto.setNotes(activity.getNotes());
                    dto.setMood(activity.getMood());
                    dto.setCreatedAt(activity.getCreatedAt().format(formatter));
                    dto.setUpdatedAt(activity.getUpdatedAt().format(formatter));
                    dto.setUserEmail(activity.getUser().getEmail());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Activity addAnActivity(User user, Activity activity) {
        activity.setUser(user);
        return activityRepository.save(activity);
    }

    public Activity updateAnActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    public void deleteAnActivityById(UUID id) {
        activityRepository.deleteById(id);
    }

    public String generateReport(List<UUID> ids) {
        try {
            List<Activity> activities = activityRepository.findByIdIn(ids);
            return reportService.generateReport(activities);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
