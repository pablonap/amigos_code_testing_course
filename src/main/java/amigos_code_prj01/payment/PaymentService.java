package amigos_code_prj01.payment;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import amigos_code_prj01.customer.Customer;
import amigos_code_prj01.customer.CustomerRepository;

@Service
public class PaymentService {

	private final CustomerRepository customerRepository;
	private final PaymentRepository paymentRepository;
	private final CardPaymentCharger cardPaymentCharger;
	
	private static final List<Currency> ACCEPTED_CURRENCIES = List.of(Currency.USD, Currency.GBP);

	@Autowired
	public PaymentService(CustomerRepository customerRepository, PaymentRepository paymentRepository,
			CardPaymentCharger cardPaymentCharger) {
		this.customerRepository = customerRepository;
		this.paymentRepository = paymentRepository;
		this.cardPaymentCharger = cardPaymentCharger;
	}

	public void chargeCard(Long customerId, PaymentRequest paymentRequest) {
		Payment payment = paymentRequest.getPayment();
		
		if (payment == null) {
			throw new IllegalStateException("Payment not received");
		}

		Optional<Customer> optCustomerFromDb = customerRepository.findById(customerId);

		if (optCustomerFromDb.isPresent() == false) {
			throw new IllegalStateException(String.format("Customer with id [%s] not found", customerId));
		}

		boolean isCurrencySupported = ACCEPTED_CURRENCIES.stream()
				.anyMatch(c -> c.equals(payment.getCurrency()));

		if (isCurrencySupported == false) {
			throw new IllegalStateException(String.format("Currency [%s] not supported", payment.getCurrency()));
		}

		CardPaymentCharge cardPaymentCharge = cardPaymentCharger.chargeCard(
				payment.getSource(), 
				payment.getAmount(),
				payment.getCurrency(), 
				payment.getDescription());

		if (cardPaymentCharge.isCardDebited() == false) {
			throw new IllegalStateException(String.format("Card not debited for customer [%s]", payment.getCustomerId()));
		}

		paymentRepository.save(payment);
	}
}
