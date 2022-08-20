package com.channelsoft.zhuaiwa.dal.cmd;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Deletion;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;

import com.channelsoft.zhuaiwa.dal.exception.DALException;

public class DeleteCommand extends Command {
	private String objectCF;
	private List<ByteBuffer> keys = new ArrayList<ByteBuffer>();
	private List<Deletion> deletionList = new ArrayList<Deletion>();
	private Map<ByteBuffer, Deletion> deletionMap = new LinkedHashMap<ByteBuffer, Deletion>();
	
	public DeleteCommand(Cassandra.Iface cassandra) {
		super(cassandra);
	}
	
	public DeleteCommand(Cassandra.Iface cassandra, ConsistencyLevel consistencyLevel) {
		super(cassandra, consistencyLevel);
	}

	public DeleteCommand Object(String objectCF) {
		this.objectCF = objectCF;
		return this;
	}
	public DeleteCommand Where(ByteBuffer... keys) {
		if (this.keys == null)
			this.keys = new ArrayList<ByteBuffer>();
		for (ByteBuffer key : keys) {
				this.keys.add(key);
		}
		return this;
	}
	public DeleteCommand DeleteKey(ByteBuffer... keys) {
		for (ByteBuffer key : keys) {
			Deletion deletion = new Deletion();
			deletion.setTimestamp(System.currentTimeMillis());
			this.deletionMap.put(key, deletion);
		}
		return this;
	}
	public DeleteCommand DeleteColumn(ByteBuffer... names) {
			Deletion deletion = new Deletion();
			deletion.setTimestamp(System.currentTimeMillis());
			SlicePredicate slicePredicate = new SlicePredicate();
			List<ByteBuffer> columnNames = new ArrayList<ByteBuffer>();
			for (ByteBuffer name : names) {
				columnNames.add(name);
			}
			slicePredicate.setColumn_names(columnNames);
			deletion.setPredicate(slicePredicate);
			this.deletionList.add(deletion);
		return this;
	}
	public DeleteCommand DeleteSubcolumn(ByteBuffer superColumnName, ByteBuffer... names) {
			Deletion deletion = new Deletion();
			deletion.setTimestamp(System.currentTimeMillis());
			deletion.setSuper_column(superColumnName);
			
			SlicePredicate slicePredicate = new SlicePredicate();
			List<ByteBuffer> columnNames = new ArrayList<ByteBuffer>();
			for (ByteBuffer name : names) {
				columnNames.add(name);
			}
			slicePredicate.setColumn_names(columnNames);
			deletion.setPredicate(slicePredicate);
			this.deletionList.add(deletion);
		return this;
	}
	public DeleteCommand DeleteColumnWithKey(ByteBuffer key, ByteBuffer... names) {
			Deletion deletion = new Deletion();
			deletion.setTimestamp(System.currentTimeMillis());
			SlicePredicate slicePredicate = new SlicePredicate();
			List<ByteBuffer> columnNames = new ArrayList<ByteBuffer>();
			for (ByteBuffer name : names) {
				columnNames.add(name);
			}
			slicePredicate.setColumn_names(columnNames);
			deletion.setPredicate(slicePredicate);
			this.deletionMap.put(key, deletion);
		return this;
	}
	public DeleteCommand DeleteSubcolumnWithKey(ByteBuffer key, ByteBuffer superColumnName, ByteBuffer... names) {
			Deletion deletion = new Deletion();
			deletion.setTimestamp(System.currentTimeMillis());
			deletion.setSuper_column(superColumnName);
			
			SlicePredicate slicePredicate = new SlicePredicate();
			List<ByteBuffer> columnNames = new ArrayList<ByteBuffer>();
			for (ByteBuffer name : names) {
				columnNames.add(name);
			}
			slicePredicate.setColumn_names(columnNames);
			deletion.setPredicate(slicePredicate);
			this.deletionMap.put(key, deletion);
		return this;
	}
	
	public void execute() throws DALException {
		Map<ByteBuffer,Map<String,List<Mutation>>> key_mutation_map = new LinkedHashMap<ByteBuffer, Map<String,List<Mutation>>>();
		for (ByteBuffer key : this.keys) {
			Map<String,List<Mutation>> cf_mutation_map = key_mutation_map.get(key);
			if (cf_mutation_map == null)
				cf_mutation_map = new LinkedHashMap<String, List<Mutation>>();
			if (this.objectCF != null) {
				List<Mutation> mutations = cf_mutation_map.get(objectCF);
				if (mutations == null)
					mutations = new ArrayList<Mutation>();
				for (Deletion deletion : this.deletionList) {
					Mutation mutation = new Mutation();
					mutation.setDeletion(deletion);
					mutations.add(mutation);
				}
				cf_mutation_map.put(objectCF, mutations);
			}
			key_mutation_map.put(key, cf_mutation_map);
		}
		for (ByteBuffer key : this.deletionMap.keySet()) {
			Map<String,List<Mutation>> cf_mutation_map = key_mutation_map.get(key);
			if (cf_mutation_map == null)
				cf_mutation_map = new LinkedHashMap<String, List<Mutation>>();
			if (this.objectCF != null) {
				List<Mutation> mutations = cf_mutation_map.get(objectCF);
				if (mutations == null)
					mutations = new ArrayList<Mutation>();
				Deletion deletion = this.deletionMap.get(key);
				Mutation mutation = new Mutation();
				mutation.setDeletion(deletion);
				mutations.add(mutation);
				cf_mutation_map.put(objectCF, mutations);
			}
			key_mutation_map.put(key, cf_mutation_map);
		}
		try {
			cassandra.batch_mutate(
					key_mutation_map,
					this.consistencyLevel
					);
		} catch (InvalidRequestException e) {
			throw new DALException(e);
		} catch (UnavailableException e) {
			throw new DALException(e);
		} catch (TimedOutException e) {
			throw new DALException(e);
		} catch (TException e) {
			throw new DALException(e);
		} catch (Throwable e) {
			throw new DALException(e);
		}
	}
}
