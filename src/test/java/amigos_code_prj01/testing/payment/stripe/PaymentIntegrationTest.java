package amigos_code_prj01.testing.payment.stripe;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.Objects;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import amigos_code_prj01.customer.Customer;
import amigos_code_prj01.customer.CustomerRegistrationRequest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentIntegrationTest {
	
    @Autowired
    private MockMvc mockMvc;
	@Test
	void itShouldCreatePaymentSuccessfully() throws Exception {
		// given
		UUID customerId = UUID.randomUUID();
		Customer customer = new Customer(customerId, "james", "123");
		
		CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

		ResultActions customerRegResultActions = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/customer-registration")
				.contentType(MediaType.APPLICATION_JSON)
				.content(Objects.requireNonNull(objectToJson(customerRegistrationRequest))));
		
		// when
		
		// then
		customerRegResultActions.andExpect(status().isOk());

	}
	private String objectToJson(Object object) {
		try {
			return new ObjectMapper().writeValueAsString(object);
		} catch(JsonProcessingException e) {
			fail("Failed to convert object to json");
			return null;
		}
	}
}
