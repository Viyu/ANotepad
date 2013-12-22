package com.android.anotepad.viyu.activities;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.anotepad.viyu.R;
import com.android.anotepad.viyu.components.MultiColorButton;
import com.android.anotepad.viyu.components.PaperEditText;
import com.android.anotepad.viyu.data.AlarmObject;
import com.android.anotepad.viyu.data.ColorSeries;
import com.android.anotepad.viyu.data.NoteObject;
import com.android.anotepad.viyu.data.NoteXmlHandler;
import com.android.anotepad.viyu.receivers.AlarmReceiver;
import com.android.anotepad.viyu.utility.Constant;

public class EditNoteActivity extends Activity implements DialogInterface.OnClickListener, OnTimeSetListener, OnDateSetListener {

	private Calendar calendar = Calendar.getInstance();
	private AlertDialog alertDialog = null;
	
	private NoteObject<String, String> noteObj = null;
	private NoteObject<String, String> preNoteObj = null;

	private boolean isSaved = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_note);
		//load environment
		loadEnvironment();
	}
	
	@Override
	protected void onStart() {
		isSaved  = false;
		super.onStart();
	}


	@Override
	protected void onRestart() {
		isSaved  = false;
		super.onRestart();
	}

	@Override
	protected void onResume() {
		isSaved  = false;
		super.onResume();
	}


	@Override
	protected void onStop() {
		isSaved = saveNote();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		isSaved = saveNote();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		isSaved = saveNote();
		super.onBackPressed();		
	}
	
	private void loadEnvironment() {
		//Make sure which activity start this activity
		Intent intent = getIntent();
        String fileName = intent.getStringExtra(Constant.INTENT_EXTRA_FILE_NAME);
        String content = intent.getStringExtra(Constant.INTENT_EXTRA_CONTENT);
        String alarmMessage = intent.getStringExtra(Constant.INTENT_EXTRA_ALARMK_MESSAGE);
        int textColorKey = intent.getIntExtra(Constant.INTENT_EXTRA_TEXTCOLOR_KEY, Constant.KEY_TEXT_COLOR_WHITE);
		
        //if it's creating a new note
        if(content == null && alarmMessage.equals(Constant.MESSAGE_ALARM_NOALARM) 
        		&& textColorKey == Constant.KEY_TEXT_COLOR_WHITE) {
        	content = "";
        	textColorKey = Constant.KEY_TEXT_COLOR_WHITE;
        }

		noteObj = new NoteObject<String, String>(fileName, content, AlarmObject.convertToAlarm(alarmMessage), 
				ColorSeries.convertToTextColor(textColorKey), -1);
		//cache previous note object to check whether this note has been changed
		preNoteObj = noteObj.copy();

		MultiColorButton colorButton = (MultiColorButton)findViewById(R.id.TextColorButton);
		colorButton.setButtonColor(noteObj.getTextColor());
		
		PaperEditText editText = (PaperEditText)findViewById(R.id.NoteEditTextId);
		editText.setText(noteObj.getContent());
		editText.setColorSeries(noteObj.getTextColor());
		
		isSaved  = false;
	}
	
	private boolean saveNote() {
		if(isSaved) {
			return true;
		}
		EditText noteEditText = (EditText)findViewById(R.id.NoteEditTextId);
		String noteText = noteEditText.getText().toString();
		noteObj.setContent(noteText);

		if(preNoteObj.getContent().length() < 1 && noteObj.getContent().length() < 1) {
			return true;
		}
		if(noteObj.noteEquals(preNoteObj)) {
			return true;
		}
		
		//register alarm if exist
		if(!noteObj.getAlarm().isNull()) {
			Intent intent = new Intent(this, AlarmReceiver.class);
			PendingIntent pi = PendingIntent.getBroadcast(this, noteObj.getAlarmCode(), intent, PendingIntent.FLAG_NO_CREATE);
			//Check whether this alarm has not been registered or the alarm is overlaped
			if((pi == null) || (pi != null && !noteObj.getAlarm().equals(preNoteObj.getAlarm()))) {
				intent.putExtra(Constant.INTENT_EXTRA_FILE_NAME, noteObj.getFileName());
				intent.putExtra(Constant.INTENT_EXTRA_CONTENT, noteObj.getContent());
				intent.putExtra(Constant.INTENT_EXTRA_TEXTCOLOR_KEY, noteObj.getTextColor().convertToKey());
				intent.putExtra(Constant.ALARM_CODE, noteObj.getAlarmCode());
				PendingIntent pendingIntent = PendingIntent.getBroadcast(this, noteObj.getAlarmCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
				AlarmManager amarmManager = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
				amarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
			} 
		}
		//Save it to xml
		NoteXmlHandler.getInstance(this).saveNote(noteObj);
		Toast.makeText(this, getResources().getString(R.string.notehasbeensaved), Toast.LENGTH_LONG).show();
		System.out.println(getResources().getString(R.string.notehasbeensaved));
		return true;
	}

	public void nextTextColor(View view) {
		ColorSeries color = ((MultiColorButton)view).nextColor();
		PaperEditText noteEditText = (PaperEditText)findViewById(R.id.NoteEditTextId);
		noteEditText.setColorSeries(color);
		//
		noteObj.setTextColor(color);
	}
	
	public void drawLineCheck(View view) {
		PaperEditText noteEditText = (PaperEditText)findViewById(R.id.NoteEditTextId);
		noteEditText.setDrawLine(((CheckBox)view).isChecked());
	}
	
	public void setAlarm(View view) {
		if(alertDialog == null) {
			LayoutInflater inflater = getLayoutInflater();
			View layout = inflater.inflate(R.layout.alarm_dialog_layout,(ViewGroup) findViewById(R.id.alarm_layout));
			alertDialog = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
			.setTitle(R.string.alarm_setting_title)
			.setView(layout)
			.setPositiveButton(R.string.positive_ok, this)
			.setNegativeButton(R.string.negative_cancel, this)
			.setNeutralButton(R.string.neutral_delete, this)
			.create();
		}
		//load existing alarm if alarm existing, otherwise, load current time
		calendar.setTimeInMillis(System.currentTimeMillis());
		AlarmObject alarm = noteObj.getAlarm();
		if(!alarm.isNull()) {
			calendar.set(Calendar.YEAR, alarm.getYear());
			calendar.set(Calendar.MONTH, alarm.getMonth());
			calendar.set(Calendar.DAY_OF_MONTH, alarm.getDay());
			calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
			calendar.set(Calendar.MINUTE, alarm.getMinute());
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
		}
		alertDialog.show();
		((Button)alertDialog.findViewById(R.id.alarm_time_chooser)).setText(DateFormat.getTimeFormat(this).format(calendar.getTime()));
		((Button)alertDialog.findViewById(R.id.alarm_date_chooser)).setText(DateFormat.getDateFormat(this).format(calendar.getTime()));
	}
	
	public void backToMain(View view) {
		isSaved = saveNote();
		super.onBackPressed();		
	}
	
	public void pickAlarmDate(View view) {
		(new DatePickerDialog(this, DatePickerDialog.THEME_DEVICE_DEFAULT_DARK, this, 
				calendar.get(Calendar.YEAR), 
				calendar.get(Calendar.MONTH), 
				calendar.get(Calendar.DAY_OF_MONTH))).show();
	}
	
	public void pickAlarmTime(View view) {
		(new TimePickerDialog(this, TimePickerDialog.THEME_DEVICE_DEFAULT_DARK, this, 
				calendar.get(Calendar.HOUR_OF_DAY), 
				calendar.get(Calendar.MINUTE), true)).show();
	}
	
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);		
		((Button)alertDialog.findViewById(R.id.alarm_time_chooser)).setText(DateFormat.getTimeFormat(this).format(calendar.getTime()));
	}
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, monthOfYear);
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		((Button)alertDialog.findViewById(R.id.alarm_date_chooser)).setText(DateFormat.getDateFormat(this).format(calendar.getTime()));
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch(which) {
		case DialogInterface.BUTTON_POSITIVE: {
			StringBuffer strBuff = new StringBuffer();
			strBuff.append(calendar.get(Calendar.YEAR)).append(Constant.SEPARATOR_ALARM_MESSAGE)
			.append(calendar.get(Calendar.MONTH)).append(Constant.SEPARATOR_ALARM_MESSAGE)
			.append(calendar.get(Calendar.DAY_OF_MONTH)).append(Constant.SEPARATOR_ALARM_MESSAGE)
			.append(calendar.get(Calendar.HOUR_OF_DAY)).append(Constant.SEPARATOR_ALARM_MESSAGE)
			.append(calendar.get(Calendar.MINUTE));
			noteObj.setAlarm(AlarmObject.convertToAlarm(strBuff.toString()));
			break;
		}
		
		case DialogInterface.BUTTON_NEUTRAL: {
			Intent intent = new Intent(this, AlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(this, noteObj.getAlarmCode(), intent, PendingIntent.FLAG_NO_CREATE);
			if(pendingIntent != null) {
				AlarmManager amarmManager = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
				amarmManager.cancel(pendingIntent);
				pendingIntent.cancel();
				noteObj.setAlarm(AlarmObject.convertToAlarm(Constant.MESSAGE_ALARM_NOALARM));
			}
			break;
		}
		}
	}
}
