package org.apache.cassandra.thrift.client;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.AuthenticationException;
import org.apache.cassandra.thrift.AuthenticationRequest;
import org.apache.cassandra.thrift.AuthorizationException;
import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Cassandra.Iface;
import org.apache.cassandra.thrift.CfDef;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.Compression;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.CounterColumn;
import org.apache.cassandra.thrift.CqlResult;
import org.apache.cassandra.thrift.IndexClause;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.KeyRange;
import org.apache.cassandra.thrift.KeySlice;
import org.apache.cassandra.thrift.KsDef;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.SchemaDisagreementException;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.TokenRange;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;

import com.zhuaiwa.dd.config.DDProperties;

public class Client implements Iface {
	SimpleClientPool pool = null;
	{
	    List<InetSocketAddress> endpoints = new ArrayList<InetSocketAddress>();
	    for (String address : DDProperties.getCassandraAddresses()) {
	        endpoints.add(new InetSocketAddress(address, DDProperties.getCassandraPort()));
	    }
	    pool = SimpleClientPool.createInstance(DDProperties.getCassandraConnections(), endpoints);
	}
	public Cassandra.Client getClient() throws TException {
		try {
			return pool.getClient();
		} catch (TimedOutException e) {
			throw new TException(e);
		}
	}
	public void releaseClient(Cassandra.Client c) {
		pool.putClient(c);
	}
    @Override
    public void login(AuthenticationRequest auth_request) throws AuthenticationException, AuthorizationException, TException {
        Cassandra.Client c = getClient();
        try {
            c.login(auth_request);
        } finally {
    		releaseClient(c);
    	}
    }
    
    @Override
    public void set_keyspace(String keyspace) throws InvalidRequestException, TException {
        Cassandra.Client c = getClient();
        try {
            c.set_keyspace(keyspace);
        } finally {
    		releaseClient(c);
    	}
    }

    @Override
    public ColumnOrSuperColumn get(ByteBuffer key, ColumnPath column_path, ConsistencyLevel consistency_level) throws InvalidRequestException, NotFoundException, UnavailableException, TimedOutException, TException {
        Cassandra.Client c = getClient();
        try {
            return c.get(key, column_path, consistency_level);
        } finally {
    		releaseClient(c);
    	}
    }

