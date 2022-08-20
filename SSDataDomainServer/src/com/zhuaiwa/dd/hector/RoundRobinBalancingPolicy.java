package com.zhuaiwa.dd.hector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import me.prettyprint.cassandra.connection.ConcurrentHClientPool;
import me.prettyprint.cassandra.connection.HClientPool;
import me.prettyprint.cassandra.connection.LoadBalancingPolicy;
import me.prettyprint.cassandra.connection.factory.HClientFactory;
import me.prettyprint.cassandra.service.CassandraHost;
import me.prettyprint.hector.api.exceptions.HTimedOutException;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Implements a RoundRobin balancing policy based off the contents of the active
 * {@link HClientPool}. If a pool is shutdown by another thread in the midst of
 * the selection process, we return the pool at position 0
 * 
 * @author zznate
 */
public class RoundRobinBalancingPolicy implements LoadBalancingPolicy {
    private static final long serialVersionUID = -9148768611757767017L;
    private int counter;

    public RoundRobinBalancingPolicy() {
        counter = 0;
    }

    @Override
    public HClientPool getPool(Collection<HClientPool> pools, Set<CassandraHost> excludeHosts) {
        
        long start = System.currentTimeMillis();

        HClientPool pool = null;
        while (pool == null) {
            ArrayList activePools = new ArrayList<HClientPool>();
            for (HClientPool activePool : Iterables.filter(pools, new Predicate<HClientPool>() {
                @Override
                public boolean apply(HClientPool pool) {
                    return pool.getIsActive();
                }
            })) {
                activePools.add(activePool);
            }
            
            pool = getPoolSafely(activePools);
    
            if (excludeHosts != null && excludeHosts.size() > 0) {
                while (excludeHosts.contains(pool.getCassandraHost())) {
                    pool = getPoolSafely(activePools);
                    if (excludeHosts.size() >= activePools.size())
                        break;
                }
            }
            
            if (pool == null) {
                long end = System.currentTimeMillis();
                if (end - start > 10000) {
                    throw new HTimedOutException("No active pool.");
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
        }

        return pool;
    }

    private HClientPool getPoolSafely(Collection<HClientPool> pools) {
        if (pools.size() == 0)
            return null;
        try {
            return Iterables.get(pools, getAndIncrement(pools.size()));
        } catch (IndexOutOfBoundsException e) {
            return pools.iterator().next();
        }
    }

    private int getAndIncrement(int size) {
        int counterToReturn;

        // There should not be that much of contention here as
        // the "if" statement plus the increment is executed real fast.
        synchronized (this) {
            if (counter >= 16384) {
                counter = 0;
            }
            counterToReturn = counter++;
        }

        return counterToReturn % size;
    }

    @Override
    public HClientPool createConnection(HClientFactory clientFactory, CassandraHost host) {
        return new ConcurrentHClientPool(clientFactory, host);
    }
}
