package com.ab.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Transaction implements Serializable {

	private static final long serialVersionUID = -8017179247741730119L;
	
	private int id;
	private int payer;
	private List<Integer> payees;
	private double amount;
	private String description;
	private boolean includePayer;
	private Date timeCreated;
	
	public Transaction() {
		super();
	}

	public Transaction(int id, int payer, List<Integer> payees, double amount, String description, boolean includePayer,
			Date timeCreated) {
		super();
		this.id = id;
		this.payer = payer;
		this.payees = payees;
		this.amount = amount;
		this.description = description;
		this.includePayer = includePayer;
		this.timeCreated = timeCreated;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPayer() {
		return payer;
	}

	public void setPayer(int payer) {
		this.payer = payer;
	}

	public List<Integer> getPayees() {
		return payees;
	}

	public void setPayees(List<Integer> payees) {
		this.payees = payees;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isIncludePayer() {
		return includePayer;
	}

	public void setIncludePayer(boolean includePayer) {
		this.includePayer = includePayer;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "Transaction [id=" + id + ", payer=" + payer + ", payees=" + payees + ", amount=" + amount
				+ ", description=" + description + ", includePayer=" + includePayer + ", timeCreated=" + timeCreated
				+ "]";
	}
}
