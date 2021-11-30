package com.code31.common.baseservice.common.response;


public class ObjMsgResponse extends MsgResponse{

    Object data;

    public ObjMsgResponse(){
       super();
        data = null;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

	public static ObjMsgResponse FAIL(String msg) {
		ObjMsgResponse response = new ObjMsgResponse();
		response.setCode(-1l);
		response.setMsg(msg);
		
		return response;
	}

    public static ObjMsgResponse FAIL(Long code, String msg) {
        ObjMsgResponse response = new ObjMsgResponse();
        if (code != null)
            response.setCode(code);
        else
            response.setCode(-1l);
        response.setMsg(msg);

        return response;
    }

    public static ObjMsgResponse OK(){
        ObjMsgResponse msgObj = new ObjMsgResponse();
        msgObj.setCode(0l);
        msgObj.setMsg("");

        return msgObj;
    }

    public static ObjMsgResponse OK(String msg){
        ObjMsgResponse msgObj = new ObjMsgResponse();
        msgObj.setCode(0l);
        msgObj.setMsg(msg);

        return msgObj;
    }

    public static ObjMsgResponse OK(Object obj){
        ObjMsgResponse msgObj = new ObjMsgResponse();
        msgObj.setCode(0l);
        msgObj.setMsg("");
        msgObj.setData(obj);

        return msgObj;
    }

}
