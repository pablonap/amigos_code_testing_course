package amigos_code_prj01;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import amigos_code_prj01.customer.Customer;
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
	}

	@Test
	void itShouldSaveCustomer() {
		// given
		UUID id = UUID.randomUUID();
		Customer customer = new Customer(id, "Abel", "0000");
		
		// when
		underTest.save(customer);
		
		// then
		Optional<Customer> optionalCustomer = underTest.findById(id);
		assertThat(optionalCustomer)
		.isPresent()
		.hasValueSatisfying(c -> {
			assertThat(c.getId()).isEqualTo(id);
			assertThat(c.getName()).isEqualTo("Abel");
			assertThat(c.getPhoneNumber()).isEqualTo("0000");
		});

	}

}
