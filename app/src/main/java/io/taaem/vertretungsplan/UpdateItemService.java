package io.taaem.vertretungsplan;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UpdateItemService extends IntentService {

    public UpdateItemService() {
        super("UpdateItemService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("INTENT", "Hello From INtent");
        if (intent != null) {
            Date d = new Date();
            SimpleDateFormat dayOfWeek = new SimpleDateFormat("EEEEE");
            if (dayOfWeek.format(d) != "S") {
                SimpleDateFormat year = new SimpleDateFormat("yyyy");
                SimpleDateFormat month = new SimpleDateFormat("M");
                SimpleDateFormat day = new SimpleDateFormat("d");

                String dayEnd = "S-" + year.format(d) + "-" + month.format(d) + "-" + day.format(d) + ".htm";

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                String klasse = sharedPreferences.getString(SettingsActivity.KEY_PREF_USERKLASSE, "");
                String buchstabe = sharedPreferences.getString(SettingsActivity.KEY_PREF_USERBUCHSTABE, "");
                String mUrl = ApiInfo.getUrl() + "/plan/" + dayEnd + "/" + klasse + buchstabe;

                // mUrl = "https://mar-eu-1-ghb1rw62.qtcloudapp.com/plan/S-2015-11-23.htm/11/";


                // Creating service handler class instance
                ServiceHandler sh = new ServiceHandler();

                String response = sh.makeServiceCall(mUrl, ApiInfo.getKey());

                if (response != null) {
                    if (response != null) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            StringBuilder stringBuilder = new StringBuilder();

                            if(jsonObj.has("msg")) {
                                JSONArray infoMessageArr = jsonObj.getJSONArray("msg");

                                for (int i = 0; i < infoMessageArr.length(); i++) {
                                    if (stringBuilder.length() > 0) {
                                        stringBuilder.append("\n");
                                    }
                                    stringBuilder.append(infoMessageArr.getString(i));
                                }
                            }

                            JSONArray jsonArr = jsonObj.getJSONArray("items");
                            String msg;
                            if (stringBuilder.toString().length() == 0){
                                msg = getString(R.string.no_msg_of_day);
                            }else{
                                msg = stringBuilder.toString();
                            }
                            String title = "Du hast heute " + jsonArr.length() + " Vertretungsstunden";

                            // Creates an explicit intent for an Activity in your app
                            Intent resultIntent = new Intent(this, StartActivity.class);

                            // The stack builder object will contain an artificial back stack for the
                            // started Activity.
                            // This ensures that navigating backward from the Activity leads out of
                            // your application to the Home screen.
                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                            // Adds the back stack for the Intent (but not the Intent itself)
                            stackBuilder.addParentStack(MainActivity.class);
                            // Adds the Intent that starts the Activity to the top of the stack
                            stackBuilder.addNextIntent(resultIntent);
                            PendingIntent resultPendingIntent =
                                    stackBuilder.getPendingIntent(
                                            0,
                                            PendingIntent.FLAG_UPDATE_CURRENT
                                    );


                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                            // Expanded Notification
                            NotificationCompat.BigTextStyle bStyle = new NotificationCompat.BigTextStyle();
                            bStyle.setBigContentTitle(title);
                            bStyle.setSummaryText(getString(R.string.no_msg_of_day));
                            bStyle.bigText(msg);
                            //
                            Notification notification =mBuilder.setSmallIcon(R.drawable.ic_notifications_black_24dp)
                                    .setTicker(title).setWhen(0)
                                    .setContentTitle(title)
                                    .setStyle(bStyle)
                                    .setContentText(msg)
                                            .setAutoCancel(true)
                                            .setContentIntent(resultPendingIntent)
                                            .build();

                            NotificationManager mNotificationManager =
                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            // mId allows you to update the notification later on.
                            mNotificationManager.notify(0, notification);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("ServiceHandler", "Couldn't get any data from the url");
                    }
                }
            }
        }
    }
}
