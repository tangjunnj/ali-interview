package com.tangjun.ali.interview.limit;

import java.util.concurrent.atomic.AtomicInteger;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2017年12月28日 下午9:10:48 
* 限流工具类
*/
public class LimitUtil {
	
	private LimitConfigration configration = null;
	
	private ResetRequestCountTask reset = null;
	
	private TpsMonitorTask monitor = null;
	
	//请求记录值
	private static AtomicInteger requestCount = new AtomicInteger(0);
	//通过请求值
	private static AtomicInteger txCount = new AtomicInteger(0);
	
	public LimitUtil(LimitConfigration configration) {
		if(configration == null){
			throw new NullPointerException();
		}
		this.configration = configration;
		if(configration.isLimit()){
			start();
		}
	}

	/**
	 * 启动
	 */
	public void start(){
		//启动归0和监视线程
		initResetTask();
		initMonitorTask();
	}
	/**
	 * 停止
	 */
	public void shutDown(){
		//停止归0和监视线程
		if(reset != null){
			reset.stopGracful();
		}
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
	 * 启动一个requstCount定期复0的任务
	 */
	private void initResetTask() {
		reset = new ResetRequestCountTask();
		Thread t = new Thread(reset);
		t.start();
	}
	
	/**
	 * 重置请求计数
	 */
	public static void resetRequestCount(){
		requestCount.set(0);
	}

	/**
	 * 重置事务计数器
	 * @return 重置前的值
	 */
	public static int resetTxCount(){
		return txCount.getAndSet(0);
	}
	
	/**
	 * 限流
	 * @return 是否可以发起一个业务
	 */
	public boolean limit(){
		if(!configration.isLimit()){
			return true;
		}
		//如果小于最大tps
		int currCount = requestCount.incrementAndGet();
		if(currCount <= configration.getMaxTps()){
			txCount.incrementAndGet();
			return true;
		}
		return false;
	}

	
	
}
