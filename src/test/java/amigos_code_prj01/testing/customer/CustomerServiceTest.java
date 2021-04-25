package amigos_code_prj01.testing.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import amigos_code_prj01.customer.Customer;
import amigos_code_prj01.customer.CustomerRegistrationRequest;
import amigos_code_prj01.customer.CustomerRegistrationRequestDto;
import amigos_code_prj01.customer.CustomerRepository;
import amigos_code_prj01.customer.CustomerService;
import amigos_code_prj01.customer.ICustomer;
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
	
	@Test
	void itShouldFindAllCustomers() {
		// given
		List<Customer> customersFromDb = this.getCustomers();
		
		List<ICustomer> icustomers = this.getCustomers()
				.stream()
				.map(c -> {ICustomer ic = c; return ic;})
				.collect(Collectors.toList());

		PageImpl<ICustomer> expectedCustomersPage = new PageImpl<ICustomer>(icustomers);
		
		Pageable pageable = PageRequest.of(0, 10);

		given(customerRepository.findAllPageableCustomers(pageable))
			.willReturn(new PageImpl<Customer>(customersFromDb));
		
		// when
		Page<ICustomer> customersPageResponse = underTest.findAllCustomers(pageable);
		
		//then
		assertEquals(expectedCustomersPage, customersPageResponse);

		int idx = 0;
		for (ICustomer c : expectedCustomersPage.getContent()) {
			ICustomer expectedCustomer = customersPageResponse.getContent().get(idx);
			assertThat(expectedCustomer.getId()).isEqualTo(c.getId());
			assertThat(expectedCustomer.getName()).isEqualTo(c.getName());
			assertThat(expectedCustomer.getPhoneNumber()).isEqualTo(c.getPhoneNumber());
			assertThat(expectedCustomer.getPassword()).isEqualTo(c.getPassword());
			idx++;
		}
	}
	
	private List<Customer> getCustomers() {
        return LongStream.rangeClosed(1, 5).mapToObj(i -> {
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
	}
}
