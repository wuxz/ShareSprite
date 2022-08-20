package com.channelsoft.zhuaiwa.dal.cmd;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Deletion;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;

import com.channelsoft.zhuaiwa.dal.exception.DALException;

public class UpdateCommand extends Command {
	static public interface UpdateOperator {
		byte[] handle(ByteBuffer key, byte[] supername, byte[] name, byte[] value);
	}
	
	private String objectCF;
	private List<ByteBuffer> keys = new ArrayList<ByteBuffer>();
	private Map<ColumnPath, UpdateOperator> columns = new LinkedHashMap<ColumnPath, UpdateOperator>();
	
	public UpdateCommand(Cassandra.Iface cassandra) {
		super(cassandra);
	}
	
	public UpdateCommand(Cassandra.Iface cassandra, ConsistencyLevel consistencyLevel) {
		super(cassandra, consistencyLevel);
	}

	public UpdateCommand Object(String objectCF) {
		this.objectCF = objectCF;
		return this;
	}
	
	public UpdateCommand Where(ByteBuffer... keys) {
		if (this.keys == null)
			this.keys = new ArrayList<ByteBuffer>();
		for (ByteBuffer key : keys) {
				this.keys.add(key);
		}
		return this;
	}
	
	public UpdateCommand Update(ByteBuffer name, UpdateOperator operator) {
			ColumnPath columnPath = new ColumnPath();
			columnPath.setColumn_family(objectCF);
			columnPath.setColumn(name);
			
			this.columns.put(columnPath, operator);
		return this;
	}
	
	public UpdateCommand Update(ByteBuffer supername, ByteBuffer name, UpdateOperator operator) {
			ColumnPath columnPath = new ColumnPath();
			columnPath.setColumn_family(objectCF);
			columnPath.setSuper_column(supername);
			columnPath.setColumn(name);
						
			this.columns.put(columnPath, operator);
		return this;
	}
	
	public void execute() throws DALException {
		try {
			Map<ByteBuffer,Map<String,List<Mutation>>> key_mutation_map = new LinkedHashMap<ByteBuffer, Map<String,List<Mutation>>>();
			for (ByteBuffer key : this.keys) {
				Map<String,List<Mutation>> cf_mutation_map = key_mutation_map.get(key);
				if (cf_mutation_map == null) {
					cf_mutation_map = new LinkedHashMap<String, List<Mutation>>();
					key_mutation_map.put(key, cf_mutation_map);
				}
				if (this.objectCF != null) {
					List<Mutation> mutations = cf_mutation_map.get(objectCF);
					if (mutations == null) {
						mutations = new ArrayList<Mutation>();
						cf_mutation_map.put(this.objectCF, mutations);
					}
					
					for (Entry<ColumnPath, UpdateOperator> entry : this.columns.entrySet()) {
						ColumnPath columnPath = entry.getKey();
						UpdateOperator operator = entry.getValue();
						
						ColumnOrSuperColumn cosc = null;
						try {
							cosc = this.cassandra.get(
									key,
									columnPath,
									this.consistencyLevel
									);
						} catch (NotFoundException e) {
							// ignore
						}
						byte[] supername = null;
						byte[] columnname = null;
						byte[] columnvalue = null;
						long timestamp = -1;
						
						if (cosc != null) {					
							if (cosc.isSetSuper_column()) {
								SuperColumn sc = cosc.getSuper_column();
								supername = sc.getName();
								if (sc.getColumnsSize() != 0) {
									Column column = sc.getColumns().get(0);
									columnname = column.getName();
									columnvalue = column.getValue();
									timestamp = column.getTimestamp();
								}
							} else if (cosc.isSetColumn()) {
								Column column = cosc.getColumn();
								columnname = column.getName();
								columnvalue = column.getValue();
								timestamp = column.getTimestamp();
							}
						}
						if (supername == null && columnPath.isSetSuper_column())
							supername = columnPath.getSuper_column();
						if (columnname == null && columnPath.isSetColumn())
							columnname = columnPath.getColumn();
	
						if (timestamp == -1) {
							timestamp = System.currentTimeMillis();
						} else {
							timestamp += 1;
						}
						
						byte[] new_columnvalue = operator.handle(
								key,
								supername,
								columnname,
								columnvalue);

						Mutation mutation = new Mutation();
						mutations.add(mutation);
						if (new_columnvalue == null) {
							
							Deletion deletion = new Deletion();
							deletion.setTimestamp(timestamp);
							if (supername != null)
								deletion.setSuper_column(supername);
							SlicePredicate slicePredicate = new SlicePredicate();
							slicePredicate.addToColumn_names(ByteBuffer.wrap(columnname));
							deletion.setPredicate(slicePredicate);
							
							mutation.setDeletion(deletion);
							
						} else {
							
							ColumnOrSuperColumn new_cosc = new ColumnOrSuperColumn();
							Column c = new Column();
							c.setName(columnname);
							c.setValue(new_columnvalue);
							c.setTimestamp(timestamp);
							if (supername != null) {
								SuperColumn sc = new SuperColumn();
								sc.setColumns(Arrays.asList(c));
								new_cosc.setSuper_column(sc);
							} else {
								new_cosc.setColumn(c);
							}
							
							mutation.setColumn_or_supercolumn(new_cosc);
						}
					}
				}
			}
			
			this.cassandra.batch_mutate(
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