package activity_tracker_backend.service.dto;

import activity_tracker_backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityResponse {
    private UUID id;
    private LocalDate activityDate;
    private String title;
    private String category;
    private LocalTime startTime;
    private LocalTime endTime;
    private String notes;
    private Byte mood;
    private String createdAt;
    private String updatedAt;
    private String userEmail;
}
