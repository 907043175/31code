package com.code31.common.baseservice.db.sql.where;


public abstract class BaseWh {
	protected String key;
	protected Object value;
	
	public BaseWh(String key, Object value) {
		super();
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Object getValue() {
		if(this.value == null){
			return null;
		}
		String svalue = value.toString();
		if (svalue.length() < 1)
			return null;
		svalue = svalue.replace("\\", "\\\\")
				.replace("'", "\\'")
				.replace("\"", "\\\"");
		return svalue;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
}
