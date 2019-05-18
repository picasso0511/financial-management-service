package com.ab.rest.transaction.resource;

import java.util.List;

public class TransactionResource {

	private int payer;
	private List<Integer>  payees;
	private double amount;
	private String description;
	private boolean includePayer;
	
	public TransactionResource() {
		super();
	}

	public int getPayer() {
		return payer;
	}

	public void setPayer(int payer) {
		this.payer = payer;
	}

	public List<Integer>  getPayees() {
		return payees;
	}

	public void setPayees(List<Integer>  payees) {
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

	@Override
	public String toString() {
		return "TransactionResource [payer=" + payer + ", payees=" + payees + ", amount=" + amount + ", description="
				+ description + ", includePayer=" + includePayer + "]";
	}
}
