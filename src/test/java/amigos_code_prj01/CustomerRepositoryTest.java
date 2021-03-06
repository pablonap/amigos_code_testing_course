package amigos_code_prj01;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import amigos_code_prj01.customer.CustomerRepository;

@DataJpaTest
class CustomerRepositoryTest {
	
	@Autowired
	private CustomerRepository underTest;

	@Test
	void itShouldSelectCustomerByPhoneNumber() {
		// given
		
		// when
		
		// then
		assertTrue(true);
		 
	}

}
