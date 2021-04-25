package amigos_code_prj01.testing.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
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
		Customer customer = CustomerUtils.customerOf();
		
		// when
		underTest.save(customer);
		Optional<Customer> optCustomerFromDb = underTest.selectCustomerByPhoneNumber(customer.getPhoneNumber());
		
		// then
		assertThat(optCustomerFromDb).isPresent().hasValueSatisfying(c -> {
			assertThat(c.getName()).isEqualTo(customer.getName());
			assertThat(c.getPhoneNumber()).isEqualTo(customer.getPhoneNumber());
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
		Customer customer = CustomerUtils.customerOf();
		
		// when
		underTest.save(customer);
		Optional<Customer> optCustomerFromDb = underTest.selectCustomerByPhoneNumber(customer.getPhoneNumber());
		
		// then
		assertThat(optCustomerFromDb).isPresent().hasValueSatisfying(c -> {
			assertThat(c.getName()).isEqualTo(customer.getName());
			assertThat(c.getPhoneNumber()).isEqualTo(customer.getPhoneNumber());
		});
	}
	
	@Test
	void itShouldSaveCustomerWithIdNull() {
		// given
		Customer customer = CustomerUtils.customerOf();
		
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
		Customer customer = CustomerUtils.customerOf();
		customer.setName(null);
		
		// then
		assertThatThrownBy(() -> underTest.save(customer))
		.hasMessageContaining(
				"not-null property references a null or transient value : amigos_code_prj01.customer.Customer.name")
		.isInstanceOf(DataIntegrityViolationException.class);
	}
	
	@Test
	void itShouldNotSaveCustomerWhenPhoneNumberIsnull() {
		// given
		Customer customer = CustomerUtils.customerOf();
		customer.setPhoneNumber(null);
		
		// then
		assertThatThrownBy(() -> underTest.save(customer))
		.hasMessageContaining(
				"not-null property references a null or transient value : amigos_code_prj01.customer.Customer.phoneNumber")
		.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	void itShouldReturnFivePageableCustomersInOnePageWhenAskingByAllCustomers() {
		// given
        saveCustomers();
        
        PageRequest pageRequest = PageRequest.of(0, 10);
        
        // when
        List<Customer> customersResponse = underTest.findAllPageableCustomers(pageRequest).getContent();

		// then
        List<Customer> expectedCustomers = LongStream.rangeClosed(1, 5).mapToObj(i -> {
        	final long id = i;
        	final String name = "customer_" + i;
        	final String phoneNumberDigit = String.valueOf(i);
        	final String phone = phoneNumberDigit + phoneNumberDigit + phoneNumberDigit + phoneNumberDigit;
        	final String password = "password123";

            Customer customer = new Customer();
            customer.setId(id);
            customer.setName(name);
            customer.setPhoneNumber(phone);
            customer.setPassword(password);
            return customer;
		}).collect(Collectors.toList());
        
        int idx = 0;
        for(Customer expectedCustomer : expectedCustomers) {
			assertThat(customersResponse.get(idx).getId()).isEqualTo(expectedCustomer.getId());
			assertThat(customersResponse.get(idx).getName()).isEqualTo(expectedCustomer.getName());
			assertThat(customersResponse.get(idx).getPassword()).isEqualTo(expectedCustomer.getPassword());
			assertThat(customersResponse.get(idx).getPhoneNumber()).isEqualTo(expectedCustomer.getPhoneNumber());
			idx++;
        }
	}

	private void saveCustomers() {
		IntStream.rangeClosed(1, 5).mapToObj(i -> {
        	final String name = "customer_" + i;
        	final String phoneNumberDigit = String.valueOf(i);
        	final String phone = phoneNumberDigit + phoneNumberDigit + phoneNumberDigit + phoneNumberDigit;
        	final String password = "password123";

            Customer customer = new Customer();
            customer.setName(name);
            customer.setPhoneNumber(phone);
            customer.setPassword(password);
            return customer;
        }).forEach(c -> underTest.save(c));
	}

}
