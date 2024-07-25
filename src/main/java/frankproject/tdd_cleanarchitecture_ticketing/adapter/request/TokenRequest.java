package frankproject.tdd_cleanarchitecture_ticketing.adapter.request;

import lombok.Data;

@Data
public class TokenRequest {
    private long customerId;
    private long concertId;
}