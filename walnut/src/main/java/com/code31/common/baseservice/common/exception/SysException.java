package com.code31.common.baseservice.common.exception;


import com.code31.common.baseservice.utils.Utils;


public class SysException extends RuntimeException {
	private static final long serialVersionUID = 1;

	public SysException(String str) {
		super(str);
	}
	
	public SysException(Throwable e) {
		super(e);
	}
	
	public SysException(Throwable e, String str) {
		super(str, e);
	}
	
	public SysException(String str, Object...params) {
		super(Utils.createStr(str, params));
	}
	
	public SysException(Throwable e, String str, Object...params) {
		super(Utils.createStr(str, params), e);
	}
}