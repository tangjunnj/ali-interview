package com.tangjun.ali.interview.limit;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	public void run() {
		try {
			for(;;){
				if(stop){
					log.info("system--TpsMonitorTask stoped");
					break;
				}
				int txCount = LimitUtil.resetTxCount();
				log.info("当前tps：{}/s", String.format("%.2f",(double)txCount/duration));
				Thread.currentThread().sleep(duration*1000);
			}
		} catch (InterruptedException e) {
			log.error("TpsMonitorTask has been interrupted");
		}
	}
	
	public void stopGracful(){
		stop = true;
	}
	
}
