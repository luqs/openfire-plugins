package com.skyseas.openfireplugins.webservice;

public interface OpenfireWebService {
	public BaseResult getAllGroup();  
    public BaseResult updateGroupStatus(String domain,String toId,String content);
    public BaseResult sendServerMessage(String domain,String toId,String content);  
    public BaseResult sendNoticeMessage(String domain, String fromId, String toId, String content);  
}
