package amigos_code_prj01.testing.customer;

import amigos_code_prj01.customer.Customer;

final class CustomerUtils {

	static Customer customerOf() {
		Customer customer = new Customer();
		customer.setId(null);
		customer.setName("luca");
		customer.setPassword("luca123");
		customer.setPhoneNumber("777");

		return customer;
	}
}
