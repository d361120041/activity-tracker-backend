package activity_tracker_backend.repository;

import activity_tracker_backend.model.Activity;
import activity_tracker_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    List<Activity> findByUserAndActivityDate(User user, LocalDate activityDate);

    List<Activity> findByUserAndActivityDateBetween(User user, LocalDate start, LocalDate end);
}
