package com.zhuaiwa.dd.cmd;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.prettyprint.cassandra.serializers.ByteBufferSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SubColumnQuery;

import org.apache.cassandra.thrift.ConsistencyLevel;

import com.zhuaiwa.dd.exception.DALException;

public class UpdateCommand extends Command {
	static public interface UpdateOperator {
	    ByteBuffer handle(String key, ByteBuffer supername, ByteBuffer name, ByteBuffer value);
	}

    private String objectCF;
	private List<String> keys = new ArrayList<String>();
	
	private String superName;
	private String name;
	private UpdateOperator operator;
	
	public UpdateCommand(Keyspace cassandra) {
		super(cassandra);
	}
	
	public UpdateCommand(Keyspace cassandra, ConsistencyLevel consistencyLevel) {
		super(cassandra, consistencyLevel);
	}

	public <T> UpdateCommand Object(String objectCF) {
	    this.objectCF = objectCF;
        return this;
	}
	
	public UpdateCommand Where(String... keys) {
		for (String key : keys) {
			if (!key.isEmpty())
				this.keys.add(key);
		}
		return this;
	}
	
	public UpdateCommand Update(String name, UpdateOperator operator) {
		this.name = name;
		this.operator = operator;
		return this;
	}
	
	public UpdateCommand Update(String supername, String name, UpdateOperator operator) {
		this.superName = supername;
		this.name = name;
		this.operator = operator;
		return this;
	}
	
	@SuppressWarnings("unchecked")
    public void execute() throws DALException {

        boolean issub = (this.superName != null);
        SubColumnQuery<String, ByteBuffer, ByteBuffer, ByteBuffer> subquery = HFactory.createSubColumnQuery(cassandra, StringSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get());
        ColumnQuery<String, ByteBuffer, ByteBuffer> query = HFactory.createColumnQuery(cassandra, StringSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get());
        
		try {
            Mutator<String> mutator = HFactory.createMutator(cassandra, StringSerializer.get());
			for (String key : this.keys) {
			    
                if (issub) {
                    subquery.setColumnFamily(this.objectCF);
                    subquery.setSuperColumn(StringSerializer.get().toByteBuffer(this.superName));
                    subquery.setKey(key);
                    subquery.setSuperColumn(Command.bytes(this.superName));
                    subquery.setColumn(Command.bytes(this.name));
	            } else {
	                query.setColumnFamily(this.objectCF);
	                query.setKey(key);
	                query.setName(Command.bytes(this.name));
	            }

                QueryResult<HColumn<ByteBuffer, ByteBuffer>> queryresult = (issub ? subquery.execute() : query.execute());
                HColumn<ByteBuffer, ByteBuffer> column = (queryresult == null ? null : queryresult.get());
				long timestamp = -1;
				if (column != null)
				    timestamp = column.getClock();
				
				long now = cassandra.createClock();
                if (timestamp == -1) {
                    timestamp = now;
                } else if(timestamp < now) {
                    timestamp = now;
                } else {
                    timestamp += 1;
                }
				
				ByteBuffer new_columnvalue = operator.handle(
				        key,
				        this.superName == null ? null : StringSerializer.get().toByteBuffer(this.superName),
				                StringSerializer.get().toByteBuffer(this.name),
						column == null ? null : column.getValue());
				if (issub) {
	                mutator.addInsertion(key, this.objectCF, HFactory.createSuperColumn(
	                        this.superName,
	                        Arrays.asList(HFactory.createColumn(this.name, new_columnvalue, timestamp, StringSerializer.get(), ByteBufferSerializer.get())),
	                        StringSerializer.get(), StringSerializer.get(), ByteBufferSerializer.get()));
				} else {
	                mutator.addInsertion(key, this.objectCF, HFactory.createColumn(this.name, new_columnvalue, timestamp, StringSerializer.get(), ByteBufferSerializer.get()));
				}
			}
			
			mutator.execute();
		} catch (Throwable e) {
			throw new DALException(e);
		}
	}
}