package com.channelsoft.zhuaiwa.dal.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.channelsoft.zhuaiwa.dal.cmd.Command;

public class KeyUtils {
	private static Logger logger = Logger.getLogger(KeyUtils.class);
	
	private static String encoding = Command.encoding;
	
	public static ByteBuffer toByteBuffer(String key) {
		if (key == null)
			return null;
		
		try {
			return ByteBuffer.wrap(key.getBytes(encoding));
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
			return null;
		}
	}
	
	public static Iterable<ByteBuffer> toByteBuffer(final Iterable<String> keys) {
		if (keys == null)
			return null;
		
		return new Iterable<ByteBuffer>() {
			@Override
			public Iterator<ByteBuffer> iterator() {
				final Iterator<String> i = keys.iterator();
				return new Iterator<ByteBuffer>() {
					@Override
					public boolean hasNext() {
						return i.hasNext();
					}
					@Override
					public ByteBuffer next() {
						return toByteBuffer(i.next());
					}
					@Override
					public void remove() {
						i.remove();
					}
				};
			}
		};
	}
	
	public static String toString(ByteBuffer key) {
		if (key == null)
			return null;
		
		try {
			return new String(key.array(), encoding);
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
			return null;
		}
	}
	
	public static Iterable<String> toString(final Iterable<ByteBuffer> keys) {
		if (keys == null)
			return null;
		
		return new Iterable<String>() {
			@Override
			public Iterator<String> iterator() {
				final Iterator<ByteBuffer> i = keys.iterator();
				return new Iterator<String>() {
					@Override
					public boolean hasNext() {
						return i.hasNext();
					}
					@Override
					public String next() {
						return KeyUtils.toString(i.next());
					}
					@Override
					public void remove() {
						i.remove();
					}
				};
			}
		};
	}
	
	public static <T> Map<String, T> toStringMap(Map<ByteBuffer, T> m) {
		if (m == null)
			return null;
		
		Map<String, T> r = new HashMap<String, T>();
		for (Entry<ByteBuffer, T> e : m.entrySet()) {
			r.put(toString(e.getKey()), e.getValue());
		}
		return r;
	}
	
	public static <T> List<T> getValueList(Map<ByteBuffer, T> m) {
		if (m == null)
			return null;
		List<T> r = new ArrayList<T>();
		for (Entry<ByteBuffer, T> e : m.entrySet()) {
			r.add(e.getValue());
		}
		return r;
	}
}
