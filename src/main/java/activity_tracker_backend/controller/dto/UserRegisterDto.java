package activity_tracker_backend.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDto {

    @NotBlank(message = "Email 不能是空白")
    @Email(message = "Email 格式不正確")
    private String email;

    @NotBlank(message = "密碼不能為空白")
    @Size(min = 8, message = "密碼至少 8 個字元")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[a-zA-Z\\d@$!%*?&]{8,}$",
            message = "密碼需同時包含大寫、小寫、數字與@$!%*?&"
    )
    private String password;
}
