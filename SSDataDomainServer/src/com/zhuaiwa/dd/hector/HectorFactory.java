package com.zhuaiwa.dd.hector;

import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;

import com.zhuaiwa.dd.config.DDProperties;

public class HectorFactory {
    public static final String KEYSPACE_NAME = "SSDataDomain";
    
    private static class ClusterHolder {
        private static Cluster cluster;
        static {
            StringBuilder hosts = new StringBuilder();
            for (String address : DDProperties.getCassandraAddresses()) {
                hosts.append(address).append(":").append(DDProperties.getCassandraPort()).append(",");
            }
            if (hosts.length() > 0 && hosts.charAt(hosts.length()-1) == ',')
                hosts.deleteCharAt(hosts.length()-1);
            CassandraHostConfigurator configurator = new CassandraHostConfigurator(hosts.toString());
            configurator.setMaxActive(DDProperties.getCassandraConnections());
            configurator.setUseThriftFramedTransport(true);
            configurator.setCassandraThriftSocketTimeout(5000);
            configurator.setMaxWaitTimeWhenExhausted(5000);
            configurator.setLoadBalancingPolicy(new RoundRobinBalancingPolicy());
            cluster = HFactory.getOrCreateCluster("Channelsoft Cluster", configurator);
        }
    }
    
    private static class KeyspaceHolder {
        private static Keyspace keyspace;
        static {
            keyspace = HFactory.createKeyspace(KEYSPACE_NAME, getCluster());
        }
    }
    
    public static Cluster getCluster() {
        return ClusterHolder.cluster;
    }
    
    public static Keyspace getKeyspace() {
        return KeyspaceHolder.keyspace;
    }
    
    public static void shutdown() {
        HFactory.shutdownCluster(getCluster());
    }
}
