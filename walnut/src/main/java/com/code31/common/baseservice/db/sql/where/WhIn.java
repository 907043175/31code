package com.code31.common.baseservice.db.sql.where;

import java.util.List;

public class WhIn  extends BaseWh{

	public WhIn(String key, List<Object> value) {
		super(key, value);
	}

	protected String tranceSQL() {
		if(value!=null){
			List<Object> values = (List<Object>) value;
			StringBuffer insql = new StringBuffer();
			for(Object vl:values){
				insql.append("'"+vl.toString().trim()+"',");
			}
			if(insql.length()>0){
				return " IN ("+ insql.subSequence(0, insql.length()-1) +") ";
			}else{
				return null;
			}
		}
		return null;
	}


}
