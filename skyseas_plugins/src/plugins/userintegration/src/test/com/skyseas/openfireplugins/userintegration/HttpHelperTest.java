package com.skyseas.openfireplugins.userintegration;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.TestCase;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;



public class HttpHelperTest extends TestCase {
	
	@Override
	protected void setUp() throws IOException, InterruptedException {
		
	}
	
	@Override
	protected void tearDown() {
	}
	
	
	public void testRequest() throws IOException {
		  ObjectMapper 	objectMapper 	= new ObjectMapper();
		UserInfo user = new UserInfo("xiaomi", "password", "zhangsan@qq.com", "ÕÅÈý");
		System.out.println(objectMapper.writeValueAsString(user));
		
	}
	

}
