package com.zhuaiwa.dd.hector;

import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.Row;

public class RowWrapper<K, N, V> implements Row<K, N, V> {
    private K key;
    private ColumnSlice<N, V> columnSlice;
    public RowWrapper(K key, ColumnSlice<N, V> columnSlice) {
        this.key = key;
        this.columnSlice = columnSlice;
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public ColumnSlice<N, V> getColumnSlice() {
        return this.columnSlice;
    }

}
