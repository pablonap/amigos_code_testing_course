package amigos_code_prj01.testing.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import amigos_code_prj01.customer.Customer;
import amigos_code_prj01.customer.CustomerRepository;

@SpringBootTest
@TestPropertySource({"classpath:application-test.properties"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class CustomerControllerTest {
	
	@Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;
    
    @BeforeAll
    void init() {
        insertCustomers();
    }

    private void insertCustomers() {
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
        }).forEach(c -> customerRepository.save(c));
    }
    
    @Test
    public void itShouldReturnCustomersSavedInDb() {
        // given/when
        IntStream.rangeClosed(1, 5).mapToObj(i -> {
        	Long id = (long) i;
            return id;
        }).forEach((id) -> {
        	final String name = "customer_" + id;
        	final String phoneNumberDigit = String.valueOf(id);
        	final String phone = phoneNumberDigit + phoneNumberDigit + phoneNumberDigit + phoneNumberDigit;
        	final String password = "password123";

            Customer customer = customerRepository.findById(id).get();

            // then
            assertThat(customer.getId()).isEqualTo(id);
            assertThat(customer.getName()).isEqualTo(name);
            assertThat(customer.getPhoneNumber()).isEqualTo(phone);
            assertThat(customer.getPassword()).isEqualTo(password);
        });
    }
}
