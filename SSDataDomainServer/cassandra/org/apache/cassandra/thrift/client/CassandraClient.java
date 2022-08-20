package org.apache.cassandra.thrift.client;

import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.AuthenticationException;
import org.apache.cassandra.thrift.AuthenticationRequest;
import org.apache.cassandra.thrift.AuthorizationException;
import org.apache.cassandra.thrift.Cassandra;
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
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;


public class CassandraClient extends Cassandra.Client {
	public CassandraClient(TProtocol prot)
    {
      super(prot, prot);
    }

    public CassandraClient(TProtocol iprot, TProtocol oprot)
    {
      super(iprot, oprot);
    }

    protected boolean connected = false;
	
	public boolean isConnected() {
		if (getInputProtocol() == null || getInputProtocol().getTransport() == null || !getInputProtocol().getTransport().isOpen())
			return false;
		
		TTransport intransport = getInputProtocol().getTransport();
		if (intransport instanceof TSocket) {
			Socket si = ((TSocket)intransport).getSocket();
			if (si != null && (!si.isConnected() || si.isClosed() || si.isInputShutdown() || si.isOutputShutdown()))
				return false;
		}
		
		if (getOutputProtocol() == null || getOutputProtocol().getTransport() == null || !getOutputProtocol().getTransport().isOpen())
			return false;
		
		TTransport outtransport = getOutputProtocol().getTransport();
		if (intransport instanceof TSocket) {
			Socket so = ((TSocket)outtransport).getSocket();
			if (so != null && (!so.isConnected() || so.isClosed() || so.isInputShutdown() || so.isOutputShutdown()))
				return false;
		}
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	public void open() throws TTransportException {
		TTransport it = null;
		TTransport ot = null;
		TProtocol ip = getInputProtocol();
		if (ip != null) {
			it = ip.getTransport();
			if (it != null && it != ot) {
				it.open();
			}
		}
		TProtocol op = getOutputProtocol();
		if (op != null) {
			ot = op.getTransport();
			if (ot != null && it != ot) {
				ot.open();
			}
		}
		
		connected = true;
	}
	
	public void close() {
		TTransport it = null;
		TTransport ot = null;
		TProtocol ip = getInputProtocol();
		if (ip != null) {
			it = ip.getTransport();
			if (it != null && it != ot) {
				it.close();
			}
		}
		TProtocol op = getOutputProtocol();
		if (op != null) {
			ot = op.getTransport();
			if (ot != null && it != ot) {
				ot.close();
			}
		}
	}
	
	private void handleTException(TException e) throws TException {
	    if (isPipeBroken(e))
            connected = false;
	}

	private boolean isPipeBroken(TException e) {
		Throwable cause = e;
		while (cause != null) {
			if (SocketException.class.isAssignableFrom(cause.getClass()))
				return true;
			cause = cause.getCause();
		}
		return false;
	}
	
	@Override
    public void login(AuthenticationRequest auth_request) throws AuthenticationException, AuthorizationException, TException {
    	try {
    		super.login(auth_request);
		} catch (TException e) {
		    handleTException(e);
		    throw e;
		}
    }
	
	@Override
	public void set_keyspace(String keyspace) throws InvalidRequestException, TException {
	    try {
	        super.set_keyspace(keyspace);
	    } catch (TException e) {
            handleTException(e);
            throw e;
        }
	}

    @Override
    public ColumnOrSuperColumn get(ByteBuffer key, ColumnPath column_path, ConsistencyLevel consistency_level) throws InvalidRequestException, NotFoundException, UnavailableException, TimedOutException, TException {
    	try {
    		return super.get(key, column_path, consistency_level);
		} catch (TException e) {
            handleTException(e);
            throw e;
		}
    }

    @Override
    public List<ColumnOrSuperColumn> get_slice(ByteBuffer key, ColumnParent column_parent, SlicePredicate predicate, ConsistencyLevel consistency_level) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
    	try {
    		return super.get_slice(key, column_parent, predicate, consistency_level);
		} catch (TException e) {
            handleTException(e);
            throw e;
		}
    }
    
