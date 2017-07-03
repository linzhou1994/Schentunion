package com.linzhou.schentunion.data;

/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.data
 *创建者:  linzhou
 *创建时间:17/04/23
 *描述:   
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ContactsList {
    public static ContactsList contactsList = new ContactsList();

    private List<Contacts> mContacts = new ArrayList<>();
    public int unread = 0;

    private ContactsList(){
    }

    private void init(JSONArray array){
        for (int i =0;i<array.length();i++){
            JSONObject jo = array.optJSONObject(i);
            addMessage(jo);
        }
    }

    private void addMessage(JSONObject jo) {
        int id=0 ;
        try {
            int typemes = jo.getInt("typemes");
            switch (typemes){
                case 0:case 4:
                    id=jo.getInt("receiver");
                    break;
                case 1:case 5:
                    id=jo.getInt("sender");
            }
            for (Contacts ct:mContacts){
                if (id==ct.getId());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public class Contacts{
        private int id;
        private String name;
        private String picture;
        private int type;//学校或者企业
        private List<Message> meslist = new ArrayList<>();
        public int unread=0;//未读消息的条数

        public Contacts(int id,String name,String picture,int type){
            this.id=id;
            this.name=name;
            this.picture=picture;
            this.type=type;
        }
        public Contacts(int id,String name,String picture,int type,Message mes){
            this.id=id;
            this.name=name;
            this.picture=picture;
            this.type=type;
            meslist.add(mes);
        }

        /**
         * 联系人构造函数
         * @param id 联系人id
         * @param name 名字
         * @param picture 图片地址
         * @param type 联系人类型
         * @param meslist 消息列表
         */
        public Contacts(int id,String name,String picture,int type,List<Message> meslist){
            this.id=id;
            this.name=name;
            this.picture=picture;
            this.type=type;
            this.meslist.addAll(meslist);
            for (Message mes:meslist){
                if (!mes.blread) unread++;
            }

        }

        public void addMessage(Message message){
            meslist.add(message);
        }

        /**
         * 添加一条聊天信息
         * @param message 消息内容
         * @param is 是否需要添加未读
         */
        public void addMessage(Message message,boolean is){
            meslist.add(message);
            if (is) unread++;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public int getType() {
            return type;
        }

        public List<Message> getMeslist() {
            return meslist;
        }
    }

    public class Message{
        private String message;
        private int mestype;//消息内容类型，图片或者文字
        private int type;//消息的类型，0：接收的 1：发送的
        public boolean blread;//是否已读

        public Message(String mes,int mestype,int type){
            this.message=mes;
            this.mestype=mestype;
            this.type=type;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getMestype() {
            return mestype;
        }

        public void setMestype(int mestype) {
            this.mestype = mestype;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
