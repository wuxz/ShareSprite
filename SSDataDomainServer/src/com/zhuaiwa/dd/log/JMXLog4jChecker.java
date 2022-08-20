package com.zhuaiwa.dd.log;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.jmx.HierarchyDynamicMBean;
import org.apache.log4j.spi.LoggerRepository;

public class JMXLog4jChecker extends Thread {
    private long interval;
    private HierarchyDynamicMBean hdm;

    public JMXLog4jChecker(HierarchyDynamicMBean hdm, long interval) {
          this.interval = interval;
          this.hdm = hdm;
          setDaemon(true);
          checkForNewLoggers();
    }
   
    @SuppressWarnings("unchecked")
    private void checkForNewLoggers() {
      LoggerRepository r = LogManager.getLoggerRepository();
      java.util.Enumeration<Logger> loggers = r.getCurrentLoggers();
      Logger logger = null;
      while (loggers.hasMoreElements()) {
          try {
              logger = (Logger) loggers.nextElement();
              hdm.addLoggerMBean(logger.getName());
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
    }

    @Override
    public void run() {
      while(true) {
          try {
              Thread.sleep(interval);
          } catch (InterruptedException e) {
          }
          checkForNewLoggers();
      }
    }
}
