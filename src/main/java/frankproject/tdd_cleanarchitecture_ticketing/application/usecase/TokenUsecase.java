package frankproject.tdd_cleanarchitecture_ticketing.application.usecase;

import frankproject.tdd_cleanarchitecture_ticketing.application.dto.TokenDTO;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Concert;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Token;
import frankproject.tdd_cleanarchitecture_ticketing.domain.service.ConcertService;
import frankproject.tdd_cleanarchitecture_ticketing.domain.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional
public class TokenUsecase {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private ConcertService concertService;

    public TokenUsecase(TokenService tokenService, ConcertService concertService) {
        this.tokenService = tokenService;
        this.concertService = concertService;
    }

    private TokenDTO convertToTokenDTO(Token token) {
        return new TokenDTO(
                token.getTokenId(),
                token.getConcertId(),
                token.getCustomerId(),
                token.getWaitNumber(),
                token.getStatus(),
                token.getCreatedAt(),
                token.getUpdatedAt()
        );
    }

    public TokenDTO issueToken(long customerId, long concertId) {

        Token targetToken = tokenService.issueToken(customerId, concertId);

        return convertToTokenDTO(tokenService.issueToken(customerId, concertId));
    }

    public void manageTokens(int size){

        List<Concert> concertList = concertService.findAll();

        for(Concert concert : concertList){
            tokenService.expireToken(concert.getConcertId());
            tokenService.activeToken(concert.getConcertId(), size);
        }
    }
}
