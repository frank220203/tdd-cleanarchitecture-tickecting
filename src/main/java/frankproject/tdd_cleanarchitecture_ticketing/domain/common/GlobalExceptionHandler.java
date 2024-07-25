package frankproject.tdd_cleanarchitecture_ticketing.domain.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CoreException.class)
    public ResponseEntity<ErrorResponse> handleCoreException(CoreException ex) {
        log.error("Exception : {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode().getCode(), ex.getMessage());
        return ResponseEntity.status(ex.getErrorCode().getCode()).body(errorResponse);
    }
}
