package amigos_code_prj01.testing.payment.stripe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
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
import amigos_code_prj01.payment.Currency;
import amigos_code_prj01.payment.Payment;
import amigos_code_prj01.payment.PaymentRepository;
import amigos_code_prj01.payment.PaymentRequest;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentIntegrationTest {
	
	// Here I'm breaking the rule about having injected only the 
	// class that I want to test (MockMvc in this case) because 
	// I don't have an endpoint to give me the list of payments 
	// so I'm simply using the repository.
	@Autowired
	private PaymentRepository paymentRepository;
	
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

        long paymentId = 1L;

        Payment payment = new Payment(
                paymentId,
                customerId,
                new BigDecimal("100.00"),
                Currency.GBP,
                "x0x0x0x0",
                "Zakat"
        );

        PaymentRequest paymentRequest = new PaymentRequest(payment);
		
		// when
        ResultActions paymentResultActions = mockMvc.perform(post("/api/v1/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(paymentRequest))));

		// then
		customerRegResultActions.andExpect(status().isOk());
        paymentResultActions.andExpect(status().isOk());
        
        assertThat(paymentRepository.findById(paymentId))
        .isPresent()
        .hasValueSatisfying(p -> assertThat(p).isEqualTo(payment));


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
