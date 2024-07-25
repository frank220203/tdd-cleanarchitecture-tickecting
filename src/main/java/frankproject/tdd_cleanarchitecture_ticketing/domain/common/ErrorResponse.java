package frankproject.tdd_cleanarchitecture_ticketing.domain.common;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final int code;
    private final String message;

    public ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
