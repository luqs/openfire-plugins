package com.skyseas.openfireplugins.chatlogs;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

import com.google.gson.Gson;

import redis.clients.jedis.Jedis;

public class Test {

	public static void main(String[] args) throws Exception {
		
//		InputStream in =new BufferedInputStream(new FileInputStream("E:/SMP_1.0/workspace/openfire-plugins/skyseas_plugins/src/plugins/chatlogs/src/redis.properties"));
//		Properties props = new Properties();
//		props.load(in);
//		JedisManager jedisUtil = JedisManager.getInstance(props);
//		Jedis t = jedisUtil.getJedis();
//		String s;
//		while( (s = t.lpop("chatlogs"))!=null){
//			System.out.println(s);
//		}
//		System.out.println("结束");
//		jedisUtil.closeJedis(t);
		
		
		ChatLogs c = new ChatLogs(1L, "luqs@localhost/Spark 2.6.3", new Timestamp(new Date().getTime()), "hi", null, 2, 0);
		c.setDetail("<message id=\"LxKvG-66\" to=\"admin@localhost\" from=\"luqs@localhost/Spark 2.6.3\" type=\"chat\"><body>hi</body><thread>q8o4C0</thread><x xmlns=\"jabber:x:event\"><offline/><composing/></x></message>");
		String json = new Gson().toJson(c);
		System.out.println(json);
		
		System.out.println(new Gson().fromJson(json, ChatLogs.class));
		
		//		messageId:1	sessionJID:luqs@localhost/Spark 2.6.3	sender:luqs	receiver:admin	createDate:2014-12-16 11:12:27.415	content:hi
//		detail:<message id="LxKvG-66" to="admin@localhost" from="luqs@localhost/Spark 2.6.3" type="chat"><body>hi</body><thread>q8o4C0</thread><x xmlns="jabber:x:event"><offline/><composing/></x></message>	length:2	state:0

		
	}

}
