package com.tangjun.ali.interview.limit;

import com.google.common.util.concurrent.RateLimiter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2017年12月28日 下午9:10:48 
* 限流工具类
*/
public class LimitUtil {
	
	private LimitConfigration configration = null;

	private static Map<String,RateLimiter> limiterMap;

	private static ReentrantLock lock = new ReentrantLock();

	private static TpsMonitorTask monitor = null;

	//通过请求值
	private static Map<String,AtomicLong> txCountMap;

	public LimitUtil(LimitConfigration configration) {
		if(configration == null){
			throw new NullPointerException();
		}
		this.configration = configration;
		limiterMap = new ConcurrentHashMap<>();
		txCountMap = new ConcurrentHashMap<>();
		initMonitorTask();

	}
	/**
	 * 停止
	 */
	public static void shutDown(){
		//停止归0和监视线程
		if(monitor != null){
			monitor.stopGracful();
		}
	}


	/**
	 * 启动一个监视TPS的任务
	 */
	private void initMonitorTask() {
		monitor = new TpsMonitorTask();
		Thread t = new Thread(monitor);
		t.start();
	}


	/**
	 * 限流
	 * @param key 限流对象的标识符
	 * @return 是否可以发起一个业务
	 */
	public boolean limit(String key){


		if(!configration.isLimit()){
			return true;
		}

		RateLimiter limiter = getRateLimiter(key);
		//如果能获取到一个令牌
		if(limiter.tryAcquire()){
			return true;
		}
		return false;
	}

	private RateLimiter getRateLimiter(String key) {
		//添加限流器
		if (!limiterMap.containsKey(key)) {
			lock.lock();
			if(!limiterMap.containsKey(key)){
				limiterMap.put(key,RateLimiter.create(configration.getMaxTps()));
			}
			lock.unlock();
		}
		return limiterMap.get(key);
	}

	public static AtomicLong getTxCount(String key) {
		//初始化事物计数器
		if(!txCountMap.containsKey(key)){
			lock.lock();
			if(!txCountMap.containsKey(key)){
				txCountMap.put(key,new AtomicLong(0));
			}
			lock.unlock();
		}
		return txCountMap.get(key);
	}

	public static Map<String, AtomicLong> getTxCountMap() {
		return txCountMap;
	}

	public static void setTxCountMap(Map<String, AtomicLong> txCountMap) {
		LimitUtil.txCountMap = txCountMap;
	}
}
