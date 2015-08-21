package com.skyseas.openfireplugins.chatlogs;

import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * function: 聊天记录Redis操作类
 */
public class JedisManager {
	private static final Logger log = LoggerFactory.getLogger(JedisManager.class);
	private static JedisManager instance =new JedisManager();
	private JedisPool pool;
	
	private JedisManager() {
		int maxActive = JiveGlobals.getIntProperty("redis.pool.maxActive",200);
		int maxIdle = JiveGlobals.getIntProperty("redis.pool.maxIdle",50);
		String host = JiveGlobals.getProperty("redis.host");
		if(host==null || host.length()==0){
			throw new RuntimeException("can not initialize Redis connection，no host was specified");
		}
		int port = JiveGlobals.getIntProperty("redis.port",6379);
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxActive);
		config.setMaxIdle(maxIdle);
		pool = new JedisPool(config, host, port);
	}
	
	public static synchronized JedisManager getInstance() {
		return instance;  
    }  

	public Jedis getJedis() {  
        Jedis jedis  = null;  
        try{   
            jedis = pool.getResource();  
            //log.info("get redis master1!");  
        } catch (Exception e) {  
            log.error("get redis master1 failed!", e);  
             // 销毁对象    
            pool.returnBrokenResource(jedis);    
        }  
        
        return jedis;  
    }  
	
	public void destroy() {  
        pool.destroy();
    } 
	
	public void closeJedis(Jedis jedis) {  
        if(jedis != null) {  
            pool.returnResource(jedis);  
        }  
    }  
}
