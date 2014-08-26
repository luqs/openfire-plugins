<%@ page import="org.jivesoftware.openfire.*" %>
<%@ page import="org.jivesoftware.util.*" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.EnumSet" %>
<%@ page import="com.skyseas.openfireplugins.userintegration.*" %>
<%@ page import="com.skyseas.openfireplugins.userintegration.UserEventSubscriber.UserEventType" %>


<%
	final String enabledProperty 			= "enabled";
	final String methodProperty 			= "method";
	final String targeturlProperty 			= "targeturl";
	final String sendcontentbodyProperty 	= "sendcontentbody";
	
   boolean isUpdate = request.getMethod().equalsIgnoreCase("post");
   if(isUpdate) {
   
   		// 设置订阅器类型
   		JiveGlobals.setProperty(UserIntegrationPlugin.REGISTER_SUBSCRIBER_CLASS_KEY, 
   			request.getParameter(UserIntegrationPlugin.REGISTER_SUBSCRIBER_CLASS_KEY));
   		
   		for(UserEventType type : UserEventType.values()) {
   			String key = HttpUserEventSubscriber.getEventTypeConfigKey(type, enabledProperty);
   			JiveGlobals.setProperty(key, 	"true".equalsIgnoreCase(request.getParameter(key)) ? "true" : "false" );
   			
   			key = HttpUserEventSubscriber.getEventTypeConfigKey(type, methodProperty);
   			JiveGlobals.setProperty(key, 	request.getParameter(key));
   			
   			key = HttpUserEventSubscriber.getEventTypeConfigKey(type, targeturlProperty);
   			JiveGlobals.setProperty(key, 	request.getParameter(key));
   			
   			key = HttpUserEventSubscriber.getEventTypeConfigKey(type, sendcontentbodyProperty);
   			JiveGlobals.setProperty(key, 	"true".equalsIgnoreCase(request.getParameter(key)) ? "true" : "false" );
   		}
   } 
%>

<html>
    <head>
        <title>User Integration Setting</title>
        <meta name="pageID" content="userintegration"/>
    </head>
    <body>
  <form action="userintegration_setting.jsp" method="post">
    <h2>base setting :</h2>
		<ul>
		<li>
			<label>Subscriber class name:
				<input type="text" style="width:400px" name="<%= UserIntegrationPlugin.REGISTER_SUBSCRIBER_CLASS_KEY %>" 
					value="<%=JiveGlobals.getProperty( UserIntegrationPlugin.REGISTER_SUBSCRIBER_CLASS_KEY, 
						HttpUserEventSubscriber.class.getName())%>" > </label>
		</li>
		</ul>


	<h2>HttpSubscriber setting:</h2>
	
	<table style="text-align:center">
		<thead>
			<tr>
				<td> enabled </td>
				<td> method </td>
				<td> target URL </td>
				<td> send content body </td>
			</tr>
		</thead>
		
		<% for(UserEventType type : UserEventType.values()) {
			String enabledKey 		= HttpUserEventSubscriber.getEventTypeConfigKey(type, enabledProperty);
			String methodKey 		= HttpUserEventSubscriber.getEventTypeConfigKey(type, methodProperty);
			String targetUrlKey 	= HttpUserEventSubscriber.getEventTypeConfigKey(type, targeturlProperty);
			String sendBody 		= HttpUserEventSubscriber.getEventTypeConfigKey(type, sendcontentbodyProperty);
		
		 %>
		<tr>
			<td> 
				<label>
					<input type="checkbox" value="true"  name="<%= enabledKey  %>" 
							<%= JiveGlobals.getProperty(enabledKey, "false").equals("true") ? "checked=\"checked\"" : ""  %> /> 
							<%= type.toString() %>
				</label>
			</td>
			<td> <input type="text" value="<%= JiveGlobals.getProperty(methodKey) %>" 
						name="<%= methodKey %>" style="width:50px" /> </td>
			<td> <input type="text" value="<%= JiveGlobals.getProperty(targetUrlKey) %>" 
						name="<%= targetUrlKey %>" style="width:300px" /> </td>
						
			<td>
				<input type="checkbox" value="true"  name="<%= sendBody  %>" 
							<%= JiveGlobals.getProperty(sendBody, "false").equals("true") ? "checked=\"checked\"" : ""  %> />  
			</td>
		</tr>
		<% } %>
	</table>
	
	<input type="submit" value="save" name="save" />
</form>  
</body>
</html>