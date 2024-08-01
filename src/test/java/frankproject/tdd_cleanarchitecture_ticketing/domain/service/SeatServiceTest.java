package frankproject.tdd_cleanarchitecture_ticketing.domain.service;

import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Seat;
import frankproject.tdd_cleanarchitecture_ticketing.domain.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SeatServiceTest {

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private SeatService seatService;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @BeforeEach
    public void setUp() {
        createTime = LocalDateTime.now().minusHours(3);
        updateTime = LocalDateTime.now().minusHours(1);
    }

    @Test
    @DisplayName("예약 가능 좌석 목록 조회 테스트")
    public void findAvailableSeatsTest() {
        // given
        long concertScheduleId = 1;
        List<Seat> mockSeats = Arrays.asList(
                new Seat(1, concertScheduleId, 1, 7000, false, 0, null, createTime, updateTime, 1),
                new Seat(2, concertScheduleId, 3, 60000, false, 0, null, createTime, updateTime, 1),
                new Seat(3, concertScheduleId, 15, 50000, false, 0, null, createTime, updateTime, 1)
        );

        // Mock 데이터 설정
        when(seatRepository.findAvailableSeats(concertScheduleId)).thenReturn(mockSeats);

        // when
        List<Seat> result = seatService.findAvailableSeats(concertScheduleId);

        // then
        assertEquals(mockSeats.size(), result.size());
        assertEquals(mockSeats, result);
        verify(seatRepository, times(1)).findAvailableSeats(concertScheduleId);
    }

    @Test
    @DisplayName("예약 가능 좌석가 존재하지 않을 때 예외 발생 테스트")
    public void findAvailableSeats_SeatNotFound() {
        // given
        long concertScheduleId = 300;
        when(seatRepository.findAvailableSeats(concertScheduleId)).thenReturn(Collections.emptyList());

        // when & then
        assertThrows(RuntimeException.class, () -> seatService.findAvailableSeats(concertScheduleId));
        verify(seatRepository, times(1)).findAvailableSeats(concertScheduleId);
    }

    @Test
    @DisplayName("좌석 저장 테스트")
    public void saveTest() {
        // given
        Seat seat = new Seat(1, 2, 1, 7000, false, 0, null, createTime, updateTime, 0);

        // Mock 데이터 설정
        when(seatRepository.save(any(Seat.class))).thenReturn(seat);

        // when
        Seat savedSeat = seatService.save(seat);

        // then
        assertEquals(seat, savedSeat);
        verify(seatRepository, times(1)).save(any(Seat.class));
    }

    @Test
    @DisplayName("낙관적 락을 이용해 동시성을 제어한 좌석 예약 테스트")
    public void reserveSeatWithOptimisticTest() {
        // given
        Seat seat = new Seat(1, 2, 1, 7000, false, 0L, null, createTime, updateTime, 0);

        // Mock 데이터 설정
        when(seatRepository.findById(seat.getSeatId())).thenReturn(Optional.of(seat));
        when(seatRepository.save(any(Seat.class))).thenReturn(seat);

        // when
        Seat reservedSeatWithOptimistic = seatService.reserveSeatWithOptimistic(seat.getSeatId(), 1L);

        // then
        assertEquals(seat, reservedSeatWithOptimistic);
        verify(seatRepository, times(1)).save(any(Seat.class));
    }

    @Test
    @DisplayName("비관적 락을 이용해 동시성을 제어한 좌석 예약 테스트")
    public void reserveSeatWithPessimisticTest() {
        // given
        Seat seat = new Seat(1, 2, 1, 7000, false, 0L, null, createTime, updateTime, 0);

        // Mock 데이터 설정
        when(seatRepository.findByIdWithPessimistic(seat.getSeatId())).thenReturn(Optional.of(seat));
        when(seatRepository.save(any(Seat.class))).thenReturn(seat);

        // when
        Seat reservedSeatWithPessimistic = seatService.reserveSeatWithPessimistic(seat.getSeatId(), 1L);

        // then
        assertEquals(seat, reservedSeatWithPessimistic);
        verify(seatRepository, times(1)).save(any(Seat.class));
    }
}