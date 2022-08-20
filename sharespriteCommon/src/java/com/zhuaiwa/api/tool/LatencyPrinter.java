package com.zhuaiwa.api.tool;

import java.net.SocketException;
import java.rmi.ConnectException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.zhuaiwa.api.statistic.LatencyStats;
import com.zhuaiwa.api.statistic.LatencyStatsMXBean;
import com.zhuaiwa.api.statistic.LatencyTracker;

public class LatencyPrinter {

    private static final String HOST_OPT_LONG = "host";
    private static final String HOST_OPT_SHORT = "h";
    private static final String PORT_OPT_LONG = "port";
    private static final String PORT_OPT_SHORT = "p";
    private static final String INTERVAL_OPT_LONG = "interval";
    private static final String INTERVAL_OPT_SHORT = "i";
    private static final String COUNT_OPT_LONG = "count";
    private static final String COUNT_OPT_SHORT = "c";
//    private static final String DOMAIN_OPT_LONG = "domain";
//    private static final String DOMAIN_OPT_SHORT = "d";
    private static Options options = null;
    
    static
    {
        options = new Options();
        Option optHost = new Option(HOST_OPT_SHORT, HOST_OPT_LONG, true, "node hostname or ip address");
        Option optPort = new Option(PORT_OPT_SHORT, PORT_OPT_LONG, true, "remote jmx agent port number");
        Option optInterval = new Option(INTERVAL_OPT_SHORT, INTERVAL_OPT_LONG, true, "sample interval, default 5s");
        Option optCount = new Option(COUNT_OPT_SHORT, COUNT_OPT_LONG, true, "sample count, default infinite");
        
        options.addOption(optHost);
        options.addOption(optPort);
        options.addOption(optInterval);
        options.addOption(optCount);
    }
    
    public static void printUsage() {
        HelpFormatter hf = new HelpFormatter();
        String usage = String.format("java %s [--host <arg>] [--port <arg>] [--interval 5] [--count 0] <object name>[...]%n", LatencyPrinter.class.getName());
        hf.printHelp(usage, "", options, "");
    }
    
    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException parseExcep) {
            System.err.println(parseExcep);
            printUsage();
            System.exit(1);
        }

        String host = cmd.getOptionValue(HOST_OPT_LONG, "localhost");
        int port = Integer.parseInt(cmd.getOptionValue(PORT_OPT_LONG, "8080"));
        int interval = Integer.parseInt(cmd.getOptionValue(INTERVAL_OPT_LONG, "5"));
        int count = Integer.parseInt(cmd.getOptionValue(COUNT_OPT_LONG, "0"));
        
        if (cmd.getArgs().length < 1)
        {
            System.err.println("Missing argument for command.");
            printUsage();
            System.exit(1);
        }
        
        final JMXServiceURL jmxUrl = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+host + ":" + port + "/jmxrmi");
        
        String[] arguments = cmd.getArgs();
        List<LatencyTracker> trackers = new ArrayList<LatencyTracker>();
        for (int i = 0; i < arguments.length; i++) {
            final ObjectName objectname = new ObjectName(arguments[i]);
            trackers.add(new LatencyTracker(new LatencyStats() {
                private LatencyStatsMXBean proxy;
                {
                    reconnect();
                }
                private boolean isConnectionBroken(Throwable e) {
                    Throwable cause = e;
                    while (cause != null) {
                        if (SocketException.class.isAssignableFrom(cause.getClass()))
                            return true;
                        if (ConnectException.class.isAssignableFrom(cause.getClass()))
                            return true;
                        cause = cause.getCause();
                    }
                    return false;
                }
                private void reconnect() throws RuntimeException {
                    try {
                        JMXConnector jmxc = JMXConnectorFactory.connect(jmxUrl, null);
                        MBeanServerConnection mbeanServerConn = jmxc.getMBeanServerConnection();
                        proxy = JMX.newMXBeanProxy(mbeanServerConn, objectname, LatencyStatsMXBean.class);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                @Override
                public long getCount() {
                    try {
                        return proxy.getCount();
                    } catch (Exception e) {
                        if (isConnectionBroken(e))
                            reconnect();
                        throw new RuntimeException(e);
                    }
                }
                @Override
                public long[] getHistogram() {
                    try {
                        return proxy.getHistogram();
                    } catch (Exception e) {
                        if (isConnectionBroken(e))
                            reconnect();
                        throw new RuntimeException(e);
                    }
                }
                @Override
                public long getLatency() {
                    try {
                        return proxy.getLatency();
                    } catch (Exception e) {
                        if (isConnectionBroken(e))
                            reconnect();
                        throw new RuntimeException(e);
                    }
                }
                @Override
                public String getName() {
                    try {
                        return proxy.getName();
                    } catch (Exception e) {
                        if (isConnectionBroken(e))
                            reconnect();
                        throw new RuntimeException(e);
                    }
                }
            }));
        }
        
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        
        for (int i = 0; (count == 0 || i < count); i++) {
            long start = System.currentTimeMillis();
            for (LatencyTracker tracker : trackers) {
                try {
                    String sample = tracker.sample();
                    String time = sdf.format(new Date());
                    System.out.print(time);
                    System.out.print(" ");
                    System.out.println(sample);
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }
            
            if (count != 0 && i >= count-1)
                break;
            
            long end = System.currentTimeMillis();
            try {
                Thread.sleep(Math.max(0, interval*1000 + start - end));
            } catch (Exception e) {
                // ignore
            }
        }
    }
}
