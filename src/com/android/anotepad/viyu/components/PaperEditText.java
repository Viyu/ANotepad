package com.android.anotepad.viyu.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;

import com.android.anotepad.viyu.data.ColorSeries;

public class PaperEditText extends EditText {
	
	private Paint paint = null;
	private boolean drawLine = true;
	
	public PaperEditText(Context context) {
		super(context);
		initPaintObj();
	}
	
	 public PaperEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaintObj();
	 }
	
	private void initPaintObj() {
		paint = new Paint(); 
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
		paint.setAlpha(255);
	}
	
	public void setDrawLine(boolean flag) {
		this.drawLine = flag;
		this.invalidate();
	}
	
	public void setColorSeries(ColorSeries color) {
		paint.setColor(getResources().getColor(color.halfValue()));
		super.setTextColor(getResources().getColor(color.value()));
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if(drawLine) {//draw line
			int dis = 5;
			int lineHeight = this.getLineHeight();
			int topPadding = this.getPaddingTop();
			int leftPadding = this.getPaddingLeft();
			float textSize = this.getTextSize();
			
			setGravity(Gravity.LEFT | Gravity.TOP);
			int y = (int) (topPadding + textSize);
			int num = this.getHeight() / lineHeight;
			int lineCount = this.getLineCount();
			if (num > lineCount) {
				for (int i = 0; i < lineCount; i++) {
					canvas.drawLine(leftPadding, y + dis, getRight() - leftPadding, y + dis, paint);
					y += lineHeight;
				}
				for (int i = lineCount + 1; i < num; i++) {
					canvas.drawLine(leftPadding, y + dis, getRight() - leftPadding, y + dis, paint);
					y += lineHeight;
				}
			} else {
				for (int i = 0; i < lineCount; i++) {
					canvas.drawLine(leftPadding, y + dis, getRight() - leftPadding, y + dis, paint);
					y += lineHeight;
				}
			}
		}
		
		//draw super
		super.onDraw(canvas);
	}
		
}