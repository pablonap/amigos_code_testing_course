package amigos_code_prj01.testing.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import amigos_code_prj01.utils.PhoneNumberValidator;

class PhoneNumberValidatorTest {
	
	private PhoneNumberValidator underTest;
	
	@BeforeEach
	void setUp() {
		underTest = new PhoneNumberValidator();
	}
	
	@ParameterizedTest
	@CsvSource({
		"+541154841444, true",
		"+5411548414441234, false",
		"5411548414441234, false"
		
	})
	void itShouldValidatePhoneNumber(String phoneNumber, boolean expected) {
		// when
		boolean isValid = underTest.test(phoneNumber);
		
		// then
		assertThat(isValid).isEqualTo(expected);
	}
}
