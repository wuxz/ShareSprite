package com.zhuaiwa.dd.hector;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import me.prettyprint.cassandra.serializers.AbstractSerializer;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.factory.HFactory;

import com.google.common.collect.Iterators;

public class Traverse<K, N, V> implements Iterator<Entry<K, Iterator<HColumn<N, V>>>> {
    private String columnFamily;
    private K startKey;
    private K endKey;
    private int pageRow;
    private int pageColumn;
    private AbstractSerializer<K> keySerializer;
    private AbstractSerializer<N> nameSerializer;
    private AbstractSerializer<V> valueSerializer;
    
    RowIterator<K, N, V> rowIterator;
    
    public Traverse(String columnFamily, K startKey, K endKey, int pageRow, int pageColumn,
            AbstractSerializer<K> keySerializer,
            AbstractSerializer<N> nameSerializer,
            AbstractSerializer<V> valueSerializer) {
        this.columnFamily = columnFamily;
        this.startKey = startKey;
        this.endKey = endKey;
        this.pageRow = pageRow;
        this.pageColumn = pageColumn;
        this.keySerializer = keySerializer;
        this.nameSerializer = nameSerializer;
        this.valueSerializer = valueSerializer;
        
        rowIterator = new RowIterator<K, N, V>(
                HectorFactory.getKeyspace(),
                this.columnFamily,
                this.startKey,
                this.endKey,
                this.pageRow,
                this.keySerializer,
                this.nameSerializer,
                this.valueSerializer);
        rowIterator.setSliceRange(null, null, false, this.pageColumn);
    }
    
    @Override
    public boolean hasNext() {
        return rowIterator.hasNext();
    }
    @Override
    public Entry<K, Iterator<HColumn<N, V>>> next() {
        Row<K, N, V> row = rowIterator.next();
        K key = row.getKey();
        ColumnSlice<N,V> slice = row.getColumnSlice();
        
        Iterator<HColumn<N, V>> columnIterator =
                (slice == null ? Iterators.<HColumn<N, V>>emptyIterator() : slice.getColumns().iterator());
        
        if (slice != null && slice.getColumns().size() >= this.pageColumn) {
            List<HColumn<N, V>> columns = slice.getColumns();
            N start = columns.get(columns.size()-1).getName();
            N finish = null;
            
            SliceIterator<K, N, V> ssi =
                    new SliceIterator<K, N, V>(
                        HFactory.createSliceQuery(
                                HectorFactory.getKeyspace(),
                                this.keySerializer,
                                this.nameSerializer,
                                this.valueSerializer)
                                .setColumnFamily(this.columnFamily)
                                .setKey(key),
                        start,
                        finish,
                        false,
                        this.pageColumn);
            if (ssi.hasNext()) {
                HColumn<N, V> first = ssi.next();
                assert(first.getName().equals(start));
            }
            columnIterator = Iterators.concat(columnIterator, ssi);
        }
        
        return new AbstractMap.SimpleEntry<K, Iterator<HColumn<N, V>>>(key, columnIterator);
    }
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
