package frankproject.tdd_cleanarchitecture_ticketing.domain.service;

import frankproject.tdd_cleanarchitecture_ticketing.domain.common.CoreException;
import frankproject.tdd_cleanarchitecture_ticketing.domain.common.ErrorCode;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Seat;
import frankproject.tdd_cleanarchitecture_ticketing.domain.repository.SeatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class SeatService {

    private final SeatRepository seatRepository;

    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    public List<Seat> findAvailableSeats(long concertScheduleId) {
        List<Seat> seats = seatRepository.findAvailableSeats(concertScheduleId);
        if (seats.isEmpty()) {
            throw new CoreException(ErrorCode.NO_AVAILABLE_SEATS);
        }
        return seats;
    }

    public Seat findById(long seatId) {
        return seatRepository.findById(seatId)
                .orElseThrow(() -> new CoreException(ErrorCode.SEAT_NOT_FOUND));
    }

    public Seat save(Seat seat) {
        return seatRepository.save(seat);
    }


    // 낙관적 락을 위한 테스트 메소드
    @Transactional
    public Seat reserveSeatWithOptimistic(long seatId, long customerId) {
        long startTime = System.currentTimeMillis(); // 시작 시간 기록
        Seat seat = null;
        Seat tempSeat = null;
        log.info("{}>> [Optimistic Lock] reserveSeatWithOptimistic 시작, 예약ID: {}", Thread.currentThread().getName(), customerId);
        try {
            seat = findById(seatId);
            seat.reserveSeat(customerId);
            tempSeat = seatRepository.save(seat);
        } catch (DataAccessException e) {
            log.error("{}>> [Optimistic Lock] 데이터베이스 접근 오류 발생: 예약ID: {}, 오류 메시지: {}",
                    Thread.currentThread().getName(), customerId, e.getMessage());
            log.error("{}>> [Optimistic Lock] 스택 트레이스: ", Thread.currentThread().getName(), e);
            throw e; // 예외를 다시 던져서 트랜잭션이 롤백되도록 합니다.
        } finally {
            long endTime = System.currentTimeMillis(); // 종료 시간 기록
            long duration = endTime - startTime;
            log.info("{}>> [Optimistic Lock] reserveSeatWithOptimistic 종료, 소요 시간: {} ms", Thread.currentThread().getName(), duration);
        }
        return tempSeat;
    }

    // 비관적 락을 위한 테스트 메소드
    public Seat findByIdWithPessimistic(long seatId) {
        return seatRepository.findByIdWithPessimistic(seatId)
                .orElseThrow(() -> new CoreException(ErrorCode.SEAT_NOT_FOUND));
    }

    // 비관적 락을 위한 테스트 메소드
    @Transactional
    public Seat reserveSeatWithPessimistic(long seatId, long customerId) {
        long startTime = System.currentTimeMillis(); // 시작 시간 기록
        Seat seat = null;
        Seat tempSeat = null;
        log.info("{}>> [Pessimistic Lock] reserveSeatWithPessimistic 시작, 예약ID: {}", Thread.currentThread().getName(), customerId);
        try {
            seat = findByIdWithPessimistic(seatId);
            seat.reserveSeat(customerId);
            tempSeat = seatRepository.save(seat);
        } catch (DataAccessException e) {
            log.error("{}>> [Pessimistic Lock] 데이터베이스 접근 오류 발생: 예약ID: {}, 오류 메시지: {}",
                    Thread.currentThread().getName(), customerId, e.getMessage());
            log.error("{}>> [Pessimistic Lock] 스택 트레이스: ", Thread.currentThread().getName(), e);
            throw e; // 예외를 다시 던져서 트랜잭션이 롤백되도록 합니다.
        } finally {
            long endTime = System.currentTimeMillis(); // 종료 시간 기록
            long duration = endTime - startTime;
            log.info("{}>> [Pessimistic Lock] reserveSeatWithPessimistic 종료, 소요 시간: {} ms", Thread.currentThread().getName(), duration);
        }
        return tempSeat;
    }
}
