package com.grant.smith;

import javafx.util.Pair;


public class SavingsNode implements Comparable<SavingsNode> {

	// Requirements of the customer (number to be delivered)
	public final double saving;
	public final Customer ci, cj;
	
	public SavingsNode(Customer ci, Customer cj, double saving){
		this.ci = ci;
		this.cj = cj;
		this.saving = saving;
	}

	public int compareTo(SavingsNode sj) {
		return saving < sj.saving ? 1 : saving == sj.saving ? 0 : -1;
	}
	
	public String toString(){
		return ci + ", " + cj + ", " + saving;
	}
}
