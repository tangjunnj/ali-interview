package com.tangjun.ali.interview.limit;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2017年12月28日 下午10:49:23 
* 类说明 
*/
public class ResetRequestCountTask implements Runnable {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private boolean stop = false;
	
	public void run() {
		try {
			for(;;){
				if(stop){
					log.info("system--stop resetLimitCount task");
					break;
				}
//				LimitUtil.resetRequestCount();
				Thread.currentThread().sleep(1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void stopGracful(){
		stop = true;
	}
	
}
