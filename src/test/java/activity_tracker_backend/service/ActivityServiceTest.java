package activity_tracker_backend.service;

import activity_tracker_backend.model.Activity;
import activity_tracker_backend.model.User;
import activity_tracker_backend.repository.ActivityRepository;
import activity_tracker_backend.repository.UserRepository;
import activity_tracker_backend.service.dto.ActivityReturn;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ActivityServiceTest {

    private final ActivityService activityService;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;

    @Autowired
    public ActivityServiceTest(ActivityService activityService, UserRepository userRepository, ActivityRepository activityRepository) {
        this.activityService = activityService;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
    }

    private final String TEST_EMAIL = "test";
    private final String TEST_PASSWORD = "test";

    private final LocalDate TEST_ACTIVITY_DATE = LocalDate.of(1970, 1, 1);
    private final String TEST_TITLE = "test";
    private final String TEST_CATEGORY = "test";
    private final LocalTime TEST_START_TIME = LocalTime.of(0, 0, 0);
    private final LocalTime TEST_END_TIME = LocalTime.of(23, 59, 59);
    private final String TEST_NOTES = "test";
    private final Byte TEST_MOOD = Byte.valueOf("0");

    private User testUser;
    private Activity testActivity;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        activityRepository.deleteAll();

        testUser = userRepository.save(
                User.builder()
                        .email(TEST_EMAIL)
                        .passwordHash(TEST_PASSWORD)
                        .build()
        );

        testActivity = activityRepository.save(
                Activity.builder()
                        .activityDate(TEST_ACTIVITY_DATE)
                        .title(TEST_TITLE)
                        .category(TEST_CATEGORY)
                        .startTime(TEST_START_TIME)
                        .endTime(TEST_END_TIME)
                        .notes(TEST_NOTES)
                        .mood(TEST_MOOD)
                        .user(testUser)
                        .build()
        );
    }

    @Test
    void testFindActivitiesByActivityDate() {
        List<Activity> activities = activityService.findActivitiesByActivityDate(testUser, testActivity.getActivityDate());
        assertThat(activities).isNotNull().isNotEmpty();

        Activity activity = activities.getFirst();
        assertThat(activity.getActivityDate()).isEqualTo(TEST_ACTIVITY_DATE);
        assertThat(activity.getTitle()).isEqualTo(TEST_TITLE);
        assertThat(activity.getCategory()).isEqualTo(TEST_CATEGORY);
        assertThat(activity.getStartTime()).isEqualTo(TEST_START_TIME);
        assertThat(activity.getEndTime()).isEqualTo(TEST_END_TIME);
        assertThat(activity.getNotes()).isEqualTo(TEST_NOTES);
        assertThat(activity.getMood()).isEqualTo(TEST_MOOD);
        assertThat(activity.getUser()).isEqualTo(testUser);
    }

    @Test
    void testFindActivitiesByActivityDateBetween() {
        List<Activity> activities = activityService.findActivitiesByActivityDateBetween(testUser, testActivity.getActivityDate(), testActivity.getActivityDate());
        assertThat(activities).isNotNull().isNotEmpty();

        Activity activity = activities.getFirst();
        assertThat(activity.getActivityDate()).isEqualTo(TEST_ACTIVITY_DATE);
        assertThat(activity.getTitle()).isEqualTo(TEST_TITLE);
        assertThat(activity.getCategory()).isEqualTo(TEST_CATEGORY);
        assertThat(activity.getStartTime()).isEqualTo(TEST_START_TIME);
        assertThat(activity.getEndTime()).isEqualTo(TEST_END_TIME);
        assertThat(activity.getNotes()).isEqualTo(TEST_NOTES);
        assertThat(activity.getMood()).isEqualTo(TEST_MOOD);
        assertThat(activity.getUser()).isEqualTo(testUser);
    }

    @Test
    void testAddAnActivity() {
        final LocalDate activityDate = LocalDate.of(1970, 1, 1);
        final String title = "add";
        final String category = "add";
        final LocalTime startTime = LocalTime.of(0, 0, 0);
        final LocalTime endTime = LocalTime.of(23 ,59, 59);
        final String notes = "add";
        final Byte mood = Byte.valueOf("0");

        Activity activityAdded = activityService.addAnActivity(
                testUser,
                Activity.builder()
                        .activityDate(activityDate)
                        .title(title)
                        .category(category)
                        .startTime(startTime)
                        .endTime(endTime)
                        .notes(notes)
                        .mood(mood)
                        .build()
        );

        Optional<Activity> activityFound = activityRepository.findById(activityAdded.getId());
        assertThat(activityFound).isPresent();

        Activity activity = activityFound.get();
        assertThat(activity).isNotNull();
        assertThat(activity.getActivityDate()).isEqualTo(activityDate);
        assertThat(activity.getTitle()).isEqualTo(title);
        assertThat(activity.getCategory()).isEqualTo(category);
        assertThat(activity.getStartTime()).isEqualTo(startTime);
        assertThat(activity.getEndTime()).isEqualTo(endTime);
        assertThat(activity.getNotes()).isEqualTo(notes);
        assertThat(activity.getMood()).isEqualTo(mood);
        assertThat(activity.getUser()).isEqualTo(testUser);
    }

    @Test
    void testUpdateAnActivity() {
        final LocalDate activityDate = LocalDate.of(1970, 1, 2);
        final String title = "update";
        final String category = "update";
        final LocalTime startTime = LocalTime.of(0, 0, 1);
        final LocalTime endTime = LocalTime.of(23, 59, 58);
        final String notes = "update";
        final Byte mood = Byte.valueOf("1");

        Activity activityExisting = activityRepository.findById(testActivity.getId())
                .orElseThrow(RuntimeException::new);
        activityExisting.setActivityDate(activityDate);
        activityExisting.setTitle(title);
        activityExisting.setCategory(category);
        activityExisting.setStartTime(startTime);
        activityExisting.setEndTime(endTime);
        activityExisting.setNotes(notes);
        activityExisting.setMood(mood);

        Activity activityUpdated = activityService.updateAnActivity(activityExisting);
        Optional<Activity> activityFound = activityRepository.findById(activityUpdated.getId());
        assertThat(activityFound).isPresent();

        Activity activity = activityFound.get();
        assertThat(activity).isNotNull();
        assertThat(activity.getActivityDate()).isEqualTo(activityDate);
        assertThat(activity.getTitle()).isEqualTo(title);
        assertThat(activity.getCategory()).isEqualTo(category);
        assertThat(activity.getStartTime()).isEqualTo(startTime);
        assertThat(activity.getEndTime()).isEqualTo(endTime);
        assertThat(activity.getNotes()).isEqualTo(notes);
        assertThat(activity.getMood()).isEqualTo(mood);
        assertThat(activity.getUser()).isEqualTo(testUser);
    }

    @Test
    void testDeleteActivityById() {
        Optional<Activity> activityBeforeDeleted = activityRepository.findById(testActivity.getId());
        assertThat(activityBeforeDeleted).isPresent();

        activityService.deleteAnActivityById(testActivity.getId());

        Optional<Activity> activityAfterDeleted = activityRepository.findById(testActivity.getId());
        assertThat(activityAfterDeleted).isEmpty();
    }

    @Test
    void testFindAll() {
        List<ActivityReturn> activities = activityService.findAll();
        assertThat(activities).isNotNull();
        assertThat(activities).isNotEmpty();

        long activityNumber = activityRepository.count();
        assertThat(activityNumber).isEqualTo(activities.size());
    }
}
