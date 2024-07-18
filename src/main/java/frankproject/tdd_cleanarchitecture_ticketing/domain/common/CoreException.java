package frankproject.tdd_cleanarchitecture_ticketing.domain.common;

import lombok.Getter;

@Getter
public class CoreException extends RuntimeException {
    private final ErrorCode errorCode;

    public CoreException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
