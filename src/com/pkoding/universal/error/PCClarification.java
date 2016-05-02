package com.pkoding.universal.error;

import java.util.ArrayList;

public class PCClarification {
	
	private ArrayList<PCClarificationDetail> details;
	
	public PCClarification() {
		details = new ArrayList<PCClarificationDetail>();
	}
	
	public ArrayList<PCClarificationDetail> details() {
		return details;
	}
	
	public void addDetail(PCClarificationDetail detail) {
		details.add(detail);
	}

}
