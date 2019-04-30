package dev.viniciusvks.sherlock.search;

public class Error {
	
	private String domain;
	private String reason;
	private String message;
	
	public String getDomain() {
		return domain;
	}
	public String getReason() {
		return reason;
	}
	public String getMessage() {
		return message;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public void setMessage(String message) {
		this.message = message;
	}

}
