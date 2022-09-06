package com.aws.samples.opensearch;

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
import com.github.javafaker.Faker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import lombok.extern.slf4j.Slf4j;
@SpringBootApplication
@Slf4j
public class SpringBootElasticSearchApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringBootElasticSearchApplication.class, args);
	}
	@Autowired
	private ElasticsearchOperations esOps;

	@Autowired
	private CustomerRepository customerRepo;

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				//enable CORS for all domains, remember to adapt this on production scenarios
				registry.addMapping("/**").allowedOrigins("*");
			}
		};
	}



	@PreDestroy
	public void deleteIndex() {
		esOps.indexOps(Customer.class).delete();
	}


	@PostConstruct
	public void buildIndex() {
		esOps.indexOps(Customer.class).refresh();
		customerRepo.deleteAll();
		customerRepo.saveAll(prepareDataset());
	}

	private Collection<Customer> prepareDataset() {
		//Generate a fake dataset using Faker library
		Faker faker = new Faker();
		SplittableRandom random=new SplittableRandom();
		String firstName;
		String lastName;
		int age;
		//generate 200 customer names using Faker
		List<Customer> customerList = new ArrayList<>();
		for (int i = 0; i < 200 ; i++) {
			 firstName = faker.name().firstName(); // Emory
			 lastName = faker.name().lastName(); // Barton
			 age = random.nextInt(10,30);
			customerList.add(Customer.builder().age(age).firstName(firstName).lastName(lastName).build());
		}

		return customerList;
	}

}