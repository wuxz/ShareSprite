package com.zhuaiwa.dd.cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.serializers.TypeInferringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.HSuperColumn;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

import org.apache.cassandra.thrift.ConsistencyLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhuaiwa.dd.exception.DALException;

public class CreateCommand extends Command {
	private static Logger logger = LoggerFactory.getLogger(CreateCommand.class);
	
	private String objectCF;
	private List<String> keys = new ArrayList<String>();
	private List<HColumn<String, Object>> columns = new ArrayList<HColumn<String, Object>>();
    private List<HSuperColumn<String, String, Object>> supercolumns = new ArrayList<HSuperColumn<String, String, Object>>();
	
	private Mutator<String> mutator;
	
	public CreateCommand(Keyspace cassandra) {
		super(cassandra);
		mutator = HFactory.createMutator(cassandra, StringSerializer.get());
	}
	
	public CreateCommand(Keyspace cassandra, ConsistencyLevel consistencyLevel) {
		super(cassandra, consistencyLevel);
		mutator = HFactory.createMutator(cassandra, StringSerializer.get());
	}

	public CreateCommand Object(String objectCF) {
		this.objectCF = objectCF;
		return this;
	}
	public CreateCommand Where(String... keys) {
		if (this.keys == null)
			this.keys = new ArrayList<String>();
		for (String key : keys) {
			if (!key.isEmpty())
				this.keys.add(key);
		}
		return this;
	}
	
	public CreateCommand Insert(String name, Object value) {
	    this.columns.add(HFactory.createColumn(name, value, StringSerializer.get(), TypeInferringSerializer.get()));
		return this;
	}
	
	@SuppressWarnings("unchecked")
    public CreateCommand Insert(String supername, String name, Object value) {
	    this.supercolumns.add(HFactory.createSuperColumn(
	            supername, 
	            Arrays.asList(HFactory.createColumn(name, value, StringSerializer.get(), TypeInferringSerializer.get())),
	            StringSerializer.get(),
	            StringSerializer.get(),
	            TypeInferringSerializer.get()));
		return this;
	}
	
	public void execute() throws DALException {
	    for (HColumn<String, Object> column : columns) {
	        for (String key : this.keys) {
	            mutator.addInsertion(key, this.objectCF, column);
	        }
	    }
        for (HSuperColumn<String, String, Object> supercolumn : supercolumns) {
            for (String key : this.keys) {
                mutator.addInsertion(key, this.objectCF, supercolumn);
            }
        }
        try {
            mutator.execute();
        } catch (HectorException e) {
            throw new DALException(e);
        }
	}
}
