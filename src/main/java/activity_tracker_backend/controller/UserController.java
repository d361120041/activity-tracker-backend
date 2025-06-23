package activity_tracker_backend.controller;

import activity_tracker_backend.dto.UserLoginDto;
import activity_tracker_backend.dto.UserRegisterDto;
import activity_tracker_backend.jwt.JwtUtils;
import activity_tracker_backend.model.User;
import activity_tracker_backend.service.RefreshTokenService;
import activity_tracker_backend.service.UserService;
import jakarta.validation.Valid;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    public UserController(UserService userService, JwtUtils jwtUtils, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
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
                        final String accessToken = jwtUtils.generateAccessToken(user.getEmail());
                        final String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());
                        refreshTokenService.saveRefreshToken(user.getEmail(), refreshToken);
                        return ResponseEntity.ok().body(
                                Map.of("accessToken", accessToken, "refreshToken", refreshToken)
                        );
                    } else {
                        return ResponseEntity.status(401).body("密碼錯誤");                    }
                })
                .orElseGet(() -> ResponseEntity.status(404).body("找不到該 Email"));
    }

    @PostMapping("/logout")
    public Map<String, Object> logout(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        if (request==null || request.isEmpty()) {
            response.put("error", "request 為 null 或 空白值");
            return response;
        }

        if (!request.containsKey("refreshToken") || request.get("refreshToken")==null) {
            response.put("error", "不存在 refreshToken 或 refreshToken 值為null");
            return response;
        }

        String refreshToken = request.get("refreshToken").toString();
        if (refreshToken!=null && !refreshToken.isEmpty()) {
            refreshTokenService.revokeRefreshToken(refreshToken);
        }

        response.put("success", true);
        return response;
    }

    @PostMapping("refresh-token")
    public String refreshToken(@RequestBody String json) {
        JSONObject request = new JSONObject(json);
        String refreshToken = request.isNull("refreshToken") ? null : request.getString("refreshToken");

        String userEmail = null;
        JSONObject response = new JSONObject();
        try {
            userEmail = jwtUtils.extractUserEmail(refreshToken);
        } catch (Exception e){
            response.put("error", "Invalid refresh token claim or expired");
            return response.toString();
        }

        if (!jwtUtils.validateRefreshToken(refreshToken)) {
            refreshTokenService.revokeRefreshToken(refreshToken);
            response.put("error", "Refresh token expired, please login again");
            return response.toString();
        }

        if (!refreshTokenService.isValidRefreshToken(userEmail, refreshToken)) {
            response.put("error", "Invalid or revoked refresh token");
            return response.toString();
        }

        refreshTokenService.revokeRefreshToken(refreshToken);
        final String newRefreshToken = jwtUtils.generateRefreshToken(userEmail);
        refreshTokenService.saveRefreshToken(userEmail, refreshToken);

        final String newAccessToken = jwtUtils.generateAccessToken(userEmail);

        response.put("accessToken", newAccessToken);
        response.put("refreshToken", newRefreshToken);

        return response.toString();
    }
}
