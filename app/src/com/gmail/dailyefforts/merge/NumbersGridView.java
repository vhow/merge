package com.gmail.dailyefforts.merge;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.GridView;

public class NumbersGridView extends GridView {

	private GestureDetector mDetecter;

	private void swipeRight() {
		mOnChangedListener.swipeRight();
	}

	private void swipeLeft() {
		mOnChangedListener.swipeLeft();
	}

	private void swipeUp() {
		mOnChangedListener.swipeUp();
	}

	private void swipeDown() {
		mOnChangedListener.swipeDown();
	}

	public interface OnChangedListener {
		void swipeRight();

		void swipeLeft();

		void swipeUp();

		void swipeDown();
	}

	OnChangedListener mOnChangedListener;

	public void setOnChangedListener(OnChangedListener listener) {
		mOnChangedListener = listener;
	}

	public NumbersGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDetecter = new GestureDetector(context,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						final float absX = Math.abs(velocityX);
						final float absY = Math.abs(velocityY);
						float deltaX = e2.getX() - e1.getX();
						float deltaY = e2.getY() - e1.getY();
						int travelX = getWidth() / 10;
						int travelY = getHeight() / 10;
						if (absY < absX && deltaX > travelX) {
							swipeRight();
							return true;
						} else if (absY < absX && deltaX < -travelX) {
							swipeLeft();
							return true;
						} else if (absX < absY && deltaY < -travelY) {
							swipeUp();
							return true;
						} else if (absX < absY / 2 && deltaY > travelY) {
							swipeDown();
							return true;

						}
						return false;
					}
				});

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mDetecter.onTouchEvent(event);
		return true;
	}

}
