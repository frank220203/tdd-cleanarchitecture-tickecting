package frankproject.tdd_cleanarchitecture_ticketing.domain.service.payment;

public interface PaymentMessagePublisher {
    public void sendMessage(String message);
}
