package com.zhuaiwa.dd.hector;

import java.util.Iterator;

import me.prettyprint.cassandra.model.RowsImpl;
import me.prettyprint.cassandra.serializers.AbstractSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;

public class RowIterator<K, N, V> implements Iterable<Row<K, N, V>>, Iterator<Row<K, N, V>> {
    private Iterator<Row<K, N, V>> rowsIterator = null;

    private RangeSlicesQuery<K, N, V> query = null;

    private K startKey = null;
    private K endKey = null;
    private int pageSize;
    
    AbstractSerializer<K> keySerializer;
    AbstractSerializer<N> nameSerializer;
    AbstractSerializer<V> valueSerializer;
    
    private Row<K, N, V> lastReadRow = null;
    private Row<K, N, V> nextRow = null;
    
    protected final static Row<?,?,?> endOfRow = (Row<?,?,?>)new RowWrapper(null, null);
    public static boolean isEndOfRow(Row<?,?,?> row) {
        return row == endOfRow;
    }

    public RowIterator(Keyspace keyspace, String columnFamily, K startKey, K endKey, int pageSize, AbstractSerializer<K> keySerializer,
            AbstractSerializer<N> nameSerializer, AbstractSerializer<V> valueSerializer) {
        this.startKey = startKey;
        this.endKey = endKey;
        this.pageSize = pageSize;
        
        this.keySerializer = keySerializer;
        this.nameSerializer = nameSerializer;
        this.valueSerializer = valueSerializer;
        
        this.query = HFactory.createRangeSlicesQuery(keyspace, keySerializer, nameSerializer, valueSerializer);
        this.query.setColumnFamily(columnFamily);
        this.query.setRange(null, null, false, Integer.MAX_VALUE);
        this.query.setRowCount(this.pageSize);
    }
    
    public void setSliceRange(N start, N finish, boolean reversed, int count) {
        this.query.setRange(start, finish, reversed, count);
    }

    private void runQuery(K start) {
        query.setKeys(start, this.endKey);

        QueryResult<OrderedRows<K, N, V>> result = query.execute();
        OrderedRows<K, N, V> rows = (result != null) ? result.get() : null;
        rowsIterator = (rows != null) ? rows.iterator() : null;

        if (lastReadRow != null && rowsIterator != null && rowsIterator.hasNext()) {
            rowsIterator.next();
        }
    }

    @Override
    public Iterator<Row<K, N, V>> iterator() {
        return this;
    }
    
    @SuppressWarnings("unchecked")
    private void computeNextRow() {
        if (isEndOfRow(nextRow))
            return;
        
        if (lastReadRow == null) { // 第一次调用computeNext
            runQuery(this.startKey);
        }
        
        if (rowsIterator == null) {
            nextRow = (Row<K, N, V>) endOfRow;
            return;
        }
        
        while (rowsIterator.hasNext()) {
            lastReadRow = rowsIterator.next();
            if (!lastReadRow.getColumnSlice().getColumns().isEmpty()) {
                nextRow = lastReadRow;
                return;
            }
        }
        
        if (!rowsIterator.hasNext() && lastReadRow != null) {
            runQuery(lastReadRow.getKey());
            computeNextRow();
            return;
        }
        
        nextRow = (Row<K, N, V>) endOfRow;
    }

    @Override
    public boolean hasNext() {
        if (nextRow == null)
            computeNextRow();
        return !isEndOfRow(nextRow);
    }

    @Override
    public Row<K, N, V> next() {
        if (!hasNext())
            return null;
        Row<K, N, V> row = this.nextRow;
        this.nextRow = null;
        return row;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
