package com.tangjun.ali.interview.dao;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** 
* @author 作者 E-mail: 
* @version 创建时间：2017年12月28日 下午9:47:23 
* 类说明 
*/
public class ShopDao {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 初始库存数量，模拟数据的值
	 */
	private static int initProductNum = 13;
	
	
	/**
	 * 扣库存产品数量
	 */
	public void reduceProductNum(int reduce){
		if(reduce > 0){
			log.info("库存数量减少了{} ",reduce);
		}
	}
	
	/**
	 * 插入交易记录
	 * @param buyers 购买人集合 
	 */
	public void createDealLog(List<String> buyers){
		
		if(buyers != null){
			buyers.forEach(str->{
//				log.info("插入购买记录:【{}】购买了一个商品",str);
			});
		}
	}

	/**
	 * 查询产品总数
	 * @return 
	 */
	public int queryTotalProduct() {
		return initProductNum;
	}
}
