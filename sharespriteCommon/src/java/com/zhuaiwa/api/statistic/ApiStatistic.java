package com.zhuaiwa.api.statistic;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiStatistic extends ArrayList<LatencyTracker> implements ApiStatisticMXBean {
	private static final long serialVersionUID = -7119090691051425477L;
	private static Logger logger = LoggerFactory.getLogger(ApiStatistic.class);
	
	private String name;
	private LatencyTracker tracker;
	
	public ApiStatistic(String name) {
		this.name = name;
		this.tracker = new LatencyTracker(new LatencyStats());
		this.tracker.getLatencyStats().setName(name);
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        String mbeanName = "com.zhuaiwa.api.server:type=" + name;
        try
        {
            logger.info("register mbean: " + mbeanName);
            mbs.registerMBean(this.tracker, new ObjectName(mbeanName));
        }
        catch (Exception e)
        {
            logger.info("Failed to register mbean " + mbeanName, e);
        }
	}

	@Override
	public String[] getAllStatistic() {
		String[] stats = new String[this.size()];
		for (int i = 0; i < this.size(); i++) {
			LatencyTracker tracker = this.get(i);
			stats[i] = tracker.sample();
		}
		return stats;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public LatencyTracker set(int index, LatencyTracker element) {
	    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        String mbeanName = "com.zhuaiwa.api.server:type=" + name + ",method=" + element.getLatencyStats().getName();
        try
        {
            logger.info("register mbean: " + mbeanName);
            mbs.registerMBean(element, new ObjectName(mbeanName));
        }
        catch (Exception e)
        {
            logger.info("Failed to register mbean " + mbeanName, e);
        }
	    return super.set(index, element);
	};

	@Override
	public LatencyTracker getStatistic(String methodName) {
		for (LatencyTracker tracker : this) {
			if (tracker.getLatencyStats().getName() != null && tracker.getLatencyStats().getName().equalsIgnoreCase(methodName)) {
				return tracker;
			}
		}
		return null;
	}
	
	public LatencyTracker getStatistic() {
	    return tracker;
	}
}
