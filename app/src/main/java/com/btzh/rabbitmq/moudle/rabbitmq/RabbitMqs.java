package com.btzh.rabbitmq.moudle.rabbitmq;

/**
 * Created by wyb on 2018/6/22.
 */

public class RabbitMqs {

    //mq的地址
    public String mqHost = "118.25.59.97";
    //mq的用户名
    public String mqUsername = "wyb";
    //mq的端口号
    public int mqPort = 5672;
    //mq的密码
    public String mqPassword = "wyb";
    //mq的队列名称
    public String mqQueueDeclare = "wangyubo";

    public String getMqHost() {
        return mqHost;
    }

    public void setMqHost(String mqHost) {
        this.mqHost = mqHost;
    }

    public String getMqUsername() {
        return mqUsername;
    }

    public void setMqUsername(String mqUsername) {
        this.mqUsername = mqUsername;
    }

    public int getMqPort() {
        return mqPort;
    }

    public void setMqPort(int mqPort) {
        this.mqPort = mqPort;
    }

    public String getMqPassword() {
        return mqPassword;
    }

    public void setMqPassword(String mqPassword) {
        this.mqPassword = mqPassword;
    }

    public String getMqQueueDeclare() {
        return mqQueueDeclare;
    }

    public void setMqQueueDeclare(String mqQueueDeclare) {
        this.mqQueueDeclare = mqQueueDeclare;
    }
}
