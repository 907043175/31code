package com.code31.common.baseservice.common.response;


public abstract class MsgResponse  {

    private Long code;
    private String msg;

    public MsgResponse(){
        code = 0L;
        msg = "";

    }

    public boolean checkOK(){
        return this.code.longValue() == 0l;
    }

    public static MsgResponse FAIL(String msg){
        ObjMsgResponse msgObj = new ObjMsgResponse();
        msgObj.setCode(-1l);
        msgObj.setMsg(msg);

        return msgObj;
    }
    public static MsgResponse FAIL(Long errorCode,String msg){
        ObjMsgResponse msgObj = new ObjMsgResponse();
        msgObj.setCode(errorCode);
        msgObj.setMsg(msg);

        return msgObj;
    }



    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }
    public void setCode(Integer code) {
        if (code == null){
            this.code = 0l;
            return;
        }

        this.code = code*1l;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


}
