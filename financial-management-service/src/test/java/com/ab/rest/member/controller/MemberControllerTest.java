package com.ab.rest.member.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.ab.domain.Member;
import com.ab.domain.Member.MemberStatus;
import com.ab.rest.AbstractTest;

public class MemberControllerTest extends AbstractTest {

	@Override
	@Before
	public void setUp() {
		super.setUp();
	}
	
	@Test
	public void createMemberStatus200() throws Exception {
		String getMembersUrl = new StringBuilder()
				.append(MemberController.MEMBER_BASE_URI)
				.append("/create")
				.toString();

		Member member = new Member();
		member.setName("F");
		member.setAddress("Address10");
		String inputJson = super.mapToJson(member);
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(getMembersUrl).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
	}
	
	@Test
	public void createMemberStatus422() throws Exception {
		String getMembersUrl = new StringBuilder()
				.append(MemberController.MEMBER_BASE_URI)
				.append("/create")
				.toString();

		Member member = new Member();
		member.setName("");
		member.setAddress("");
		String inputJson = super.mapToJson(member);
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(getMembersUrl).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(422, status);
	}
	
	@Test
	public void getMembers() throws Exception {
		String getMembersUrl = new StringBuilder()
				.append(MemberController.MEMBER_BASE_URI)
				.append("/get-members")
				.toString();
		
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(getMembersUrl).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		
		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
	}
	
	@Test
	public void updateMemberStatus200() throws Exception {
		String getMembersUrl = new StringBuilder()
				.append(MemberController.MEMBER_BASE_URI)
				.append("/update")
				.toString();

		Member member = new Member();
		member.setId(8);
		member.setAddress("Address5522255");
		String inputJson = super.mapToJson(member);
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(getMembersUrl).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
	}
	
	@Test
	public void updateMemberStatus404() throws Exception {
		String getMembersUrl = new StringBuilder()
				.append(MemberController.MEMBER_BASE_URI)
				.append("/update")
				.toString();

		Member member = new Member();
		member.setId(55);
		member.setAddress("Address100");
		String inputJson = super.mapToJson(member);
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(getMembersUrl).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(404, status);
	}
	
	@Test
	public void deleteMemberStatus200() throws Exception {
		String getMembersUrl = new StringBuilder()
				.append(MemberController.MEMBER_BASE_URI)
				.append("/delete/7")
				.toString();

		Member member = new Member();
		member.setStatus(MemberStatus.DELETED);
		String inputJson = super.mapToJson(member);
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(getMembersUrl).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
	}
	
	@Test
	public void deleteMemberStatus404() throws Exception {
		String getMembersUrl = new StringBuilder()
				.append(MemberController.MEMBER_BASE_URI)
				.append("/delete/55")
				.toString();

		Member member = new Member();
		member.setStatus(MemberStatus.DELETED);
		String inputJson = super.mapToJson(member);
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(getMembersUrl).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(404, status);
	}
}
