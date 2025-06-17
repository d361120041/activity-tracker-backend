package activity_tracker_backend.repository;

import activity_tracker_backend.model.Activity;
import activity_tracker_backend.model.User;
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
public class ActivityRepositoryTest {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    @Autowired
    public ActivityRepositoryTest(ActivityRepository activityRepository, UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    private final String TEST_EMAIL = "test";
    private final String TEST_PASSWORD = "test";

    private final LocalDate TEST_ACTIVITYDATE = LocalDate.of(1970, 1, 1);
    private final String TEST_TITLE = "test";
    private final String TEST_CATEGORY = "test";
    private final LocalTime TEST_STARTTIME = LocalTime.of(0, 0, 0);
    private final LocalTime TEST_ENDTIME = LocalTime.of(23, 59, 59);
    private final String TEST_NOTES = "test";
    private final Byte TEST_MOOD = Byte.valueOf("0");

    private Activity test;
    private User testUser;

    @BeforeEach
    public void setup() {
        activityRepository.deleteAll();
        userRepository.deleteAll();

        testUser = userRepository.save(
                User.builder()
                        .email(TEST_EMAIL)
                        .passwordHash(TEST_PASSWORD)
                        .build()
        );

        test = activityRepository.save(
                Activity.builder()
                        .activityDate(TEST_ACTIVITYDATE)
                        .title(TEST_TITLE)
                        .category(TEST_CATEGORY)
                        .startTime(TEST_STARTTIME)
                        .endTime(TEST_ENDTIME)
                        .notes(TEST_NOTES)
                        .mood(TEST_MOOD)
                        .user(testUser)
                        .build()
        );
    }

    @Test
    void testFindByUserAndActivityDateBetween() {
        List<Activity> activities = activityRepository.findByUserAndActivityDateBetween(testUser, TEST_ACTIVITYDATE, TEST_ACTIVITYDATE);
        assertThat(activities).isNotNull().isNotEmpty();

        Activity activity = activities.getFirst();
        assertThat(activity).isNotNull();
        assertThat(activity.getActivityDate()).isEqualTo(TEST_ACTIVITYDATE);
        assertThat(activity.getTitle()).isEqualTo(TEST_TITLE);
        assertThat(activity.getStartTime()).isEqualTo(TEST_STARTTIME);
        assertThat(activity.getEndTime()).isEqualTo(TEST_ENDTIME);
        assertThat(activity.getNotes()).isEqualTo(TEST_NOTES);
        assertThat(activity.getMood()).isEqualTo(TEST_MOOD);
        assertThat(activity.getUser()).isEqualTo(testUser);
    }

    @Test
    void testFindByUserAndActivityDate() {
        List<Activity> activities = activityRepository.findByUserAndActivityDate(testUser, TEST_ACTIVITYDATE);
        assertThat(activities).isNotNull().isNotEmpty();

        Activity activity = activities.getFirst();
        assertThat(activity).isNotNull();
        assertThat(activity.getActivityDate()).isEqualTo(TEST_ACTIVITYDATE);
        assertThat(activity.getTitle()).isEqualTo(TEST_TITLE);
        assertThat(activity.getStartTime()).isEqualTo(TEST_STARTTIME);
        assertThat(activity.getEndTime()).isEqualTo(TEST_ENDTIME);
        assertThat(activity.getNotes()).isEqualTo(TEST_NOTES);
        assertThat(activity.getMood()).isEqualTo(TEST_MOOD);
        assertThat(activity.getUser()).isEqualTo(testUser);
    }

    @Test
    void testCountActivities() {
        long activitiesNumber = activityRepository.count();

        activityRepository.deleteById(test.getId());
        long activitiesNumberMinusOne = activityRepository.count();
        assertThat(activitiesNumberMinusOne).isEqualTo(--activitiesNumber);
    }

    @Test
    void testFindActivityById() {
        Optional<Activity> findById = activityRepository.findById(test.getId());
        assertThat(findById).isNotEmpty().isPresent();

        Activity activity = findById.get();
        assertThat(activity.getActivityDate()).isEqualTo(TEST_ACTIVITYDATE);
        assertThat(activity.getTitle()).isEqualTo(TEST_TITLE);
        assertThat(activity.getCategory()).isEqualTo(TEST_CATEGORY);
        assertThat(activity.getStartTime()).isEqualTo(TEST_STARTTIME);
        assertThat(activity.getEndTime()).isEqualTo(TEST_ENDTIME);
        assertThat(activity.getNotes()).isEqualTo(TEST_NOTES);
        assertThat(activity.getMood()).isEqualTo(TEST_MOOD);
    }

    @Test
    void testFindAllActivities() {
        List<Activity> activities = activityRepository.findAll();
        assertThat(activities).isNotNull().isNotEmpty();
        assertThat(activities.size()).isEqualTo(activityRepository.count());
    }

    @Test
    void testInsertActivity() {
        final LocalDate INSERT_ACTIVITYDATE = LocalDate.of(1970, 1, 1);
        final String INSERT_TITLE = "insert";
        final String INSERT_CATEGORY = "insert";
        final LocalTime INSERT_STARTTIME = LocalTime.of(0, 0, 0);
        final LocalTime INSERT_ENDTIME = LocalTime.of(23, 59, 59);
        final String INSERT_NOTES = "insert";
        final Byte INSERT_MOOD = Byte.valueOf("0");

        Activity insertActivity = activityRepository.save(
                Activity.builder()
                        .activityDate(INSERT_ACTIVITYDATE)
                        .title(INSERT_TITLE)
                        .category(INSERT_CATEGORY)
                        .startTime(INSERT_STARTTIME)
                        .endTime(INSERT_ENDTIME)
                        .notes(INSERT_NOTES)
                        .mood(INSERT_MOOD)
                        .build()
        );

        Optional<Activity> findById = activityRepository.findById(insertActivity.getId());
        assertThat(findById).isPresent().isNotEmpty();

        Activity activity = findById.get();
        assertThat(activity).isNotNull();
        assertThat(activity.getId()).isNotNull();
        assertThat(activity.getActivityDate()).isEqualTo(INSERT_ACTIVITYDATE);
        assertThat(activity.getTitle()).isEqualTo(INSERT_TITLE);
        assertThat(activity.getCategory()).isEqualTo(INSERT_CATEGORY);
        assertThat(activity.getStartTime()).isEqualTo(INSERT_STARTTIME);
        assertThat(activity.getEndTime()).isEqualTo(INSERT_ENDTIME);
        assertThat(activity.getNotes()).isEqualTo(INSERT_NOTES);
        assertThat(activity.getMood()).isEqualTo(INSERT_MOOD);
    }

    @Test
    void testUpdateActivity() {
        final LocalDate UPDATE_ACTIVITYDATE = LocalDate.of(1970, 1, 2);
        final String UPDATE_TITLE = "update";
        final String UPDATE_CATEGORY = "update";
        final LocalTime UPDATE_STARTTIME = LocalTime.of(0, 0, 1);
        final LocalTime UPDATE_ENDTIME = LocalTime.of(23, 59, 58);
        final String UPDATE_NOTES = "update";
        final Byte UPDATE_MOOD = Byte.valueOf("1");

        Activity activityExisting = activityRepository.findById(test.getId())
                .orElseThrow(AssertionError::new);
        activityExisting.setActivityDate(UPDATE_ACTIVITYDATE);
        activityExisting.setTitle(UPDATE_TITLE);
        activityExisting.setCategory(UPDATE_CATEGORY);
        activityExisting.setStartTime(UPDATE_STARTTIME);
        activityExisting.setEndTime(UPDATE_ENDTIME);
        activityExisting.setNotes(UPDATE_NOTES);
        activityExisting.setMood(UPDATE_MOOD);

        Activity activityUpdated = activityRepository.save(activityExisting);
        assertThat(activityUpdated).isNotNull();
        assertThat(activityUpdated.getId()).isNotNull();

        Optional<Activity> activityFound = activityRepository.findById(activityUpdated.getId());
        assertThat(activityFound).isPresent().isNotEmpty();

        Activity activity = activityFound.get();
        assertThat(activity).isNotNull();
        assertThat(activity.getId()).isNotNull();
        assertThat(activity.getActivityDate()).isEqualTo(UPDATE_ACTIVITYDATE);
        assertThat(activity.getTitle()).isEqualTo(UPDATE_TITLE);
        assertThat(activity.getCategory()).isEqualTo(UPDATE_CATEGORY);
        assertThat(activity.getStartTime()).isEqualTo(UPDATE_STARTTIME);
        assertThat(activity.getEndTime()).isEqualTo(UPDATE_ENDTIME);
        assertThat(activity.getNotes()).isEqualTo(UPDATE_NOTES);
        assertThat(activity.getMood()).isEqualTo(UPDATE_MOOD);
    }

    @Test
    void testDeleteActivityById() {
        long activitiesNumber = activityRepository.count();
        activityRepository.deleteById(test.getId());

        long activitiesNumberMinusOne = activityRepository.count();
        assertThat(activitiesNumberMinusOne).isEqualTo(--activitiesNumber);

        Optional<Activity> activityFound = activityRepository.findById(test.getId());
        assertThat(activityFound).isNotPresent();
    }

    @Test
    void testDeleteAll() {
        activityRepository.deleteAll();

        long activitiesNumber = activityRepository.count();
        assertThat(activitiesNumber).isEqualTo(0);

        List<Activity> activitiesFound = activityRepository.findAll();
        assertThat(activitiesFound).isEmpty();
    }
}
