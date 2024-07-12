package frankproject.tdd_cleanarchitecture_ticketing.application.usecase;

import frankproject.tdd_cleanarchitecture_ticketing.application.dto.TokenDTO;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Concert;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Customer;
import frankproject.tdd_cleanarchitecture_ticketing.domain.repository.TokenRepository;
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

    @Autowired
    private TokenRepository tokenRepository;

    @Test
    @DisplayName("토큰 발급, 토큰 조회, 토큰 만료, 토큰 활성화 동시 접근 테스트")
    public void issueToken() throws Exception {

        // given
        int expectedTokens = 20;

        // given Concert
        LocalDateTime createTime = LocalDateTime.now().minusHours(3);
        LocalDateTime updateTime = LocalDateTime.now().minusHours(1);
        concertService.save(new Concert(1, "이무진 콘서트", createTime, updateTime));
        concertService.save(new Concert(2, "소수빈 콘서트", createTime, updateTime));
        concertService.save(new Concert(3, "뉴진스 콘서트", createTime, updateTime));

        // given Customer
        int totalCustomer = 100;
        for(int i = 0; i < totalCustomer; i++){
            synchronized (customerService) {
                customerService.save(new Customer(i, "" + i, 0, createTime, updateTime));
            }
        }

        // given MultiThread
        int concurrentThreads = 1000; // 동시 실행할 쓰레드 수
        ExecutorService executorService = Executors.newFixedThreadPool(totalCustomer);
        CountDownLatch latch = new CountDownLatch(totalCustomer);

        // when customer polling token
        // 각 쓰레드가 실행할 작업
        Runnable task = () -> {
            try {
                Random random = new Random();
                int randomCustomerId = random.nextInt(totalCustomer);
                TokenDTO result = tokenUsecase.issueToken(randomCustomerId, 1L);

                assertNotNull(result);
                assertEquals(randomCustomerId, result.getCustomerId());
                assertEquals(1L, result.getConcertId());
                assertEquals("PENDING", result.getStatus());

            } finally {
                latch.countDown();
            }
        };

        // 동시에 실행할 쓰레드 수만큼 작업 제출 // 토큰 만료 확인 위해 3번 실행
        for(int c = 0; c < 3; c++){
            IntStream.range(0, concurrentThreads).forEach(i -> executorService.submit(task));
            // 토큰 만료를 위해 발급 후 3초간 sleep
            TimeUnit.SECONDS.sleep(3);
        }

        // 모든 쓰레드가 완료될 때까지 기다림
        latch.await();
        executorService.shutdown();
        executorService.awaitTermination(60, TimeUnit.SECONDS);

    }
}
