package amigos_code_prj01.testing.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import amigos_code_prj01.customer.Customer;
import amigos_code_prj01.customer.CustomerRepository;
import amigos_code_prj01.customer.CustomerResponseDto;
import amigos_code_prj01.testing.utils.RestResponsePage;

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
    
    private static final String API_PREFIX = "/api/v1";
    private static final String ENDPOINT = API_PREFIX + "/customers";
    
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
    
    @Test
    public void itShouldReturnFivePageableCustomersInOnePageWhenAskingByAllCustomers()
            throws Exception {
        // given
        List<CustomerResponseDto> expectedResponse =
                IntStream.rangeClosed(1, 5).mapToObj(i -> {
                	final String name = "customer_" + i;
                	final String phoneNumberDigit = String.valueOf(i);
                	final String phone = phoneNumberDigit + phoneNumberDigit + phoneNumberDigit + phoneNumberDigit;

                    return customerResponseDtoOf(name, phone);
                }).collect(Collectors.toList());

        // method execution
        ResultActions resultActions = mvc.perform(get(ENDPOINT)
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                	.andExpect(jsonPath("$.numberOfElements", is(5)))
                	.andExpect(jsonPath("$.totalElements", is(5)))
                	.andExpect(jsonPath("$.last", is(true)))
                	.andExpect(status().isOk());

        // assertion
        isExptectedContentEqualsToPageResults(expectedResponse, resultActions);
    }

    @Test
    public void itShouldReturnThreePageableCustomersInTheFirstPageWhenAskingByAllCustomers()
            throws Exception {
        // given
        List<CustomerResponseDto> expectedResponse =
                IntStream.rangeClosed(1, 3).mapToObj(i -> {
                	final String name = "customer_" + i;
                	final String phoneNumberDigit = String.valueOf(i);
                	final String phone = phoneNumberDigit + phoneNumberDigit + phoneNumberDigit + phoneNumberDigit;

                    return customerResponseDtoOf(name, phone);
                }).collect(Collectors.toList());

        // method execution
        ResultActions resultActions = mvc.perform(get(ENDPOINT)
                .param("page", "0")
                .param("size", "3")
                .contentType(MediaType.APPLICATION_JSON))
                	.andExpect(jsonPath("$.numberOfElements", is(3)))
                	.andExpect(jsonPath("$.totalElements", is(5)))
                	.andExpect(jsonPath("$.last", is(false)))
                	.andExpect(status().isOk());

        // assertion
        isExptectedContentEqualsToPageResults(expectedResponse, resultActions);
    }

    @Test
    public void itShouldReturnTwoPageableCustomersInTheSecondPageWhenAskingByAllCustomers()
            throws Exception {
        // given
        List<CustomerResponseDto> expectedResponse =
                IntStream.rangeClosed(1, 2).mapToObj(i -> {
                	int number = i + 3;
                	final String name = "customer_" + number;
                	final String phoneNumberDigit = String.valueOf(number);
                	final String phone = phoneNumberDigit + phoneNumberDigit + phoneNumberDigit + phoneNumberDigit;

                    return customerResponseDtoOf(name, phone);
                }).collect(Collectors.toList());

        // method execution
        ResultActions resultActions = mvc.perform(get(ENDPOINT)
                .param("page", "1")
                .param("size", "3")
                .contentType(MediaType.APPLICATION_JSON))
                	.andExpect(jsonPath("$.numberOfElements", is(2)))
                	.andExpect(jsonPath("$.totalElements", is(5)))
                	.andExpect(jsonPath("$.last", is(true)))
                	.andExpect(status().isOk());

        // assertion
        isExptectedContentEqualsToPageResults(expectedResponse, resultActions);
    }

    @Test
    public void itShouldReturnEmptyPageInTheThirdPageWhenAskingByAllCustomers()
            throws Exception {
        // given
        List<CustomerResponseDto> expectedResponse = List.of();

        // method execution
        ResultActions resultActions = mvc.perform(get(ENDPOINT)
                .param("page", "2")
                .param("size", "3")
                .contentType(MediaType.APPLICATION_JSON))
                	.andExpect(jsonPath("$.numberOfElements", is(0)))
                	.andExpect(jsonPath("$.totalElements", is(5)))
                	.andExpect(jsonPath("$.last", is(true)))
                	.andExpect(status().isOk());

        // assertion
        isExptectedContentEqualsToPageResults(expectedResponse, resultActions);
    }
    
    private CustomerResponseDto customerResponseDtoOf(String name, String phone) {
    	CustomerResponseDto customerResponseDto =
                new CustomerResponseDto();
    	
    	customerResponseDto.setName(name);
    	customerResponseDto.setPhoneNumber(phone);

        return customerResponseDto;
    }
    
    private void isExptectedContentEqualsToPageResults(
            List<CustomerResponseDto> expectedContentPage,
            ResultActions resultActions) throws JsonMappingException,
            JsonProcessingException, UnsupportedEncodingException {

        List<CustomerResponseDto> returnedContentPage =
                getContentFromResultActions(resultActions);

        assertThat(expectedContentPage.size()).isEqualTo(returnedContentPage.size());

        for (int i = 0; i < returnedContentPage.size(); i++) {
            checkSimpleResult(expectedContentPage.get(i),
                    returnedContentPage.get(i));
        }
    }
    
    private List<CustomerResponseDto> getContentFromResultActions(
            ResultActions resultActions) throws JsonMappingException,
            JsonProcessingException, UnsupportedEncodingException {

        String responseAsString =
                resultActions.andReturn().getResponse().getContentAsString();

        Boolean isResponseEmpty = responseAsString.contains("[]");

        List<CustomerResponseDto> response = null;

        if (isResponseEmpty) {
            response = new ArrayList<>();
        } else {
            Page<CustomerResponseDto> pageFromResult =
                    objectMapper.readValue(responseAsString,
                            new TypeReference<RestResponsePage<CustomerResponseDto>>() {});

            response = new ArrayList<>(pageFromResult.getContent().stream()
                    .collect(Collectors.toList()));
        }

        return response;
    }
    
    private void checkSimpleResult(CustomerResponseDto expectedDto,
    		CustomerResponseDto resultDto) {
        assertThat(expectedDto.getName()).isEqualTo(resultDto.getName());
        assertThat(expectedDto.getPhoneNumber()).isEqualTo(resultDto.getPhoneNumber());
    }

}
