package com.android.anotepad.viyu.data;

import com.android.anotepad.viyu.utility.Constant;

/**
 *
 */
public class AlarmObject {
	private int year = -1;
	private int month = -1;
	private int day = -1;
	private int hour = -1;
	private int minute = -1;

	public AlarmObject(int year, int month, int day, int hour, int minute) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
	}

	public static AlarmObject convertToAlarm(String message) {
		if (message.equals(Constant.MESSAGE_ALARM_NOALARM)) {
			return new AlarmObject(-1, -1, -1, -1, -1);
		}
		try {
			AlarmObject obj = new AlarmObject(-1, -1, -1, -1, -1);
			String[] strs = message.split(Constant.SEPARATOR_ALARM_MESSAGE);
			obj.year = Integer.parseInt(strs[0]);
			obj.month = Integer.parseInt(strs[1]);
			obj.day = Integer.parseInt(strs[2]);
			obj.hour = Integer.parseInt(strs[3]);
			obj.minute = Integer.parseInt(strs[4]);
			return obj;
		} catch (Exception ex) {

		}
		return new AlarmObject(-1, -1, -1, -1, -1);
	}
	
	public String convertToMessage() {
		if(this.isNull()) {
			return Constant.MESSAGE_ALARM_NOALARM;
		}
		StringBuffer strBuff = new StringBuffer();
		strBuff.append(this.year).append(Constant.SEPARATOR_ALARM_MESSAGE)
		.append(this.month).append(Constant.SEPARATOR_ALARM_MESSAGE)
		.append(this.day).append(Constant.SEPARATOR_ALARM_MESSAGE)
		.append(this.hour).append(Constant.SEPARATOR_ALARM_MESSAGE)
		.append(this.minute);
		return strBuff.toString();
	}
	
	public boolean isNull() {
		return this.year == -1 || this.month == -1 || this.day == -1 || this.hour == -1 || this.minute == -1;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof AlarmObject) {
			AlarmObject obj = (AlarmObject)o;
			return this.year == obj.year && this.month == obj.month && this.day == obj.day
					&& this.hour == obj.hour && this.minute == obj.minute;
		} else {
			return false;
		}
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}
}