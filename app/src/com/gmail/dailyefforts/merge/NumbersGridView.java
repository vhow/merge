
package com.gmail.dailyefforts.merge;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.GridView;

public class NumbersGridView extends GridView {

    private GestureDetector mDetecter;
    private OnSwipeListener mOnSwipeListener;

    public interface OnSwipeListener {
        void onSwipe(final int direction);
    }

    public void setOnChangedListener(OnSwipeListener listener) {
        mOnSwipeListener = listener;
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
                        final float deltaX = e2.getX() - e1.getX();
                        final float deltaY = e2.getY() - e1.getY();
                        final int travelX = getWidth() / 10;
                        final int travelY = getHeight() / 10;
                        if (absY < absX && deltaX < -travelX) {
                            mOnSwipeListener.onSwipe(Direction.LEFT);
                            return true;
                        } else if (absY < absX && deltaX > travelX) {
                            mOnSwipeListener.onSwipe(Direction.RIGHT);
                            return true;
                        } else if (absX < absY && deltaY > travelY) {
                            mOnSwipeListener.onSwipe(Direction.DOWN);
                            return true;
                        } else if (absX < absY && deltaY < -travelY) {
                            mOnSwipeListener.onSwipe(Direction.UP);
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
