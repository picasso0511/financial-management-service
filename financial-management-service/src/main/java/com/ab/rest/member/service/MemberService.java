package com.ab.rest.member.service;

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
import com.ab.rest.member.resource.MemberResource;
import com.ab.util.Common;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Service
public class MemberService {
	private final static Logger LOGGER = LoggerFactory.getLogger(MemberService.class);
	
	@Autowired
	private Common common;
	
	@Value("${members.file.path}")
	private String membersFilePath;
	
	/**
	 * Create new member
	 * 
	 * @param resource
	 * @return Returns ResponseEntity with new Member object or appropriate message in case some error and HTTP status
	 */
	public ResponseEntity<?> createMember(MemberResource resource) {
		LOGGER.debug("Create member - started.");
		
		List<Member> members = getMembersFromFile();

		boolean validation = validate(resource);
		
		if(validation) {
			//create new member
			Member member = new Member();
			member.setId(members.size() + 1);
			member.setName(resource.getName());
			member.setAddress(resource.getAddress());
			member.setStatus(MemberStatus.ACTIVE);
			member.setAmount(0);
			member.setTimeCreated(new Date());
			member.setLastModified(new Date());

			// add new member to existing list of members
			members.add(member);
			Gson gson = new Gson();

			// convert java object to JSON format,
			// and returned as JSON formatted string
			String json = gson.toJson(members);

			common.saveJson(json, membersFilePath);
			
			LOGGER.debug("New member successfully created {}.", member);
			return new ResponseEntity<>(member, HttpStatus.OK);
		} else {
			LOGGER.warn("Fields name and address can't be empty.");
			return new ResponseEntity<>("Fields name and address can't be empty.", HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}
	
	/**
	 * Get all members
	 * 
	 * @return Returns ResponseEntity with list of members and HTTP status
	 */
	public ResponseEntity<?> getMembers() {
		LOGGER.debug("Getting list of members from JSON file - started.");
		
		List<Member> members = getMembersFromFile();
		
		LOGGER.debug("Getting list of members from JSON file successfully finished {}.", members);
		return new ResponseEntity<>(members, HttpStatus.OK);
	}
	
	/**
	 * Update member
	 * 
	 * @param resource
	 * @return Returns Returns ResponseEntity with updated Member object or appropriate message in case some error and HTTP status
	 */
	public ResponseEntity<?> updateMember(MemberResource resource) {
		LOGGER.debug("Update member - started.");
		
		List<Member> members = getMembersFromFile();

		if(members.isEmpty()) {
			LOGGER.warn("There are no saved members.");
			return new ResponseEntity<>("There are no saved members.", HttpStatus.NOT_FOUND);
		} else {
			boolean validation = isNullOrEmpty(resource.getAddress());
			
			if(!validation) {
				// get member by id
				Member member = new Member();
				boolean memberExists = false;
				for(Member m : members) {
					if(m.getId() == resource.getId() && m.getStatus().equals(MemberStatus.ACTIVE)) {
						member = m;
						memberExists = true;
					}
				}
				
				if(memberExists) {
					//set values for update member
					member.setAddress(resource.getAddress());
					member.setLastModified(new Date());

					Gson gson = new Gson();

					// convert java object to JSON format,
					// and returned as JSON formatted string
					String json = gson.toJson(members);

					common.saveJson(json, membersFilePath);
					
					LOGGER.debug("Member successfully updated {}.", member);
					return new ResponseEntity<>(member, HttpStatus.OK);
				} else {
					LOGGER.warn("Member with ID " + resource.getId() + " doesn't exist or is not ACTIVE.");
					return new ResponseEntity<>("Member with ID " + resource.getId() + " doesn't exist or is not ACTIVE.", HttpStatus.NOT_FOUND);
				}
				
			} else {
				LOGGER.warn("Field address can't be empty.");
				return new ResponseEntity<>("Field address can't be empty.", HttpStatus.UNPROCESSABLE_ENTITY);
			}
		}
	}
	
	/**
	 * Delete member
	 * 
	 * @param id
	 * @return Returns ResponseEntity with deleted Member object or appropriate message in case some error and HTTP status
	 */
	public ResponseEntity<?> deleteMember(int id) {
		LOGGER.debug("Delete member - started.");

		List<Member> members = getMembersFromFile();

		if (members.isEmpty()) {
			LOGGER.warn("There are no saved members.");
			return new ResponseEntity<>("There are no saved members.", HttpStatus.NOT_FOUND);
		} else {
			// get member by id
			Member member = new Member();
			boolean memberExists = false;
			for (Member m : members) {
				if (m.getId() == id && m.getStatus().equals(MemberStatus.ACTIVE)) {
					member = m;
					memberExists = true;
				}
			}

			if (memberExists) {
				// set status DELETED 
				member.setStatus(MemberStatus.DELETED);
				member.setLastModified(new Date());

				Gson gson = new Gson();

				// convert java object to JSON format,
				// and returned as JSON formatted string
				String json = gson.toJson(members);

				common.saveJson(json, membersFilePath);

				LOGGER.debug("The member was successfully deleted.");
				return new ResponseEntity<>(member, HttpStatus.OK);
			} else {
				LOGGER.warn("Member with ID " + id + " doesn't exist or has already been DELETED.");
				return new ResponseEntity<>("Member with ID " + id + " doesn't exist or has already been DELETED.", HttpStatus.NOT_FOUND);
			}
		}
	}

	/**
	 * Get members from JSON file
	 * 
	 * @return Returns list of all members from JSON file
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
		return listOfMembers;
	}
	
	/**
	 * Validate resource values from request body
	 * 
	 * @param resource
	 * @return Returns boolean value after checking name and address
	 */
	private boolean validate(MemberResource resource) {
		if(isNullOrEmpty(resource.getName()) || isNullOrEmpty(resource.getAddress())) {
			return false;
		} else {
			return true;
		}
	}
	
	private static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }
}
