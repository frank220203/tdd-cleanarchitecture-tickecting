package frankproject.tdd_cleanarchitecture_ticketing.adapter.controller;

import frankproject.tdd_cleanarchitecture_ticketing.adapter.request.TokenRequest;
import frankproject.tdd_cleanarchitecture_ticketing.application.dto.TokenDTO;
import frankproject.tdd_cleanarchitecture_ticketing.application.usecase.TokenUsecase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tokens")
@Tag(name = "대기열 토큰 Controller", description = "토큰 발급 API, 대기열 조회 API")
public class TokenController {

    private final TokenUsecase tokenUsecase;

    @Autowired
    public TokenController(TokenUsecase tokenUsecase) {
        this.tokenUsecase = tokenUsecase;
    }

    @Operation(summary = "토큰 발급/콘서트 대기열 참가")
    @PostMapping("/generate")
    public ResponseEntity<TokenDTO> generateNewToken(@RequestBody TokenRequest request) {
        TokenDTO tokenDTO = tokenUsecase.generateNewToken(request.getCustomerId(), request.getConcertId());
        return ResponseEntity.ok(tokenDTO);
    }

    @Operation(summary = "본인 콘서트 대기열 조회")
    @GetMapping("/check")
    public ResponseEntity<TokenDTO> checkToken(@RequestParam("customerId") long customerId, @RequestParam("concertId") long concertId) {
        TokenDTO tokenDTO = tokenUsecase.checkToken(customerId, concertId);
        return ResponseEntity.ok(tokenDTO);
    }
}
