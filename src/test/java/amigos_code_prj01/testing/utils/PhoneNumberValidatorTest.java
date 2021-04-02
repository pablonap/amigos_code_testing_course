package amigos_code_prj01.testing.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import amigos_code_prj01.utils.PhoneNumberValidator;

class PhoneNumberValidatorTest {
	
	private PhoneNumberValidator underTest;
	
	@BeforeEach
	void setUp() {
		underTest = new PhoneNumberValidator();
	}
	
	@Test
	void itShouldValidatePhoneNumber() {
		// given
		String phoneNumber = "+541154841444";
		
		// when
		boolean isValid = underTest.test(phoneNumber);
		
		// then
		assertThat(isValid).isTrue();

	}

}
