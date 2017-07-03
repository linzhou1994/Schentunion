package com.linzhou.schentunion.data;

/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.data
 *创建者:  linzhou
 *创建时间:17/04/25
 *描述:   
 */


import java.util.Date;

public class Contact {
    public int id;
    public int eid;
    public String picture;
    public int type;//联系人类型
    public String name;
    public String message;
    public Date time;
    public boolean mesnew;//是否又新消息
}
