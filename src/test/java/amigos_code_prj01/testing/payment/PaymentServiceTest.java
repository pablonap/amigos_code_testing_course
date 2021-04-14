package amigos_code_prj01.testing.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import amigos_code_prj01.customer.Customer;
import amigos_code_prj01.customer.CustomerRepository;
import amigos_code_prj01.payment.CardPaymentCharge;
import amigos_code_prj01.payment.CardPaymentCharger;
import amigos_code_prj01.payment.Currency;
import amigos_code_prj01.payment.Payment;
import amigos_code_prj01.payment.PaymentRepository;
import amigos_code_prj01.payment.PaymentRequest;
import amigos_code_prj01.payment.PaymentService;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private CardPaymentCharger cardPaymentCharger;
	
	@InjectMocks
	PaymentService underTest;
	
	@Test
	void itShouldChargeCardSuccessfully() {
		// given
		Long customerId = 7L;
		Payment payment = new Payment(1L, customerId, new BigDecimal(1250), Currency.USD, "Visa", "english course");
		PaymentRequest request = new PaymentRequest(payment);
		
		CardPaymentCharge cardPaymentCharge = new CardPaymentCharge(Boolean.TRUE);
		
		given(customerRepository.findById(customerId)).willReturn(Optional.of(Mockito.mock(Customer.class)));
		
		given(cardPaymentCharger
				.chargeCard(payment.getSource(), payment.getAmount(), payment.getCurrency(), payment.getDescription()))
				.willReturn(cardPaymentCharge);
		
		// when
		underTest.chargeCard(customerId, request);
		
		// then
		ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

		then(paymentRepository).should().save(paymentCaptor.capture());
		assertThat(paymentCaptor.getValue()).isEqualTo(payment);
	}
	
	@Test
	void itShouldThrowWhenThereIsNoPayment() {
		// given
		Payment payment = null;
		PaymentRequest request = new PaymentRequest(payment);
		
		// when
		assertThatThrownBy(() -> underTest.chargeCard(7L, request))
		.hasMessageContaining("Payment not received")
		.isInstanceOf(IllegalStateException.class);

		// then
		then(cardPaymentCharger).shouldHaveNoInteractions();
		then(paymentRepository).should(never()).save(any(Payment.class));
	}
	
	@Test
	void itShouldThrowWhenCustomerIsNotFound() {
		// given
		Payment payment = new Payment();
		PaymentRequest request = new PaymentRequest(payment);
		Long customerId = 7L;
		
		given(customerRepository.findById(any())).willReturn(Optional.empty());
		
		// when
		assertThatThrownBy(() -> underTest.chargeCard(customerId, request))
		.hasMessageContaining(String.format("Customer with id [%s] not found", customerId))
		.isInstanceOf(IllegalStateException.class);

		// then
		then(cardPaymentCharger).shouldHaveNoInteractions();
		then(paymentRepository).should(never()).save(any(Payment.class));
	}
	
	@Test
	void itShouldThrowWhenCardIsNotDebited() {
		// given
		Long customerId = 7L;
		Payment payment = new Payment(1L, customerId, new BigDecimal(1250), Currency.USD, "Visa", "english course");
		PaymentRequest request = new PaymentRequest(payment);
		
		CardPaymentCharge cardPaymentCharge = new CardPaymentCharge(Boolean.FALSE);
		
		given(customerRepository.findById(customerId)).willReturn(Optional.of(Mockito.mock(Customer.class)));
		
		given(cardPaymentCharger
				.chargeCard(payment.getSource(), payment.getAmount(), payment.getCurrency(), payment.getDescription()))
				.willReturn(cardPaymentCharge);
		
		// when
		assertThatThrownBy(() -> underTest.chargeCard(customerId, request))
		.hasMessageContaining(String.format("Card not debited for customer [%s]", payment.getCustomerId()) )
		.isInstanceOf(IllegalStateException.class);
		
		// then
		then(paymentRepository).should(never()).save(any(Payment.class));
	}
	
	@Test
	void itShouldNotChargeCardWhenCurrencyNotSupported() {
		// given
		Long customerId = 7L;
		Payment payment = new Payment(1L, customerId, new BigDecimal(1250), Currency.EUR, "Visa", "english course");
		PaymentRequest request = new PaymentRequest(payment);
		
		given(customerRepository.findById(customerId)).willReturn(Optional.of(Mockito.mock(Customer.class)));
		
		// when
		assertThatThrownBy(() -> underTest.chargeCard(customerId, request))
		.hasMessageContaining(String.format(String.format("Currency [%s] not supported", payment.getCurrency())))
		.isInstanceOf(IllegalStateException.class);

		// then
		then(cardPaymentCharger).shouldHaveNoInteractions();
		then(paymentRepository).should(never()).save(any(Payment.class));
	}
}
