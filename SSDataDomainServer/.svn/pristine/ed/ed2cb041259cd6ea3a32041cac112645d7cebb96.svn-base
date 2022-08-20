package com.zhuaiwa.dd.hector;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import me.prettyprint.cassandra.serializers.IntegerSerializer;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.beans.Rows;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;

import com.zhuaiwa.api.Common.SSAccount;
import com.zhuaiwa.api.util.SSIdUtils;
import com.zhuaiwa.dd.domain.Account;

public class HectorUtils {
    public static HColumn<String, String> createStringColumn(String name, String value) {
        return HFactory.createStringColumn(name, value);
    }
    public static HColumn<String, Long> createLongColumn(String name, Long value) {
        return HFactory.createColumn(name, value, StringSerializer.get(), LongSerializer.get());
    }
    public static HColumn<String, Integer> createIntegerColumn(String name, Integer value) {
        return HFactory.createColumn(name, value, StringSerializer.get(), IntegerSerializer.get());
    }
    
    public static void buildAccount(SSAccount.Builder account, Row<String,String,ByteBuffer> row) {
        ColumnSlice<String, ByteBuffer> slices = (row == null ? null : row.getColumnSlice());
        List<HColumn<String, ByteBuffer>> columns = (slices != null ? slices.getColumns() : null);
        String id = (row == null ? null : row.getKey());
        if (columns != null) {
            for (HColumn<String, ByteBuffer> column : columns) {
                if (Account.FN_EMAIL.equalsIgnoreCase(column.getName())) {
                    account.addAliasIdList(SSIdUtils.fromEmail(StringSerializer.get().fromByteBuffer(column.getValue())));
                    continue;
                }
                if (Account.FN_FIRST_LOGIN.equalsIgnoreCase(column.getName())) {
                    account.setIsFirstLogin(IntegerSerializer.get().fromByteBuffer(column.getValue()));
                    continue;
                }
                if (Account.FN_IS_ACTIVATED.equalsIgnoreCase(column.getName())) {
                    account.setIsActive(IntegerSerializer.get().fromByteBuffer(column.getValue()));
                    continue;
                }
                if (Account.FN_IS_EDUCATION_HIDDEN.equalsIgnoreCase(column.getName())) {
                    account.setIsEducationHidden(IntegerSerializer.get().fromByteBuffer(column.getValue()));
                    continue;
                }
                if (Account.FN_IS_EMAIL_HIDDEN.equalsIgnoreCase(column.getName())) {
                    account.setIsEmailHidden(IntegerSerializer.get().fromByteBuffer(column.getValue()));
                    continue;
                }
                if (Account.FN_IS_NICKNAME_HIDDEN.equalsIgnoreCase(column.getName())) {
                    account.setIsNicknameHidden(IntegerSerializer.get().fromByteBuffer(column.getValue()));
                    continue;
                }
                if (Account.FN_IS_PHONE_HIDDEN.equalsIgnoreCase(column.getName())) {
                    account.setIsPhoneHidden(IntegerSerializer.get().fromByteBuffer(column.getValue()));
                    continue;
                }
                if (Account.FN_IS_WORK_HIDDEN.equalsIgnoreCase(column.getName())) {
                    account.setIsWorkHidden(IntegerSerializer.get().fromByteBuffer(column.getValue()));
                    continue;
                }
                if (Account.FN_MESSAGE_FILTER.equalsIgnoreCase(column.getName())) {
                    account.setMessageFilter(IntegerSerializer.get().fromByteBuffer(column.getValue()));
                    continue;
                }
                if (Account.FN_PASSWORD.equalsIgnoreCase(column.getName())) {
                    account.setPassword(StringSerializer.get().fromByteBuffer(column.getValue()));
                    continue;
                }
                if (Account.FN_PHONE_NUMBER.equalsIgnoreCase(column.getName())) {
                    account.addAliasIdList(SSIdUtils.fromPhone(StringSerializer.get().fromByteBuffer(column.getValue())));
                    continue;
                }
                if (Account.FN_REGISTER_TIME.equalsIgnoreCase(column.getName())) {
                    account.setRegisterTime(LongSerializer.get().fromByteBuffer(column.getValue()));
                    continue;
                }
                if (Account.FN_ROLE.equalsIgnoreCase(column.getName())) {
                    account.setRole(IntegerSerializer.get().fromByteBuffer(column.getValue()));
                    continue;
                }
                if (Account.FN_SECURITY_CODE.equalsIgnoreCase(column.getName())) {
                    account.setSecurityCode(StringSerializer.get().fromByteBuffer(column.getValue()));
                    continue;
                }
                if (Account.FN_SECURITY_CODE_TIME.equalsIgnoreCase(column.getName())) {
                    account.setSecurityCodeTime(LongSerializer.get().fromByteBuffer(column.getValue()));
                    continue;
                }
                if (Account.FN_SHARE_FORBIDDEN_TIME.equalsIgnoreCase(column.getName())) {
                    account.setShareForbiddenTime(LongSerializer.get().fromByteBuffer(column.getValue()));
                    continue;
                }
                if (Account.FN_USERID.equalsIgnoreCase(column.getName())) {
                    account.setUserid(StringSerializer.get().fromByteBuffer(column.getValue()));
                    continue;
                }
            }
        }
        if (account.getUserid() == null && id != null) {
            account.setUserid(id);
        }
    }
    
    public static List<SSAccount.Builder> extractAccount(QueryResult<Rows<String, String, ByteBuffer>> result) {
        Rows<String,String,ByteBuffer> rows = (result == null ? null : result.get());
        List<SSAccount.Builder> accounts = new ArrayList<SSAccount.Builder>();
        if (rows != null) {
            for (Row<String, String, ByteBuffer> row : rows) {
                SSAccount.Builder builder = SSAccount.newBuilder();
                buildAccount(builder, row);
                accounts.add(builder);
            }
        }
        return accounts;
    }
    public static SSAccount.Builder extractAccount(String userid, QueryResult<ColumnSlice<String, ByteBuffer>> result) {
        ColumnSlice<String,ByteBuffer> slice = (result == null ? null : result.get());
        if (slice != null && !slice.getColumns().isEmpty()) {
            SSAccount.Builder builder = SSAccount.newBuilder();
            buildAccount(builder, new RowWrapper<String, String, ByteBuffer>(userid, slice));
            return builder;
        }
        return null;
    }
}
