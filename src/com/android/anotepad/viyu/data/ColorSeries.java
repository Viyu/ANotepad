package com.android.anotepad.viyu.data;

import com.android.anotepad.viyu.R;
import com.android.anotepad.viyu.utility.Constant;

public enum ColorSeries {
	White(R.color.white, R.color.white_half), 
	Pink(R.color.pink, R.color.pink_half), 
	Yellow(R.color.yellow, R.color.yellow_half);
	private boolean isTrue = false;
	private int value = -1;
	private int halfValue = -1;
	
	private ColorSeries(int value, int halfValue) {
		this.value = value;
		this.halfValue = halfValue;
	}

	public static ColorSeries convertToTextColor(int key) {
		switch(key) {
		case Constant.KEY_TEXT_COLOR_WHITE:
			return White;
		case Constant.KEY_TEXT_COLOR_PINK:
			return Pink;
		case Constant.KEY_TEXT_COLOR_YELLOW:
			return Yellow;
		default:
			return White;
		}
	}
	
	public int convertToKey() {
		switch(this) {
		case White:
			return Constant.KEY_TEXT_COLOR_WHITE;
		case Pink:
			return Constant.KEY_TEXT_COLOR_PINK;
		case Yellow:
			return Constant.KEY_TEXT_COLOR_YELLOW;
		default:
			return Constant.KEY_TEXT_COLOR_WHITE;
		}
	}
	
	public void setTrue(boolean flag) {
		this.isTrue = flag;
	}
	
	public boolean isTrue() {
		return this.isTrue;
	}
	
	public int value() {
		return this.value;
	}
	
	public int halfValue() {
		return this.halfValue;
	}
}
