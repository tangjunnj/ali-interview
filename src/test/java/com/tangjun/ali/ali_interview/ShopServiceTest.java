package com.tangjun.ali.ali_interview;

import com.tangjun.ali.interview.limit.LimitUtil;
import com.tangjun.ali.interview.service.ShopService;
import junit.framework.TestCase;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2017年12月28日 下午11:32:51 
* 类说明 
*/
public class ShopServiceTest extends TestCase{
	private ShopService shopService;
	
	public void setUp() throws Exception {
		shopService = new ShopService();
	}

	
	public void tearDown() throws Exception {
		//等待清道夫逻辑结束
		Thread.sleep(2000);
		LimitUtil.shutDown();
	}
	
	
	public void testBuySomething(){
		try {
			for(int i =0;i<1000;i++){
				BuySomethingTestThread t = new BuySomethingTestThread(shopService, "buyer"+i);
				t.start();
				//模拟请求间隔
				Thread.sleep((long) (Math.random()*200));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static class BuySomethingTestThread extends Thread{
		private  ShopService shopService ;
		
		private String buyer;

		public BuySomethingTestThread(ShopService shopService, String buyer) {
			this.shopService = shopService;
			this.buyer = buyer;
		}

		@Override
		public void run() {
			boolean success = shopService.buySomething(buyer);
			System.out.println("TEST:"+buyer+"消费"+(success?"成功":"失败"));
		}
		
	}

}
