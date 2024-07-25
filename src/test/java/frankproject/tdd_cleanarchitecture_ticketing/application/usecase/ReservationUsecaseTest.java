package frankproject.tdd_cleanarchitecture_ticketing.application.usecase;

import frankproject.tdd_cleanarchitecture_ticketing.application.dto.PaymentDTO;
import frankproject.tdd_cleanarchitecture_ticketing.application.dto.ReservationDTO;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Customer;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Payment;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Reservation;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Seat;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Token;
import frankproject.tdd_cleanarchitecture_ticketing.domain.service.CustomerService;
import frankproject.tdd_cleanarchitecture_ticketing.domain.service.PaymentService;
import frankproject.tdd_cleanarchitecture_ticketing.domain.service.ReservationService;
import frankproject.tdd_cleanarchitecture_ticketing.domain.service.SeatService;
import frankproject.tdd_cleanarchitecture_ticketing.domain.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReservationUsecaseTest {

    @Autowired
    private SeatService seatService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ReservationUsecase reservationUsecase;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime expiredTime;
    private LocalDateTime validUntilTime;

    @BeforeEach
    public void setUp() {
        createTime = LocalDateTime.now().minusHours(3);
        updateTime = LocalDateTime.now().minusHours(1);
        expiredTime = LocalDateTime.now().minusMinutes(5);      // 현재 시간에서 5분 전, 만료된 시간
        validUntilTime = LocalDateTime.now().plusMinutes(3);    // 현재 시간에서 3분 후, 유효한 시간
    }

    @Test
    @DisplayName("주기적으로 만료된 예약을 취소하는 메소드 테스트")
    public void cancelExpiredReservationsTest() {
        // given
        List<Seat> seats = Arrays.asList(
                new Seat(1, 1, 1, 7000, false, 1, expiredTime, createTime, updateTime, 0),
                new Seat(2, 1, 3, 60000, false, 2, expiredTime, createTime, updateTime, 0),
                new Seat(3, 1, 15, 50000, false, 3, expiredTime, createTime, updateTime, 0)
        );
        List<Reservation> reservations = Arrays.asList(
                new Reservation(1, 1, 1, 1, expiredTime, "PENDING", createTime, updateTime),
                new Reservation(2, 2, 2, 1, expiredTime, "PENDING", createTime, updateTime),
                new Reservation(3, 3, 3, 1, expiredTime, "PENDING", createTime, updateTime)
        );

        for (int i = 0; i < seats.size(); i++) {
            Seat seat = seats.get(i);
            Reservation reservation = reservations.get(i);

            seatService.save(seat);
            reservationService.save(reservation);
        }

        // when
        reservationUsecase.cancelExpiredReservations();

        // then
        Reservation result1 = reservationService.findById(1);
        Reservation result2 = reservationService.findById(2);
        Reservation result3 = reservationService.findById(3);
        Seat resultSeat1 = seatService.findById(1);
        Seat resultSeat2 = seatService.findById(2);
        Seat resultSeat3 = seatService.findById(3);
        assertEquals("CANCELLED", result1.getStatus());
        assertEquals("CANCELLED", result2.getStatus());
        assertEquals("CANCELLED", result3.getStatus());
        assertNull(resultSeat1.getTempAssignExpiresAt());
        assertNull(resultSeat2.getTempAssignExpiresAt());
        assertNull(resultSeat3.getTempAssignExpiresAt());
    }

    @Test
    @DisplayName("결제 처리 및 결제 내역 생성 테스트")
    public void processPaymentTest() {
        // given
        Token token = new Token(1, 1, 1, 1, "ACTIVE", createTime, updateTime);
        tokenService.save(token);

        Customer customer = new Customer(1, "홍길동", 10000, createTime, updateTime); // 포인트가 충분한 고객
        customerService.save(customer);

        Seat seat = new Seat(1, 1, 1, 7000, false, customer.getCustomerId(), validUntilTime, createTime, updateTime, 0);
        seatService.save(seat);

        Reservation reservation = new Reservation(1, customer.getCustomerId(), seat.getSeatId(), seat.getConcertScheduleId(), LocalDateTime.now(), "PENDING", createTime, updateTime);
        reservationService.save(reservation);

        // when
        PaymentDTO paymentDTO = reservationUsecase.processPayment(reservation.getReservationId(), 7000);

        // then
        Payment payment = paymentService.findById(paymentDTO.getPaymentId());
        assertNotNull(payment);
        assertEquals(reservation.getReservationId(), payment.getReservationId());
        assertEquals(7000, payment.getAmount());
        assertNotNull(payment.getPaymentTime()); // 결제 시간이 null이 아닌지 확인

        // Check reservation status
        Reservation updatedReservation = reservationService.findById(reservation.getReservationId());
        assertEquals("COMPLETED", updatedReservation.getStatus());

        // Check seat status
        Seat updatedSeat = seatService.findById(seat.getSeatId());
        assertTrue(updatedSeat.isFinallyReserved());

        // Check customer points
        Customer updatedCustomer = customerService.findById(customer.getCustomerId());
        assertEquals(3000, updatedCustomer.getPoint()); // 포인트가 7000 차감되어 3000 남아야 함
    }

    @Test
    @DisplayName("낙관적 락을 이용한 동시성 제어 좌석 예약 테스트")
    public void createReservationWithOptimisticTest() throws InterruptedException {
        // given
        Seat seat = new Seat(1, 1, 1, 7000, false, 0, expiredTime, createTime, updateTime, 0);
        seatService.save(seat);

        // when
        int numThreads = 20; // 동시에 처리할 스레드 수
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads); // CountDownLatch 초기화

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        long testStartTime = System.currentTimeMillis();

        for (int i = 0; i < numThreads; i++) {
            long customerId = (i + 1);    // 1, 2, 3, 4, 5, 6, 7, 8, 9, 10

            executorService.submit(() -> {
                try {
                    ReservationDTO result = reservationUsecase.createReservationWithOptimistic(seat.getSeatId(), customerId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown(); // 작업 완료 시 CountDownLatch 감소
                }
            });
        }

        latch.await(); // 모든 스레드가 countDown()을 호출할 때까지 대기

        executorService.shutdown();
        if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            executorService.shutdownNow(); // 작업이 완료되지 않았다면 강제 종료
        }

        long testEndTime = System.currentTimeMillis();
        System.out.println("전체 테스트 종료, 소요 시간: "+(testEndTime - testStartTime)+" ms");
        System.out.println("성공한 요청 수 : " + successCount.get());
        System.out.println("실패한 요청 수 : " + failCount.get());

        Reservation result = reservationService.findById(1);

        // then
        assertNotNull(result);
        assertEquals(1, successCount.get());
        assertTrue(failCount.get() > 0);
        assertEquals(seat.getSeatId(), result.getSeatId());
        assertEquals(seat.getConcertScheduleId(), result.getConcertScheduleId());
        assertNotNull(result.getReservationTime()); // reservationTime이 null이 아닌지 확인
        assertEquals("PENDING", result.getStatus()); // 예약 상태가 "PENDING"인지 확인
    }

    @Test
    @DisplayName("비관적 락을 이용한 동시성 제어 좌석 예약 테스트")
    public void createReservationWithPessimisticTest() throws InterruptedException {
        // given
        Seat seat = new Seat(1, 1, 7000, false, 0, expiredTime, createTime, updateTime);
        Seat seat2 = new Seat(1, 2, 7000, false, 0, expiredTime, createTime, updateTime);
        Seat seat3 = new Seat(1, 3, 7000, false, 0, expiredTime, createTime, updateTime);
        Seat seat4 = new Seat(1, 4, 7000, false, 0, expiredTime, createTime, updateTime);
        seatService.save(seat);
        seatService.save(seat2);
        seatService.save(seat3);
        seatService.save(seat4);

        // when
        int numThreads = 20; // 동시에 처리할 스레드 수
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads); // CountDownLatch 초기화

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        long testStartTime = System.currentTimeMillis();

        for (int i = 0; i < numThreads; i++) {
            long customerId = (i + 1);    // 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
            long seatId = (i + 1);
            executorService.submit(() -> {
                try {
                    ReservationDTO result = reservationUsecase.createReservationWithPessimistic(seatId, customerId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown(); // 작업 완료 시 CountDownLatch 감소
                }
            });
        }

        latch.await(); // 모든 스레드가 countDown()을 호출할 때까지 대기

        executorService.shutdown();
        if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            executorService.shutdownNow(); // 작업이 완료되지 않았다면 강제 종료
        }

        long testEndTime = System.currentTimeMillis();
        System.out.println("전체 테스트 종료, 소요 시간: "+(testEndTime - testStartTime)+" ms");
        System.out.println("성공한 요청 수 : " + successCount.get());
        System.out.println("실패한 요청 수 : " + failCount.get());

        Reservation result = reservationService.findById(1);

        // then
        assertNotNull(result);
        assertEquals(1, successCount.get());
        assertTrue(failCount.get() > 0);
        assertEquals(seat.getSeatId(), result.getSeatId());
        assertEquals(seat.getConcertScheduleId(), result.getConcertScheduleId());
        assertNotNull(result.getReservationTime()); // reservationTime이 null이 아닌지 확인
        assertEquals("PENDING", result.getStatus()); // 예약 상태가 "PENDING"인지 확인
    }
}