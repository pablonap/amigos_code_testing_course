package amigos_code_prj01.testing.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import amigos_code_prj01.customer.Customer;
import amigos_code_prj01.customer.CustomerRegistrationRequest;
import amigos_code_prj01.customer.CustomerRegistrationRequestDto;
import amigos_code_prj01.customer.CustomerRepository;
import amigos_code_prj01.customer.CustomerService;
import amigos_code_prj01.utils.PhoneNumberValidator;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private PhoneNumberValidator phoneNumberValidator;

	@Mock
	ModelMapper mapper;

	@InjectMocks
	private CustomerService underTest;

	@Captor
	private ArgumentCaptor<Customer> customerArgumentCaptor;

	@Test
	void itShouldSaveNewCustomer() {
		// given
		String phoneNumber = "777";

		Customer customer = CustomerUtils.customerOf();

		CustomerRegistrationRequest request = new CustomerRegistrationRequest(Mockito.mock(CustomerRegistrationRequestDto.class));

		given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

		given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

		given(mapper.map(any(), any())).willReturn(customer);

		// when
		underTest.registerNewCustomer(request);

		// then
		then(customerRepository).should().save(customerArgumentCaptor.capture());

		Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
		assertThat(customerArgumentCaptorValue).isEqualTo(customer);
	}

	@Test
	void itShouldNotSaveNewCustomerWhenPhoneNumberIsInvalid() {
		// given
		String phoneNumber = "777";

		Customer customer = CustomerUtils.customerOf();

		CustomerRegistrationRequest request = new CustomerRegistrationRequest(Mockito.mock(CustomerRegistrationRequestDto.class));

		given(mapper.map(any(), any())).willReturn(customer);

		given(phoneNumberValidator.test(phoneNumber)).willReturn(false);

		// when
		assertThatThrownBy(() -> underTest.registerNewCustomer(request)).isInstanceOf(IllegalStateException.class);

		// then
		then(customerRepository).shouldHaveNoInteractions();
	}

	@Test
	void itShouldSaveNewCustomerWhenIdIsNull() {
		// given
		String phoneNumber = "777";

		Customer customer = CustomerUtils.customerOf();

		CustomerRegistrationRequest request = new CustomerRegistrationRequest(Mockito.mock(CustomerRegistrationRequestDto.class));

		given(mapper.map(any(), any())).willReturn(customer);

		given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

		given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

		// when
		underTest.registerNewCustomer(request);

		// then
		then(customerRepository).should().save(customerArgumentCaptor.capture());

		Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();

		assertThat(customerArgumentCaptorValue.getName()).isEqualTo(customer.getName());
	}

	@Test
	void itShouldNotSaveNewCustomerWhenCustomerExists() {
		// given
		String phoneNumber = "777";

		Customer customer = CustomerUtils.customerOf();

		CustomerRegistrationRequest request = new CustomerRegistrationRequest(Mockito.mock(CustomerRegistrationRequestDto.class));

		given(mapper.map(any(), any())).willReturn(customer);

		given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer));

		given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

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
		String phoneNumber = "777";

		Customer customer = CustomerUtils.customerOf();
		Customer savedCustomer = CustomerUtils.customerOf();
		savedCustomer.setName("peter");

		CustomerRegistrationRequest request = new CustomerRegistrationRequest(Mockito.mock(CustomerRegistrationRequestDto.class));

		given(mapper.map(any(), any())).willReturn(customer);

		given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(savedCustomer));

		given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

		// when
		// then
		assertThatThrownBy(() -> underTest.registerNewCustomer(request))
				.hasMessageContaining(String.format("phone number [%s] is taken", phoneNumber))
				.isInstanceOf(IllegalStateException.class);

		then(customerRepository).should(never()).save(any(Customer.class));
	}

}
