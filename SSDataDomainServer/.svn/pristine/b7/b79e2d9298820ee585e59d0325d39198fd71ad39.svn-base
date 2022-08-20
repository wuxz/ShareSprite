package com.zhuaiwa.dd.cmd;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import me.prettyprint.cassandra.serializers.ByteBufferSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.OrderedSuperRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.beans.SuperRow;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.RangeSubSlicesQuery;
import me.prettyprint.hector.api.query.RangeSuperSlicesQuery;

import org.apache.cassandra.thrift.ConsistencyLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhuaiwa.dd.annotation.ColumnFamily;
import com.zhuaiwa.dd.annotation.Columns;
import com.zhuaiwa.dd.annotation.SuperColumns;
import com.zhuaiwa.dd.exception.DALException;
import com.zhuaiwa.dd.model.Model;
import com.zhuaiwa.dd.model.Model.ModelType;
import com.zhuaiwa.dd.model.Model.ValueType;
import com.zhuaiwa.dd.model.ModelUtils;

public class IterateCommand extends Command implements Iterable {
    private static Logger logger = LoggerFactory.getLogger(IterateCommand.class);
    
    private ByteBuffer startKey = null;
    private ByteBuffer endKey = null;
    private ByteBuffer cursorKey = null;
    private int pageSize = 0;
    
	private String superColumnName;
	private List<String> columnNames = new ArrayList<String>();
	private String cursor = null;
	private String limit = null;
	private int size = 0;
	
	private Model model = null;
	
