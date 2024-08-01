package frankproject.tdd_cleanarchitecture_ticketing.domain.service;

import frankproject.tdd_cleanarchitecture_ticketing.domain.common.CoreException;
import frankproject.tdd_cleanarchitecture_ticketing.domain.common.ErrorCode;
import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Customer;
import frankproject.tdd_cleanarchitecture_ticketing.domain.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer findById(long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CoreException(ErrorCode.USER_NOT_FOUND));
    }

    public Customer findByIdWithLock(long customerId) {
        return customerRepository.findByIdWithLock(customerId)
                .orElseThrow(() -> new CoreException(ErrorCode.USER_NOT_FOUND));
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }
}
