package frankproject.tdd_cleanarchitecture_ticketing.infrastructure.repository;

import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Payment.PaymentOutbox;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentOutboxJpaRepository extends JpaRepository<PaymentOutbox, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PaymentOutbox p WHERE p.paymentId = :paymentId AND p.status = 'INIT'")
    List<PaymentOutbox> findByPaymentId(@Param("paymentId") long paymentId);

    @Query("SELECT p FROM PaymentOutbox p WHERE p.status = :status")
    List<PaymentOutbox> findAllByStatus(@Param("status") String status);
}
