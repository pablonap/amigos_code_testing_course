package amigos_code_prj01.testing.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import amigos_code_prj01.customer.Customer;
import amigos_code_prj01.customer.CustomerRepository;

@DataJpaTest(properties = { "spring.jpa.properties.javax.persistence.validation.mode=none" })
class CustomerRepositoryTest {

	@Autowired
	private CustomerRepository underTest;

	@Test
	void itShouldSelectCustomerByPheNumber() {
		// given
		UUID id = UUID.randomUUID();
		String name = "john";
		String phoneNumber = "777";

		Customer customer = new Customer(id, name, phoneNumber);
		
		// when
		underTest.save(customer);
		Optional<Customer> optCustomerFromDb = underTest.selectCustomerByPhoneNumber(phoneNumber);
		
		// then
		assertThat(optCustomerFromDb).isPresent().hasValueSatisfying(c -> {
			assertThat(c.getId()).isEqualTo(id);
			assertThat(c.getName()).isEqualTo(name);
			assertThat(c.getPhoneNumber()).isEqualTo(phoneNumber);
		});
	}

	@Test
	void itNotShouldSelectCustomerByPhoneNumberWhenNumberDoesNotExists() {
		// given
		String phoneNumber = "777";

		// when
		Optional<Customer> optCustomerFromDb = underTest.selectCustomerByPhoneNumber(phoneNumber);
		
		// then
		assertThat(optCustomerFromDb).isNotPresent();


	}

	@Test
	void itShouldSaveCustomer() {
		// given
		UUID id = UUID.randomUUID();
		String name = "john";
		String phone = "777";
		
		Customer customer = new Customer(id, name, phone);
		
		// when
		underTest.save(customer);
		Optional<Customer> optCustomerFromDb = underTest.findById(id);
		
		// then
		assertThat(optCustomerFromDb).isPresent().hasValueSatisfying(c -> {
			assertThat(c.getId()).isEqualTo(id);
			assertThat(c.getName()).isEqualTo(name);
			assertThat(c.getPhoneNumber()).isEqualTo(phone);
		});
	}

	@Test
	void itShouldNotSaveCustomerWhenNameIsNull() {
		// given
		UUID id = UUID.randomUUID();
		String phone = "777";
		
		Customer customer = new Customer(id, null, phone);
		
		// then
		assertThatThrownBy(() -> underTest.save(customer))
		.hasMessageContaining(
				"not-null property references a null or transient value : amigos_code_prj01.customer.Customer.name")
		.isInstanceOf(DataIntegrityViolationException.class);
	}
	
	@Test
	void itShouldNotSaveCustomerWhenPhoneNumberIsnull() {
		// given
		UUID id = UUID.randomUUID();
		String name = "john";
		
		Customer customer = new Customer(id, name, null);
		
		// then
		assertThatThrownBy(() -> underTest.save(customer))
		.hasMessageContaining(
				"not-null property references a null or transient value : amigos_code_prj01.customer.Customer.phoneNumber")
		.isInstanceOf(DataIntegrityViolationException.class);
	}


}
