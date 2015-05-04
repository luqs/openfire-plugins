package com.skyseas.openfireplugins.webservice;

public class BaseResult {
	protected String errorMessage ;
	protected int errorCode;
	protected String content;
	public BaseResult(){
		
	}
	public BaseResult(int errorCode,String errorMessage){
		this.errorCode=errorCode;
		this.errorMessage=errorMessage;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
