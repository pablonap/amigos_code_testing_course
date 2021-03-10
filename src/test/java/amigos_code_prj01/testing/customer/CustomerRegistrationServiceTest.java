package amigos_code_prj01.testing.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import amigos_code_prj01.customer.Customer;
import amigos_code_prj01.customer.CustomerRegistrationRequest;
import amigos_code_prj01.customer.CustomerRegistrationService;
import amigos_code_prj01.customer.CustomerRepository;

@ExtendWith(MockitoExtension.class)
public class CustomerRegistrationServiceTest {
	
	@Mock
    private CustomerRepository customerRepository;
	
	@InjectMocks
	private CustomerRegistrationService underTest;

	@Captor
	private ArgumentCaptor<Customer> customerArgumentCaptor;
	
	@Test
	void itShouldSaveNewCustomer() {
		// given
		String phoneNumber = "777";
		Customer customer = new Customer(UUID.randomUUID(), "luca", phoneNumber);
		
		CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
		
		given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
			.willReturn(Optional.empty());
		
		// when
		underTest.registerNewCustomer(request);
		
		// then
		then(customerRepository).should().save(customerArgumentCaptor.capture());
		
		Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
		assertThat(customerArgumentCaptorValue).isEqualTo(customer);
	}

	@Test
	void itShouldSaveNewCustomerWhenIdIsNull() {
		// given
		String phoneNumber = "777";
		Customer customer = new Customer(null, "luca", phoneNumber);
		
		CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
		
		given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
			.willReturn(Optional.empty());
		
		// when
		underTest.registerNewCustomer(request);
		
		// then
		then(customerRepository).should().save(customerArgumentCaptor.capture());
		
		Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();

		assertThat(customerArgumentCaptorValue.getName()).isEqualTo(customer.getName());
		assertThat(customerArgumentCaptorValue.getId()).isNotNull();
	}

	@Test
	void itShouldNotSaveNewCustomerWhenCustomerExists() {
		// given
		String phoneNumber = "777";
		Customer customer = new Customer(UUID.randomUUID(), "luca", phoneNumber);
		
		CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
		
		given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
			.willReturn(Optional.of(customer));
		
		// when
		underTest.registerNewCustomer(request);
		
		// then
		then(customerRepository).should(never()).save(any());
		// Another option:
//		then(customerRepository).should().selectCustomerByPhoneNumber(phoneNumber);
//		then(customerRepository).shouldHaveNoMoreInteractions();
	}

	@Test
	void itShouldThrowWhenPhoneNumberIsTaken() {
		// given
		String phoneNumber = "123";
		Customer savedCustomer = new Customer(UUID.randomUUID(), "luca", phoneNumber);
		Customer requestedCustomer = new Customer(UUID.randomUUID(), "john", phoneNumber);
		
		CustomerRegistrationRequest request = new CustomerRegistrationRequest(requestedCustomer);
		
		given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
			.willReturn(Optional.of(savedCustomer));
		
		// when
		// then
		assertThatThrownBy(() -> underTest.registerNewCustomer(request))
			.hasMessageContaining(String.format("phone number [%s] is taken", phoneNumber))
			.isInstanceOf(IllegalStateException.class);
		
		then(customerRepository).should(never()).save(any(Customer.class));
	}

}
