package com.channelsoft.zhuaiwa.dal.cmd;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;

import com.channelsoft.zhuaiwa.dal.annotation.ColumnFamily;
import com.channelsoft.zhuaiwa.dal.exception.DALException;

public class CreateCommand extends Command {
	private String objectCF;
	private List<ByteBuffer> keys = new ArrayList<ByteBuffer>();
	private List<ColumnOrSuperColumn> columns = new ArrayList<ColumnOrSuperColumn>();
	private Map<ByteBuffer, Map<ByteBuffer,ColumnOrSuperColumn>> columnsMap = new LinkedHashMap<ByteBuffer, Map<ByteBuffer, ColumnOrSuperColumn>>();
	
	public CreateCommand(Cassandra.Iface cassandra) {
		super(cassandra);
	}
	
	public CreateCommand(Cassandra.Iface cassandra, ConsistencyLevel consistencyLevel) {
		super(cassandra, consistencyLevel);
	}

	public CreateCommand Object(String objectCF) {
		this.objectCF = objectCF;
		return this;
	}
	
	public CreateCommand Object(Class<?> clazz) {
		ColumnFamily cf = clazz.getAnnotation(ColumnFamily.class);
		this.objectCF = cf.value();
		return this;
	}
	
	public CreateCommand Where(ByteBuffer... keys) {
		if (this.keys == null)
			this.keys = new ArrayList<ByteBuffer>();
		for (ByteBuffer key : keys) {
			this.keys.add(key);
		}
		return this;
	}
	public CreateCommand InsertWithKey(ByteBuffer key, ByteBuffer name, Object value) {
		Map<ByteBuffer, ColumnOrSuperColumn> coscMap = this.columnsMap.get(key);
		if (coscMap == null) {
			coscMap = new LinkedHashMap<ByteBuffer, ColumnOrSuperColumn>();
			this.columnsMap.put(key, coscMap);
		}
		
		ColumnOrSuperColumn cosc = coscMap.get(name);
		if (cosc == null) {
			cosc = new ColumnOrSuperColumn();
			coscMap.put(name, cosc);
		}
		
		Column column = new Column();
		column.setName(name);
		column.setValue(Object2ByteArray(value));
		column.setTimestamp(System.currentTimeMillis());
		cosc.setColumn(column);
		return this;
	}
	public CreateCommand InsertWithKey(ByteBuffer key, ByteBuffer name, Object value, long timestamp) {
			Map<ByteBuffer, ColumnOrSuperColumn> coscMap = this.columnsMap.get(key);
			if (coscMap == null) {
				coscMap = new LinkedHashMap<ByteBuffer, ColumnOrSuperColumn>();
				this.columnsMap.put(key, coscMap);
			}
			
			ColumnOrSuperColumn cosc = coscMap.get(name);
			if (cosc == null) {
				cosc = new ColumnOrSuperColumn();
				coscMap.put(name, cosc);
			}
			
			Column column = new Column();
			column.setName(name);
			column.setValue(Object2ByteArray(value));
			column.setTimestamp(timestamp);
			cosc.setColumn(column);
		return this;
	}
	
	public CreateCommand InsertWithKey(ByteBuffer key, ByteBuffer supername, ByteBuffer name, Object value) {
			Map<ByteBuffer, ColumnOrSuperColumn> coscMap = this.columnsMap.get(key);
			if (coscMap == null) {
				coscMap = new LinkedHashMap<ByteBuffer, ColumnOrSuperColumn>();
				this.columnsMap.put(key, coscMap);
			}
			ColumnOrSuperColumn cosc = coscMap.get(supername);
			if (cosc == null) {
				cosc = new ColumnOrSuperColumn();
				coscMap.put(supername, cosc);
			}
			
			Column column = new Column();
			column.setName(name);
			column.setValue(Object2ByteArray(value));
			column.setTimestamp(System.currentTimeMillis());
			
			SuperColumn sc = null;
			if (cosc.isSetSuper_column()
					&& supername.compareTo(ByteBuffer.wrap(cosc.getSuper_column().getName())) == 0) {
				sc = cosc.getSuper_column();
			}
			if (sc == null) {
				sc = new SuperColumn();
				sc.setName(name);
				cosc.setSuper_column(sc);
			}
			
			if (sc.getColumns() == null)
				sc.setColumns(new ArrayList<Column>());
			sc.getColumns().add(column);
		return this;
	}
	
	public CreateCommand Insert(ByteBuffer name, Object value) {
			Column column = new Column();
			column.setName(name);
			column.setValue(Object2ByteArray(value));
			column.setTimestamp(System.currentTimeMillis());
			
			ColumnOrSuperColumn cosc = new ColumnOrSuperColumn();
			cosc.setColumn(column);
			
			this.columns.add(cosc);
		return this;
	}
	
	public CreateCommand Insert(ByteBuffer supername, ByteBuffer name, Object value) {
			Column column = new Column();
			column.setName(name);
			column.setValue(Object2ByteArray(value));
			column.setTimestamp(System.currentTimeMillis());
			
			ColumnOrSuperColumn cosc = null;
			SuperColumn sc = null;
			for (ColumnOrSuperColumn cosct : this.columns) {
				if (cosct.isSetSuper_column()
						&& supername.compareTo(ByteBuffer.wrap(cosct.getSuper_column().getName())) == 0) {
					sc = cosct.getSuper_column();
					cosc = cosct;
				}
			}
			if (sc == null) {
				sc = new SuperColumn();
				sc.setName(supername);
			}
			if (sc.getColumns() == null)
				sc.setColumns(new ArrayList<Column>());
			sc.getColumns().add(column);
			
			if (cosc == null) {
				cosc = new ColumnOrSuperColumn();
				this.columns.add(cosc);
			}
			cosc.setSuper_column(sc);
		return this;
	}
	
	public void execute() throws DALException {
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
					cf_mutation_map.put(objectCF, mutations);
				}
				for (ColumnOrSuperColumn cosc : this.columns) {
					Mutation mutation = new Mutation();
					mutation.setColumn_or_supercolumn(cosc);
					mutations.add(mutation);
				}
			}
		}
		for (ByteBuffer key : this.columnsMap.keySet()) {
			Map<String,List<Mutation>> cf_mutation_map = key_mutation_map.get(key);
			if (cf_mutation_map == null) {
				cf_mutation_map = new LinkedHashMap<String, List<Mutation>>();
				key_mutation_map.put(key, cf_mutation_map);
			}
			if (this.objectCF != null) {
				List<Mutation> mutations = cf_mutation_map.get(objectCF);
				if (mutations == null) {
					mutations = new ArrayList<Mutation>();
					cf_mutation_map.put(objectCF, mutations);
				}
				Map<ByteBuffer, ColumnOrSuperColumn> coscMap = this.columnsMap.get(key);
				for (ByteBuffer columnName : coscMap.keySet()) {
					ColumnOrSuperColumn cosc = coscMap.get(columnName);
					Mutation mutation = new Mutation();
					mutation.setColumn_or_supercolumn(cosc);
					mutations.add(mutation);
				}
			}
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
