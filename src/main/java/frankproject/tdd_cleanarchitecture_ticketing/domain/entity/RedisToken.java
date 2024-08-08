package frankproject.tdd_cleanarchitecture_ticketing.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RedisToken {

    private long customerId;
    private UUID tokenID;
    private long rank;

    public RedisToken(long customerId, UUID tokenID) {
        this.customerId = customerId;
        this.tokenID = tokenID;
    }
}
