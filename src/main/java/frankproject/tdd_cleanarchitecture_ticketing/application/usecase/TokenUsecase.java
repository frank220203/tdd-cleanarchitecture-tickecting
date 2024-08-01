package frankproject.tdd_cleanarchitecture_ticketing.application.usecase;

import frankproject.tdd_cleanarchitecture_ticketing.application.dto.TokenDTO;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Concert;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Customer;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Token;
import frankproject.tdd_cleanarchitecture_ticketing.domain.service.ConcertService;
import frankproject.tdd_cleanarchitecture_ticketing.domain.service.CustomerService;
import frankproject.tdd_cleanarchitecture_ticketing.domain.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TokenUsecase {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ConcertService concertService;

    public TokenUsecase(TokenService tokenService, ConcertService concertService) {
        this.tokenService = tokenService;
        this.concertService = concertService;
    }

    // 반환할 토큰 DTO 생성
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

    // 콘서트 대기열 참가
    public TokenDTO generateNewToken(long customerId, long concertId) {

        Customer customer = customerService.findById(customerId);
        Concert concert = concertService.findById(concertId);

        return convertToTokenDTO(tokenService.generateNewToken(customer.getCustomerId(), concert.getConcertId()));
    }

    // 본인 콘서트 대기열 조회
    public TokenDTO checkToken(long customerId, long concertId) {

        Customer customer = customerService.findById(customerId);
        Concert concert = concertService.findById(concertId);

        return  convertToTokenDTO(tokenService.checkToken(customer.getCustomerId(), concert.getConcertId()));
    }

    // 토큰 활성화 여부 조회
    public boolean isActiveToken(long tokenId){
        return tokenService.isActiveToken(tokenId);
    }

    // 스케줄러가 자동으로 토큰 상태 관리
    public void manageTokens(int size){

        List<Concert> concertList = concertService.findAll();

        for(Concert concert : concertList){
            tokenService.expireToken(concert.getConcertId());
            tokenService.activeToken(concert.getConcertId(), size);
        }
    }
}
