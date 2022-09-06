package com.aws.samples.opensearch.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.aws.samples.opensearch.model.Customer;
import com.aws.samples.opensearch.repository.CustomerRepository;
import com.aws.samples.opensearch.service.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DisplayName(value="Elastic Search Spring Boot Test")
@SpringBootTest
class CustomerControllerTest {
	
	@Autowired
	CustomerService customerService;
	
	@MockBean
	CustomerRepository customerRepositoryMock;
	
	@Test
	@DisplayName(value ="Test findAllCustomers Method")
	public void findAllCustomers(){
		List<Customer> customerList = new ArrayList<>();
		customerList.add(new Customer("1","dinesh","chand",32));
		when(customerRepositoryMock.findAll()).thenReturn(customerList);
		
		assertEquals(1,customerService.findAll().spliterator().getExactSizeIfKnown());
	}
	
	@Test
	@DisplayName(value ="Test findByFirstNameTest Method")
	public void findByFirstNameTest(){
		List<Customer> customerList = new ArrayList<>();
		customerList.add(new Customer("1","dinesh","chand",32));
		when(customerRepositoryMock.findByFirstName("dinesh")).thenReturn(customerList);
		
		assertEquals(1, customerService.findByFirstName("dinesh").size());
	}
	
	@Test
	@DisplayName(value ="Test findByLastNameTest Method")
	public void findByLastNameTest(){
		List<Customer> customerList = new ArrayList<>();
		customerList.add(new Customer("1","dinesh","chand",32));
		when(customerRepositoryMock.findByLastName("chand")).thenReturn(customerList);
		
		assertEquals(1, customerService.findByLastName("chand").size());
	}
	
	@Test
	@DisplayName(value ="Test saveCustomerTest Method")
	public void saveCustomerTest(){
		Customer customer = new Customer("1","dinesh","chand",32);
		when(customerRepositoryMock.save(customer)).thenReturn(customer);
		
		Assertions.assertEquals(customer, customerService.save(customer));
	}
	
	@Test
	@DisplayName(value ="Test saveCustomersTest Method")
	public void saveCustomersTest(){
		List<Customer> customerList = new ArrayList<>();
		customerList.add(new Customer("1","dinesh","chand",32));
		customerList.add(new Customer("2","mugund","shrisha",3));
		when(customerRepositoryMock.saveAll(customerList)).thenReturn(customerList);
		
		assertEquals(2,customerService.saveAll(customerList).spliterator().getExactSizeIfKnown());
	}
	

}
