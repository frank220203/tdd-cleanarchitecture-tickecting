package frankproject.tdd_cleanarchitecture_ticketing.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO {

    private long tokenId;
    private long concertId;
    private long customerId;
    private long waitNumber;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TokenDTO(long concertId, long customerId, long waitNumber, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.concertId = concertId;
        this.customerId = customerId;
        this.waitNumber = waitNumber;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
