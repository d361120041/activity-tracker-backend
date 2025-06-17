package activity_tracker_backend.controller;

import activity_tracker_backend.dto.UserLoginDto;
import activity_tracker_backend.dto.UserRegisterDto;
import activity_tracker_backend.jwt.JwtUtils;
import activity_tracker_backend.model.User;
import activity_tracker_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    public UserController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDto dto) {
            User user = userService.register(dto.getEmail(), dto.getPassword());
            user.setPasswordHash(null);
            return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto dto) {
        return userService.findByEmail(dto.getEmail())
                .map(user -> {
                    if(userService.checkPassword(user, dto.getPassword())) {
                        String jwt = jwtUtils.generateToken(user.getId());
                        return ResponseEntity.ok().body(
                                Map.of("token", jwt)
                        );
                    } else {
                        return ResponseEntity.status(401).body("密碼錯誤");                    }
                })
                .orElseGet(() -> ResponseEntity.status(404).body("找不到該 Email"));
    }
}
