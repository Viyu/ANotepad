package com.android.anotepad.viyu.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.android.anotepad.viyu.R;
import com.android.anotepad.viyu.data.ColorSeries;

public class MultiColorButton extends ImageButton {

	private static final int[] WHITE = { R.attr.white};
	private static final int[] PINK = { R.attr.pink};
	private static final int[] YELLOW = { R.attr.yellow };
	
	private ColorSeries[] colors = new ColorSeries[]{ColorSeries.White, ColorSeries.Pink, ColorSeries.Yellow};;
	
	//the index of category colors
	private int index = 0;
	
	public MultiColorButton(Context context) {
		super(context);
	}

	public MultiColorButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
		
	@Override
	public int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 3);
		if (ColorSeries.White.isTrue()) {
	        mergeDrawableStates(drawableState, WHITE);
	    }
	    if (ColorSeries.Pink.isTrue()) {
	        mergeDrawableStates(drawableState, PINK);
	    }
	    if (ColorSeries.Yellow.isTrue()) {
	        mergeDrawableStates(drawableState, YELLOW);
	    }
	    return drawableState;
	}
	
	public ColorSeries nextColor() {
		index = (index + 1) % colors.length;
		for(int i = 0; i < colors.length; i++) {
			if(i != index) {
				colors[i].setTrue(false);
			} else {
				colors[i].setTrue(true);
			}
		}
		return colors[index];
	}
	
	public void setButtonColor(ColorSeries textColor) {
		for(ColorSeries color : colors) {
			if(textColor == color) {
				color.setTrue(true);
			} else {
				color.setTrue(false);
			}
		}
	}
	
	public ColorSeries currentColor() {
		for(ColorSeries color : colors) {
			if(color.isTrue()) {
				return color;
			}
		}
		return ColorSeries.White;
	}
}