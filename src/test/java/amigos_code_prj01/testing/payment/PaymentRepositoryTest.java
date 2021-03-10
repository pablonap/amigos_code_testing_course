package amigos_code_prj01.testing.payment;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import amigos_code_prj01.payment.Currency;
import amigos_code_prj01.payment.Payment;
import amigos_code_prj01.payment.PaymentRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = { "spring.jpa.properties.javax.persistence.validation.mode=none" })
class PaymentRepositoryTest {
	
	@Autowired
	private PaymentRepository underTest;
	
	@Test
	void itShouldInsertPayment() {
		// given
		long paymentId = 1L;
		Payment payment = new Payment(paymentId, UUID.randomUUID(), new BigDecimal(35), Currency.USD, "card123",
			"testing course");
		
		// when
		underTest.save(payment);
		
		// then
		Optional<Payment> optPaymentFromDb = underTest.findById(paymentId);
		assertThat(optPaymentFromDb)
			.isPresent()
			.hasValueSatisfying(p -> {
				assertThat(p).isEqualTo(payment);
			});
	}

}
