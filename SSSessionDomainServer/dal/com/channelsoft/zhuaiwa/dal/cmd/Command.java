package com.channelsoft.zhuaiwa.dal.cmd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.ConsistencyLevel;


public abstract class Command {
	public static final String keyspace = "SSDataDomain";
	public static final String encoding = "UTF-8";
	
	protected Cassandra.Iface cassandra;
	protected ConsistencyLevel consistencyLevel = ConsistencyLevel.QUORUM;
	
	public String getKeyspace() {
		return keyspace;
	}
	public String getEncoding() {
		return encoding;
	}
	public Cassandra.Iface getCassandra() {
		return cassandra;
	}
	public Command(Cassandra.Iface cassandra) {
		this.cassandra = cassandra;
	}
	
	public Command(Cassandra.Iface cassandra, ConsistencyLevel consistencyLevel) {
		this.cassandra = cassandra;
		this.consistencyLevel = consistencyLevel;
	}
	
	public ConsistencyLevel getConsistencyLevel() {
		return consistencyLevel;
	}
	public void setConsistencyLevel(ConsistencyLevel consistencyLevel) {
		this.consistencyLevel = consistencyLevel;
	}
	
	public byte[] Object2ByteArray(Object o) {
		return Object2ByteArray(o, null);
	}
	
