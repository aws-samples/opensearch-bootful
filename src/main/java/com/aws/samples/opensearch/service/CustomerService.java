package com.aws.samples.opensearch.service;

/*
 *        Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *        Permission is hereby granted, free of charge, to any person obtaining a copy of this
 *        software and associated documentation files (the "Software"), to deal in the Software
 *        without restriction, including without limitation the rights to use, copy, modify,
 *        merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 *        permit persons to whom the Software is furnished to do so.
 *
 *        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *        INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 *        PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *        HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *        OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *        SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import com.aws.samples.opensearch.model.Customer;
import com.aws.samples.opensearch.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This service uses spring data approach (repository based interaction) to query OpenSearch
 *
 * @author Angel Conde
 */
@Service
public class CustomerService {

	private final CustomerRepository customerRepository;

	@Autowired
	public CustomerService(CustomerRepository repository){
		customerRepository=repository;
	}

	public Iterable<Customer> findAll() {
		return customerRepository.findAll();
	}

	public List<Customer> findByFirstName(String firstName) {
		return customerRepository.findByFirstName(firstName);
	}

	public List<Customer> findByLastName(String lastName) {
		return customerRepository.findByLastName(lastName);
	}

	public Customer save(Customer customer) {
		return customerRepository.save(customer);
	}

	public Iterable<Customer> saveAll(List<Customer> customers) {
		return customerRepository.saveAll(customers);
	}

	public List<Customer> fetchProductNamesContaining(final String name){
		return customerRepository.findByFirstNameContaining(name);
	}

}
