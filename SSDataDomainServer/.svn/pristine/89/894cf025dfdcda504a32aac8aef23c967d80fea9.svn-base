package com.zhuaiwa.session.search;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Speed implements Runnable {
	private static Logger LOG = LoggerFactory.getLogger(Speed.class);
	private static AtomicInteger counter = new AtomicInteger(0);

	private long start = System.currentTimeMillis();

	public void count(int count) {
		counter.addAndGet(count);
	}
	
	public int getCount(){
		return counter.get();
	}

	public void run() {
		while (true) {
			try {
				float second = 1.0f * (System.currentTimeMillis() - start) / 1000;
				if (second > 0) {
					float speed = counter.get() / second;
					LOG.info("共处理：" + counter.get() + ", 速度：" + new DecimalFormat("#,##0").format(speed) + "/s");
				}
				Thread.sleep(1000);
			} catch (Exception e) {
				LOG.error("", e);
			}
		}
	}

}
