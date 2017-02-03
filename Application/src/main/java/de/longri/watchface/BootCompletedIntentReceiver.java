package de.longri.watchface;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Longri on 03.02.2017.
 */
public class BootCompletedIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            //start service which is UpdateService.java
            Intent pushIntent = new Intent(context, UpdateService.class);
            context.startService(pushIntent);
        }
    }
}
