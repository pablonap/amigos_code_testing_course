package amigos_code_prj01.utils;

import java.util.function.Predicate;

public class PhoneNumberValidator implements Predicate<String> {

	public boolean test(String phoneNumber) {
		return phoneNumber.startsWith("+5411") && phoneNumber.length() == 13;
	}

}