    @Override
    public int get_count(ByteBuffer key, ColumnParent column_parent, SlicePredicate predicate, ConsistencyLevel consistency_level) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        try {
            return super.get_count(key, column_parent, predicate, consistency_level);
        } catch (TException e) {
            handleTException(e);
            throw e;
        }
    }

    @Override
    public Map<ByteBuffer,List<ColumnOrSuperColumn>> multiget_slice(List<ByteBuffer> keys, ColumnParent column_parent, SlicePredicate predicate, ConsistencyLevel consistency_level) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
    	try {
    		return super.multiget_slice(keys, column_parent, predicate, consistency_level);
		} catch (TException e) {
            handleTException(e);
            throw e;
		}
    }

    @Override
    public Map<ByteBuffer,Integer> multiget_count(List<ByteBuffer> keys, ColumnParent column_parent, SlicePredicate predicate, ConsistencyLevel consistency_level) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
    	try {
    		return super.multiget_count(keys, column_parent, predicate, consistency_level);
		} catch (TException e) {
            handleTException(e);
            throw e;
		}
    }

    @Override
    public List<KeySlice> get_range_slices(ColumnParent column_parent, SlicePredicate predicate, KeyRange range, ConsistencyLevel consistency_level) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
    	try {
    		return super.get_range_slices(column_parent, predicate, range, consistency_level);
		} catch (TException e) {
            handleTException(e);
            throw e;
		}
    }
    
    @Override
    public List<KeySlice> get_indexed_slices(ColumnParent column_parent, IndexClause index_clause, SlicePredicate column_predicate, ConsistencyLevel consistency_level) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        try {
            return super.get_indexed_slices(column_parent, index_clause, column_predicate, consistency_level);
        } catch (TException e) {
            handleTException(e);
            throw e;
        }
    }

    @Override
    public void insert(ByteBuffer key, ColumnParent column_parent, Column column, ConsistencyLevel consistency_level) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
    	try {
    		super.insert(key, column_parent, column, consistency_level);
		} catch (TException e) {
            handleTException(e);
            throw e;
		}
    }
    
    @Override
    public void add(ByteBuffer key, ColumnParent column_parent, CounterColumn column, ConsistencyLevel consistency_level) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        try {
            super.add(key, column_parent, column, consistency_level);
        } catch (TException e) {
            handleTException(e);
            throw e;
        }
    }

    @Override
    public void remove(ByteBuffer key, ColumnPath column_path, long timestamp, ConsistencyLevel consistency_level) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
    	try {
    		super.remove(key, column_path, timestamp, consistency_level);
		} catch (TException e) {
            handleTException(e);
            throw e;
		}
    }
    
    @Override
    public void remove_counter(ByteBuffer key, ColumnPath path, ConsistencyLevel consistency_level) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        try {
            super.remove_counter(key, path, consistency_level);
        } catch (TException e) {
            handleTException(e);
            throw e;
        }
    }

    @Override
    public void batch_mutate(Map<ByteBuffer,Map<String,List<Mutation>>> mutation_map, ConsistencyLevel consistency_level) throws InvalidRequestException, UnavailableException, TimedOutException, TException {
    	try {
    		super.batch_mutate(mutation_map, consistency_level);
		} catch (TException e) {
            handleTException(e);
            throw e;
		}
    }
    
    @Override
    public void truncate(String cfname) throws InvalidRequestException, UnavailableException, TException {
        try {
            super.truncate(cfname);
        } catch (TException e) {
            handleTException(e);
            throw e;
        }
    }
    
    @Override
    public Map<String,List<String>> describe_schema_versions() throws InvalidRequestException, TException {
        try {
            return super.describe_schema_versions();
        } catch (TException e) {
            handleTException(e);
            throw e;
        }
    }
    
    @Override
    public List<KsDef> describe_keyspaces() throws InvalidRequestException, TException {
        try {
            return super.describe_keyspaces();
        } catch (TException e) {
            handleTException(e);
            throw e;
        }
    }
    
    @Override
    public String describe_cluster_name() throws TException {
        try {
            return super.describe_cluster_name();
        } catch (TException e) {
            handleTException(e);
            throw e;
        }
    }

    @Override
    public String describe_version() throws TException {
        try {
            return super.describe_version();
        } catch (TException e) {
            handleTException(e);
            throw e;
        }
    }
    
    @Override
    public List<TokenRange> describe_ring(String keyspace) throws InvalidRequestException, TException {
        try {
            return super.describe_ring(keyspace);
        } catch (TException e) {
            handleTException(e);
            throw e;
        }
    }
    
    @Override
    public String describe_partitioner() throws TException {
        try {
            return super.describe_partitioner();
        } catch (TException e) {
            handleTException(e);
            throw e;
        }
    }

    @Override
    public String describe_snitch() throws TException {
        try {
            return super.describe_snitch();
        } catch (TException e) {
            handleTException(e);
            throw e;
        }
    }

    @Override
    public KsDef describe_keyspace(String keyspace) throws NotFoundException, InvalidRequestException, TException {
        try {
            return super.describe_keyspace(keyspace);
        } catch (TException e) {
            handleTException(e);
            throw e;
        }
    }

    @Override
    public List<String> describe_splits(String cfName, String start_token, String end_token, int keys_per_split) throws InvalidRequestException, TException {
        try {
            return super.describe_splits(cfName, start_token, end_token, keys_per_split);
        } catch (TException e) {
            handleTException(e);
            throw e;
        }
    }

    @Override
    public String system_add_column_family(CfDef cf_def) throws InvalidRequestException, SchemaDisagreementException, TException {
        try {
            return super.system_add_column_family(cf_def);
        } catch (TException e) {
            handleTException(e);
            throw e;
        }
    }

    @Override
    public String system_drop_column_family(String column_family) throws InvalidRequestException, SchemaDisagreementException, TException {
        try {
            return super.system_drop_column_family(column_family);
        } catch (TException e) {
            handleTException(e);
            throw e;
        }
    }

    @Override
    public String system_add_keyspace(KsDef ks_def) throws InvalidRequestException, SchemaDisagreementException, TException {
        try {
            return super.system_add_keyspace(ks_def);
        } catch (TException e) {
            handleTException(e);
            throw e;
        }
    }

    @Override
    public String system_drop_keyspace(String keyspace) throws InvalidRequestException, SchemaDisagreementException, TException {
        try {
            return super.system_drop_keyspace(keyspace);
        } catch (TException e) {
            handleTException(e);
            throw e;
        }
    }

    @Override
    public String system_update_keyspace(KsDef ks_def) throws InvalidRequestException, SchemaDisagreementException, TException {
        try {
            return super.system_update_keyspace(ks_def);
        } catch (TException e) {
            handleTException(e);
            throw e;
        }
    }

    @Override
    public String system_update_column_family(CfDef cf_def) throws InvalidRequestException, SchemaDisagreementException, TException {
        
        try {
            return super.system_update_column_family(cf_def);
        } catch (TException e) {
            handleTException(e);
            throw e;
        }
    }

    @Override
    public CqlResult execute_cql_query(ByteBuffer query, Compression compression) throws InvalidRequestException, UnavailableException, TimedOutException, SchemaDisagreementException, TException {
        try {
            return super.execute_cql_query(query, compression);
        } catch (TException e) {
            handleTException(e);
            throw e;
        }
    }
}
