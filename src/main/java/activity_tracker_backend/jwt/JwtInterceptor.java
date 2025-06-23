package activity_tracker_backend.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    public JwtInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response,
            Object handler
    ) throws Exception {
        String method = request.getMethod();
        if ("OPTIONS".equals(method)) {
            return true;
        }

        String token = null;
        String userEmail = null;

        String auth = request.getHeader("Authorization");
        if (auth!=null && auth.startsWith("Bearer ")) {
            token = auth.substring(7);
            try {
                userEmail = jwtUtils.extractUserEmail(token);
            } catch (Exception e) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Unauthorized: " + e.getMessage()
                );
                return false;
            }
        } else {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Unauthorized: JWT token does not begin with Bearer string or is missing"
            );
            return false;
        }

        if (userEmail!=null && jwtUtils.validateToken(token, userEmail)) {
            request.setAttribute("userEmail", userEmail);
            return true;
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: invalid JWT token");
            return false;
        }
    }
}
