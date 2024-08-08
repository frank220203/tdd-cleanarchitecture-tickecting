package frankproject.tdd_cleanarchitecture_ticketing.application.usecase;

import frankproject.tdd_cleanarchitecture_ticketing.application.dto.ConcertDTO;
import frankproject.tdd_cleanarchitecture_ticketing.application.dto.ConcertScheduleDTO;
import frankproject.tdd_cleanarchitecture_ticketing.application.dto.SeatDTO;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Concert;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.ConcertSchedule;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Seat;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Token;
import frankproject.tdd_cleanarchitecture_ticketing.domain.service.ConcertScheduleService;
import frankproject.tdd_cleanarchitecture_ticketing.domain.service.ConcertService;
import frankproject.tdd_cleanarchitecture_ticketing.domain.service.SeatService;
import frankproject.tdd_cleanarchitecture_ticketing.domain.service.TokenService;
import frankproject.tdd_cleanarchitecture_ticketing.util.MultiThread;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ConcertUsecaseTest {

    @Autowired
    private ConcertService concertService;

    @Autowired
    private ConcertScheduleService concertScheduleService;

    @Autowired
    private SeatService seatService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ConcertUsecase concertUsecase;

    private LocalDate date1;
    private LocalDate date2;
    private LocalDate date3;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @BeforeEach
    public void setUp() {
        date1 = LocalDate.of(2024, 7, 27);
        date2 = LocalDate.of(2024, 7, 28);
        date3 = LocalDate.of(2024, 8, 3);
        createTime = LocalDateTime.now().minusHours(3);
        updateTime = LocalDateTime.now().minusHours(1);
    }

    @Test
    @DisplayName("모든 콘서트 조회 테스트")
    public void getAllConcertsTest() {
        // given
        List<Concert> concerts = Arrays.asList(
                new Concert(1, "이무진 콘서트", createTime, updateTime),
                new Concert(2, "소수빈 콘서트", createTime, updateTime),
                new Concert(3, "뉴진스 콘서트", createTime, updateTime)
        );

        // Save each concert
        for (Concert concert : concerts) {
            concertService.save(concert);
        }

        // when
        List<ConcertDTO> result = concertUsecase.getAllConcerts();

        // then
        assertNotNull(result); // 결과가 null이 아닌지 확인
        assertEquals(concerts.size(), result.size()); // 저장된 콘서트 수와 조회된 콘서트 수가 일치하는지 확인

        // 각 콘서트 객체와 DTO의 필드 값이 일치하는지 확인
        for (int i = 0; i < concerts.size(); i++) {
            Concert expectedConcert = concerts.get(i);
            ConcertDTO actualConcertDTO = result.get(i);

            assertEquals(expectedConcert.getConcertId(), actualConcertDTO.getConcertId());
            assertEquals(expectedConcert.getConcertName(), actualConcertDTO.getConcertName());
        }
    }

    @Test
    @DisplayName("예약 가능 날짜 목록 테스트")
    public void getAvailableDatesTest() {
        // given
        Token token = new Token(1, 1, 1, 1, "ACTIVE", createTime, updateTime);
        tokenService.save(token);

        long concertId = 1L;
        List<ConcertSchedule> schedules = Arrays.asList(
                new ConcertSchedule(1, concertId, 50, date1, createTime, updateTime),
                new ConcertSchedule(2, concertId, 50, date2, createTime, updateTime),
                new ConcertSchedule(3, concertId, 50, date3, createTime, updateTime)
        );

        // Save each schedule
        for (ConcertSchedule schedule : schedules) {
            concertScheduleService.save(schedule);
        }

        // when
        List<ConcertScheduleDTO> result = concertUsecase.getAvailableDates(concertId);

        // then
        assertNotNull(result); // 결과가 null이 아닌지 확인
        assertEquals(schedules.size(), result.size()); // 저장된 일정 수와 조회된 일정 수가 일치하는지 확인

        // 각 콘서트 일정 객체와 DTO의 필드 값이 일치하는지 확인
        for (int i = 0; i < schedules.size(); i++) {
            ConcertSchedule expectedSchedule = schedules.get(i);
            ConcertScheduleDTO actualScheduleDTO = result.get(i);

            assertEquals(expectedSchedule.getConcertScheduleId(), actualScheduleDTO.getConcertScheduleId());
            assertEquals(expectedSchedule.getSeatCount(), actualScheduleDTO.getSeatCount());
            assertEquals(expectedSchedule.getConcertDate(), actualScheduleDTO.getConcertDate());
        }
    }

    @Test
    @DisplayName("예약 가능 좌석 목록 테스트")
    public void getAvailableSeatsTest() {
        // given
        Token token = new Token(1, 1, 1, 1, "ACTIVE", createTime, updateTime);
        tokenService.save(token);

        long concertScheduleId = 1L;
        List<Seat> seats = Arrays.asList(
                new Seat(1, concertScheduleId, 1, 7000, false, 0, null, createTime, updateTime, 1),
                new Seat(2, concertScheduleId, 3, 60000, false, 0, null, createTime, updateTime, 1),
                new Seat(3, concertScheduleId, 15, 50000, false, 0, null, createTime, updateTime, 1)
        );

        // Save each seat
        for (Seat seat : seats) {
            seatService.save(seat);
        }

        // when
        List<SeatDTO> result = concertUsecase.getAvailableSeats(concertScheduleId);

        // then
        assertNotNull(result); // 결과가 null이 아닌지 확인
        assertEquals(seats.size(), result.size()); // 저장된 좌석 수와 조회된 좌석 수가 일치하는지 확인

        // 각 좌석 객체와 DTO의 필드 값이 일치하는지 확인
        for (int i = 0; i < seats.size(); i++) {
            Seat expectedSeat = seats.get(i);
            SeatDTO actualSeatDTO = result.get(i);

            assertEquals(expectedSeat.getSeatId(), actualSeatDTO.getSeatId());
            assertEquals(expectedSeat.getConcertScheduleId(), actualSeatDTO.getConcertScheduleId());
            assertEquals(expectedSeat.getSeatNumber(), actualSeatDTO.getSeatNumber());
            assertEquals(expectedSeat.getPrice(), actualSeatDTO.getPrice());
            assertEquals(expectedSeat.isFinallyReserved(), actualSeatDTO.isFinallyReserved());
            assertEquals(expectedSeat.getTempAssigneeId(), actualSeatDTO.getTempAssigneeId());
        }
    }

    // 테스트를 위한 용도
    @Test
    @DisplayName("좌석 200만 건 저장")
    public void insertSeat() throws InterruptedException {
        int lastNum = 0;
        int finishedJ = 0;
        for(int i = 1; i <= 20; i++) {
            for(int j = 1; j <= 100000; j+=5) {
                Thread thread1 = new Thread(new MultiThread(j + lastNum, i, j, seatService));
                Thread thread2 = new Thread(new MultiThread(j + lastNum + 1, i, j, seatService));
                Thread thread3 = new Thread(new MultiThread(j + lastNum + 2, i, j, seatService));
                Thread thread4 = new Thread(new MultiThread(j + lastNum + 3, i, j, seatService));
                Thread thread5 = new Thread(new MultiThread(j + lastNum + 4, i, j, seatService));
                thread1.start();
                thread2.start();
                thread3.start();
                thread4.start();
                thread5.start();
                finishedJ = j + lastNum;
            }
            lastNum = lastNum + finishedJ;
        }
    }

    // 테스트코드로 유의미한 비교분석 값을 얻을 수 있을 지 테스트, 10ms차이가 나오긴함
    @Test
    @DisplayName("쿼리 성능 조회")
    public void getAvailableSeats() {
        long startTime = System.currentTimeMillis();
        long concertScheduleId = 1L;
        concertUsecase.getAvailableSeats(concertScheduleId);
        long endTime = System.currentTimeMillis();
        System.out.println("Query Execution Time: " + (endTime - startTime) + " ms");
    }
}
