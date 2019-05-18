package com.ab.rest.transaction.service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ab.domain.Member;
import com.ab.domain.Member.MemberStatus;
import com.ab.domain.Transaction;
import com.ab.rest.transaction.resource.TransactionResource;
import com.ab.util.Common;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Service
public class TransactionService {
	private final static Logger LOGGER = LoggerFactory.getLogger(TransactionService.class);
	
	@Autowired
	private Common common;
	
	@Value("${transactions.file.path}")
	private String transactionsFilePath;
	
	@Value("${members.file.path}")
	private String membersFilePath;
	
	/**
	 * Create new transaction
	 * 
	 * @param resource
	 * @return Returns ResponseEntity with Transaction object or appropriate message in case some error and HTTP status
	 */
	public ResponseEntity<?> createTransaction(TransactionResource resource) {
		LOGGER.debug("Create transaction - started.");
		
		List<Transaction> transactions = getTransactionsFromFile();

		boolean validateResource = validateResource(resource);
		boolean validateMembers = validateMembers(resource);

		if (validateResource) {
			if(validateMembers) {
				// create new transaction
				Transaction transaction = new Transaction();
				transaction.setId(transactions.size() + 1);
				transaction.setPayer(resource.getPayer());
				transaction.setPayees(resource.getPayees());
				transaction.setAmount(resource.getAmount());
				transaction.setDescription(resource.getDescription());
				transaction.setIncludePayer(resource.isIncludePayer());
				transaction.setTimeCreated(new Date());
				
				// add new transaction to existing list of transactions
				transactions.add(transaction);
				
				//update amounts of members
				updateAmountsOfMembers(resource);
				
				Gson gson = new Gson();

				// convert java object to JSON format,
				// and returned as JSON formatted string
				String json = gson.toJson(transactions);

				common.saveJson(json, transactionsFilePath);
				
				LOGGER.debug("Create transaction - finished.");
				return new ResponseEntity<>(transaction, HttpStatus.OK);
			} else {
				LOGGER.warn("Payer/payee(s) aren't found in list of ACTIVE members or payer is set in payees list.");
				return new ResponseEntity<>("Payer/payee(s) aren't found in list of ACTIVE members or payer is set in payees list.", HttpStatus.UNPROCESSABLE_ENTITY);
			}
		} else {
			LOGGER.warn("Fields payer or amount doesn't have correct value.");
			return new ResponseEntity<>("Fields payer or amount doesn't have correct value.", HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}
	
	/**
	 * Get all transactions
	 * 
	 * @return Returns ResponseEntity with list of transactions and HTTP status
	 */
	public ResponseEntity<?> getTransactions() {
		LOGGER.debug("Getting list of transactions from JSON file - started.");
		
		List<Transaction> transactions = getTransactionsFromFile();
		
		LOGGER.debug("Getting list of transactions from JSON file successfully finished {}.", transactions);
		return new ResponseEntity<>(transactions, HttpStatus.OK);
	}
	
	
	/**
	 * Update members account amount after transaction creating a transaction
	 * 
	 * @param resource
	 */
	private void updateAmountsOfMembers(TransactionResource resource) {
		LOGGER.debug("Update members account amount - started.");
		
		List<Member> members = getMembersFromFile();
		List<Member> activeMembers = getActiveMembersFromFile(); 
		
		List<Integer> payees = resource.getPayees();

		
		//if payees list is empty money is shared to all active member, otherwise it share it's shared to member from payees list
		//also it checks if money will be shared to payer or only on members from payee list
		if (payees.size() == 0) {
			if(resource.isIncludePayer()) {
				for (Member m : members) {
					if(resource.getPayer() == m.getId()) {
						double newAmount = m.getAmount() - ((resource.getAmount() / activeMembers.size())) * (activeMembers.size() - 1);
						m.setAmount(newAmount);
						m.setLastModified(new Date());
					} else if(m.getStatus().equals(MemberStatus.ACTIVE)) {
						double newAmount = m.getAmount() + (resource.getAmount() / activeMembers.size());
						m.setAmount(newAmount);
						m.setLastModified(new Date());
					}
				}
			} else {
				for (Member m : members) {
					if(resource.getPayer() == m.getId()) {
						double newAmount = m.getAmount() - ((resource.getAmount() / (activeMembers.size() - 1))) * (activeMembers.size() - 1);
						m.setAmount(newAmount);
						m.setLastModified(new Date());
					} else if(m.getStatus().equals(MemberStatus.ACTIVE)) {
						double newAmount = m.getAmount() + (resource.getAmount() / (activeMembers.size() - 1));
						m.setAmount(newAmount);
						m.setLastModified(new Date());
					}
				}
			}
		} else {
			if(resource.isIncludePayer()) {
				for (Member m : members) {
					if(payees.contains(m.getId())) {
						double newAmount = m.getAmount() + (resource.getAmount() / (payees.size() + 1));
						m.setAmount(newAmount);
						m.setLastModified(new Date());
					} else if (resource.getPayer() == m.getId()) {
						double newAmount = m.getAmount() - (resource.getAmount() / (payees.size() + 1));
						m.setAmount(newAmount);
						m.setLastModified(new Date());
					} 
				}
			} else {
				for (Member m : members) {
					if(payees.contains(m.getId())) {
						double newAmount = m.getAmount() + (resource.getAmount() / payees.size());
						m.setAmount(newAmount);
						m.setLastModified(new Date());
					} else if (resource.getPayer() == m.getId()) {
						double newAmount = m.getAmount() - (resource.getAmount() / payees.size());
						m.setAmount(newAmount);
						m.setLastModified(new Date());
					} 
				}
			}
		}
	
		
		Gson gson = new Gson();

		// convert java object to JSON format,
		// and returned as JSON formatted string
		String json = gson.toJson(members);
		
		//save JSON to file
		common.saveJson(json, membersFilePath);
		LOGGER.debug("Update members account amount - finished.");
	}
	
	/**
	 * Get all transactions from JSON file
	 * 
	 * @return Returns all transactions from JSON file
	 */
	public List<Transaction> getTransactionsFromFile() {
		List<Transaction> listOfTransactions = new ArrayList<>();
		File f = new File(transactionsFilePath);
		if(f.exists()) {
			try {
				LOGGER.debug("Reading JSON from a file");
				LOGGER.debug("----------------------------");

				String jsonString = new String(Files.readAllBytes(Paths.get(transactionsFilePath)), StandardCharsets.UTF_8);
				
				if(!StringUtils.isEmpty(jsonString)) {
					Type listType = new TypeToken<ArrayList<Transaction>>(){}.getType();
					listOfTransactions = new Gson().fromJson(jsonString, listType);
					LOGGER.debug("Reading JSON from a file finished");
				} 
				
			} catch (IOException e) {
				LOGGER.error("Failed to read JSON file. Message: " + e.getMessage(), e);
				e.printStackTrace();
			}
		} else {
			try {
				f.createNewFile();
			} catch (IOException e) {
				LOGGER.error("Failed to create new JSON file. Message: " + e.getMessage(), e);
				e.printStackTrace();
			}
		}
		return listOfTransactions;
	}
	
	/**
	 * Get all active members from JSON file
	 * 
	 * @return Returns list of all active members from JSON file
	 */
	private List<Member> getActiveMembersFromFile() {
		List<Member> listOfMembers = new ArrayList<>();
		List<Member> listOfActiveMembers = new ArrayList<>();
		
		File f = new File(membersFilePath);
		if(f.exists()) {
			try {
				LOGGER.debug("Reading JSON from a file");
				LOGGER.debug("----------------------------");

				String jsonString = new String(Files.readAllBytes(Paths.get(membersFilePath)), StandardCharsets.UTF_8);
				
				if(!StringUtils.isEmpty(jsonString)) {
					Type listType = new TypeToken<ArrayList<Member>>(){}.getType();
					listOfMembers = new Gson().fromJson(jsonString, listType);
					LOGGER.debug("Reading JSON from a file finished");
				} 
				
			} catch (IOException e) {
				LOGGER.error("Failed to read JSON file. Message: " + e.getMessage(), e);
				e.printStackTrace();
			}
			
			for(Member m : listOfMembers) {
				if(m.getStatus().equals(MemberStatus.ACTIVE)) {
					listOfActiveMembers.add(m);
				}
			}
		}
		return listOfActiveMembers;
	}
	
	/**
	 * Get all members from JSON file
	 * 
	 * @return Returns list of members from JSON file
	 */
	private List<Member> getMembersFromFile() {
		List<Member> listOfMembers = new ArrayList<>();
		
		File f = new File(membersFilePath);
		if(f.exists()) {
			try {
				LOGGER.debug("Reading JSON from a file");
				LOGGER.debug("----------------------------");

				String jsonString = new String(Files.readAllBytes(Paths.get(membersFilePath)), StandardCharsets.UTF_8);
				
				if(!StringUtils.isEmpty(jsonString)) {
					Type listType = new TypeToken<ArrayList<Member>>(){}.getType();
					listOfMembers = new Gson().fromJson(jsonString, listType);
					LOGGER.info("Reading JSON from a file finished");
				} 
				
			} catch (IOException e) {
				LOGGER.error("Failed to read JSON file. Message: " + e.getMessage(), e);
				e.printStackTrace();
			}
		}
		return listOfMembers;
	}

	/**
	 * Validate payer and amount value.
	 * 
	 * @param resource
	 * @return Returns boolean value after checking payer and amount
	 */
	private boolean validateResource(TransactionResource resource) {
		if (resource.getPayer() == 0 || resource.getAmount() == 0.0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Validate member IDs that user sent in request for creating transaction
	 * 
	 * @param resource
	 * @return Returns boolean value after checking payer and payees
	 */
	private boolean validateMembers(TransactionResource resource) {		
		List<Member> members = getActiveMembersFromFile();				//get list of active members for checking
		List<Integer> memberIds = new ArrayList<>(members.size());		//get list of IDs of all active members
		List<Integer> resourceMembers = resource.getPayees();			//get list of IDs of payees from resource
		
		//make list of all member IDs
		members.forEach(m -> {
			memberIds.add(m.getId());
		});
		
		//check if payer exist in list of payees
		if(resourceMembers.contains(resource.getPayer())) {
			return false;
		}
		
		//check does every member from request body exist
		if (memberIds.size() < resourceMembers.size()) {
			return false;
		} else {
			boolean exist = false;
			if(resourceMembers.size() == 0 ) {
				return true;
			} else {
				for (int i = 0; i < resourceMembers.size(); i++){
					if (memberIds.contains(resourceMembers.get(i))) {
						exist = true;
					} else {
						return false;
					}
				}
			}
			
			return exist;
		}
	}
}
