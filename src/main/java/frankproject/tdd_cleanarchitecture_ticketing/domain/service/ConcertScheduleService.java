package frankproject.tdd_cleanarchitecture_ticketing.domain.service;

import frankproject.tdd_cleanarchitecture_ticketing.domain.common.CoreException;
import frankproject.tdd_cleanarchitecture_ticketing.domain.common.ErrorCode;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.ConcertSchedule;
import frankproject.tdd_cleanarchitecture_ticketing.domain.repository.ConcertScheduleRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ConcertScheduleService {

    private final ConcertScheduleRepository concertScheduleRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public ConcertScheduleService(ConcertScheduleRepository concertScheduleRepository, RedisTemplate<String, Object> redisTemplate) {
        this.concertScheduleRepository = concertScheduleRepository;
        this.redisTemplate = redisTemplate;
    }

    public List<ConcertSchedule> findByConcertId(long concertId) {
        String cacheKey = "concertSchedule::" + concertId;
        List<ConcertSchedule> schedules = (List<ConcertSchedule>) redisTemplate.opsForValue().get(cacheKey);

        if (schedules == null) {
            schedules = concertScheduleRepository.findByConcertId(concertId);
            if (schedules.isEmpty()) {
                throw new CoreException(ErrorCode.NO_AVAILABLE_DATES);
            }
            redisTemplate.opsForValue().set(cacheKey, schedules, 10, TimeUnit.MINUTES); // 캐시 만료 시간을 10분으로 설정
        }

        return schedules;
    }

    public ConcertSchedule save(ConcertSchedule concertSchedule) {
        ConcertSchedule savedSchedule = concertScheduleRepository.save(concertSchedule);
        updateCache(concertSchedule.getConcertId());
        return savedSchedule;
    }

    private void updateCache(long concertId) {
        String cacheKey = "concertSchedule::" + concertId;
        List<ConcertSchedule> schedules = concertScheduleRepository.findByConcertId(concertId);
        redisTemplate.opsForValue().set(cacheKey, schedules, 10, TimeUnit.MINUTES); // 캐시 만료 시간을 10분으로 설정
    }
}

