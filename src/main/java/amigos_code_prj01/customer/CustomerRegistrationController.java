package amigos_code_prj01.customer;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/customer-registration")
public class CustomerRegistrationController {
	
	@PutMapping
	public void registerNewCustomer(
			@Valid @RequestBody CustomerRegistrationRequest request) {
		System.out.println(request);
	}

}
