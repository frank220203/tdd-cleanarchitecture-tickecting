package frankproject.tdd_cleanarchitecture_ticketing.application.usecase;

import frankproject.tdd_cleanarchitecture_ticketing.application.dto.RedisTokenDTO;
import frankproject.tdd_cleanarchitecture_ticketing.application.dto.TokenDTO;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Concert;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Customer;
import frankproject.tdd_cleanarchitecture_ticketing.domain.service.ConcertService;
import frankproject.tdd_cleanarchitecture_ticketing.domain.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Execution(ExecutionMode.CONCURRENT)
@DirtiesContext
public class TokenUsecaseTest {

    @Autowired
    private TokenUsecase tokenUsecase;

    @Autowired
    private ConcertService concertService;

    @Autowired
    private CustomerService customerService;

    @Test
    @DisplayName("토큰 발급 동시 접근 테스트")
    public void generateNewToken() throws Exception {

        // given
        int expectedTokens = 20;

        // given Concert
        LocalDateTime createTime = LocalDateTime.now().minusHours(3);
        LocalDateTime updateTime = LocalDateTime.now().minusHours(1);
        concertService.save(new Concert(1, "이무진 콘서트", createTime, updateTime));

        // given Customer
        int totalCustomer = 100;
        for(int i = 0; i < totalCustomer; i++){
            customerService.save(new Customer(i, "" + i, 0, createTime, updateTime));
        }

        // given MultiThread
        int concurrentThreads = 100; // 동시 실행할 쓰레드 수
        ExecutorService executorService = Executors.newFixedThreadPool(totalCustomer);
        CountDownLatch latch = new CountDownLatch(totalCustomer);

        // when customer polling token
        // 각 쓰레드가 실행할 작업
        Runnable task = () -> {
            try {
                Random random = new Random();
                int randomCustomerId = random.nextInt(totalCustomer);
                TokenDTO generateResult = tokenUsecase.generateNewToken(randomCustomerId, 1L);
                System.out.println("customerId : " + generateResult.getCustomerId() + " tokenId : " + generateResult.getTokenId() + " number : " + generateResult.getWaitNumber());
                assertNotNull(generateResult);
                assertEquals(randomCustomerId, generateResult.getCustomerId());
                assertEquals(1L, generateResult.getConcertId());
                assertEquals("PENDING", generateResult.getStatus());

            } finally {
                latch.countDown();
            }
        };

        // 동시에 실행할 쓰레드 수만큼 작업 제출 // 토큰 만료 확인 위해 3번 실행
        for(int c = 0; c < 3; c++){
            IntStream.range(0, concurrentThreads).forEach(i -> executorService.submit(task));
            // 토큰 만료를 위해 발급 후 30초간 sleep
            TimeUnit.SECONDS.sleep(30);
        }

        // 모든 쓰레드가 완료될 때까지 기다림
        latch.await();
        executorService.shutdown();
        executorService.awaitTermination(60, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("레디스 대기열 토큰 발급 테스트")
    public void generateTokenWithRedis(){
        LocalDateTime createTime = LocalDateTime.now().minusHours(3);
        LocalDateTime updateTime = LocalDateTime.now().minusHours(1);
        customerService.save(new Customer(1, "홍길동", 0, createTime, updateTime));
        RedisTokenDTO generateResult = tokenUsecase.generateNewTokenWithRedis(1);
        assertNotNull(generateResult);
        assertEquals(1, generateResult.getCustomerId());
    }

    @Test
    public void checkToken() {
        // given
        long customerId = 1L;
        long concertId = 1L;
        customerService.save(new Customer(customerId, "" + customerId, 0, LocalDateTime.now(), null));
        concertService.save(new Concert(1, "이무진 콘서트", LocalDateTime.now(), LocalDateTime.now()));
        TokenDTO generateToken = tokenUsecase.generateNewToken(customerId, concertId);

        // when
        TokenDTO result = tokenUsecase.checkToken(customerId, concertId);

        // then
        assertEquals(generateToken.getTokenId(), result.getTokenId());
        assertEquals(generateToken.getCustomerId(), result.getCustomerId());
        assertEquals(generateToken.getConcertId(), result.getConcertId());
        assertEquals(generateToken.getStatus(), result.getStatus());
    }

    @Test
    @DisplayName("레디스 대기열 조회 테스트")
    public void checkTokenWithRedis() {
        LocalDateTime createTime = LocalDateTime.now().minusHours(3);
        LocalDateTime updateTime = LocalDateTime.now().minusHours(1);
        customerService.save(new Customer(1, "홍길동", 0, createTime, updateTime));
        RedisTokenDTO generateResult = tokenUsecase.checkTokenWithRedis(1);
        assertNotNull(generateResult);
        assertEquals(1, generateResult.getCustomerId());
        System.out.println("고객 ID : " + generateResult.getCustomerId() + " 토큰 ID : " + generateResult.getTokenID() + " 대기순서 : " + generateResult.getRank());
    }

    @Test
    @DisplayName("레디스 토큰 활성화/만료 테스트")
    public void manageTokens() throws InterruptedException {
        LocalDateTime createTime = LocalDateTime.now().minusHours(3);
        LocalDateTime updateTime = LocalDateTime.now().minusHours(1);
        customerService.save(new Customer(1, "홍길동", 0, createTime, updateTime));
        customerService.save(new Customer(2, "고양이", 0, createTime, updateTime));
        RedisTokenDTO generateResult = tokenUsecase.generateNewTokenWithRedis(1);
        RedisTokenDTO generateResult2 = tokenUsecase.generateNewTokenWithRedis(2);
        System.out.println("=========== 생성 완료 =================");
        RedisTokenDTO result = tokenUsecase.checkTokenWithRedis(1);
        System.out.println("고객 1 ID : " + result.getCustomerId() + " 토큰 ID : " + result.getTokenID() + " 대기순서 : " + result.getRank());
        RedisTokenDTO result2 = tokenUsecase.checkTokenWithRedis(2);
        System.out.println("고객 2 ID : " + result2.getCustomerId() + " 토큰 ID : " + result2.getTokenID() + " 대기순서 : " + result2.getRank());
        System.out.println("=========== 조회 완료 =================");
        assertNotNull(result);
        assertNotNull(result2);
        tokenUsecase.manageTokensWithRedis(1);
        Thread.sleep(60000);
        tokenUsecase.manageTokensWithRedis(1);
    }

    @Test
    @DisplayName("레디스 토큰 활성화 여부 테스트")
    public void isActiveTokenWithRedis() {
        LocalDateTime createTime = LocalDateTime.now().minusHours(3);
        LocalDateTime updateTime = LocalDateTime.now().minusHours(1);
        customerService.save(new Customer(1, "홍길동", 0, createTime, updateTime));
        customerService.save(new Customer(2, "고양이", 0, createTime, updateTime));
        RedisTokenDTO generateResult = tokenUsecase.generateNewTokenWithRedis(1);
        RedisTokenDTO generateResult2 = tokenUsecase.generateNewTokenWithRedis(2);
        System.out.println("=========== 생성 완료 =================");
        RedisTokenDTO result = tokenUsecase.checkTokenWithRedis(1);
        System.out.println("고객 1 ID : " + result.getCustomerId() + " 토큰 ID : " + result.getTokenID() + " 대기순서 : " + result.getRank());
        RedisTokenDTO result2 = tokenUsecase.checkTokenWithRedis(2);
        System.out.println("고객 2 ID : " + result2.getCustomerId() + " 토큰 ID : " + result2.getTokenID() + " 대기순서 : " + result2.getRank());
        System.out.println("=========== 조회 완료 =================");
        assertNotNull(result);
        assertNotNull(result2);
        tokenUsecase.manageTokensWithRedis(1);
        System.out.println("활성화 여부 : " + tokenUsecase.isActiveTokenWithRedis(result.getTokenID()));
        System.out.println("활성화 여부 : " + tokenUsecase.isActiveTokenWithRedis(result2.getTokenID()));
    }
}
