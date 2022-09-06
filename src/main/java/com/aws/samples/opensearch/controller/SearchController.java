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

import java.util.List;

import com.aws.samples.opensearch.model.Customer;
import com.aws.samples.opensearch.service.CustomerSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author Angel Conde
 *
 */
@RestController
@RequestMapping("/")
@Slf4j
public class SearchController {
	
	private final CustomerSearchService searchService;

	@Autowired
	public SearchController(CustomerSearchService searchService) {
	    this.searchService = searchService;
	}
	
	@GetMapping("/customers")
	@ResponseBody
	public List<Customer> fetchByNameOrDesc(@RequestParam(value = "q", required = false) String query) {
        log.info("searching by name {}",query);
		List<Customer> customers = searchService.processSearch(query) ;
	    log.info("customers {}",customers);
		return customers;
	  }
	
	@GetMapping("/suggestions")
	@ResponseBody
	public List<String> fetchSuggestions(@RequestParam(value = "q", required = false) String query) {                         
        log.info("fetch suggests {}",query);
        List<String> suggests = searchService.fetchSuggestions(query);
        log.info("suggests {}",suggests);
        return suggests;
	  }

}