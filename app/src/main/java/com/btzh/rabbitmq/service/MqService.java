package com.btzh.rabbitmq.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;


import com.btzh.rabbitmq.moudle.rabbitmq.RabbitMqs;
import com.btzh.rabbitmq.receive.MqReceiver;

import com.btzh.rabbitmq.view.common.AppConstants;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * 实时监听mq服务
 * create by wyb 17/06/13
 * （当检测到版本信息为空时是否要新开启service来重新加载版本信息......）
 */
public class MqService extends Service {
    private final static int MQMESSAGE_WHAT = -101;
    private static Channel mChannel;
    private static ExecutorService executorService;
    private static String declareName = "";

    public MqService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        executorService = Executors.newCachedThreadPool();
        executorService.execute(new MyTask());

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    Channel initRabbitMq(RabbitMqs mversion) {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(mversion.getMqHost());
        factory.setUsername(mversion.getMqUsername());
        factory.setPort(mversion.getMqPort());//注意这里的端口与管理插件的端口不一样
        factory.setPassword(mversion.getMqPassword());
        Connection connection = null;
        try {
            connection = factory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        Channel channel;
        try {
            if (connection == null) {
                Toast.makeText(MqService.this, "消息连接初始化失败", Toast.LENGTH_SHORT).show();
                return null;
            }
            channel = connection.createChannel();
            //声明一个dirent模式的交换机
            //channel.exchangeDeclare("exchange_name", BuiltinExchangeType.DIRECT, true);
            //声明一个非持久化自动删除的队列(网格员的id) mversion.getMqQueueDeclare()
            channel.queueDeclare(AppConstants.AppDeclareName, true, false, false, null);//如果该队列不在被使用就删除他 zhe
            //将绑定到改交换机
            //channel.queueBind(mversion.getMqQueueDeclare(), "exchange_name", "route_key");

            channel.basicQos(1);
            return channel;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    void initChannel(Channel channel) {
        if (channel == null) {
            //RabbitMQ不允许重新定义一个已有的队列信息
            Toast.makeText(MqService.this, "消息连接初始化失败", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            DefaultConsumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    String mqMessage = new String(body, "UTF-8");
                    if (!TextUtils.isEmpty(mqMessage))
                        HandleMessage(mqMessage);
                }
            };
            channel.basicConsume(AppConstants.AppDeclareName, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void HandleMessage(String message) {

        Message handlerMsg = new Message();
        handlerMsg.obj = message;
        handlerMsg.what = MQMESSAGE_WHAT;
        handler.sendMessage(handlerMsg);

    }

    private class MyTask implements Runnable {
        @Override
        public void run() {

            RabbitMqs rabbitMqs = new RabbitMqs();
            if (rabbitMqs == null) {
                Toast.makeText(MqService.this, "MQ信息加载失败!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(AppConstants.AppDeclareName)) {
                Toast.makeText(MqService.this, "消息队列名称为空!", Toast.LENGTH_SHORT).show();
                return;
            }
            mChannel = initRabbitMq(rabbitMqs);
            initChannel(mChannel);
        }
    }


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MQMESSAGE_WHAT) {
                Intent intent = new Intent();
                intent.putExtra("MQMES", msg.obj.toString());
                intent.setAction(MqReceiver.Mq_Message);
                intent.setComponent(new ComponentName(MqService.this, MqReceiver.SimpleClassName));
                sendBroadcast(intent);
            }
            return false;
        }
    });

    //通过mesType来区分消息类别
    public static void sendMqMessage(JSONObject mesType, String declareName) {

        executorService.execute(new sendMqMessageTask(mesType, declareName));

    }

    //通过mesType来区分消息类别
    public static void sendMqMessage(String mesType, String declareName) {

        executorService.execute(new sendMqMessageTask(mesType, declareName));

    }

    private static class sendMqMessageTask implements Runnable {

        JSONObject mesType;
        String declareName;
        String messageStr;

        public sendMqMessageTask(JSONObject mesType, String declareName) {
            this.mesType = mesType;
            this.declareName = declareName;

        }

        public sendMqMessageTask(String messageStr, String declareName) {
            this.messageStr = messageStr;
            this.declareName = declareName;

        }

        @Override
        public void run() {
            try {
                byte[] meaageByte = new byte[0];
                if (mesType != null) {
                    meaageByte = mesType.toString().getBytes();
                }
                if (!TextUtils.isEmpty(messageStr)) {
                    meaageByte = messageStr.toString().getBytes();
                }

                if (mChannel != null) {
                    mChannel.basicPublish("", declareName, null, meaageByte);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
