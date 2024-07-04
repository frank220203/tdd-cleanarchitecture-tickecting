package frankproject.tdd_cleanarchitecture_ticketing.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/concert")
public class ConcertController {

    Map<String, Object> response = new HashMap<>();

    @GetMapping("{concertId}/schedule")
    public ResponseEntity<?> getConcertSchedule(@RequestHeader HttpHeaders headers, @PathVariable int concertId) {

        String tokenId = headers.getFirst(HttpHeaders.AUTHORIZATION);
        List<Map<String, Object>> concertScheduleList = new ArrayList<>();

        // Check if concert exists in mock data
        assert tokenId != null;
        if (!tokenId.equals("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Token");
        }

        if (concertId != 1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Concert not found");
        }


        // Prepare response
        response.put("concertId", 1);
        response.put("concertName", "싱어게인");

        Map<String, Object> concert1 = new HashMap<>();
        concert1.put("concertScheduleId", 1);
        concert1.put("concertDate", "2024-07-14");
        concert1.put("seatCount", 50);
        concertScheduleList.add(concert1);

        Map<String, Object> concert2 = new HashMap<>();
        concert2.put("concertScheduleId", 2);
        concert2.put("concertDate", "2024-07-20");
        concert2.put("seatCount", 30);
        concertScheduleList.add(concert2);

        Map<String, Object> concert3 = new HashMap<>();
        concert3.put("concertScheduleId", 3);
        concert3.put("concertDate", "2024-07-27");
        concert3.put("seatCount", 40);
        concertScheduleList.add(concert3);

        response.put("concertScheduleList", concertScheduleList);


        return ResponseEntity.ok(response);
    }

    @GetMapping("/{concertScheduleId}/seat")
    public ResponseEntity<?> getConcertSeat(@RequestHeader HttpHeaders headers, @PathVariable int concertScheduleId) {

        String tokenId = headers.getFirst(HttpHeaders.AUTHORIZATION);
        List<Map<String, Object>> concertSeatList = new ArrayList<>();

        // Check if concert exists in mock data
        assert tokenId != null;
        if (!tokenId.equals("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Token");
        }

        if (concertScheduleId != 1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Concert not found");
        }


        // Prepare response
        response.put("concertScheduleId", 1);
        response.put("concertDate", "2024-07-14");
        response.put("seatCount", 50);

        Map<String, Object> seat1 = new HashMap<>();
        seat1.put("seatId", 1);
        seat1.put("seatNumber", 1);
        seat1.put("isFinallyReserved", false);
        seat1.put("temporaryAssigneeId", 0);
        seat1.put("temporaryAssignExpiresAt", "");
        seat1.put("price", 50000);
        concertSeatList.add(seat1);

        Map<String, Object> seat2 = new HashMap<>();
        seat2.put("seatId", 1);
        seat2.put("seatNumber", 2);
        seat2.put("isFinallyReserved", false);
        seat2.put("temporaryAssigneeId", 0);
        seat2.put("temporaryAssignExpiresAt", "");
        seat2.put("price", 50000);
        concertSeatList.add(seat2);

        Map<String, Object> seat3 = new HashMap<>();
        seat3.put("seatId", 1);
        seat3.put("seatNumber", 3);
        seat3.put("isFinallyReserved", false);
        seat3.put("temporaryAssigneeId", 0);
        seat3.put("temporaryAssignExpiresAt", "");
        seat3.put("price", 50000);
        concertSeatList.add(seat3);

        response.put("concertScheduleList", concertSeatList);


        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllConcerts")
    public ResponseEntity<?> getAllConcerts() {

        List<Map<String, Object>> concertList = new ArrayList<>();

        Map<String, Object> concert1 = new HashMap<>();
        concert1.put("concertId", 1);
        concert1.put("concertName", "싱어게인");
        concertList.add(concert1);

        Map<String, Object> concert2 = new HashMap<>();
        concert2.put("concertId", 2);
        concert2.put("concertName", "이무진 콘서트");
        concertList.add(concert2);

        Map<String, Object> concert3 = new HashMap<>();
        concert3.put("concertId", 3);
        concert3.put("concertName", "과학 콘서트");
        concertList.add(concert3);

        // Prepare response
        response.put("concertList", concertList);

        return ResponseEntity.ok(response);
    }
}