    @Override
    public List<ColumnOrSuperColumn> get_slice(ByteBuffer key, ColumnParent column_parent, SlicePredicate predicate, ConsistencyLevel consistency_level) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        Cassandra.Client c = getClient();
        try {
            return c.get_slice(key, column_parent, predicate, consistency_level);
        } finally {
    		releaseClient(c);
    	}
    }
    
    @Override
    public int get_count(ByteBuffer key, ColumnParent column_parent, SlicePredicate predicate, ConsistencyLevel consistency_level) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        Cassandra.Client c = getClient();
        try {
            return c.get_count(key, column_parent, predicate, consistency_level);
        } finally {
    		releaseClient(c);
    	}
    }

    @Override
    public Map<ByteBuffer,List<ColumnOrSuperColumn>> multiget_slice(List<ByteBuffer> keys, ColumnParent column_parent, SlicePredicate predicate, ConsistencyLevel consistency_level) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        Cassandra.Client c = getClient();
        try {
            return c.multiget_slice(keys, column_parent, predicate, consistency_level);
        } finally {
    		releaseClient(c);
    	}
    }

    @Override
    public Map<ByteBuffer,Integer> multiget_count(List<ByteBuffer> keys, ColumnParent column_parent, SlicePredicate predicate, ConsistencyLevel consistency_level) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        Cassandra.Client c = getClient();
        try {
            return c.multiget_count(keys, column_parent, predicate, consistency_level);
        } finally {
    		releaseClient(c);
    	}
    }

    @Override
    public List<KeySlice> get_range_slices(ColumnParent column_parent, SlicePredicate predicate, KeyRange range, ConsistencyLevel consistency_level) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        Cassandra.Client c = getClient();
        try {
            return c.get_range_slices(column_parent, predicate, range, consistency_level);
        } finally {
    		releaseClient(c);
    	}
    }
    
    @Override
    public List<KeySlice> get_indexed_slices(ColumnParent column_parent, IndexClause index_clause, SlicePredicate column_predicate, ConsistencyLevel consistency_level) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        Cassandra.Client c = getClient();
        try {
            return c.get_indexed_slices(column_parent, index_clause, column_predicate, consistency_level);
        } finally {
    		releaseClient(c);
    	}
    }

    @Override
    public void insert(ByteBuffer key, ColumnParent column_parent, Column column, ConsistencyLevel consistency_level) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        Cassandra.Client c = getClient();
        try {
            c.insert(key, column_parent, column, consistency_level);
        } finally {
    		releaseClient(c);
    	}
    }
    
    @Override
    public void add(ByteBuffer key, ColumnParent column_parent, CounterColumn column, ConsistencyLevel consistency_level) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        Cassandra.Client c = getClient();
        try {
            c.add(key, column_parent, column, consistency_level);
        } finally {
    		releaseClient(c);
    	}
    }

    @Override
    public void remove(ByteBuffer key, ColumnPath column_path, long timestamp, ConsistencyLevel consistency_level) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        Cassandra.Client c = getClient();
        try {
            c.remove(key, column_path, timestamp, consistency_level);
        } finally {
    		releaseClient(c);
    	}
    }
    
    @Override
    public void remove_counter(ByteBuffer key, ColumnPath path, ConsistencyLevel consistency_level) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        Cassandra.Client c = getClient();
        try {
            c.remove_counter(key, path, consistency_level);
        } finally {
    		releaseClient(c);
    	}
    }

    @Override
    public void batch_mutate(Map<ByteBuffer,Map<String,List<Mutation>>> mutation_map, ConsistencyLevel consistency_level) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        Cassandra.Client c = getClient();
        try {
            c.batch_mutate(mutation_map, consistency_level);
        } finally {
    		releaseClient(c);
    	}
    }
    
    @Override
    public void truncate(String cfname) throws InvalidRequestException, UnavailableException, TException {
        Cassandra.Client c = getClient();
        try {
            c.truncate(cfname);
        } finally {
    		releaseClient(c);
    	}
    }
    
    @Override
    public Map<String,List<String>> describe_schema_versions() throws InvalidRequestException, TException {
        Cassandra.Client c = getClient();
        try {
            return c.describe_schema_versions();
        } finally {
    		releaseClient(c);
    	}
    }
    
    @Override
    public List<KsDef> describe_keyspaces() throws InvalidRequestException, TException {
        Cassandra.Client c = getClient();
        try {
            return c.describe_keyspaces();
        } finally {
    		releaseClient(c);
    	}
    }
    
    @Override
    public String describe_cluster_name() throws TException {
        Cassandra.Client c = getClient();
        try {
            return c.describe_cluster_name();
        } finally {
    		releaseClient(c);
    	}
    }

    @Override
    public String describe_version() throws TException {
        Cassandra.Client c = getClient();
        try {
            return c.describe_version();
        } finally {
    		releaseClient(c);
    	}
    }
    
    @Override
    public List<TokenRange> describe_ring(String keyspace) throws InvalidRequestException, TException {
        Cassandra.Client c = getClient();
        try {
            return c.describe_ring(keyspace);
        } finally {
    		releaseClient(c);
    	}
    }
    
    @Override
    public String describe_partitioner() throws TException {
        Cassandra.Client c = getClient();
        try {
            return c.describe_partitioner();
        } finally {
    		releaseClient(c);
    	}
    }

    @Override
    public String describe_snitch() throws TException {
        Cassandra.Client c = getClient();
        try {
            return c.describe_snitch();
        } finally {
    		releaseClient(c);
    	}
    }

    @Override
    public KsDef describe_keyspace(String keyspace) throws NotFoundException, InvalidRequestException, TException {
        Cassandra.Client c = getClient();
        try {
            return c.describe_keyspace(keyspace);
        } finally {
    		releaseClient(c);
    	}
    }

    @Override
    public List<String> describe_splits(String cfName, String start_token, String end_token, int keys_per_split) throws InvalidRequestException, TException {
        Cassandra.Client c = getClient();
        try {
            return c.describe_splits(cfName, start_token, end_token, keys_per_split);
        } finally {
    		releaseClient(c);
    	}
    }

    @Override
    public String system_add_column_family(CfDef cf_def) throws InvalidRequestException, SchemaDisagreementException, TException {
        Cassandra.Client c = getClient();
        try {
            return c.system_add_column_family(cf_def);
        } finally {
    		releaseClient(c);
    	}
    }

    @Override
    public String system_drop_column_family(String column_family) throws InvalidRequestException, SchemaDisagreementException, TException {
        Cassandra.Client c = getClient();
        try {
            return c.system_drop_column_family(column_family);
        } finally {
    		releaseClient(c);
    	}
    }

    @Override
    public String system_add_keyspace(KsDef ks_def) throws InvalidRequestException, SchemaDisagreementException, TException {
        Cassandra.Client c = getClient();
        try {
            return c.system_add_keyspace(ks_def);
        } finally {
    		releaseClient(c);
    	}
    }

    @Override
    public String system_drop_keyspace(String keyspace) throws InvalidRequestException, SchemaDisagreementException, TException {
        Cassandra.Client c = getClient();
        try {
            return c.system_drop_keyspace(keyspace);
        } finally {
    		releaseClient(c);
    	}
    }

    @Override
    public String system_update_keyspace(KsDef ks_def) throws InvalidRequestException, SchemaDisagreementException, TException {
        Cassandra.Client c = getClient();
        try {
            return c.system_update_keyspace(ks_def);
        } finally {
    		releaseClient(c);
    	}
    }

    @Override
    public String system_update_column_family(CfDef cf_def) throws InvalidRequestException, SchemaDisagreementException, TException {
        
        Cassandra.Client c = getClient();
        try {
            return c.system_update_column_family(cf_def);
        } finally {
    		releaseClient(c);
    	}
    }

    @Override
    public CqlResult execute_cql_query(ByteBuffer query, Compression compression) throws InvalidRequestException, UnavailableException, TimedOutException, SchemaDisagreementException, TException {
        Cassandra.Client c = getClient();
        try {
            return c.execute_cql_query(query, compression);
        } finally {
    		releaseClient(c);
    	}
    }
}
