package com.zhuaiwa.dd.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DDProperties {

    private static Logger logger = LoggerFactory.getLogger(DDProperties.class);
    
    private static Properties props = new Properties();
    public static String getProperty(String key) {
        if (props.containsKey(key))
            return props.getProperty(key);
        return System.getProperty(key);
    }
    public static String getProperty(String key, String defaultValue) {
        if (props.containsKey(key))
            return props.getProperty(key, defaultValue);
        return System.getProperty(key, defaultValue);
    }
    public static void setProperty(String key, String value) {
        props.setProperty(key, value);
    }
    
    /* zookeeper */
    private static String zooKeeperRoot;
    private static String zooKeeperPort;
    private static Set<String> zooKeeperAddresses;
    
    /* data domain server */
    private static InetAddress listenAddress;
    private static Integer listenPort;
    
    /* cassandra */
    private static Set<String> cassandraAddresses;
    private static Integer cassandraPort;
    private static Integer cassandraConnections;
    
    private final static String CONF_FILENAME = "dd.properties";

    static URL getStorageConfigURI() throws FileNotFoundException  {
        String confdir = System.getProperty("dd.server.conf.dir");
        if (confdir != null) {
            String scp = confdir + File.separator + CONF_FILENAME;
            File scpf = new File(scp);
            if (scpf.exists()) {
                try {
                    return scpf.toURI().toURL();
                } catch (MalformedURLException e) {
                }
            }
        }

        // try the classpath
        ClassLoader loader = DDProperties.class.getClassLoader();
        URL scpurl = loader.getResource(CONF_FILENAME);
        if (scpurl != null)
            return scpurl;

        throw new FileNotFoundException("Cannot locate " + CONF_FILENAME + " via dd.server.conf.dir system property or classpath lookup.");
    }
    static {
        try {
            URL configFileURI = getStorageConfigURI();
            if (logger.isDebugEnabled())
                    logger.debug("Loading settings from " + configFileURI);
            
            props.load(configFileURI.openStream());
        } catch (FileNotFoundException e) {
            logger.warn("Can't get configuration file.");
        } catch (IOException e) {
            logger.error("Fatal error: " + e.getMessage());
            System.err.println("Bad configuration; unable to start server");
            System.exit(1);
        }
    }
    
    public static String getZooKeeperRoot()
    {
        if (zooKeeperRoot == null) {
            try {
                zooKeeperRoot = getProperty("dd.zookeeper.root", "locks");
            } catch (Exception e) {
                logger.warn("Can't get dd.zookeeper.root: " + e.getMessage());
                zooKeeperRoot = "locks";
            }
        }
        return zooKeeperRoot;
    }

    public static String getZooKeeperPort()
    {
        if (zooKeeperPort == null) {
            try {
                zooKeeperPort = getProperty("dd.zookeeper.port", "2181");
            } catch (Exception e) {
                logger.warn("Can't get dd.zookeeper.port: " + e.getMessage());
                zooKeeperPort = "2181";
            }
        }
        return zooKeeperPort;
    }

    public static Set<String> getZooKeeperAddresses()
    {
        if (zooKeeperAddresses == null) {
            try {
                zooKeeperAddresses = new HashSet<String>();
                String[] zooKeepers = getProperty("dd.zookeeper.addresses", "127.0.0.1").split(";|,");
                for (int i=0; i<zooKeepers.length; ++i)
                    zooKeeperAddresses.add(zooKeepers[i]);
            } catch (Exception e) {
                logger.warn("Can't get dd.zookeeper.addresses: " + e.getMessage());
                zooKeeperAddresses = new HashSet<String>();
                zooKeeperAddresses.add("127.0.0.1");
            }
        }
        return zooKeeperAddresses;
    }
    
    /* data domain server */
    public static InetAddress getListenAddress() {
        if (listenAddress == null) {
            try {
            /* Local IP or hostname to bind thrift server to */
            String ssAddr = getProperty("dd.server.listen.address", "0.0.0.0");
            if ( ssAddr != null )
                listenAddress = InetAddress.getByName(ssAddr);
            } catch (Exception e) {
                logger.warn("Can't get dd.server.listen.address: " + e.getMessage());
                try {
                    listenAddress = InetAddress.getByName("0.0.0.0");
                } catch (UnknownHostException e1) {
                }
            }
        }
        return listenAddress;
    }
    
    public static int getListenPort() {
        if (listenPort == null) {
            try {
            String port = getProperty("dd.server.listen.port", "9161");
            if (port != null)
                listenPort = Integer.parseInt(port);
            } catch (Exception e) {
                logger.warn("Can't get dd.server.listen.port: " + e.getMessage());
                listenPort = 9161;
            }
        }
        return listenPort;
    }
    
    /* cassandra */
    
    public static Set<String> getCassandraAddresses() {
        if (cassandraAddresses == null) {
            try {
                cassandraAddresses = new HashSet<String>();
                String[] addresses = getProperty("dd.cassandra.addresses", "127.0.0.1").split(";|,");
                for (String address : addresses) {
                    cassandraAddresses.add(address);
                }
            } catch (Exception e) {
                logger.warn("Can't get dd.cassandra.addresses: " + e.getMessage());
                cassandraAddresses = new HashSet<String>();
                cassandraAddresses.add("127.0.0.1");
            }
        }
        return cassandraAddresses;
    }
    
    public static int getCassandraPort() {
        if (cassandraPort == null) {
            try {
                String port = getProperty("dd.cassandra.port", "9160");
                cassandraPort = Integer.valueOf(port);
            } catch (Exception e) {
                logger.warn("Can't get dd.cassandra.port: " + e.getMessage());
                cassandraPort = 9160;
            }
        }
        return cassandraPort;
    }
    
    public static int getCassandraConnections() {
        if (cassandraConnections == null) {
            try {
                String connections = getProperty("dd.cassandra.connections", "16");
                cassandraConnections = Integer.valueOf(connections);
            } catch (Exception e) {
                logger.warn("Can't get dd.cassandra.connections: " + e.getMessage());
                cassandraConnections = 16;
            }
        }
        return cassandraConnections;
    }
}
