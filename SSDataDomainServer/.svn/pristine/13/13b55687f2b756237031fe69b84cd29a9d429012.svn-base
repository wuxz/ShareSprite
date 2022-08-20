package com.zhuaiwa.dd.hector;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import me.prettyprint.cassandra.serializers.AbstractSerializer;
import me.prettyprint.hector.api.beans.HSuperColumn;
import me.prettyprint.hector.api.beans.SuperRow;
import me.prettyprint.hector.api.beans.SuperSlice;
import me.prettyprint.hector.api.factory.HFactory;

import com.google.common.collect.Iterators;

public class SuperTraverse<K, SN, N, V> implements Iterator<Entry<K, Iterator<HSuperColumn<SN, N, V>>>> {
    private String columnFamily;
    private K startKey;
    private K endKey;
    private int pageRow;
    private int pageColumn;
    private AbstractSerializer<K> keySerializer;
    private AbstractSerializer<SN> snameSerializer;
    private AbstractSerializer<N> nameSerializer;
    private AbstractSerializer<V> valueSerializer;
    
    SuperRowIterator<K, SN, N, V> rowIterator;
    
    public SuperTraverse(String columnFamily, K startKey, K endKey, int pageRow, int pageColumn,
            AbstractSerializer<K> keySerializer,
            AbstractSerializer<SN> snameSerializer,
            AbstractSerializer<N> nameSerializer,
            AbstractSerializer<V> valueSerializer) {
        this.columnFamily = columnFamily;
        this.startKey = startKey;
        this.endKey = endKey;
        this.pageRow = pageRow;
        this.pageColumn = pageColumn;
        this.keySerializer = keySerializer;
        this.snameSerializer = snameSerializer;
        this.nameSerializer = nameSerializer;
        this.valueSerializer = valueSerializer;
        
        rowIterator = new SuperRowIterator<K, SN, N, V>(
                HectorFactory.getKeyspace(),
                this.columnFamily,
                this.startKey,
                this.endKey,
                this.pageRow,
                this.keySerializer,
                this.snameSerializer,
                this.nameSerializer,
                this.valueSerializer);
        rowIterator.setSliceRange(null, null, false, this.pageColumn);
    }
    
    @Override
    public boolean hasNext() {
        return rowIterator.hasNext();
    }
    @Override
    public Entry<K, Iterator<HSuperColumn<SN, N, V>>> next() {
        SuperRow<K, SN, N, V> row = rowIterator.next();
        K key = row.getKey();
        SuperSlice<SN,N,V> slice = row.getSuperSlice();
        
        Iterator<HSuperColumn<SN, N, V>> columnIterator =
                (slice == null ? Iterators.<HSuperColumn<SN, N, V>>emptyIterator() : slice.getSuperColumns().iterator());
        
        if (slice != null && slice.getSuperColumns().size() >= this.pageColumn) {
            List<HSuperColumn<SN, N, V>> columns = slice.getSuperColumns();
            SN start = columns.get(columns.size()-1).getName();
            SN finish = null;
            
            SuperSliceIterator<K, SN, N, V> ssi =
                    new SuperSliceIterator<K, SN, N, V>(
                        HFactory.createSuperSliceQuery(
                                HectorFactory.getKeyspace(),
                                this.keySerializer,
                                this.snameSerializer,
                                this.nameSerializer,
                                this.valueSerializer)
                                .setColumnFamily(this.columnFamily)
                                .setKey(key),
                        start,
                        finish,
                        false,
                        this.pageColumn);
            if (ssi.hasNext()) {
                HSuperColumn<SN, N, V> first = ssi.next();
                assert(first.getName().equals(start));
            }
            columnIterator = Iterators.concat(columnIterator, ssi);
        }
        
        return new AbstractMap.SimpleEntry<K, Iterator<HSuperColumn<SN, N, V>>>(key, columnIterator);
    }
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