	static public byte[] Object2ByteArray(Object o, Class<?> type) {
		byte[] retValue = new byte[0];
		if (type == null) {
			if (o == null)
				return retValue;
			type = o.getClass();
		}
		
		if (String.class.isAssignableFrom(type)) {
			try {
				retValue = ((String)(o == null ? "" : o)).getBytes(encoding);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else if (Long.class.isAssignableFrom(type)) {
			retValue = toByteArray(((Long)o));
		} else if (Integer.class.isAssignableFrom(type)) {
			retValue = toByteArray(((Integer)o));
		} else if (Short.class.isAssignableFrom(type)) {
			retValue = toByteArray(((Short)o));
		} else if (Byte.class.isAssignableFrom(type)) {
			retValue = toByteArray((Byte)o);
		} else if (Double.class.isAssignableFrom(type)) {
			retValue = toByteArray((Double)o);
		} else if (Float.class.isAssignableFrom(type)) {
			retValue = toByteArray((Float)o);
		} else if (Boolean.class.isAssignableFrom(type)) {
			retValue = toByteArray(((Boolean)o));
		} else {
			ObjectOutputStream oos = null;
			ByteArrayOutputStream baos = null;
			try {
				baos = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(baos);
				oos.writeObject(o);
				retValue = baos.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (baos != null)
						baos.close();
					if (oos != null)
						oos.close();
				} catch (IOException e) {
				}
			}
		}
		
		return retValue;
	}
	
	@SuppressWarnings({ "unchecked"})
	static public <Type> Type ByteArray2Object(byte[] bytes, Class<Type> type) {
		Type retValue = null;
		if (String.class.isAssignableFrom(type)) {
			try {
				retValue = (Type)new String(bytes, encoding);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else if (Long.class.isAssignableFrom(type)) {
			retValue = (Type)(Long)byteArrayToLong(bytes);
		} else if (Integer.class.isAssignableFrom(type)) {
			retValue = (Type)(Integer)byteArrayToInt(bytes);
		} else if (Short.class.isAssignableFrom(type)) {
			retValue = (Type)(Short)byteArrayToShort(bytes);
		} else if (Byte.class.isAssignableFrom(type)) {
			retValue = (Type)(Byte)byteArrayToByte(bytes);
		} else if (Double.class.isAssignableFrom(type)) {
			retValue = (Type)(Double)byteArrayToDouble(bytes);
		} else if (Float.class.isAssignableFrom(type)) {
			retValue = (Type)(Float)byteArrayToFloat(bytes);
		} else if (Boolean.class.isAssignableFrom(type)) {
			retValue = (Type)(Boolean)byteArrayToBoolean(bytes);
		} else {
			ByteArrayInputStream bais = null;
			ObjectInputStream ois = null;
			try {
				bais = new ByteArrayInputStream(bytes);
				ois = new ObjectInputStream(bais);
				retValue = (Type)ois.readObject();
			} catch (IOException e) {
				e.printStackTrace();
				retValue = null;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				retValue = null;
			} finally {
				try {
					if (bais != null)
						bais.close();
					if (ois != null)
						ois.close();
				} catch (IOException e) {
				}
			}
		}
		return retValue;
	}
	
	//================================================================================================
    public static byte[] toByteArray(byte b) {
    	byte[] bytes = new byte[1];
    	ByteBuffer.wrap(bytes).put(b);
    	return bytes;
    }
    
    public static byte[] toByteArray(short s) {
    	byte[] bytes = new byte[2];
    	ByteBuffer.wrap(bytes).putShort(s);
    	return bytes;
    }
    
    public static byte[] toByteArray(int i)
    {
        byte[] bytes = new byte[4];
        ByteBuffer.wrap(bytes).putInt(i);
        return bytes;
    }
    
    public static byte[] toByteArray(long n)
    {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putLong(n);
        return bytes;
    }
    
    public static byte[] toByteArray(float f)
    {
        byte[] bytes = new byte[4];
        ByteBuffer.wrap(bytes).putFloat(f);
        return bytes;
    }
    
    public static byte[] toByteArray(double d)
    {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putDouble(d);
        return bytes;
    }
    
    public static byte[] toByteArray(boolean b)
    {
        byte[] bytes = new byte[1];
        if (b)
        	bytes[0] = 1;
        else
        	bytes[0] = 0;
        return bytes;
    }
    
    public static byte byteArrayToByte(byte[] bytes) {
    	if (bytes.length < 1) {
            throw new IllegalArgumentException("An byte must be 1 bytes in size.");
    	}
    	return ByteBuffer.wrap(bytes).get();
    }
    
    public static short byteArrayToShort(byte[] bytes) {
    	if (bytes.length < 2) {
            throw new IllegalArgumentException("An short must be 2 bytes in size.");
    	}
    	return ByteBuffer.wrap(bytes).getShort();
    }

    public static int byteArrayToInt(byte[] bytes)
    {
        if ( bytes.length < 4 )
        {
            throw new IllegalArgumentException("An integer must be 4 bytes in size.");
        }
        return ByteBuffer.wrap(bytes).getInt();
    }

    public static long byteArrayToLong(byte[] bytes)
    {
        if ( bytes.length < 8 )
        {
            throw new IllegalArgumentException("An long must be 8 bytes in size.");
        }
        return ByteBuffer.wrap(bytes).getLong();
    }

    public static float byteArrayToFloat(byte[] bytes)
    {
        if ( bytes.length < 4 )
        {
            throw new IllegalArgumentException("An float must be 4 bytes in size.");
        }
        return ByteBuffer.wrap(bytes).getFloat();
    }

    public static double byteArrayToDouble(byte[] bytes)
    {
        if ( bytes.length < 8 )
        {
            throw new IllegalArgumentException("An double must be 8 bytes in size.");
        }
        return ByteBuffer.wrap(bytes).getDouble();
    }

    public static boolean byteArrayToBoolean(byte[] bytes)
    {
        if ( bytes.length < 1 )
        {
            throw new IllegalArgumentException("An boolean must be 1 bytes in size.");
        }
        if (bytes[0] > 0)
        	return true;
        return false;
    }
    
    public static int compareByteArrays(byte[] bytes1, byte[] bytes2){
        if(null == bytes1){
            if(null == bytes2) return 0;
            else return -1;
        }
        if(null == bytes2) return 1;

        int minLength = Math.min(bytes1.length, bytes2.length);
        for(int i = 0; i < minLength; i++)
        {
            if(bytes1[i] == bytes2[i])
                continue;
            // compare non-equal bytes as unsigned
            return (bytes1[i] & 0xFF) < (bytes2[i] & 0xFF) ? -1 : 1;
        }
        if(bytes1.length == bytes2.length) return 0;
        else return (bytes1.length < bytes2.length)? -1 : 1;
    }
    
    public static String join(List<String> l) {
    	StringBuilder sb = new StringBuilder();
    	for (String s : l) {
    		sb.append(s).append(";");
    	}
    	return sb.toString();
    }
    public static List<String> split(String s) {
    	String[] r = s.split(";");
    	return Arrays.asList(r);
    }
}
