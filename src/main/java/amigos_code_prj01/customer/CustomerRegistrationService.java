package amigos_code_prj01.customer;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import amigos_code_prj01.utils.PhoneNumberValidator;

@Service
public class CustomerRegistrationService {
	
	private final CustomerRepository customerRepository;
	private final PhoneNumberValidator phoneNumberValidator;
	
	@Autowired
	public CustomerRegistrationService(CustomerRepository customerRepository, PhoneNumberValidator phoneNumberValidator) {
		this.customerRepository = customerRepository;
		this.phoneNumberValidator = phoneNumberValidator;
	}

	public void registerNewCustomer(CustomerRegistrationRequest request) {
		Customer requestedCustomer = request.getCustomer();
		String phoneNumber = requestedCustomer.getPhoneNumber();
		
		if (!phoneNumberValidator.test(phoneNumber)) {
			throw new IllegalStateException("Phone number " + phoneNumber + " is not valid");
		}
		
		Optional<Customer> optCustomerFromDb = customerRepository.selectCustomerByPhoneNumber(phoneNumber);

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
