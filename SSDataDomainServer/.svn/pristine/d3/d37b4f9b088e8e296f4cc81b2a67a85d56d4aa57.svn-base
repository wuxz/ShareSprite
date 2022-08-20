package com.zhuaiwa.dd.domain;

import com.zhuaiwa.dd.annotation.Column;
import com.zhuaiwa.dd.annotation.ColumnFamily;
import com.zhuaiwa.dd.annotation.Key;

@ColumnFamily("Torrent")
public class Torrent extends BaseObject {
	public static final String CN_TORRENT = "Torrent";
	public static final String FN_VALUE = "value";
	
	@Key
	private String key;
	@Column(name=FN_VALUE)
	private byte[] value;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public byte[] getValue() {
		return value;
	}
	public void setValue(byte[] value) {
		this.value = value;
	}
}
