package activity_tracker_backend.service;

import activity_tracker_backend.model.User;
import activity_tracker_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User register(String email, String password) {
        if(userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email 已被使用。");
        }
        String hashed = bCryptPasswordEncoder.encode(password);
        User user = new User(email, hashed);
        return userRepository.save(user);
    }

    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean checkPassword(User user, String password) {
        return bCryptPasswordEncoder.matches(password, user.getPasswordHash());
    }
}
