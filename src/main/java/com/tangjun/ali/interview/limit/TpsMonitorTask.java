package com.tangjun.ali.interview.limit;


import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2017年12月28日 下午10:49:23 
* 类说明 
*/
public class TpsMonitorTask implements Runnable {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private boolean stop = false;

	//监视频率
	private int duration = 1;
	private long begin = 0;
	public void run() {
		try {
			for(;;){
				if(stop){
					log.info("system--TpsMonitorTask stoped");
					break;
				}
				if(begin == 0){
					begin = System.currentTimeMillis();
				}
				Map<String, AtomicLong> txCountMap = LimitUtil.getTxCountMap();
				if (txCountMap != null) {
					//只是监控，不用加锁
					Set<String> keySet = txCountMap.keySet();
					keySet.forEach(key->{
						AtomicLong atomicLong = txCountMap.get(key);
						long sec = (System.currentTimeMillis()-begin)/1000;
						log.info("当前【{}】接口的tps为：{}/s", key,String.format("%.2f",(double)atomicLong.get()/sec));
					});
				}
				Thread.sleep(duration*1000);
			}
		} catch (InterruptedException e) {
			log.error("TpsMonitorTask has been interrupted");
		}
	}
	
	public void stopGracful(){
		stop = true;
	}
	
}
