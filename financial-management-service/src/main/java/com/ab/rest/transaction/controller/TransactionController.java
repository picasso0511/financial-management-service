package com.ab.rest.transaction.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ab.rest.transaction.resource.TransactionResource;
import com.ab.rest.transaction.service.TransactionService;

@RestController
@RequestMapping(TransactionController.TRANSACTION_BASE_URI)
public class TransactionController {
	
	public static final String TRANSACTION_BASE_URI = "/api/transaction";
	
	private final static Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);

	@Autowired
	private TransactionService transactionService;

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<?> createTransaction(@RequestBody TransactionResource resource) {
		LOGGER.info("Incoming request to create a transaction: {}", resource);
		return transactionService.createTransaction(resource);
	}
	
	@RequestMapping(value = "/get-transactions", method = RequestMethod.GET)
	public ResponseEntity<?> getTransactions() {
		LOGGER.info("Incoming request to get list of transactions.");
		return transactionService.getTransactions();
	}
}
