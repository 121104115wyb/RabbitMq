package com.btzh.rabbitmq.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.btzh.rabbitmq.R;
import com.btzh.rabbitmq.moudle.notification.NotificationDO;
import com.btzh.rabbitmq.moudle.notification.Notifications;
import com.btzh.rabbitmq.service.MqService;
import com.btzh.rabbitmq.view.common.AppConstants;

/**
 * 消息的接受与分发
 * create by wyb
 */

public class MqReceiver extends BroadcastReceiver {

    /*接收到推送消息**/
    public static final String Mq_Message = "Mq_Receive_Message";
    /*网络定位成功**/
    public static final String Mq_LocationMessage = "Mq_LocationMessage";
    /*receiver类名**/
    public static final String SimpleClassName = "com.btzh.rabbitmq.receive.MqReceiver";

    private String mqMessage;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        switch (action) {

            case Mq_Message:
                mqMessage = intent.getStringExtra("MQMES");
                if (!TextUtils.isEmpty(mqMessage)) {
                    //创建通知栏......或者新建一个activity（仿微信）
                    Intent it = new Intent(context, MqReceiver.class);
                    it.setAction(Notifications.ACTION_NOTIFICATION_OPENED);
                    NotificationDO ndo = creatSelf(context, mqMessage);
                    Notifications.I.notify(ndo, it);
                }

                break;
            case Notifications.ACTION_NOTIFY_CANCEL:
                Notifications.I.clean(intent);
                break;
            case Notifications.ACTION_NOTIFICATION_OPENED:
                Notifications.I.clean(intent);
                break;
            case Mq_LocationMessage:
                Double latD = intent.getDoubleExtra("lat", 0);
                Double lonD = intent.getDoubleExtra("lon", 0);
                MqService.sendMqMessage(String.valueOf(latD) + ";" + String.valueOf(lonD), AppConstants.SendDeclareName);
                //Toast.makeText(context, , Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }


    //创建通知栏的消息体
    private NotificationDO creatSelf(Context context, String message) {
        if (TextUtils.isEmpty(message)) {
            message = context.getString(R.string.default_message);
        }

        NotificationDO ndo = new NotificationDO();
        ndo.setContent(message);
        ndo.setTitle(context.getString(R.string.app_name));
        ndo.setTicker(context.getString(R.string.ticker_default));
        //ndo.setNid(1);
        //ndo.setExtras();
        return ndo;
    }

}
