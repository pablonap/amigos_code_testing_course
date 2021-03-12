package amigos_code_prj01.payment;

import java.math.BigDecimal;

public interface CardPaymentCharger {
	
	CardPaymentCharge chargeCard(
			String cardSource,
			BigDecimal amount,
			Currency currency,
			String description
			);
}
