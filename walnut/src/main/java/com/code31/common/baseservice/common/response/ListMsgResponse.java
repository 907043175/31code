package com.code31.common.baseservice.common.response;


import com.google.common.collect.Lists;

import java.util.List;

public class ListMsgResponse extends MsgResponse {

    private List<Object> data;

    private Long count;

    public ListMsgResponse(){
        super();
        data = Lists.newArrayList();
    }

    public static ListMsgResponse OK(String msg){
        ListMsgResponse msgObj = new ListMsgResponse();
        msgObj.setCode(0l);
        msgObj.setMsg(msg);
        return msgObj;
    }

    public static ListMsgResponse OK(){
        ListMsgResponse msgObj = new ListMsgResponse();
        msgObj.setCode(0l);
        msgObj.setMsg("");
        return msgObj;
    }


    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }


}
