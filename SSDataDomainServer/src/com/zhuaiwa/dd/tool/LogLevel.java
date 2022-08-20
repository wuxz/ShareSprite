package com.zhuaiwa.dd.tool;

import javax.management.Attribute;
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

public class LogLevel {
    private static final String HOST_OPT_LONG = "host";
    private static final String HOST_OPT_SHORT = "h";
    private static final String PORT_OPT_LONG = "port";
    private static final String PORT_OPT_SHORT = "p";
    
    private static Options options = null;
    static {
        options = new Options();
        Option optHost = new Option(HOST_OPT_SHORT, HOST_OPT_LONG, true, "remote jmx agent hostname or ip address");
        Option optPort = new Option(PORT_OPT_SHORT, PORT_OPT_LONG, true, "remote jmx agent port number");
        
        options.addOption(optHost);
        options.addOption(optPort);
    }
    
    public static void printUsage() {
        HelpFormatter hf = new HelpFormatter();
        String usage = String.format("java %s [--host <arg>] [--port <arg>] [DEBUG|TRACE|INFO|WARN|ERROR]%n", LogLevel.class.getName());
        hf.printHelp(usage, "", options, "");
    }
    
    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace(System.err);
            printUsage();
            System.exit(1);
        }
        
        String host = cmd.getOptionValue(HOST_OPT_LONG, "localhost");
        int port = Integer.parseInt(cmd.getOptionValue(PORT_OPT_LONG, "8080"));
        String[] arguments = cmd.getArgs();
        
        String url = String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi", host, port);
        JMXServiceURL serviceUrl = new JMXServiceURL(url);
        JMXConnector connector = JMXConnectorFactory.connect(serviceUrl, null);
        MBeanServerConnection connection = connector.getMBeanServerConnection();
        ObjectName name = new ObjectName("log4j:logger=root");
        
//        DynamicMBean logger = JMX.newMBeanProxy(connection, name, DynamicMBean.class);
        if (arguments.length == 0) {
//            String priority = (String)logger.getAttribute("priority");
            String priority = (String)connection.getAttribute(name, "priority");
            System.out.println(String.format("root logger level: %s", priority));
            System.exit(0);
        }
        
        String priority = arguments[0];
        priority = priority.toUpperCase();
        if (!priority.matches("DEBUG|TRACE|INFO|WARN|ERROR")) {
            printUsage();
            System.exit(1);
        }
//        logger.setAttribute(new Attribute("priority", priority));
        connection.setAttribute(name, new Attribute("priority", priority));
        System.out.println(String.format("set root logger level to %s", priority));
        System.exit(0);
    }
}
