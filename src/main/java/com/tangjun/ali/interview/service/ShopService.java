package com.tangjun.ali.interview.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tangjun.ali.interview.dao.ShopDao;
import com.tangjun.ali.interview.limit.LimitConfigration;
import com.tangjun.ali.interview.limit.LimitUtil;


/**
 * @author Administrator
 * 服务接口
 */
public class ShopService {
	//日志
	private Logger log = LoggerFactory.getLogger(this.getClass());
	//限流器
	private LimitUtil limitUtil;
	//业务辅助类
	private ShopHelper helper;
	//数据操作
	private ShopDao dao;
	
	
	public ShopService() {
		this.limitUtil = new LimitUtil(new LimitConfigration());
		this.dao = new ShopDao();
		this.helper = new ShopHelper(this.dao.queryTotalProduct(),5);
	}

	/**
	 * 暴露的服务
	 * @param req
	 * @return
	 */
	public boolean buySomething(String req){
		//通过限流限制
		if(limitUtil.limit("shopService.buySomething")){
			if(helper.canBuy()){
				log.debug("{}通过限流",req);
				//任务是否添加成功
				return doBuy(req);
			}
			else{
				log.warn("{}，商品已售罄，请下次再来",req);
			}
		}
		else{
			log.warn("{}，系统繁忙，请稍后再试",req);
		}
		return false;
	}

	/**
	 * 合并购买
	 * @param buyer
	 * @return
	 */
	private boolean doBuy(String buyer) {
		log.debug("{}开始正式消费",buyer);
		boolean result = false;
		
		CountDownLatch latch = new CountDownLatch(1);
		ShopHelper.CacheReq cacheReq = new ShopHelper.CacheReq(buyer,latch);
		//获取合并后的请求
		List<ShopHelper.CacheReq> realReq = helper.cacheReq(cacheReq);
		//如果不为空，那么当前线程作为提交者
		if(realReq != null && !realReq.isEmpty()){
			result = doBuyCommit(realReq);
		}
		else{
			//如果返回空，那么等待其他线程提交
			try {
				log.debug("开始阻塞等待countdown");
				latch.await(1000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				log.error("线程{}被阻断",Thread.currentThread().getId());
			}
			log.debug("当前线程已经醒来，当前countdown值是：{}，buyer是:{}",latch.getCount(),buyer);
			if(latch.getCount() == 0){
				//countdown==0说明本线程的请求已经在其他线程提交了，只需验证就可以返回结果了
				//check something(查询数据库交易记录是否存在，存在则返回购买成功 )
				result = true;
			}
			else{
				//否则就是等待时间到了，需要自己变成一个清道夫提交缓冲区里被剩余的请求
				log.debug("开始清道夫逻辑");
				List<ShopHelper.CacheReq> cacheReqs = helper.setAndGetCache();
				result = doBuyCommit(cacheReqs);
			}
		}
		return result;
	}

	private boolean doBuyCommit(List<ShopHelper.CacheReq> realReq) {
		if(realReq != null && !realReq.isEmpty()){
			dao.reduceProductNum(realReq.size());
			List<String> buyers = new ArrayList<String>();
			realReq.forEach(r->{
				buyers.add(r.getBuyer());
			});
			dao.createDealLog(buyers);
			//提交成功后countDown所有的latch
			realReq.forEach(r->{
				r.getLatch().countDown();
			});
			return true;
		}
		return false;
	}
}
