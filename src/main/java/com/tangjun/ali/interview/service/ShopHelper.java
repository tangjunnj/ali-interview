package com.tangjun.ali.interview.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2017年12月29日 上午1:09:35 
* 类说明 
*/
public class ShopHelper {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());

	//产品总数，初始化时确定
	private int totalProduct;
	
	//已消费的数量
	private volatile int consumed;
	
	//请求缓冲池的指针
	private int reqCacheIndex = 0;
	private CacheReq[] reqCache;

	
	//处理消费计数的锁
	private ReentrantLock consumeLock = new  ReentrantLock();
	
	//处理请求缓冲区的锁
	private ReentrantLock cacheLock = new  ReentrantLock();
	
	public ShopHelper(int totalProduct,int cacheSize) {
		this.totalProduct = totalProduct;
		reqCache = new CacheReq[cacheSize];
	}

	/**
	 * 是否能够购买，验证consumed <= totalProduct
	 * 如果可以发起购买，会在消费计数上加1
	 * @return 是否可以买
	 */
	public boolean canBuy() {
		//已消费的是否超过了总数
		if(consumed < totalProduct){
			consumeLock.lock();
			//二次验证
			if(consumed < totalProduct){
				consumed ++;
				log.debug("准备消费一个产品，consumed:{}",consumed);
				consumeLock.unlock();
				return true;
			}
			consumeLock.unlock();
		}
		return false;
	}

	/**
	 * 缓冲请求，当缓冲区满了之后，将多个请求合并为一个请求返回
	 * 如果缓冲区没有满，则返回NULL
	 * @param req
	 * @return 返回一组请求，返回null表示没有满足阈值
	 */
	public List<CacheReq> cacheReq(CacheReq req) {
		log.debug("cacheReq start");
		List<CacheReq> result = new ArrayList<CacheReq>();
		cacheLock.lock();
		//数组是否已经满了
		if(reqCacheIndex < reqCache.length){
			//还有空位就填空
			reqCache[reqCacheIndex] = req;
			reqCacheIndex ++;
		}
		//已经满了就合并请求
		if(reqCacheIndex == reqCache.length){
			for(int i =0;i<reqCacheIndex;i++){
				result.add(reqCache[i]);
			}
			reqCacheIndex = 0;
		}
		cacheLock.unlock();
		return result;
	}
	
	/**
	 * 原子set and get cache
	 * @return
	 */
	public List<CacheReq> setAndGetCache() {
		List<CacheReq> result = new ArrayList<CacheReq>();
		cacheLock.lock();
		for(int i =0;i<reqCacheIndex;i++){
			result.add(reqCache[i]);
		}
		//置空缓冲区
		reqCacheIndex = 0;
		cacheLock.unlock();
		return result;
	}
	/**
	 * 传输缓冲区保存的请求的实体
	 * @author Administrator
	 *
	 */
	public static class CacheReq{
		private String buyer;
		
		private CountDownLatch latch;

		
		public CacheReq(String buyer, CountDownLatch latch) {
			super();
			this.buyer = buyer;
			this.latch = latch;
		}

		public String getBuyer() {
			return buyer;
		}

		public void setBuyer(String buyer) {
			this.buyer = buyer;
		}

		public CountDownLatch getLatch() {
			return latch;
		}

		public void setLatch(CountDownLatch latch) {
			this.latch = latch;
		}
	}

	
}
