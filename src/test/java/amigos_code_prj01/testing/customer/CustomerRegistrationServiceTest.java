package amigos_code_prj01.testing.customer;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;

import amigos_code_prj01.customer.CustomerRegistrationService;
import amigos_code_prj01.customer.CustomerRepository;

public class CustomerRegistrationServiceTest {
	
	private CustomerRepository customerRepository = mock(CustomerRepository.class);

	private CustomerRegistrationService underTest;
	
	@BeforeEach
	void setUp() {
		underTest = new CustomerRegistrationService(customerRepository);
	}

}
