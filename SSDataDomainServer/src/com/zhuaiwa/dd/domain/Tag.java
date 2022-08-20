package com.zhuaiwa.dd.domain;

import java.util.ArrayList;
import java.util.List;

import com.zhuaiwa.dd.annotation.Column;
import com.zhuaiwa.dd.annotation.ColumnFamily;
import com.zhuaiwa.dd.annotation.Key;
import com.zhuaiwa.dd.annotation.SuperColumns;

@ColumnFamily(value="Tag", isSuper=true)
public class Tag extends BaseObject {
	public static final String CN_TAG = "Tag";

	@Key
	private String userid;

	@SuperColumns(valueType = TagInfo.class)
	private List<TagInfo> tags = new ArrayList<TagInfo>();

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public List<TagInfo> getTags() {
		return tags;
	}

	public void setTags(List<TagInfo> tags) {
		this.tags = tags;
	}

	public static class TagInfo {
		public static final String FN_TAGID = "tagid";
		public static final String FN_NAME = "name";
		public static final String FN_SYSTEM = "system";
		public static final String FN_BINARY = "binary";

		@Key
		@Column(name = FN_TAGID)
		private String tagid;

		@Column(name = FN_NAME)
		private String name;

		@Column(name = FN_SYSTEM)
		private Integer system;

		@Column(name = FN_BINARY)
		private byte[] binary;

		public String getTagid() {
			return tagid;
		}

		public void setTagid(String tagid) {
			this.tagid = tagid;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getSystem() {
			return system;
		}

		public void setSystem(Integer system) {
			this.system = system;
		}

		public byte[] getBinary() {
			return binary;
		}

		public void setBinary(byte[] binary) {
			this.binary = binary;
		}

	}

}
