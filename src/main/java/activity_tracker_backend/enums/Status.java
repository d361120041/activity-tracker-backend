package activity_tracker_backend.enums;

import lombok.Getter;

@Getter
public enum Status {

    OK(0L, "success"),
    ERROR(1L, "error");

    private final long statusCode;
    private final String message;

    private Status(long statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

}
