package amigos_code_prj01.testing.payment.stripe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.ArgumentMatchers.*;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;

import amigos_code_prj01.payment.CardPaymentCharge;
import amigos_code_prj01.payment.Currency;
import amigos_code_prj01.payment.stripe.StripeApi;
import amigos_code_prj01.payment.stripe.StripeService;

@ExtendWith(MockitoExtension.class)
public class StripeServiceTest {
	
	@Mock
	private StripeApi stripeApi;
	
	@InjectMocks
	private StripeService underTest;
	
	@Test
	void itShouldChargeCard() throws StripeException {
		// given
		String cardSource = "0x0x0x";
		BigDecimal amount = new BigDecimal(10);
		Currency currency = Currency.USD;
		String description = "course";
		
		Charge charge = new Charge();
		charge.setPaid(true);
		given(stripeApi.create(anyMap(), any())).willReturn(charge);
		
		// when
		CardPaymentCharge cardPaymentCharge = underTest.chargeCard(cardSource, amount, currency, description);
		
		// then
		ArgumentCaptor<Map<String, Object>> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
		ArgumentCaptor<RequestOptions> optionsArgumentCaptor = ArgumentCaptor.forClass(RequestOptions.class);
		
		then(stripeApi).should().create(mapArgumentCaptor.capture(), optionsArgumentCaptor.capture());

		Map<String, Object> requestMap = mapArgumentCaptor.getValue();
		
		assertThat(requestMap.keySet()).hasSize(4);
		assertThat(requestMap.get("amount")).isEqualTo(amount);
		assertThat(requestMap.get("currency")).isEqualTo(currency);
		assertThat(requestMap.get("source")).isEqualTo(cardSource);
		assertThat(requestMap.get("description")).isEqualTo(description);
		
		RequestOptions options = optionsArgumentCaptor.getValue();
		
		// I use isNotNull because I don't care about the api key. Just I wanna make sure that
		// I'm passing the options inside.
		assertThat(options).isNotNull();
		
		assertThat(cardPaymentCharge.isCardDebited()).isTrue();
	}
}
