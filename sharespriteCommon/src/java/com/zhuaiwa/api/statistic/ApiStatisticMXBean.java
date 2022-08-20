package com.zhuaiwa.api.statistic;


public interface ApiStatisticMXBean {
	String getName();
	String[] getAllStatistic();
	LatencyTracker getStatistic(String methodName);
}
