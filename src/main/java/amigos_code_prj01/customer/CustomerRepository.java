package amigos_code_prj01.customer;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
	
	@Query(
			value = "select id, name, phone_number "
					+ "from customer where phone_number = :phone_number",
			nativeQuery = true
			)
	Optional<Customer> selectCustomerByPhoneNumber(
			@Param("phone_number") String phoneNumber);
	
	@Query("SELECT c FROM Customer c")
	Page<Customer> findAllPageableCustomers(Pageable pageable);

}
