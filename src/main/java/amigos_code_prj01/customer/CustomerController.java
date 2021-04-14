package amigos_code_prj01.customer;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CustomerController {
	
	private final CustomerService customerService;
	
	@Autowired
	private ModelMapper mapper;
	
	@Autowired
	public CustomerController(CustomerService customerRegistrationService) {
		this.customerService = customerRegistrationService;
	}

	@PutMapping("/customer-registration")
	public void registerNewCustomer(
			@Valid @RequestBody CustomerRegistrationRequest request) {
		customerService.registerNewCustomer(request);
	}
	
	@GetMapping("/customers")
	public ResponseEntity<Page<CustomerResponseDto>> findAllCustomers(
			@RequestParam(name="page", defaultValue="0", required=false) int page,
			@RequestParam(name="size", defaultValue="30", required=false) int size) {
		return new ResponseEntity<>(
				customerService.findAllCustomers(
						PageRequest.of(page, size))
				.map(response -> mapper.map(response, CustomerResponseDto.class)), 
				HttpStatus.OK);
	}
}
