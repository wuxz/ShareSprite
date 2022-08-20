package com.zhuaiwa.api.statistic;

public interface LatencyStatsMXBean {
    public String getName();
    public long getCount();
    public long getLatency();
    public long[] getHistogram();
}
