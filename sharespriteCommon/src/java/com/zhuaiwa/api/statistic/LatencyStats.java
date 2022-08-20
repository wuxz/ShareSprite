package com.zhuaiwa.api.statistic;

import java.util.concurrent.atomic.AtomicLong;

public class LatencyStats implements LatencyStatsMXBean {
    private String name;
    private final AtomicLong count = new AtomicLong(0);
    private final AtomicLong latency = new AtomicLong(0);
    private final Histogram histogram = new Histogram();
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public long[] getHistogram() {
        return histogram.get(false);
    }
    public long getCount() {
        return count.get();
    }
    public long getLatency() {
        return latency.get();
    }
    
    public void addNano(long nanos) {
        addMicro(nanos / 1000);
    }
    public void addMicro(long micros) {
        count.incrementAndGet();
        latency.addAndGet(micros);
        histogram.add(micros);
    }
}
