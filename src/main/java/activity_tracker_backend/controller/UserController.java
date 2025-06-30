package activity_tracker_backend.controller;

import activity_tracker_backend.dto.UserLoginDto;
import activity_tracker_backend.dto.UserRegisterDto;
import activity_tracker_backend.jwt.JwtUtils;
import activity_tracker_backend.model.User;
import activity_tracker_backend.service.RefreshTokenService;
import activity_tracker_backend.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
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
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto dto, HttpServletResponse response) {
        return userService.findByEmail(dto.getEmail())
                .map(user -> {
                    if(userService.checkPassword(user, dto.getPassword())) {
                        final String accessToken = jwtUtils.generateAccessToken(user.getEmail());
                        final String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

                        refreshTokenService.saveRefreshToken(user.getEmail(), refreshToken);
                        Cookie refreshTokenCookie = createRefreshTokenCookie(refreshToken);
                        response.addCookie(refreshTokenCookie);

                        return ResponseEntity.ok().body(Map.of(
                                "accessToken", accessToken,
                                "userId", user.getId()
                        ));
                    } else {
                        return ResponseEntity.status(401).body("密碼錯誤");                    }
                })
                .orElseGet(() -> ResponseEntity.status(404).body("找不到該 Email"));
    }

    @PostMapping("/logout")
    public Map<String, Object> logout(HttpServletRequest req, HttpServletResponse res) {

        String refreshToken = null;

        if (req.getCookies()!=null) {
            for (Cookie cookie : req.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        Map<String, Object> response = new HashMap<>();
        if (refreshToken!=null && !refreshToken.isEmpty()) {
            refreshTokenService.revokeRefreshToken(refreshToken);
        }

        clearRefreshTokenCookie(res);

        response.put("success", true);
        return response;
    }

    @PostMapping("refresh-token")
    public String refreshToken(HttpServletRequest req, HttpServletResponse res) {

        String refreshToken = null;
        if (req.getCookies()!=null) {
            for (Cookie cookie : req.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        JSONObject response = new JSONObject();
        if (refreshToken==null || refreshToken.isEmpty()) {
            response.put("error", "Refresh token missing from cookie");
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return response.toString();
        }

        String userEmail = null;
        try {
            userEmail = jwtUtils.extractUserEmail(refreshToken);
        } catch (Exception e){
            response.put("error", "Invalid refresh token claim or expired");
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return response.toString();
        }

        if (!jwtUtils.validateRefreshToken(refreshToken)) {
            refreshTokenService.revokeRefreshToken(refreshToken);
            clearRefreshTokenCookie(res);
            response.put("error", "Refresh token expired, please login again");
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return response.toString();
        }

        if (!refreshTokenService.isValidRefreshToken(userEmail, refreshToken)) {
            clearRefreshTokenCookie(res);
            response.put("error", "Invalid or revoked refresh token");
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return response.toString();
        }

        refreshTokenService.revokeRefreshToken(refreshToken);
        final String newRefreshToken = jwtUtils.generateRefreshToken(userEmail);
        refreshTokenService.saveRefreshToken(userEmail, newRefreshToken);

        Cookie newRefreshTokenCookie = createRefreshTokenCookie(newRefreshToken);
        res.addCookie(newRefreshTokenCookie);

        final String newAccessToken = jwtUtils.generateAccessToken(userEmail);

        response.put("accessToken", newAccessToken);

        return response.toString();
    }

    private Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int)jwtUtils.getRefreshTokenExpirationMs()/1000);
        return refreshTokenCookie;
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
