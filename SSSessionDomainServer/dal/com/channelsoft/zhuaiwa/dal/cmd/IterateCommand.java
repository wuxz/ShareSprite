package com.channelsoft.zhuaiwa.dal.cmd;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.KeyRange;
import org.apache.cassandra.thrift.KeySlice;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.cassandra.thrift.Cassandra.Iface;
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

@SuppressWarnings("unchecked")
public class IterateCommand extends Command implements Iterable {
    private static Logger logger = Logger.getLogger(IterateCommand.class);
    
    private ByteBuffer startKey = null;
    private ByteBuffer endKey = null;
    private ByteBuffer cursorKey = null;
    private int pageCount = 0;
    
	private ByteBuffer superColumnName;
	private List<ByteBuffer> columnNames = new ArrayList<ByteBuffer>();
	private ByteBuffer cursor = null;
	private ByteBuffer limit = null;
	private int size = 0;
	
	private Model model = null;
	
	public IterateCommand(Cassandra.Iface cassandra) {
		super(cassandra, ConsistencyLevel.ONE);
	}
	public IterateCommand(Iface cassandra, ConsistencyLevel consistencyLevel) {
		super(cassandra, consistencyLevel);
	}

	public <T> IterateCommand Object(Class<T> clazz) {
		model = ModelUtils.parser(clazz);
		return this;
	}

	public IterateCommand Select() {
		this.size = Integer.MAX_VALUE;
		return this;
	}
	public IterateCommand Select(ByteBuffer name) {
		this.columnNames.add(name);
		return this;
	}
	public IterateCommand Select(ByteBuffer parent, ByteBuffer name) {
		this.superColumnName = parent;
		this.columnNames.add(name);
		return this;
	}
	public IterateCommand Where(ByteBuffer key, int pageCount) {
		this.startKey = key;
		this.cursorKey = key;
		this.endKey = null;
		this.pageCount = pageCount;
		return this;
	}
	public IterateCommand Limit(ByteBuffer cursor, int size) {
		this.cursor = cursor;
		this.size = size;
		return this;
	}
	public IterateCommand Limit(ByteBuffer start, ByteBuffer end, int size) {
		this.cursor = start;
		this.limit = end;
		this.size = size;
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> execute() throws DALException {
		try {
			if (this.cursorKey == null)
				return new LinkedList<T>();
			
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
				
				SliceRange sliceRange = new SliceRange(start, finish, false, this.size < 0 ? -this.size : this.size);
				slicePredicate.setSlice_range(sliceRange);
			} else {
				slicePredicate.setColumn_names(new ArrayList<ByteBuffer>());
				for (ByteBuffer columnName : this.columnNames) {
					slicePredicate.getColumn_names().add(columnName);
				}
			}
			
			KeyRange keyRange = new KeyRange(this.pageCount+1);
			keyRange.setStart_key(this.cursorKey);
			keyRange.setEnd_key(this.endKey);
//			keyRange.setStart_token(this.cursorKey);
//			keyRange.setEnd_token(this.endKey);
			List<KeySlice> keyslice_list = this.cassandra.get_range_slices(
					columnParent,
					slicePredicate,
					keyRange,
					this.consistencyLevel
					);
			
			if (keyslice_list.size() == this.pageCount+1) {
				this.cursorKey = keyslice_list.remove(keyslice_list.size()-1).BufferForKey();
			} else {
				this.cursorKey = null;
			}
			
			List<T> result = new LinkedList<T>();
			for (KeySlice keyslice : keyslice_list) {
				T obj = (T)ModelUtils.ctorFromColumnOrSuperColumn(model, keyslice.getKey(), keyslice.getColumns());
				if (obj != null)
					result.add(obj);
			}
			
			return result;
		} catch (DALException e) {
			throw e;
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
	
	@SuppressWarnings("unchecked")
	@Override
	public Iterator<?> iterator() {
		return new Iterator() {
			
			private Iterator it = null;

			@Override
			public boolean hasNext() {
				if (it == null || !it.hasNext()) {
					try {
						List result = IterateCommand.this.execute();
						if (result == null || result.size() == 0)
							return false;
						it = result.iterator();
					} catch (DALException e) {
						return false;
					}
				}
				if (it != null)
					return it.hasNext();
				return false;
			}
			
			@Override
			public Object next() {
				if (it == null || !it.hasNext()) {
					try {
						List result = IterateCommand.this.execute();
						if (result == null || result.size() == 0)
							return null;
						it = result.iterator();
					} catch (DALException e) {
						return null;
					}
				}
				
				if (it == null)
					return null;
				
				return it.next();
			}
			
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
