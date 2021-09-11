package de.cypix.tasks_check_bot.spring;


import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Integer>{

    Customer findCustomerById(Integer id);


}
