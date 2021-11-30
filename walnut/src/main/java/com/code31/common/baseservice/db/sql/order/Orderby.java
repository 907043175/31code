package com.code31.common.baseservice.db.sql.order;

public class Orderby {
	protected String key;
	
	public Orderby(String key){
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
