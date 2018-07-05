package com.btzh.rabbitmq.moudle.notification;

import org.json.JSONObject;

/**
 * Created by wyb on 2018/6/14.
 */

public class NotificationDO {
    private String msgId;
    private String title;  //标题
    private String content;//内容
    private Integer nid; //主要用于聚合通知，非必填
    private Byte flags; //特性字段。 0x01:声音   0x02:震动 0x03:闪灯
    private String largeIcon; // 大图标
    private String ticker; //和title一样
    private Integer number;
    private JSONObject extras; //附加信息

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getNid() {
        return nid;
    }

    public void setNid(Integer nid) {
        this.nid = nid;
    }

    public Byte getFlags() {
        return flags;
    }

    public void setFlags(Byte flags) {
        this.flags = flags;
    }

    public String getLargeIcon() {
        return largeIcon;
    }

    public void setLargeIcon(String largeIcon) {
        this.largeIcon = largeIcon;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public JSONObject getExtras() {
        return extras;
    }

    public void setExtras(JSONObject extras) {
        this.extras = extras;
    }
}
