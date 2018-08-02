package com.ecommerce.customer.fypproject.adapter;

public class ChatList {
    private String name;
    private String msg;
    private String date;
    private String recvUID;

    public String getRecvUID() {
        return recvUID;
    }

    public void setRecvUID(String recvUID) {
        this.recvUID = recvUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
