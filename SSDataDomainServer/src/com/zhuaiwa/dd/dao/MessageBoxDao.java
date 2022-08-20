package com.zhuaiwa.dd.dao;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import me.prettyprint.cassandra.serializers.ByteBufferSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.beans.HSuperColumn;
import me.prettyprint.hector.api.beans.SuperSlice;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SuperSliceQuery;

import org.omg.CORBA.BooleanHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhuaiwa.dd.domain.BaseObject;
import com.zhuaiwa.dd.domain.MessageInfo;
import com.zhuaiwa.dd.exception.DALException;
import com.zhuaiwa.dd.model.ModelUtils;
import com.zhuaiwa.dd.util.ReflectUtils;

public abstract class MessageBoxDao<T extends BaseObject> extends AbstractDao<T> {
	
	private static Logger logger = LoggerFactory.getLogger(MessageBoxDao.class);
	
	public static String generateMessageId(String userid, long number) {
		return String.format("%1$016X%2$s", number, userid);
	}

	public static String generateMaxMessageId(long number) {
		return generateMessageId("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz", number);
	}

	public static String generateMinMessageId(long number) {
		return generateMessageId("00000000000000000000000000000000", number);
	}
	
	public T getMessageByTimestamp(String userid, long starttime, long endtime) throws DALException {
		return getMessageByTimestamp(userid, starttime, endtime, Integer.MAX_VALUE);
	}
	
	public T getMessageByTimestamp(String userid, long starttime, long endtime, int count) throws DALException {
		String cursorId = null;
		String limit = null;
		if (starttime < endtime) {
			cursorId = generateMaxMessageId(starttime);
			limit = generateMaxMessageId(endtime);
			count = (Integer.MIN_VALUE == count ? Integer.MAX_VALUE : Math.abs(count));
		} else {
			cursorId = generateMinMessageId(starttime);
			limit = generateMinMessageId(endtime);
			count = (Integer.MAX_VALUE == count ? Integer.MIN_VALUE : -Math.abs(count));
		}
		
		return getMessage(userid, cursorId, limit, count, null);
	}
	
	public T getMessage(String userid, String cursor_id, int count, BooleanHolder eol) throws DALException {
		return getMessage(userid, cursor_id, null, count, eol);
	}

    @SuppressWarnings("unchecked")
    public T getMessage(String userid, String cursor_id, String limit, int count, BooleanHolder eol) throws DALException {
//        logger.info("userid: " + userid);
//        logger.info("cursor_id: " + cursor_id);
//        logger.info("limit: " + limit);
//        logger.info("count: " + count);
        
        SuperSliceQuery<String,ByteBuffer,ByteBuffer,ByteBuffer> query = HFactory.createSuperSliceQuery(keyspace, StringSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get());
        query.setColumnFamily(this.columnFamilyName);
        query.setKey(userid);
        
        ByteBuffer start = null;
        ByteBuffer finish = null;
        boolean reversed;
        int size;
        
        if (cursor_id != null)
            start = StringSerializer.get().toByteBuffer(cursor_id);
        if (limit != null)
            finish = StringSerializer.get().toByteBuffer(limit);
        
        reversed = count > 0;
        
        if (eol != null)
            eol.value = false;
        if (count <= Integer.MAX_VALUE-1 || count >= Integer.MIN_VALUE+1) {
            if (eol != null)
                eol.value = true;
            size = Integer.MAX_VALUE;
        } else {
            size = Math.abs(count) + 2;
        }
        
        query.setRange(start, finish, reversed, size);
        
        QueryResult<SuperSlice<ByteBuffer,ByteBuffer,ByteBuffer>> result = query.execute();
        SuperSlice<ByteBuffer,ByteBuffer,ByteBuffer> slice = (result == null ? null : result.get());
        List<HSuperColumn<ByteBuffer,ByteBuffer,ByteBuffer>> columns = (slice == null ? null : slice.getSuperColumns());
        if (columns == null)
            return null;
        T obj = (T)ModelUtils.ctorFromSuperColumn(ModelUtils.parser(getClazz()), userid, columns);
        if (obj == null) {
            logger.info("No object with " + userid);
            return null;
        }
        
        ArrayList<MessageInfo> messageids;
        try {
            messageids = (ArrayList<MessageInfo>)ReflectUtils.getFieldValue(getClazz(), "messages", obj);
        } catch (IllegalArgumentException e) {
            logger.info("", e);
            throw new DALException(e);
        } catch (IllegalAccessException e) {
            logger.info("", e);
            throw new DALException(e);
        }
        if (messageids == null) {
            messageids = new ArrayList<MessageInfo>();
        }
        
//        StringBuilder sb = new StringBuilder();
//        sb.append("original messageids: " + messageids.size() + ".\n");
//        for (MessageInfo msgid : messageids) {
//            sb.append("messageid: ").append(msgid.getMsgid()).append("\n");
//        }
//        logger.info(sb.toString());
        
        if (cursor_id != null && messageids.size() > 0) {
            if (messageids.get(0).getMsgid().equals(cursor_id))
                messageids.remove(0);
        }
        
        if (count != Integer.MAX_VALUE && count != Integer.MIN_VALUE  && count != 0) {
            if (messageids.size() <= Math.abs(count)) {
                if (eol != null)
                    eol.value = true;
            } else {
                do {
                    messageids.remove(messageids.size()-1);
                } while (messageids.size() > Math.abs(count));
            }
        }
        return obj;
    }
}
