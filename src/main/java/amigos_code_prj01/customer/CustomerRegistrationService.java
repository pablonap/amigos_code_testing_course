package amigos_code_prj01.customer;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerRegistrationService {
	
	private final CustomerRepository customerRepository;
	
	@Autowired
	public CustomerRegistrationService(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	public void registerNewCustomer(CustomerRegistrationRequest request) {
		Customer requestedCustomer = request.getCustomer();
		
		Optional<Customer> optCustomerFromDb = customerRepository.selectCustomerByPhoneNumber(requestedCustomer.getPhoneNumber());

		if (optCustomerFromDb.isPresent()) {
			Customer customerFromBd = optCustomerFromDb.get();
			if (customerFromBd.getName().equals(requestedCustomer.getName())) {
				return;
			} 
			
			throw new IllegalStateException(String.format("phone number [%s] is taken", requestedCustomer.getPhoneNumber()));
		}
		
		customerRepository.save(requestedCustomer);
	}

}
