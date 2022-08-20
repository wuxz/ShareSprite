package com.zhuaiwa.api.statistic;

public interface LatencyTrackerMXBean extends LatencyStatsMXBean {
    public String sample();
}
