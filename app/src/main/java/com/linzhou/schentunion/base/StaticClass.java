package com.linzhou.schentunion.base;

/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.base
 *创建者:  linzhou
 *创建时间:17/04/22
 *描述:   静态常量
 */

public class StaticClass {
    //IP
   //public static final String IP = "172.27.35.3:8080/SchEntUnion/";
    //IP服务器
    public static final String IP = "120.24.70.156:8080/SchEntUnion/";
    //长连接url
    public static final String WEBRUL = "ws://" + IP + "connect";
    //http
    public static final String HTTPIMAGE = "http://" + IP;
    //上传头像
    public static final String UPLOADIMAGE = "http://" + IP + "upload/upload";
    //内容
    public static final String CONTENT = "content";
    //类型
    public static final String TYPE = "type";
    //返回类型
    public static final String RETYPE = "re";
    //登录
    public static final String LOGIN = "login";
    //注册
    public static final String REGISTERED = "register";
    //获取所有专业信息
    public static final String PCLIST = "pclist";
    //获取简历信息
    public static final String GETRESUME = "getresume";
    //修改简历
    public static final String UPDATERESUME = "updateresume";
    //修改个人信息
    public static final String UPDATESTUDENT = "supdate";
    //登录成功后向服务器请基本数据
    public static final String DETAIL = "detail";
    //获取学校推荐的岗位
    public static final String POSTS = "posts_c";
    //搜索岗位
    public static final String SEARCHPO = "searchpo";
    //获取我投递过简历的岗位
    public static final String SPOLIST = "spolist";
    //获取岗位详细
    public static final String PODETAIL = "podetail";
    //投简历
    public static final String DELIVER = "deliver";

    //bmob短信验证id
    //public static final String SMSID="779a1bdd359517c32ce2e2d4b21647ba";
    public static final String SMSID="5407b3136da9df780af2e38572a9177e";
    //bmob短信验证模板名称
    public static final String SMSNAME=" 校企合作平台";
    //下线
    public static final String LOGOUT="logout";
    //获取企业详情
    public static final String EDETAIL="edetail";
    //获取企业列表
    public static final String ELIST="elist";
    //搜做企业
    public static final String SEARCHETP = "searchetp";
    //获取会话列表
    public static final String SESSIONLIST="sessionlist";
    //接收消息
    public static final String REV="rev";
    //发送消息
    public static final String CHAT="chat";
    //获取历史消息
    public static final String MSG="msg";
    //获取软件最新版本信息
    public static final String GETCODE="getcode";
    //版本信息获取处
    public static final String CHECK_UPDATE_URL =HTTPIMAGE+"VERSION/version.json";

}
