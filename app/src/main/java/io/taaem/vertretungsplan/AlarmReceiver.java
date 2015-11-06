package io.taaem.vertretungsplan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by taaem on 22.11.15.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver{
    Context mContext;
    @Override
    public void onReceive(Context context, Intent intent){
        Log.i("Received Broadcast", "true");

        /*
         * Creates a new Intent to start the RSSPullService
         * IntentService. Passes a URI in the
         * Intent's "data" field.
         */
        Intent mServiceIntent = new Intent(context, UpdateItemService.class);

        context.startService(mServiceIntent);
    }

}
