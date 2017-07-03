package com.linzhou.schentunion.base;

/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.bese
 *创建者:  linzhou
 *创建时间:17/04/21
 *描述:   app属性配置类
 */

import com.linzhou.schentunion.data.Contact;
import com.linzhou.schentunion.data.Student;

import java.util.ArrayList;
import java.util.List;

public class AppConfig {

    //用户属性类
    public static Student student = new Student();

    public static List<Contact> mContacts = new ArrayList<>();

    //当前书否处于聊天界面
    public static boolean ONWCHAT=false;
    //聊天对象id
    public static int ONWCHATID=-1;

    public static final String  APATCHID="2.0";

}
