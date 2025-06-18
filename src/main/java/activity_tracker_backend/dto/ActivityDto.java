package activity_tracker_backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDto {

    @NotNull(message = "userId 是必填")
    private UUID userId;

    @NotNull(message = "activityDate 是必填")
    private LocalDate activityDate;

    @NotBlank(message = "title 不能是空白")
    private String title;

    @NotBlank(message = "category 不能是空白")
    private String category;

    @NotNull(message = "startTime 是必填")
    private LocalTime startTime;

    @NotNull(message = "endTime 是必填")
    private LocalTime endTime;

    private String notes;

    @Min(value = 1, message = "mood 最少為 1")
    @Max(value = 5, message = "mood 最多為 5")
    @NotNull(message = "mood 是必填")
    private Byte mood;
}
