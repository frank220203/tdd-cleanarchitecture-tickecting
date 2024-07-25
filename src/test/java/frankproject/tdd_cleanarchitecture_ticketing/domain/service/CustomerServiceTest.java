package frankproject.tdd_cleanarchitecture_ticketing.domain.service;

import frankproject.tdd_cleanarchitecture_ticketing.domain.entity.Customer;
import frankproject.tdd_cleanarchitecture_ticketing.domain.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @BeforeEach
    public void setUp() {
        createTime = LocalDateTime.now().minusHours(3);
        updateTime = LocalDateTime.now().minusHours(1);
    }

    @Test
    @DisplayName("고객 조회 테스트")
    public void findByIdTest() {
        // given
        long customerId = 1;
        Customer mockCustomer = new Customer(1, "홍길동", 10000, createTime, updateTime);

        // Mock 데이터 설정
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));

        // when
        Customer result = customerService.findById(customerId);

        // then
        assertEquals(mockCustomer, result);
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    @DisplayName("고객 조회 Lock 테스트")
    public void findByIdWithLockTest() {
        // given
        long customerId = 1;
        Customer mockCustomer = new Customer(1, "홍길동", 10000, createTime, updateTime);

        // Mock 데이터 설정
        when(customerRepository.findByIdWithLock(customerId)).thenReturn(Optional.of(mockCustomer));

        // when
        Customer result = customerService.findByIdWithLock(customerId);

        // then
        assertEquals(mockCustomer, result);
        verify(customerRepository, times(1)).findByIdWithLock(customerId);
    }

    @Test
    @DisplayName("고객이 존재하지 않을 때 예외 발생 테스트")
    public void findById_CustomerNotFound() {
        // given
        long customerId = 500;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(RuntimeException.class, () -> customerService.findById(customerId));
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    @DisplayName("고객 저장 테스트")
    public void saveTest() {
        // given
        Customer customer = new Customer(1, "홍길동", 10000, createTime, updateTime);

        // Mock 데이터 설정
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // when
        Customer savedCustomer = customerService.save(customer);

        // then
        assertEquals(customer, savedCustomer);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }
}
