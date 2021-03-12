package amigos_code_prj01.payment;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import amigos_code_prj01.customer.CustomerRepository;

@Service
public class PaymentService {
	
	private final CustomerRepository customerRepository;
	private final PaymentRepository paymentRepository;

	@Autowired
	public PaymentService(CustomerRepository customerRepository, PaymentRepository paymentRepository) {
		this.customerRepository = customerRepository;
		this.paymentRepository = paymentRepository;
	}
	
	void chargeCard(UUID customerId, PaymentRequest paymentRequest) {
		
	}

}
