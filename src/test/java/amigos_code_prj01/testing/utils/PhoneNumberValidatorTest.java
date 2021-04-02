package amigos_code_prj01.testing.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

	@Test
	@DisplayName("Should fail when length is bigger than 13")
	void itShouldValidatePhoneNumberWhenIncorrectAndHasLengthBiggerThan13() {
		// given
		String phoneNumber = "+5411548414441234";
		
		// when
		boolean isValid = underTest.test(phoneNumber);
		
		// then
		assertThat(isValid).isFalse();

	}

	@Test
	@DisplayName("Should fail when does not start with +")
	void itShouldValidatePhoneNumberWhenDoesNotStartWithPlusSign() {
		// given
		String phoneNumber = "5411548414441234";
		
		// when
		boolean isValid = underTest.test(phoneNumber);
		
		// then
		assertThat(isValid).isFalse();

	}

}
