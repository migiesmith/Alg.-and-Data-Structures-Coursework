package com.grant.smith;


public class SavingsNode implements Comparable<SavingsNode> {

	// Saving to be made
	public final double saving;
	// Customers that make the saving
	public final Customer ci, cj;
	
	public SavingsNode(Customer ci, Customer cj, double saving){
		this.ci = ci;
		this.cj = cj;
		this.saving = saving;
	}

	// Comparator for sorting
	public int compareTo(SavingsNode sj) {
		return saving < sj.saving ? 1 : saving == sj.saving ? 0 : -1;
	}
	
}
