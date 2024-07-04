package frankproject.tdd_cleanarchitecture_ticketing.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CustomerController {

    Map<String, Object> response = new HashMap<>();

    @GetMapping("/{customerId}")
    public ResponseEntity<?> getCustomer(@PathVariable int customerId) {

        // Check if user exists in mock data
        if (customerId != 1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        response.put("customerId", 1);
        response.put("customerName", "스프링");
        response.put("point", 1500);
        response.put("tokenId", "");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/customer/point")
    public ResponseEntity<?> chargePoint(@RequestBody Map<String, Object> request) {

        int customerId = (int) request.get("customerId");
        int amount = (int) request.get("amount");

        // Validate request data
        if (customerId != 1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (amount <= 0) {
            return ResponseEntity.badRequest().body("Invalid amount");
        }

        // Update user balance
        response.put("customerId", 1);
        response.put("point", 1500 + amount);

        return ResponseEntity.ok(response);
    }
}