package com.btzh.rabbitmq.moudle.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wyb on 2018/6/14.
 */

public class Notifications {
    public static final String ACTION_NOTIFY_CANCEL = "com.yc.sqt.NOTIFY_CANCEL";
    public static final String ACTION_NOTIFICATION_OPENED = "com.yc.sqt.NOTIFICATION_OPENED";

    public static final String EXTRA_MESSAGE_ID = "msg_id";
    public static final Notifications I = new Notifications();
    private int nIdSeq = 1;
    private final Map<Integer, Integer> nIds = new HashMap<>();
    private Context context;
    private NotificationManager nm;
    private int smallIcon;
    private Bitmap largeIcon;
    private int defaults;

    private String[] defChannelId = {"receive", "subscribe"};
    private String[] defchannelName = {"接收消息", "订阅消息"};
    private int[] defImportance = {NotificationManager.IMPORTANCE_HIGH, NotificationManager.IMPORTANCE_DEFAULT};

    public void init(Context context, String[] channelId, String[] channelName, int[] importance) {
        this.context = context;
        this.nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int defaults = Notification.DEFAULT_ALL
                | Notification.FLAG_AUTO_CANCEL;
        this.defaults = defaults;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if ((channelId == null || channelName == null || importance == null)
                    || !(channelId.length == channelName.length && channelName.length == importance.length)) {
                channelId = defChannelId;
                channelName = defchannelName;
                importance = defImportance;
            }
            for (int i = 0; i < channelId.length; i++) {
                createNotificationChannel(channelId[i], channelName[i], importance[i]);
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.setShowBadge(true);
        nm.createNotificationChannel(channel);
    }

    public boolean hasInit() {
        return context != null;
    }

    public Notifications setSmallIcon(int smallIcon) {
        this.smallIcon = smallIcon;
        return this;
    }

    public Notifications setLargeIcon(Bitmap largeIcon) {
        this.largeIcon = largeIcon;
        return this;
    }

    public Notifications setDefaults(int defaults) {
        this.defaults = defaults;
        return this;
    }

    public int notify(NotificationDO message, Intent clickIT) {
        if (message == null || clickIT == null) return -1;
        Integer nid = message.getNid();
        //1.如果NID不存在则新生成一个，且新生成的Id在nIds是不存在的
        if (nid == null || nid <= 0) {
            do {
                nid = nIdSeq++;
            } while (nIds.containsKey(nid));
        }

        //处理总数
        Integer count = nIds.get(nid);
        if (count == null) {
            count = 0;
        }
        nIds.put(nid, ++count);
        Intent cancelIT = new Intent(ACTION_NOTIFY_CANCEL);
        cancelIT.putExtra(EXTRA_MESSAGE_ID, nid);
        clickIT.putExtra(EXTRA_MESSAGE_ID, nid);
        PendingIntent clickPI = PendingIntent.getBroadcast(context, 0, clickIT, 0);//处理点击
        PendingIntent cancelPI = PendingIntent.getBroadcast(context, 0, cancelIT, 0);//处理滑动取消
        nm.notify(nid, build(clickPI, cancelPI,
                message.getTitle(),
                message.getTitle(),
                message.getContent(),
                count));
        return nid;
    }

    public void clean(Integer nId) {
        Integer count = nIds.remove(nId);
        if (count != null) nm.cancel(nId);
    }

    public void clean(Intent intent) {
        int nId = intent.getIntExtra(Notifications.EXTRA_MESSAGE_ID, 0);
        if (nId > 0) clean(nId);
    }

    public void cleanAll() {
        nIds.clear();
        nm.cancelAll();
    }

    private Notification build(PendingIntent clickIntent, PendingIntent cancelIntent,
                               String ticker, String title, String content, int number) {
        Notification notification;
        String channelld = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelld = checkChannel("receive");
        }

        notification = new NotificationCompat.Builder(context, channelld)
                .setSmallIcon(smallIcon)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(content)
                .setTicker(ticker)
                .setContentIntent(clickIntent)
                .setDeleteIntent(cancelIntent)
                .setNumber(number)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                //.setLights(0xff00ff00, 5000, 5000)
                .setDefaults(defaults)
                .build();

        return notification;
    }


    private String checkChannel(String channelName) {

        if (TextUtils.isEmpty(channelName)) {
            return null;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = nm.getNotificationChannel(channelName);
            if (channel == null) return null;

            if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
                context.startActivity(intent);
            }
        }
        return channelName;
    }


}
