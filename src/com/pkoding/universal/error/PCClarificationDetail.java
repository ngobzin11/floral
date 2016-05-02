package com.pkoding.universal.error;

public class PCClarificationDetail {
	
	public final String code;
	
	public final String explanation;
	
	public PCClarificationDetail(String code, String explanation) {
		this.code = code;
		this.explanation = explanation;
	}
	
	public String toString() {
		return code + " - " + explanation;
	}

}
