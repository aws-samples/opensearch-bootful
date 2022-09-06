package com.aws.samples.opensearch.controller;

/*
 *        Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *       Permission is hereby granted, free of charge, to any person obtaining a copy of this
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
import com.aws.samples.opensearch.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author Angel Conde
 *
 */
@RestController
@Slf4j
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @RequestMapping(method = RequestMethod.GET, value ="/findAll")
    private Iterable<Customer> findAllCustomers() {
        log.info("inside CRUD controller findAllCustomers " + new Date());
        return customerService.findAll();
    }

    @RequestMapping(method = RequestMethod.GET, value ="/findByFirstName/{firstName}")
    private Iterable<Customer> findByFirstName(@PathVariable String firstName) {
        log.info("inside CRUD controller findByFirstName " + new Date() + " firstName is " + firstName);
        return customerService.findByFirstName(firstName);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/findByLastName/{lastName}")
    private Iterable<Customer> findByLastName(@PathVariable String lastName) {
        log.info("inside CRUD controller findByLastName " + new Date() + " lastName is " + lastName);
        return customerService.findByLastName(lastName);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/saveCustomer")
    private String saveCustomer(@RequestBody Customer customer) {
        log.info("inside CRUD controller saveCustomer " + new Date() + " customer is " + customer.getFirstName());
        customerService.save(customer);
        return customer.getId();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/saveCustomers")
    private int saveCustomers(@RequestBody List<Customer> customers) {
        log.info("inside CRUD controller saveCustomers " + new Date() + " size is " + customers.size());
        customerService.saveAll(customers);
        return customers.size();
    }

}