	public IterateCommand(Keyspace cassandra) {
		super(cassandra, ConsistencyLevel.QUORUM);
	}
	public IterateCommand(Keyspace cassandra, ConsistencyLevel consistencyLevel) {
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
	public IterateCommand Select(String name) {
		this.columnNames.add(name);
		return this;
	}
	public IterateCommand Select(String parent, String name) {
		this.superColumnName = parent;
		this.columnNames.add(name);
		return this;
	}
	public IterateCommand Where(String key, int pageCount) {
		this.startKey = Command.bytes(key);
		this.cursorKey = Command.bytes(key);
		this.endKey = Command.EMPTY_BYTE_BUFFER;
		this.pageSize = pageCount;
		return this;
	}
	public IterateCommand Limit(String cursor, int size) {
		this.cursor = cursor;
		this.size = size;
		return this;
	}
	public IterateCommand Limit(String start, String end, int size) {
		this.cursor = start;
		this.limit = end;
		this.size = size;
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> execute() throws DALException {

        boolean issuper = this.model.getClazz().getAnnotation(ColumnFamily.class).isSuper();
        boolean issub = (this.superColumnName != null);
        RangeSuperSlicesQuery<String, ByteBuffer, ByteBuffer, ByteBuffer> superquery = HFactory.createRangeSuperSlicesQuery(cassandra, StringSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get());
        RangeSubSlicesQuery<String, ByteBuffer, ByteBuffer, ByteBuffer> subquery = HFactory.createRangeSubSlicesQuery(cassandra, StringSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get());
        RangeSlicesQuery<String, ByteBuffer, ByteBuffer> query = HFactory.createRangeSlicesQuery(cassandra, StringSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get());
        
		try {
			if (this.cursorKey == null)
				return new LinkedList<T>();
			
			String cf = this.model.getClazz().getAnnotation(ColumnFamily.class).value();
			if (cf == null || cf.isEmpty())
				cf = this.model.getClazz().getSimpleName();
			
			if (issuper) {
                if (issub) {
                    subquery.setColumnFamily(cf);
                    subquery.setSuperColumn(StringSerializer.get().toByteBuffer(this.superColumnName));
                    subquery.setKeys(Command.string(this.cursorKey), Command.string(this.endKey));
                    subquery.setRowCount(this.pageSize+1);
                } else {
                    superquery.setColumnFamily(cf);
                    superquery.setKeys(Command.string(this.cursorKey), Command.string(this.endKey));
                    superquery.setRowCount(this.pageSize+1);
                }
            } else {
                query.setColumnFamily(cf);
                query.setKeys(Command.string(this.cursorKey), Command.string(this.endKey));
                query.setRowCount(this.pageSize+1);
            }
			
			if (this.columnNames.isEmpty()) {
				byte[] start = new byte[0];
				byte[] finish = new byte[0];
				boolean descending = false;
				if (!issub) {
					if (this.model.getValueType() == ValueType.LIST)
					{
						Field field = this.model.getListField();
						if (this.model.getModelType() == ModelType.SUPERCOLUMN) {
							SuperColumns supercolumnsAnnotation = field.getAnnotation(SuperColumns.class);
							assert(supercolumnsAnnotation != null);
							descending = supercolumnsAnnotation.descending();
							start = supercolumnsAnnotation.minName();
							finish = supercolumnsAnnotation.maxName();
						} else if (this.model.getModelType() == ModelType.COLUMN) {
							Columns columnsAnnotation = field.getAnnotation(Columns.class);
							assert(columnsAnnotation != null);
							descending = columnsAnnotation.descending();
							start = columnsAnnotation.minName();
							finish = columnsAnnotation.maxName();
						} else {
							assert(false);
						}
					}
				}
				
				if (this.cursor != null && !this.cursor.isEmpty()) {
					if (this.size > 0) {
						start = this.cursor.getBytes(Command.encoding);
						if (this.limit != null && !this.limit.isEmpty())
							finish = this.limit.getBytes(Command.encoding);
					} else {
						finish = this.cursor.getBytes(Command.encoding);
						if (this.limit != null && !this.limit.isEmpty())
							start = this.limit.getBytes(Command.encoding);
					}
				}
                
                boolean reverse = this.size < 0;
                if (descending) {
                    reverse = !reverse;
                }
                
                if (issuper) {
                    if (issub) {
                        subquery.setRange(ByteBuffer.wrap(start), ByteBuffer.wrap(finish), reverse, (this.size == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math.abs(this.size)));
                    } else {
                        superquery.setRange(ByteBuffer.wrap(start), ByteBuffer.wrap(finish), reverse, (this.size == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math.abs(this.size)));
                    }
                } else {
                    query.setRange(ByteBuffer.wrap(start), ByteBuffer.wrap(finish), reverse, (this.size == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math.abs(this.size)));
                }
			} else {
				for (String columnName : this.columnNames) {
					if (issuper) {
                        if (issub) {
                            subquery.setColumnNames(Command.bytes(columnName));
                        } else {
                            superquery.setColumnNames(Command.bytes(columnName));
                        }
                    } else {
                        query.setColumnNames(Command.bytes(columnName));
                    }
				}
			}
			

            List<T> result = new LinkedList<T>();
			
			if (issuper) {
                if (issub) {
                    QueryResult<OrderedRows<String, ByteBuffer, ByteBuffer>> queryresult = subquery.execute();
                    OrderedRows<String, ByteBuffer, ByteBuffer> rows = (queryresult == null ? null : queryresult.get());
                    List<Row<String, ByteBuffer, ByteBuffer>> row_list = (rows == null ? null : rows.getList());
                    if (row_list == null || row_list.isEmpty())
                        return result;
                    if (rows.getCount() == this.pageSize + 1) {
                        this.cursorKey = Command.bytes(row_list.remove(row_list.size()-1).getKey());
                    } else {
                        this.cursorKey = null;
                    }
                    for (Row<String, ByteBuffer, ByteBuffer> row : row_list) {
                        T obj = (T)ModelUtils.ctorFromColumn(model, row.getKey(), row.getColumnSlice().getColumns());
                        if (obj != null)
                            result.add(obj);
                    }
                } else {
                    QueryResult<OrderedSuperRows<String, ByteBuffer, ByteBuffer, ByteBuffer>> queryresult = superquery.execute();
                    OrderedSuperRows<String, ByteBuffer, ByteBuffer, ByteBuffer> rows = (queryresult == null ? null : queryresult.get());
                    List<SuperRow<String, ByteBuffer, ByteBuffer, ByteBuffer>> super_row_list = (rows == null ? null : rows.getList());
                    if (super_row_list == null || super_row_list.isEmpty())
                        return result;
                    if (rows.getCount() == this.pageSize + 1) {
                        this.cursorKey = Command.bytes(super_row_list.remove(super_row_list.size()-1).getKey());
                    } else {
                        this.cursorKey = null;
                    }
                    for (SuperRow<String, ByteBuffer, ByteBuffer, ByteBuffer> row : super_row_list) {
                        T obj = (T)ModelUtils.ctorFromSuperColumn(model, row.getKey(), row.getSuperSlice().getSuperColumns());
                        if (obj != null)
                            result.add(obj);
                    }
                }
            } else {
                QueryResult<OrderedRows<String, ByteBuffer, ByteBuffer>> queryresult = query.execute();
                OrderedRows<String, ByteBuffer, ByteBuffer> rows = (queryresult == null ? null : queryresult.get());
                List<Row<String, ByteBuffer, ByteBuffer>> row_list = (rows == null ? null : rows.getList());
                if (row_list == null || row_list.isEmpty())
                    return result;
                if (rows.getCount() == this.pageSize + 1) {
                    this.cursorKey = Command.bytes(row_list.remove(row_list.size()-1).getKey());
                } else {
                    this.cursorKey = null;
                }
                for (Row<String, ByteBuffer, ByteBuffer> row : row_list) {
                    T obj = (T)ModelUtils.ctorFromColumn(model, row.getKey(), row.getColumnSlice().getColumns());
                    if (obj != null)
                        result.add(obj);
                }
            }
			
			return result;
		} catch (DALException e) {
			throw e;
		} catch (UnsupportedEncodingException e) {
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
