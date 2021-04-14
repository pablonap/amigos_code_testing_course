package amigos_code_prj01.customer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerRegistrationRequest {
	
	private final CustomerRegistrationRequestDto dto;

	public CustomerRegistrationRequest(@JsonProperty("customer") CustomerRegistrationRequestDto dto) {
		this.dto = dto;
	}

	public CustomerRegistrationRequestDto getCustomer() {
		return dto;
	}

	@Override
	public String toString() {
		return "CustomerRegistrationRequest [customer=" + dto + "]";
	}

}
