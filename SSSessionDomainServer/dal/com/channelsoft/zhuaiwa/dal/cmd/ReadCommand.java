package com.channelsoft.zhuaiwa.dal.cmd;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import com.channelsoft.zhuaiwa.dal.annotation.ColumnFamily;
import com.channelsoft.zhuaiwa.dal.annotation.Columns;
import com.channelsoft.zhuaiwa.dal.annotation.SuperColumns;
import com.channelsoft.zhuaiwa.dal.exception.DALException;
import com.channelsoft.zhuaiwa.dal.model.Model;
import com.channelsoft.zhuaiwa.dal.model.ModelUtils;
import com.channelsoft.zhuaiwa.dal.model.Model.ModelType;
import com.channelsoft.zhuaiwa.dal.model.Model.ValueType;

public class ReadCommand extends Command {
    private static Logger logger = Logger.getLogger(ReadCommand.class);

	private ByteBuffer superColumnName;
	private List<ByteBuffer> columnNames = new ArrayList<ByteBuffer>();
	private List<ByteBuffer> keys = new ArrayList<ByteBuffer>();
	private ByteBuffer cursor = null;
	private ByteBuffer limit = null;
	private int size = 0;
	
	private Model model;
	
	public ReadCommand(Cassandra.Iface cassandra) {
		super(cassandra, ConsistencyLevel.ONE);
	}
	public ReadCommand(Cassandra.Iface cassandra, ConsistencyLevel consistencyLevel) {
		super(cassandra, consistencyLevel);
	}
	public <T> ReadCommand Object(Class<T> clazz) {
		model = ModelUtils.parser(clazz);
		return this;
	}
	public ReadCommand Select() {
		this.size = Integer.MAX_VALUE;
		return this;
	}
	public ReadCommand Select(ByteBuffer name) {
		this.columnNames.add(name);
		return this;
	}
	public ReadCommand Select(ByteBuffer parent, ByteBuffer name) {
		this.superColumnName = parent;
		this.columnNames.add(name);
		return this;
	}
	public ReadCommand Where(ByteBuffer... keys) {
		if (this.keys == null)
			this.keys = new ArrayList<ByteBuffer>();
		for (ByteBuffer key : keys) {
				this.keys.add(key);
		}
		return this;
	}
	public ReadCommand Where(Iterable<ByteBuffer> keys) {
		if (this.keys == null)
			this.keys = new ArrayList<ByteBuffer>();
		for (ByteBuffer key : keys) {
				this.keys.add(key);
		}
		return this;
	}
	public ReadCommand Limit(ByteBuffer cursor, int size) {
		this.cursor = cursor;
		this.size = size;
		return this;
	}
	public ReadCommand Limit(ByteBuffer start, ByteBuffer end, int size) {
		this.cursor = start;
		this.limit = end;
		this.size = size;
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <T> Map<ByteBuffer, T> execute() throws DALException {
		try {
			String cf = this.model.getClazz().getAnnotation(ColumnFamily.class).value();
			if (cf == null || cf.isEmpty())
				cf = this.model.getClazz().getSimpleName();
			
			ColumnParent columnParent = new ColumnParent(cf);
			if (this.superColumnName != null) {
				columnParent.setSuper_column(this.superColumnName);
			}
			
			SlicePredicate slicePredicate = new SlicePredicate();
			if (this.columnNames.isEmpty()) {
				ByteBuffer start = ByteBuffer.allocate(0);
				ByteBuffer finish = ByteBuffer.allocate(0);
				if (!columnParent.isSetSuper_column()) {
					if (this.model.getValueType() == ValueType.LIST)
					{
						Field field = this.model.getListField();
						if (this.model.getModelType() == ModelType.SUPERCOLUMN) {
							SuperColumns supercolumnsAnnotation = field.getAnnotation(SuperColumns.class);
							assert(supercolumnsAnnotation != null);
							start = ByteBuffer.wrap(supercolumnsAnnotation.minName());
							finish = ByteBuffer.wrap(supercolumnsAnnotation.maxName());
						} else if (this.model.getModelType() == ModelType.COLUMN) {
							Columns columnsAnnotation = field.getAnnotation(Columns.class);
							assert(columnsAnnotation != null);
							start = ByteBuffer.wrap(columnsAnnotation.minName());
							finish = ByteBuffer.wrap(columnsAnnotation.maxName());
						} else {
							assert(false);
						}
					}
				}
				
				if (this.cursor != null) {
					if (this.size > 0) {
						start = this.cursor;
						if (this.limit != null)
							finish = this.limit;
					} else {
						finish = this.cursor;
						if (this.limit != null)
							start = this.limit;
					}
				}
				
//				boolean reverse = this.size < 0;
				SliceRange sliceRange = new SliceRange(start, finish, false, this.size < 0 ? -this.size : this.size);
				slicePredicate.setSlice_range(sliceRange);
			} else {
				slicePredicate.setColumn_names(new ArrayList<ByteBuffer>());
				for (ByteBuffer columnName : this.columnNames) {
					slicePredicate.getColumn_names().add(columnName);
				}
			}

			Map<ByteBuffer, List<ColumnOrSuperColumn>> columnsMap = this.cassandra.multiget_slice(
					this.keys,
					columnParent,
					slicePredicate,
					this.consistencyLevel
					);
			Map<ByteBuffer, T> result = new LinkedHashMap<ByteBuffer, T>();
			for (ByteBuffer key : this.keys) {
				
				List<ColumnOrSuperColumn> columns = columnsMap.get(key);
				if (columns == null) {
					logger.warn("No object with key " + key + " in " + columnParent.getColumn_family());
					continue;
				}
				
				// 赋值
				T obj = (T)ModelUtils.ctorFromColumnOrSuperColumn(this.model, key, columns);
				if (obj != null) {
					logger.info("Succeed to get " + key + " from " + columnParent.getColumn_family());
					result.put(key, obj);
				} else {
					logger.warn("No object with key " + key + " in " + columnParent.getColumn_family());
					continue;
				}
			}
			if (result == null)
				result = new LinkedHashMap<ByteBuffer, T>();
			return result;
		} catch (InvalidRequestException e) {
			throw new DALException(e);
		} catch (UnavailableException e) {
			throw new DALException(e);
		} catch (TimedOutException e) {
			throw new DALException(e);
		} catch (TException e) {
			throw new DALException(e);
		} catch (IllegalArgumentException e) {
			throw new DALException(e);
		} catch (DALException e) {
			throw e;
		} catch (Throwable e) {
			throw new DALException(e);
		}
	}
}
