package com.android.anotepad.viyu.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.android.anotepad.viyu.R;
import com.android.anotepad.viyu.activities.EditNoteActivity;
import com.android.anotepad.viyu.data.AlarmObject;
import com.android.anotepad.viyu.data.ColorSeries;
import com.android.anotepad.viyu.data.NoteObject;
import com.android.anotepad.viyu.data.NoteXmlHandler;
import com.android.anotepad.viyu.utility.Constant;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
        //get extra message
		String content = intent.getStringExtra(Constant.INTENT_EXTRA_CONTENT);
		String fileName = intent.getStringExtra(Constant.INTENT_EXTRA_FILE_NAME);
		int textColorKey = intent.getIntExtra(Constant.INTENT_EXTRA_TEXTCOLOR_KEY, Constant.KEY_TEXT_COLOR_WHITE);
		int alarmCode = intent.getIntExtra(Constant.ALARM_CODE, -1);
		//resave xml file to remove alarm
		NoteObject<String, String> noteObj = new NoteObject<String, String>(fileName, content, 
				AlarmObject.convertToAlarm(Constant.MESSAGE_ALARM_NOALARM), 
				ColorSeries.convertToTextColor(textColorKey), -1);
		NoteXmlHandler.getInstance(context).saveNote(noteObj);
		
		//launch notification
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle(context.getResources().getString(R.string.app_name))
		        .setContentText(content);
		Intent resultIntent = new Intent(context, EditNoteActivity.class);
		resultIntent.putExtra(Constant.INTENT_EXTRA_FILE_NAME, fileName);
		resultIntent.putExtra(Constant.INTENT_EXTRA_CONTENT, content);
		resultIntent.putExtra(Constant.INTENT_EXTRA_TEXTCOLOR_KEY, textColorKey);
		resultIntent.putExtra(Constant.INTENT_EXTRA_ALARMK_MESSAGE, Constant.MESSAGE_ALARM_NOALARM);
		//
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(EditNoteActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(alarmCode, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setAutoCancel(true);
		mBuilder.setDefaults(Notification.DEFAULT_ALL);
		NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(alarmCode, mBuilder.build());
	}
}