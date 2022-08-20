package com.zhuaiwa.api.statistic;
/*
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 */


import java.util.Arrays;

public class LatencyTracker implements LatencyTrackerMXBean
{
    private long lastCount = 0;
    private long lastLatency = 0;
    private long[] lastHistogram = null;
    
    private final LatencyStats stats;
    
    public LatencyTracker(LatencyStats stats) {
        this.stats = stats;
    }
    
    public LatencyStats getLatencyStats() {
        return this.stats;
    }
	
	public String sample() {
        long count = stats.getCount();
        long latency = stats.getLatency();
        long[] histogram = stats.getHistogram();
        
        long d_count = count - lastCount;
        long d_latency = latency - lastLatency;
        long[] d_histogram = lastHistogram;
        
        lastCount = count;
        lastLatency = latency;
        lastHistogram = histogram;

        long[] bucketOffsets = Histogram.getBucketOffsets();
        int numbuckets = bucketOffsets.length+1;
        if (d_histogram == null)
            d_histogram = new long[numbuckets];
        for (int i = 0; i < numbuckets; i++) {
            d_histogram[i] = histogram[i] - d_histogram[i];
        }
        
        double average_latency = (d_count == 0 ? 0 : (((double)d_latency) / d_count));
        
        int i1 = Arrays.binarySearch(bucketOffsets,  100000)+1;
        int i2 = Arrays.binarySearch(bucketOffsets,  300000)+1;
        int i3 = Arrays.binarySearch(bucketOffsets, 1000000)+1;
        int i4 = Arrays.binarySearch(bucketOffsets, 3000000)+1;
        int c1 = 0;
        int c2 = 0;
        int c3 = 0;
        int c4 = 0;
        int cx = 0;
        
        int i = 0;
        for (; i <= i1; i++) {
            c1 += d_histogram[i];
        }
        for (; i <= i2; i++) {
            c2 += d_histogram[i];
        }
        for (; i <= i3; i++) {
            c3 += d_histogram[i];
        }
        for (; i <= i4; i++) {
            c4 += d_histogram[i];
        }
        for (; i < numbuckets; i++) {
            cx += d_histogram[i];
        }
        
        StringBuilder message = new StringBuilder();
        message.append(String.format("[%21s]", stats.getName()));
        message.append(String.format("%6.1f %12.1f/%-8d, ", (average_latency/1000), (d_latency/1000.0), d_count));
        message.append(String.format("%8d 100ms %6d 300ms %4d 1s %2d 3s %1d", c1, c2, c3, c4, cx));
        return message.toString();
	}
	
	@Override
	public String toString() {
		return sample();
	}

    @Override
    public long getCount() {
        return stats.getCount();
    }

    @Override
    public long[] getHistogram() {
        return stats.getHistogram();
    }

    @Override
    public long getLatency() {
        return stats.getLatency();
    }

    @Override
    public String getName() {
        return stats.getName();
    }
}
