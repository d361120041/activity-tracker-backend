package activity_tracker_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:4173", "http://192.168.50.140:4173",
                        "http://localhost:5173", "http://192.168.50.140:5173",
                        "http://localhost:6173", "http://192.168.50.140:6173"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE");
//                .allowedHeaders("*");
//                .allowCredentials(true);
    }
}
