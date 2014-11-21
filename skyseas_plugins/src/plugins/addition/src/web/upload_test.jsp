<%@ page import="org.jivesoftware.openfire.*" %>
<%@ page import="com.skyseas.openfireplugins.addition.AdditionPlugin" %>

<%
    AdditionPlugin plugin = (AdditionPlugin) XMPPServer.getInstance().getPluginManager().getPlugin(AdditionPlugin.PLUGIN_NAME);
%>


<html>
<head>
    <title>Http Push Testing</title>
    <meta name="pageID" content="upload_resource"/>
</head>
<body>
<h1>
    <%= plugin.getServletUrl()%>
</h1>
</body>
</html>
