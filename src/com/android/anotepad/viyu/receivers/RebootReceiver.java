package com.android.anotepad.viyu.receivers;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.anotepad.viyu.data.AlarmObject;
import com.android.anotepad.viyu.data.NoteObject;
import com.android.anotepad.viyu.data.NoteXmlHandler;
import com.android.anotepad.viyu.utility.Constant;

public class RebootReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
        	//register alarms after reboot
        	List<NoteObject<String, String>> list = NoteXmlHandler.getInstance(context).loadNoteList();
        	for(NoteObject<String, String> noteObj : list) {
        		AlarmObject alarm = noteObj.getAlarm();
        		if(!alarm.isNull()) {
	    			Intent alarmIntent = new Intent(context, AlarmReceiver.class);
    				alarmIntent.putExtra(Constant.INTENT_EXTRA_FILE_NAME, noteObj.getFileName());
    				alarmIntent.putExtra(Constant.INTENT_EXTRA_CONTENT, noteObj.getContent());
    				alarmIntent.putExtra(Constant.INTENT_EXTRA_TEXTCOLOR_KEY, noteObj.getTextColor().convertToKey());
    				alarmIntent.putExtra(Constant.ALARM_CODE, noteObj.getAlarmCode());
    				PendingIntent pendingIntent = PendingIntent.getBroadcast(context, noteObj.getAlarmCode(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    				AlarmManager amarmManager = (AlarmManager)context.getSystemService(Activity.ALARM_SERVICE);
    				Calendar calendar = Calendar.getInstance();
    				calendar.setTimeInMillis(System.currentTimeMillis());
    				calendar.set(Calendar.YEAR, alarm.getYear());
    				calendar.set(Calendar.MONTH, alarm.getMonth());
    				calendar.set(Calendar.DAY_OF_MONTH, alarm.getDay());
    				calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
    				calendar.set(Calendar.MINUTE, alarm.getMinute());
    				calendar.set(Calendar.SECOND, 0);
    				calendar.set(Calendar.MILLISECOND, 0);
    				amarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        		}
        	}
        }
    }
}
