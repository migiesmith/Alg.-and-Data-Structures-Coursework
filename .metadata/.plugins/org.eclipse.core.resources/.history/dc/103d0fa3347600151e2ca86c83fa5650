package com.grant.smith;

import java.awt.geom.Point2D;



public class SavingsNode implements Comparable<SavingsNode> {

	// Requirements of the customer (number to be delivered)
	public double saving;
	public Customer ci, cj;
	
	public SavingsNode(Customer ci, Customer cj, double saving){
		this.ci = ci;
		this.cj = cj;
		this.saving = saving;
	}

	public int compareTo(SavingsNode sj) {
		return saving < sj.saving ? 1 : saving == sj.saving ? 0 : -1;
	}
}
