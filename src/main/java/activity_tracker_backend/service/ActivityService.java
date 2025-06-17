package activity_tracker_backend.service;

import activity_tracker_backend.model.Activity;
import activity_tracker_backend.model.User;
import activity_tracker_backend.repository.ActivityRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
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
}
