package com.ab.rest.transaction.controller;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.ab.domain.Transaction;
import com.ab.rest.AbstractTest;

public class TransactionControllerTest extends AbstractTest {

	@Override
	@Before
	public void setUp() {
		super.setUp();
	}
	
	@Test
	public void createTransactionStatus200() throws Exception {
		String getTransactionsUrl = new StringBuilder()
				.append(TransactionController.TRANSACTION_BASE_URI)
				.append("/create")
				.toString();

		List<Integer> payees = new ArrayList<>();
		Transaction transaction = new Transaction();
		transaction.setPayer(1);
		transaction.setPayees(payees);
		transaction.setAmount(100);
		transaction.setDescription("Test");
		transaction.setIncludePayer(false);
		String inputJson = super.mapToJson(transaction);
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(getTransactionsUrl).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
	}
	
	@Test
	public void createTransactionStatus422() throws Exception {
		String getTransactionsUrl = new StringBuilder()
				.append(TransactionController.TRANSACTION_BASE_URI)
				.append("/create")
				.toString();

		List<Integer> payees = new ArrayList<>();
		Transaction transaction = new Transaction();
		transaction.setPayer(1);
		transaction.setPayees(payees);
		transaction.setAmount(0);
		transaction.setDescription("Test");
		transaction.setIncludePayer(false);
		String inputJson = super.mapToJson(transaction);
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(getTransactionsUrl).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(422, status);
	}
	
	@Test
	public void getTransactions() throws Exception {
		String getTransactionsUrl = new StringBuilder()
				.append(TransactionController.TRANSACTION_BASE_URI)
				.append("/get-transactions")
				.toString();
		
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(getTransactionsUrl).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		
		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
	}

}
