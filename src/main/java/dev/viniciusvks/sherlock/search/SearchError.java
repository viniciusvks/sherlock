package dev.viniciusvks.sherlock.search;

public class SearchError {
	
	private Error[] errors;
	private Integer code;
	private String message;
	
	public Error[] getErrors() {
		return errors;
	}
	public Integer getCode() {
		return code;
	}
	public String getMessage() {
		return message;
	}
	public void setErrors(Error[] errors) {
		this.errors = errors;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
