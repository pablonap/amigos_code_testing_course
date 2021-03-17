package amigos_code_prj01.payment.stripe;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;

import amigos_code_prj01.payment.CardPaymentCharge;
import amigos_code_prj01.payment.CardPaymentCharger;
import amigos_code_prj01.payment.Currency;

//This code is taken from https://stripe.com/docs/api
@Service
@ConditionalOnProperty(
		value = "stripe.enabled",
		havingValue = "true"
		)
public class StripeService implements CardPaymentCharger {
	
	private final StripeApi stripeApi;
	
	@Autowired
	public StripeService(StripeApi stripeApi) {
		this.stripeApi = stripeApi;
	}

	// Grab from Authentication section
	private final static RequestOptions requestOptions = RequestOptions.builder()
			  .setApiKey("sk_test_4eC39HqLyjWDarjtT1zdp7dc")
			  .build();

	@Override
	// Taken from Create charge
	public CardPaymentCharge chargeCard(String cardSource, BigDecimal amount, Currency currency, String description) {
		Map<String, Object> params = new HashMap<>();
		params.put("amount", amount);
		params.put("currency", currency);
		params.put("source", cardSource);
		params.put("description", description);

		try {
			// Here I'm trying to connect to the real stripe API.
			Charge charge = stripeApi.create(params, requestOptions);
			return new CardPaymentCharge(charge.getPaid());
		} catch (StripeException e) {
			throw new IllegalStateException("Cannot make stripe charge", e);
		}
	}

}
