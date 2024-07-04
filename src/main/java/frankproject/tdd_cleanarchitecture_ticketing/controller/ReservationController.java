package frankproject.tdd_cleanarchitecture_ticketing.controller;

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
public class ReservationController {

    Map<String, Object> response = new HashMap<>();

    @PostMapping("/reservation")
    public ResponseEntity<?> reserveSeat(@RequestHeader HttpHeaders headers, @RequestBody Map<String, Object> request) {

        String tokenId = headers.getFirst(org.springframework.http.HttpHeaders.AUTHORIZATION);
        int customerId = (int) request.get("customerId");
        int concertScheduleId = (int) request.get("concertScheduleId");
        int seatId = (int) request.get("seatId");
        boolean isFinallyReserved = (boolean) request.get("isFinallyReserved");
        int temporaryAssigneeId = (int) request.get("temporaryAssigneeId");

        // Validate request data
        assert tokenId != null;
        if (!tokenId.equals("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Token");
        }

        if (concertScheduleId != 1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Concert not found");
        } else if(seatId != 1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Seat not found");
        } else if(isFinallyReserved || temporaryAssigneeId > 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid Seat");
        }

        response.put("concertScheduleId", 1);
        response.put("concertDate", "2024-07-14");
        response.put("seatId", 1);
        response.put("seatNumber", 1);
        response.put("isFinallyReserved", true);
        response.put("temporaryAssigneeId", customerId);
        response.put("price", 50000);
        response.put("reservationId", 1);

        // 5분 더하기
        LocalDateTime plusFiveMinutes = LocalDateTime.now().plusMinutes(5);
        response.put("temporaryAssignExpiresAt", plusFiveMinutes);

        return ResponseEntity.ok(response);
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