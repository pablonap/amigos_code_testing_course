package amigos_code_prj01.payment;

import java.math.BigDecimal;
import java.util.UUID;

public class Payment {
	
	private Long paymentId;
	
	private UUID customerId;
	
	private BigDecimal amount;
	
	private Currency currency;
	
	private String source;
	
	private String description;

	public Payment(Long paymentId, UUID customerId, BigDecimal amount, Currency currency, String source,
			String description) {
		this.paymentId = paymentId;
		this.customerId = customerId;
		this.amount = amount;
		this.currency = currency;
		this.source = source;
		this.description = description;
	}

	public Payment() {
	}

	public Long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
	}

	public UUID getCustomerId() {
		return customerId;
	}

	public void setCustomerId(UUID customerId) {
		this.customerId = customerId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}