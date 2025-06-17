package activity_tracker_backend.repository;

import activity_tracker_backend.model.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class UserRepositoryTest {

    private final String TEST_EMAIL = "test@test.com";
    private final String TEST_PASSWORD = "test";

    private final UserRepository userRepository;

    @Autowired
    public UserRepositoryTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private User userTest;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        userTest = userRepository.save(
                User.builder()
                        .email(TEST_EMAIL)
                        .passwordHash(TEST_PASSWORD)
                        .build()
        );
    }

    @Test
    void testFindUserByEmail() {
        Optional<User> userFound = userRepository.findByEmail(userTest.getEmail());
        assertThat(userFound).isNotEmpty().isPresent();

        User user = userFound.get();
        assertThat(user.getEmail()).isEqualTo(TEST_EMAIL);
    }

    @Test
    void testFindUserById() {
        Optional<User> user = userRepository.findById(userTest.getId());

        assertThat(user).isPresent().isNotEmpty();
        assertThat(user.get().getEmail())
                .isEqualTo(TEST_EMAIL);
        assertThat(user.get().getPasswordHash())
                .isEqualTo(TEST_PASSWORD);
    }

    @Test
    void testInsertUser() {
        final String INSERT_EMAIL = "insert@test.com";
        final String INSERT_PASSWORD = "insert";

        User insertUser = userRepository.save(
                User.builder()
                        .email(INSERT_EMAIL)
                        .passwordHash(INSERT_PASSWORD)
                        .build()
        );
        assertNotNull(insertUser);
        assertNotNull(insertUser.getId());
        assertEquals(INSERT_EMAIL, insertUser.getEmail());
        assertEquals(INSERT_PASSWORD, insertUser.getPasswordHash());

        userRepository.findById(insertUser.getId())
                .ifPresentOrElse(
                        userFound -> {
                            assertEquals(insertUser.getId(), userFound.getId());
                            assertEquals(INSERT_EMAIL, insertUser.getEmail());
                            assertEquals(INSERT_PASSWORD, insertUser.getPasswordHash());
                        },
                        () -> {
                            throw new AssertionError("User not found after insertion.");
                        }
                );
    }

    @Test
    void testUpdateUser() {
        final String UPDATE_EMAIL = "update@test.com";
        final String UPDATE_PASSWORD = "update";

        User updateUser = userRepository.save(
                User.builder()
                        .id(userTest.getId())
                        .email(UPDATE_EMAIL)
                        .passwordHash(UPDATE_PASSWORD)
                        .build()
        );
        assertNotNull(updateUser);
        assertNotNull(updateUser.getId());
        assertEquals(UPDATE_EMAIL, updateUser.getEmail());
        assertEquals(UPDATE_PASSWORD, updateUser.getPasswordHash());

        userRepository.findById(updateUser.getId())
                .ifPresentOrElse(
                        userFound -> {
                            assertEquals(updateUser.getId(), userFound.getId());
                            assertEquals(UPDATE_EMAIL, updateUser.getEmail());
                            assertEquals(UPDATE_PASSWORD, updateUser.getPasswordHash());
                        },
                        () -> {
                            throw new AssertionError("User not found after insertion.");
                        }
                );
    }

    @Test
    void testDeleteUser() {
        userRepository.deleteById(userTest.getId());

        Optional<User> userDeleted = userRepository.findById(userTest.getId());
        assertThat(userDeleted).isNotPresent();

        assertThat(userRepository.count()).isEqualTo(0L);
    }
}
