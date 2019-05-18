package com.ab.domain;

import java.io.Serializable;
import java.util.Date;

public class Member implements Serializable {

	private static final long serialVersionUID = -582782234465669621L;

	public enum MemberStatus {
		ACTIVE,
		DELETED
	}

	private int id;
	private String name;
	private String address;
	private MemberStatus status;
	private double amount;
	private Date timeCreated;
	private Date lastModified;
	
	public Member() {
		super();
	}

	public Member(int id, String name, String address, MemberStatus status, double amount, Date timeCreated,
			Date lastModified) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.status = status;
		this.amount = amount;
		this.timeCreated = timeCreated;
		this.lastModified = lastModified;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public MemberStatus getStatus() {
		return status;
	}

	public void setStatus(MemberStatus status) {
		this.status = status;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public String toString() {
		return "Member [id=" + id + ", name=" + name + ", address=" + address + ", status=" + status + ", amount="
				+ amount + ", timeCreated=" + timeCreated + ", lastModified=" + lastModified + "]";
	}
}
