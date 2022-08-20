package com.zhuaiwa.dd.hector;

import java.util.Iterator;

import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.query.SliceQuery;

public class SliceIterator<K, N, V> implements Iterator<HColumn<N, V>> {

    private SliceQuery<K, N, V> query;
    private Iterator<HColumn<N, V>> iterator;
    private N start;
    private SliceFinish<N> finish;
    private boolean reversed;
    private int count = 100;
    private int columns = 0;
    private int total = -1;

    /**
     * Constructor
     *
     * @param query Base SliceQuery to execute
     * @param start Starting point of the range
     * @param finish Finish point of the range.
     * @param reversed  Whether or not the columns should be reversed
     */
    public SliceIterator(SliceQuery<K, N, V> query, N start, final N finish, boolean reversed, int count) {
        this(query, start, new SliceFinish<N>() {

            @Override
            public N function() {
                return finish;
            }
        }, reversed, count);
    }

    /**
     * Constructor
     *
     * @param query Base SliceQuery to execute
     * @param start Starting point of the range
     * @param finish Finish point of the range.  Allows for a dynamically determined point
     * @param reversed  Whether or not the columns should be reversed
     */
    public SliceIterator(SliceQuery<K, N, V> query, N start, SliceFinish<N> finish, boolean reversed, int count) {
        this.query = query;
        this.start = start;
        this.finish = finish;
        this.reversed = reversed;
        this.count = count;

        this.query.setRange(this.start, this.finish.function(), this.reversed, this.count);
    }
    
    public void setMaxCount(int count) {
        this.total = count;
    }
    public int getMaxCount() {
        return this.total;
    }

    @Override
    public boolean hasNext() {
        if (iterator == null) {
            iterator = query.execute().get().getColumns().iterator();
        } else if (!iterator.hasNext() && (columns % count) == 0) {  // only need to do another query if maximum columns were retrieved
            if (total > 0 && columns >= total)
                return false;
            query.setRange(start, finish.function(), reversed, count);
            iterator = query.execute().get().getColumns().iterator();

            // First element is start which was the last element on the previous query result - skip it
            if (iterator.hasNext()) {
                next();
            }
        }
        if (total > 0 && columns >= total)
            return false;

        return iterator.hasNext();
    }

    @Override
    public HColumn<N, V> next() {
        HColumn<N, V> column = iterator.next();
        start = column.getName();
        columns++;

        return column;
    }

    @Override
    public void remove() {
        iterator.remove();
    }

    /**
     * When iterating over a ColumnSlice, it may be desirable to move the finish point for each query.  This interface
     * allows for a user defined function which will return the new finish point.  This is especially useful for column
     * families which have a TimeUUID as the column name.
     */
    public interface SliceFinish<SN> {

        /**
         * Generic function for deriving a new finish point.
         * 
         * @return New finish point
         */
        SN function();
    }
}
