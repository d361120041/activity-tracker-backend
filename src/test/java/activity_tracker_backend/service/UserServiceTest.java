package activity_tracker_backend.service;

import activity_tracker_backend.model.User;
import activity_tracker_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class UserServiceTest {

    private final UserService userService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceTest(UserService userService, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    private final String TEST_EMAIL = "test";
    private final String TEST_PASSWORD_HASH = "test";

    private User test;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        test = userRepository.save(
                User.builder()
                    .email(TEST_EMAIL)
                    .passwordHash(bCryptPasswordEncoder.encode(TEST_PASSWORD_HASH))
                    .build()
        );
    }

    @Test
    void testRegister() {
        final String email = "register";
        final String password = "register";

        User user = userService.register(email, password);
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(bCryptPasswordEncoder.matches(password, user.getPasswordHash())).isTrue();
    }

    @Test
    void testFindByEmail() {
        Optional<User> findByEmail = userRepository.findByEmail(TEST_EMAIL);
        assertThat(findByEmail).isNotEmpty().isPresent();

        User user = findByEmail.get();
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(bCryptPasswordEncoder.matches(TEST_PASSWORD_HASH, user.getPasswordHash())).isTrue();
    }

    @Test
    void testCheckPassword() {
        boolean isPasswordCorrect = userService.checkPassword(test, TEST_PASSWORD_HASH);
        assertThat(isPasswordCorrect).isTrue();
    }
}
