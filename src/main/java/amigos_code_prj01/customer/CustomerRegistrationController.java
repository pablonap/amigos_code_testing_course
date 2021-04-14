package amigos_code_prj01.customer;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CustomerRegistrationController {
	
	private final CustomerRegistrationService customerRegistrationService;
	
	@Autowired
	public CustomerRegistrationController(CustomerRegistrationService customerRegistrationService) {
		this.customerRegistrationService = customerRegistrationService;
	}

	@PutMapping("/customer-registration")
	public void registerNewCustomer(
			@Valid @RequestBody CustomerRegistrationRequest request) {
		customerRegistrationService.registerNewCustomer(request);
	}
	
}
