package com.ab.rest.member.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ab.rest.member.resource.MemberResource;
import com.ab.rest.member.service.MemberService;

@RestController
@RequestMapping(MemberController.MEMBER_BASE_URI)
public class MemberController {
	
	public static final String MEMBER_BASE_URI = "/api/member";
	
	private final static Logger LOGGER = LoggerFactory.getLogger(MemberController.class);

	@Autowired
	private MemberService memberService;
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<?> createMember(@RequestBody MemberResource resource) {
		LOGGER.info("Incoming request to create a member: {}", resource);
		return memberService.createMember(resource);
	}
	
	@RequestMapping(value = "/get-members", method = RequestMethod.GET)
	public ResponseEntity<?> getMembers() {
		LOGGER.info("Incoming request to get list of members.");
		return memberService.getMembers();
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ResponseEntity<?> updateMember(@RequestBody MemberResource resource) {
		LOGGER.info("Incoming request to update a member: {}", resource);
		return memberService.updateMember(resource);
	}
	
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
	public ResponseEntity<?> deleteMember(@PathVariable int id) {
		LOGGER.info("Incoming request to delete a member with id: {}", id);
		return memberService.deleteMember(id);
	}
}
