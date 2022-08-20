package com.zhuaiwa.dd.log;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.log4j.jmx.HierarchyDynamicMBean;

public class JMXLog4jConfigurator {
    public static void config() {

        try {
            HierarchyDynamicMBean hdm = new HierarchyDynamicMBean();
            ManagementFactory.getPlatformMBeanServer()
            .registerMBean(hdm, new ObjectName("log4j:hierarchy=default"));
            
//            org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger();
//            hdm.addLoggerMBean(rootLogger.getName());
//            JMXLog4jChecker checker = new JMXLog4jChecker(hdm, 30);
//            checker.start();
        } catch (InstanceAlreadyExistsException e) {
            throw new RuntimeException("Can't register log4j to jmx.", e);
        } catch (MBeanRegistrationException e) {
            throw new RuntimeException("Can't register log4j to jmx.", e);
        } catch (NotCompliantMBeanException e) {
            throw new RuntimeException("Can't register log4j to jmx.", e);
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException("Can't register log4j to jmx.", e);
        } catch (NullPointerException e) {
            throw new RuntimeException("Can't register log4j to jmx.", e);
        } catch (Exception e) {
            throw new RuntimeException("Can't register log4j to jmx.", e);
        }
    }
}
