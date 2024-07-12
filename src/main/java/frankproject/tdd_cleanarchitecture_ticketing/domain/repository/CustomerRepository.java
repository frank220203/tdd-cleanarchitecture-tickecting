package frankproject.tdd_cleanarchitecture_ticketing.domain.repository;

import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Customer;

import java.util.Optional;

public interface CustomerRepository {

    Optional<Customer> findById(long customerId);

    Optional<Customer> findByIdWithLock(long customerId);

    Customer save(Customer customer);
}
