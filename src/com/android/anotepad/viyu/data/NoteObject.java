package com.android.anotepad.viyu.data;

import java.util.HashMap;

import com.android.anotepad.viyu.utility.Constant;

public class NoteObject<K, V> extends HashMap<String, Object> implements Comparable<NoteObject<K, V>> {

	private static final long serialVersionUID = 1L;
	
	//The xml file name which will be saved if user want to save it, is null if this object is loaded from xml
	private String fileName = null;
	//The note content, if == null, indicate this is a new created note
	private String content = null;
	//Text color
	private ColorSeries textColor = null;
	//Alarm info object
	private AlarmObject alarm = null;
	//
	private int alarmCode = -1;
	//
	private long lastModified = -1;
	/**
	 * Call this constructor when loading  note from xml
	 * @param fileName
	 * @param content
	 * @param textColor
	 * @param lastModified
	 */
	public NoteObject(String fileName, String content, AlarmObject alarm, ColorSeries textColor, long lastModified) {
		this.fileName = fileName;
		this.content = content;
		this.textColor = textColor;
		this.alarm = alarm;
		this.alarmCode = this.fileName.hashCode();
		this.lastModified = lastModified;
		
		put(Constant.NOTE_LIST_COLUMN_CONTENT, this);
		put(Constant.NOTE_LIST_COLUMN_LASTMODIFIED, lastModified);
	}
	
	/**
	 * Alarm code, identify the PendingIntent for this alarm, it's corresponding with file name
	 */
	public int getAlarmCode() {
		return this.alarmCode;
	}
	
	public String getContent() {
		return this.content;
	}
	
	public String getFileName() {
		return this.fileName;
	}
	
	public ColorSeries getTextColor() {
		return this.textColor;
	}
	
	public void setTextColor(ColorSeries textColor) {
		this.textColor = textColor;
	}
	
	public boolean noteEquals(NoteObject<String, String> noteObj) {
		return noteObj.getContent().equals(this.content) 
				&& noteObj.getTextColor().equals(this.textColor)
				&& noteObj.getAlarm().equals(this.alarm);
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public AlarmObject getAlarm() {
		return this.alarm;
	}
	
	public void setAlarm(AlarmObject alarm) {
		this.alarm = alarm;
	}

	public NoteObject<String, String> copy() {
		return new NoteObject<String, String>(fileName, content, alarm, textColor, -1);
	}
	
	@Override
	public String toString() {
		return this.content;
	}

	@Override
	public int compareTo(NoteObject<K, V> another) {
		if(this.lastModified > another.lastModified) {
			return -1;
		} else if(this.lastModified < another.lastModified) {
			return 1;
		} else {
			return 0;
		}
	}
}