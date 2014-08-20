package com.gmail.dailyefforts.merge;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class SingleGrid extends TextView {

	public SingleGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
		post(new Runnable() {
			@Override
			public void run() {
				setHeight(getWidth());
			}
		});
	}
}
