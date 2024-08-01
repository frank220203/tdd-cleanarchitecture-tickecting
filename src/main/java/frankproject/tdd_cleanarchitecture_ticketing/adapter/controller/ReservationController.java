package frankproject.tdd_cleanarchitecture_ticketing.adapter.controller;

import frankproject.tdd_cleanarchitecture_ticketing.adapter.request.ReservationRequest;
import frankproject.tdd_cleanarchitecture_ticketing.application.dto.ReservationDTO;
import frankproject.tdd_cleanarchitecture_ticketing.application.usecase.ReservationUsecase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "예약 Controller", description = "좌석 예약 요청 API, 결제 API")
public class ReservationController {

    Map<String, Object> response = new HashMap<>();
    private final ReservationUsecase reservationUsecase;

    public ReservationController(ReservationUsecase reservationUsecase) {
        this.reservationUsecase = reservationUsecase;
    }

    /**
     * 좌석 예약 요청 API
     *
     * @param reservationRequest 예약 요청 정보 (seatId, customerId)
     * @return 예약 정보를 포함한 응답
     */
    @Operation(
            summary = "좌석 예약 요청",
            security = {@SecurityRequirement(name = "headerAuth")}
    )
    @PostMapping("/reservation")
    public ResponseEntity<ReservationDTO> reserveSeat(@RequestBody ReservationRequest reservationRequest) {
        ReservationDTO reservationDTO = reservationUsecase.createReservationWithOptimistic(reservationRequest.getSeatId(), reservationRequest.getCustomerId());
        return ResponseEntity.ok(reservationDTO);
    }

    @PostMapping("/reservation/pay")
    public ResponseEntity<?> paySeat(@RequestHeader HttpHeaders headers, @RequestBody Map<String, Object> request) {

        String tokenId = headers.getFirst(org.springframework.http.HttpHeaders.AUTHORIZATION);
        int reservationId = (int) request.get("reservationId");

        // Validate request data
        assert tokenId != null;
        if (!tokenId.equals("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Token");
        }

        if (reservationId != 1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid reservation");
        }

        response.put("concertName", "싱어게인");
        response.put("concertScheduleId", 3);
        response.put("concertDate", "2024-07-14");
        response.put("seatId", 1);
        response.put("seatNumber", 1);
        response.put("amount", 50000);
        response.put("reservationId", 1);
        response.put("isPaid", true);
        response.put("paymentId", 1);
        response.put("paymentTime", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}