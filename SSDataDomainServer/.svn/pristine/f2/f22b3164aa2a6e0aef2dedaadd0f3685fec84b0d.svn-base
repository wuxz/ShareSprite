package org.apache.cassandra.thrift.client;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 一个简易的cassandra客户池实现。
 * <br/>
 * <br/>支持lazy connection、断开自动重连
 * @author yaosw
 *
 */
public class SimpleClientPool {
	private static Logger logger = LoggerFactory.getLogger(SimpleClientPool.class);
	
	private int MAX_AVAILABLE;
	private Semaphore available;
	
	enum State {
		Uninitialized,
		Initializing,
		Initialized,
	}

	protected CassandraClient[] clients;
	protected State[] states;
	protected boolean[] used;
//	protected RingCache ringCache;
//	protected int retryTimes;
//	public int getRetryTimes() {
//		return retryTimes;
//	}
//
//	public void setRetryTimes(int retryTimes) {
//		this.retryTimes = retryTimes;
//		if (clients != null) {
//			for (CassandraClient client : clients) {
//				if (client != null)
//					client.setRetryTimes(retryTimes);
//			}
//		}
//	}
//
//	public RingCache getRingCache() {
//		return ringCache;
//	}
//
//	public void setRingCache(RingCache ringCache) {
//		this.ringCache = ringCache;
//	}

	protected InetSocketAddress[] addresses;
	protected SetupThread setupThread;
	
	public static SimpleClientPool createInstance(int maxSize, List<InetSocketAddress> endpoints) {
		try {
			SimpleClientPool pool = new SimpleClientPool(maxSize, endpoints);
			return pool;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private SimpleClientPool(int maxSize, List<InetSocketAddress> endpoints) {
		clients = new CassandraClient[maxSize];
		states = new State[maxSize];
		used = new boolean[maxSize];
		addresses = new InetSocketAddress[maxSize];

		if (endpoints == null || endpoints.size() == 0) {
			throw new InvalidParameterException("endpoints should not been empty.");
		}

		endpoints = new ArrayList<InetSocketAddress>(endpoints);
		Collections.shuffle(endpoints);
		
		MAX_AVAILABLE = 0;
		int size = endpoints.size();
		for (int i = 0; i < maxSize; i++) {
			clients[i] = null;
			states[i] = State.Uninitialized;
			used[i] = false;
			
			addresses[i] = endpoints.get(i%size);
			MAX_AVAILABLE++;
		}
		
		setupThread = new SetupThread();
		available = new Semaphore(MAX_AVAILABLE, true);
		setupThread.setDaemon(true);
		setupThread.start();
	}
	
	public synchronized void close() {
		for (int i = 0; i < clients.length; ++i) {
			if (states[i] == State.Initialized) {
				states[i] = State.Uninitialized;
				clients[i].close();
			}
		}
	}
	
	public Cassandra.Client getClient() throws TimedOutException {
//		available.acquireUninterruptibly();
	    
	    try {
            if (available.tryAcquire(10, TimeUnit.SECONDS)) {
                return getNextAvailableClient();
            }
        } catch (InterruptedException e) {
        }
		throw new TimedOutException();
	}

	public void putClient(Cassandra.Client client) {
		if (markAsUnused(client))
			available.release();
	}
	
	protected synchronized Cassandra.Client getNextAvailableClient() throws TimedOutException {
		long x =0;
		while (true) {
			for (int i = 0; i < used.length; ++i) {
				if (!used[i]) {
					if (states[i] == State.Uninitialized) {
					    TSocket tsocket = new TSocket(addresses[i].getHostName(), addresses[i].getPort());
					    tsocket.setTimeout(10000);
						clients[i] = new CassandraClient(new TBinaryProtocol(new TFramedTransport(tsocket)));
						states[i] = State.Initializing;
						synchronized (setupThread) {
							setupThread.notifyAll();
						}
//						available.acquireUninterruptibly();
						try {
							if (!available.tryAcquire(10, TimeUnit.SECONDS)) {
								throw new TimedOutException();
							}
						} catch (InterruptedException e) {
						}
						continue;
					}
					
					if (states[i] == State.Initializing) {
						continue;
					}
					
					if (!clients[i].isConnected()) {
//						for (int n = 0; n < used.length; ++n) {
//							if (used[n] || (states[n] == State.Initializing))
//								continue;
//							if (isEqualAddresses(addresses[i], addresses[n])) {
//								clients[n].close();
//								clients[n] = null;
//								states[n] = State.Uninitialized;
//							}
//						}
						clients[i].close();
						clients[i] = null;
						states[i] = State.Uninitialized;
						continue;
					}
					
					used[i] = true;
					return clients[i];
				}
			}
			
			if (((++x)%1000) == 0) {
				logger.info("waiting idle connection.");
			}
			try {Thread.sleep(10);} catch (Exception e) {}
		}
	}
	
	//
	// 由于客户端和服务端可能不在同一个网段内，所以只判断IP地址的第四字节是否相等
	//
	private boolean isEqualAddresses(InetSocketAddress a1, InetSocketAddress a2) {
		if ((a1 == null && a2 != null ) || (a1 != null && a2 == null))
			return false;
		if (a1.equals(a2))
			return true;
		InetAddress ia1 = a1.getAddress();
		InetAddress ia2 = a2.getAddress();
		byte[] ba1 = ia1 != null ? ia1.getAddress() : null;
		byte[] ba2 = ia2 != null ? ia2.getAddress() : null;
		if (ba1 != null && ba2 != null && ba1.length == 4 && ba2.length == 4 && ba1[3] == ba2[3]) {
			return true;
		}
		String s1 = a1.toString();
		String s2 = a2.toString();
		String v1 = s1.substring(s1.lastIndexOf("."));
		String v2 = s2.substring(s2.lastIndexOf("."));
		if (v1.equals(v2))
			return true;
		return false;
	}

	protected synchronized boolean markAsUnused(Cassandra.Client client) {
		if (client == null)
			return false;
		
		for (int i = 0; i < clients.length; ++i) {
			if (client == clients[i]) {
				if (used[i]) {
					used[i] = false;
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}
	
	private class SetupThread extends Thread {
		@Override
		public synchronized void start() {
			setDaemon(true);
			setName("simple_client_pool_setup_thread");
			super.start();
		}
		@Override
		public void run() {
			while (true) {
				try {
					try {
						for (int i = 0; i < states.length; i++) {
							if (states[i] != SimpleClientPool.State.Initializing) {
								continue;
							}
							
							try {
							    clients[i].close();
							} catch (Exception e) {
                                // ignore
                            }
							
							boolean opened = false;
							try {
								clients[i].open();
								opened = true;
							} catch (TTransportException e) {
								opened = false;
								logger.info("open " + addresses[i].toString() + " failed.", e);
							} catch (Throwable e) {
								opened = false;
								logger.info("open " + addresses[i].toString() + " failed.", e);
							} finally {
								clients[i].setConnected(opened);
								clients[i].set_keyspace("SSDataDomain");
								if (opened) {
									available.release();
									states[i] = SimpleClientPool.State.Initialized;
								}
							}
						}
					} finally {
						synchronized (this) {
							try {
								wait(5000);
							} catch (InterruptedException e) {
								// ignore
							}
						}
					}
				} catch (Throwable e) {
					// ignore
				}
			}
		}
	}

}
