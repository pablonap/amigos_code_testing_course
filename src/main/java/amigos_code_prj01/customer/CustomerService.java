package amigos_code_prj01.customer;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import amigos_code_prj01.utils.PhoneNumberValidator;

@Service
public class CustomerService {
	
	private final CustomerRepository customerRepository;
	private final PhoneNumberValidator phoneNumberValidator;
	private final ModelMapper mapper;
	
	@Autowired
	public CustomerService(CustomerRepository customerRepository, PhoneNumberValidator phoneNumberValidator, ModelMapper mapper) {
		this.customerRepository = customerRepository;
		this.phoneNumberValidator = phoneNumberValidator;
		this.mapper = mapper;
	}

	public void registerNewCustomer(CustomerRegistrationRequest request) {
		CustomerRegistrationRequestDto dto = request.getCustomer();
		
		Customer requestedCustomer = mapper.map(dto, Customer.class);

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
	
	public Page<ICustomer> findAllCustomers(Pageable pageable) {
		return customerRepository.findAllPageableCustomers(pageable).map(r -> r);
	}

}
