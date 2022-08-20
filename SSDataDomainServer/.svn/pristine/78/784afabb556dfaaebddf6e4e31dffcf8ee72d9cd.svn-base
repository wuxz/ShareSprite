package com.zhuaiwa.dd.cmd;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.prettyprint.cassandra.model.AbstractSliceQuery;
import me.prettyprint.cassandra.serializers.ByteBufferSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.beans.Rows;
import me.prettyprint.hector.api.beans.SuperRow;
import me.prettyprint.hector.api.beans.SuperRows;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.MultigetSliceQuery;
import me.prettyprint.hector.api.query.MultigetSubSliceQuery;
import me.prettyprint.hector.api.query.MultigetSuperSliceQuery;
import me.prettyprint.hector.api.query.QueryResult;

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

public class ReadCommand extends Command {
    private static Logger logger = LoggerFactory.getLogger(ReadCommand.class);

	private String superColumnName;
	private List<String> columnNames = new ArrayList<String>();
	private List<String> keys = new ArrayList<String>();
	private String cursor = null;
	private String limit = null;
	private int size = 0;
	
	private Model model;
	
	public ReadCommand(Keyspace cassandra) {
		super(cassandra);
	}
	public ReadCommand(Keyspace cassandra, ConsistencyLevel consistencyLevel) {
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
	public ReadCommand Select(String name) {
		this.columnNames.add(name);
		return this;
	}
	public ReadCommand Select(String parent, String name) {
		this.superColumnName = parent;
		this.columnNames.add(name);
		return this;
	}
	public ReadCommand Where(String... keys) {
		for (String key : keys) {
			if (!key.isEmpty())
				this.keys.add(key);
		}
		return this;
	}
	public ReadCommand Limit(String cursor, int size) {
		this.cursor = cursor;
		this.size = size;
		return this;
	}
	public ReadCommand Limit(String start, String end, int size) {
		this.cursor = start;
		this.limit = end;
		this.size = size;
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <T> Map<String, T> execute() throws DALException {
	    
	    boolean issuper = this.model.getClazz().getAnnotation(ColumnFamily.class).isSuper();
	    boolean issub = (this.superColumnName != null);
	    MultigetSuperSliceQuery<String, ByteBuffer, ByteBuffer, ByteBuffer> superquery = HFactory.createMultigetSuperSliceQuery(cassandra, StringSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get());
	    MultigetSubSliceQuery<String, ByteBuffer, ByteBuffer, ByteBuffer> subquery = HFactory.createMultigetSubSliceQuery(cassandra, StringSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get());
	    MultigetSliceQuery<String, ByteBuffer, ByteBuffer> query = HFactory.createMultigetSliceQuery(cassandra, StringSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get());
	    
		try {
			String cf = this.model.getClazz().getAnnotation(ColumnFamily.class).value();
			if (cf == null || cf.isEmpty())
				cf = this.model.getClazz().getSimpleName();
			if (issuper) {
			    if (issub) {
	                subquery.setColumnFamily(cf);
	                subquery.setSuperColumn(StringSerializer.get().toByteBuffer(this.superColumnName));
	                subquery.setKeys(this.keys);
			    } else {
			        superquery.setColumnFamily(cf);
			        superquery.setKeys(this.keys);
			    }
			} else {
                query.setColumnFamily(cf);
                query.setKeys(this.keys);
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
							if (this.size > 0) {
								start = supercolumnsAnnotation.minName();
								finish = supercolumnsAnnotation.maxName();
							} else {
								start = supercolumnsAnnotation.maxName();
								finish = supercolumnsAnnotation.minName();
							}
						} else if (this.model.getModelType() == ModelType.COLUMN) {
							Columns columnsAnnotation = field.getAnnotation(Columns.class);
							assert(columnsAnnotation != null);
                            descending = columnsAnnotation.descending();
							if (this.size > 0) {
								start = columnsAnnotation.minName();
								finish = columnsAnnotation.maxName();
							} else {
								start = columnsAnnotation.maxName();
								finish = columnsAnnotation.minName();
							}
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
						start = this.cursor.getBytes(Command.encoding);
						if (this.limit != null && !this.limit.isEmpty())
							finish = this.limit.getBytes(Command.encoding);
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
			    if (issuper) {
	                if (issub) {
	                    subquery.setColumnNames(StringSerializer.get().toBytesList(this.columnNames));
	                } else {
	                    superquery.setColumnNames(StringSerializer.get().toBytesList(this.columnNames));
	                }
			    } else {
                    ((AbstractSliceQuery)query).setColumnNames(StringSerializer.get().toBytesList(this.columnNames));
                }
			}
			
            Map<String, T> result = new LinkedHashMap<String, T>();
            
			if (issuper) {
                if (issub) {
                    QueryResult<Rows<String, ByteBuffer, ByteBuffer>> queryresult = subquery.execute();
                    Rows<String, ByteBuffer, ByteBuffer> rows = (queryresult == null ? null : queryresult.get());

                    for (String key : this.keys) {
                        
                        Row<String, ByteBuffer, ByteBuffer> row = (rows == null ? null : rows.getByKey(key));
                        if (row == null) {
                            if (logger.isTraceEnabled()) {
                                logger.trace("No object with key " + key + " in " + cf);
                            }
                            continue;
                        }
                        
                        // 赋值
                        T obj = (T)ModelUtils.ctorFromColumn(model, key, row.getColumnSlice().getColumns());
                        if (obj != null) {
                            result.put(key, obj);
                        } else {
                            if (logger.isTraceEnabled()) {
                                logger.trace("No object with key " + key + " in " + cf);
                            }
                            continue;
                        }
                    }
                } else {
                    QueryResult<SuperRows<String, ByteBuffer, ByteBuffer, ByteBuffer>> queryresult = superquery.execute();
                    SuperRows<String, ByteBuffer, ByteBuffer, ByteBuffer> rows = (queryresult == null ? null : queryresult.get());

                    for (String key : this.keys) {
                        
                        SuperRow<String, ByteBuffer, ByteBuffer, ByteBuffer> row = (rows == null ? null : rows.getByKey(key));
                        if (row == null) {
                            if (logger.isTraceEnabled()) {
                                logger.trace("No object with key " + key + " in " + cf);
                            }
                            continue;
                        }
                        
                        // 赋值
                        T obj = (T)ModelUtils.ctorFromSuperColumn(model, key, row.getSuperSlice().getSuperColumns());
                        if (obj != null) {
                            result.put(key, obj);
                        } else {
                            if (logger.isTraceEnabled()) {
                                logger.trace("No object with key " + key + " in " + cf);
                            }
                            continue;
                        }
                    }
                }
            } else {
                QueryResult<Rows<String, ByteBuffer, ByteBuffer>> queryresult = query.execute();
                Rows<String, ByteBuffer, ByteBuffer> rows = (queryresult == null ? null : queryresult.get());

                for (String key : this.keys) {
                    
                    Row<String, ByteBuffer, ByteBuffer> row = (rows == null ? null : rows.getByKey(key));
                    if (row == null) {
                        if (logger.isTraceEnabled()) {
                            logger.trace("No object with key " + key + " in " + cf);
                        }
                        continue;
                    }
                    
                    // 赋值
                    T obj = (T)ModelUtils.ctorFromColumn(model, key, row.getColumnSlice().getColumns());
                    if (obj != null) {
                        result.put(key, obj);
                    } else {
                        if (logger.isTraceEnabled()) {
                            logger.trace("No object with key " + key + " in " + cf);
                        }
                        continue;
                    }
                }
            }
			
			return result;
		} catch (UnsupportedEncodingException e) {
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
