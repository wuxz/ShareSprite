package com.zhuaiwa.dd.cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

import org.apache.cassandra.thrift.ConsistencyLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhuaiwa.dd.exception.DALException;

public class DeleteCommand extends Command {
    private static Logger logger = LoggerFactory.getLogger(DeleteCommand.class);
    
	private String objectCF;
	private List<String> keys = new ArrayList<String>();
    private Mutator<String> mutator;
	
	public DeleteCommand(Keyspace cassandra) {
		super(cassandra);
        mutator = HFactory.createMutator(cassandra, StringSerializer.get());
	}
	
	public DeleteCommand(Keyspace cassandra, ConsistencyLevel consistencyLevel) {
		super(cassandra, consistencyLevel);
        mutator = HFactory.createMutator(cassandra, StringSerializer.get());
	}

	public DeleteCommand Object(String objectCF) {
		this.objectCF = objectCF;
		return this;
	}
	public DeleteCommand Where(String... keys) {
		for (String key : keys) {
			if (!key.isEmpty())
				this.keys.add(key);
		}
		return this;
	}
	public DeleteCommand DeleteKey(String... keys) {
		mutator.addDeletion(Arrays.asList(keys), this.objectCF);
		return this;
	}
	public DeleteCommand DeleteColumn(String... names) {
	    for (String key : this.keys) {
	        for (String name : names) {
	            mutator.addDeletion(key, this.objectCF, name, StringSerializer.get());
	        }
	    }
		return this;
	}
	public DeleteCommand DeleteSubcolumn(String superColumnName, String... names) {
        for (String key : this.keys) {
            for (String name : names) {
                mutator.addSubDelete(key, this.objectCF, superColumnName, name, StringSerializer.get(), StringSerializer.get());
            }
        }
		return this;
	}
	public DeleteCommand DeleteColumnWithKey(String key, String... names) {
	    for (String name : names) {
	        mutator.addDeletion(key, this.objectCF, name, StringSerializer.get());
	    }
		return this;
	}
	public DeleteCommand DeleteSubcolumnWithKey(String key, String superColumnName, String... names) {
	    for (String name : names) {
	        mutator.addSubDelete(key, this.objectCF, superColumnName, name, StringSerializer.get(), StringSerializer.get());
	    }
		return this;
	}
	
	public void execute() throws DALException {
        try {
            mutator.execute();
        } catch (HectorException e) {
            throw new DALException(e);
        }
	}
}
