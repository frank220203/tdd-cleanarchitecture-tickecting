package frankproject.tdd_cleanarchitecture_ticketing.domain.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "사용자를 찾을 수 없습니다"),
    CONCERT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "콘서트를 찾을 수 없습니다"),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "토큰을 찾을 수 없습니다"),
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "좌석을 찾을 수 없습니다"),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "예약을 찾을 수 없습니다"),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "결제를 찾을 수 없습니다"),
    NO_AVAILABLE_DATES(HttpStatus.NOT_FOUND.value(), "해당 콘서트에 예약 가능한 날짜가 없습니다"),
    NO_AVAILABLE_SEATS(HttpStatus.NOT_FOUND.value(), "해당 날짜에 예약 가능한 좌석이 없습니다"),
    SEAT_ALREADY_RESERVED(HttpStatus.NOT_FOUND.value(), "이미 예약된 좌석입니다"),
    INSUFFICIENT_POINTS(HttpStatus.NOT_FOUND.value(), "포인트가 부족합니다"),
    PAYMENT_FAILED(HttpStatus.NOT_FOUND.value(), "결제 처리에 실패했습니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "토큰이 유효하지 않거나 대기열 통과를 실패했습니다"),
    ALREADY_IN_QUEUE(HttpStatus.CONFLICT.value(), "이미 대기열에 참가한 사용자입니다");


    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
