package amigos_code_prj01.testing.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;

import amigos_code_prj01.customer.Customer;
import amigos_code_prj01.customer.CustomerRepository;

@DataJpaTest(properties = {"spring.jpa.properties.javax.persistence.validation.mode=none"})
@TestPropertySource({"classpath:application-test.properties"})
class CustomerRepositoryTest {

	@Autowired
	private CustomerRepository underTest;
	
	@BeforeEach
	void cleanDataBase() {
		underTest.deleteAll();
	}

	@Test
	void itShouldSelectCustomerByPheNumber() {
		// given
		String name = "john";
		String phoneNumber = "777";

		Customer customer = new Customer(null, name, phoneNumber);
		
		// when
		underTest.save(customer);
		Optional<Customer> optCustomerFromDb = underTest.selectCustomerByPhoneNumber(phoneNumber);
		
		// then
		assertThat(optCustomerFromDb).isPresent().hasValueSatisfying(c -> {
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
		Long id = 1L;
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
	void itShouldSaveCustomerWithIdNull() {
		// given
		String name = "john";
		String phone = "777";
		
		Customer customer = new Customer(null, name, phone);
		
		// when
		long amounUserstBeforeSave = underTest.count();
		underTest.save(customer);
		long amounUserstAfterSave = underTest.count();
		
		// then
		assertThat(amounUserstAfterSave).isGreaterThan(amounUserstBeforeSave);
	}

	@Test
	void itShouldNotSaveCustomerWhenNameIsNull() {
		// given
		String phone = "777";
		
		Customer customer = new Customer(null, null, phone);
		
		// then
		assertThatThrownBy(() -> underTest.save(customer))
		.hasMessageContaining(
				"not-null property references a null or transient value : amigos_code_prj01.customer.Customer.name")
		.isInstanceOf(DataIntegrityViolationException.class);
	}
	
	@Test
	void itShouldNotSaveCustomerWhenPhoneNumberIsnull() {
		// given
		String name = "john";
		
		Customer customer = new Customer(null, name, null);
		
		// then
		assertThatThrownBy(() -> underTest.save(customer))
		.hasMessageContaining(
				"not-null property references a null or transient value : amigos_code_prj01.customer.Customer.phoneNumber")
		.isInstanceOf(DataIntegrityViolationException.class);
	}


}
