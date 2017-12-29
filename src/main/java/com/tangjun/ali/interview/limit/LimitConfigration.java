package com.tangjun.ali.interview.limit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LimitConfigration {
	private static final String CONFIG = "/limit.properties";
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	//监控频率
	private int monitorDur = 1;
	//是否打开限流
	private boolean limit = true;
	//最大允许吞吐量
	private int maxTps=10;

	
	
	public LimitConfigration() {
		init();
	}
	

	private void init() {
		try {

			Properties prop = new Properties();
			prop.load(this.getClass().getResourceAsStream(CONFIG));
			String monitorDurStr = prop.getProperty("monitorDur");
			String maxTpsStr = prop.getProperty("maxTps");
			String limitStr = prop.getProperty("limit");
			monitorDur = monitorDurStr!= null?Integer.parseInt(monitorDurStr):monitorDur;
			limit = limitStr!= null?Boolean.parseBoolean(limitStr):limit;
			maxTps = maxTpsStr!= null?Integer.parseInt(maxTpsStr):maxTps;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public int getMaxTps() {
		return maxTps;
	}

	public void setMaxTps(int maxTps) {
		this.maxTps = maxTps;
	}

	public boolean isLimit() {
		return limit;
	}

	public void setLimit(boolean limit) {
		this.limit = limit;
	}

	public int getMonitorDur() {
		return monitorDur;
	}

	public void setMonitorDur(int monitorDur) {
		this.monitorDur = monitorDur;
	}
	
	
}
