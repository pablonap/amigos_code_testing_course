package amigos_code_prj01.payment.stripe;

import java.math.BigDecimal;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import amigos_code_prj01.payment.CardPaymentCharge;
import amigos_code_prj01.payment.CardPaymentCharger;
import amigos_code_prj01.payment.Currency;

@Service
@ConditionalOnProperty(
		value = "stripe.enabled",
		havingValue = "false"
		)
public class MockStripeService implements CardPaymentCharger {

	@Override
	public CardPaymentCharge chargeCard(
			String cardSource, 
			BigDecimal amount, 
			Currency currency, 
			String description) {
		CardPaymentCharge cardPaymentCharge = new CardPaymentCharge(true);

		return cardPaymentCharge ;
	}

}
