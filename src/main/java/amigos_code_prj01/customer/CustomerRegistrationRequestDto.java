package amigos_code_prj01.customer;

public class CustomerRegistrationRequestDto {
	
	private String name;

	private String phoneNumber;
	
	private String password;
	
	public CustomerRegistrationRequestDto() {
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}
