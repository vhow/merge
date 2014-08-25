
package com.gmail.dailyefforts.merge;

import java.util.ArrayList;
import java.util.Random;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextSwitcher;
import android.widget.ViewSwitcher.ViewFactory;

public class MainActivity extends Activity {

    private static final String PREFS_FIlE_NAME = "prefs";
    private static final String MAX_SCORE = "max_score";
    private static final int EMPTY = 0;
    private static final int ROW = 4;
    private static final int COL = 4;
    private static int[][] sMatrix = new int[ROW][COL];
    private static ArrayList<Integer> sEmptyList = new ArrayList<Integer>();
    private static final int[] BgIds = {
            R.drawable.item_bg,
            R.drawable.item_bg_2, R.drawable.item_bg_4, R.drawable.item_bg_8,
            R.drawable.item_bg_16, R.drawable.item_bg_32,
            R.drawable.item_bg_64, R.drawable.item_bg_128,
            R.drawable.item_bg_256, R.drawable.item_bg_512,
            R.drawable.item_bg_1024, R.drawable.item_bg_2048,
    };
    private NumbersGridView mGridView;
    private long mCurrentSorce;
    private long mMaxScore;
    private GridAdapter mAdapter;
    private SharedPreferences mSharedPrefs;
    private TextSwitcher mScoreTextSwitcher;
    private TextSwitcher mMaxScoreTextSwitcher;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear:
                init();
                mAdapter.notifyDataSetChanged();
                udpateScore();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void init() {
        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COL; col++) {
                sMatrix[row][col] = EMPTY;
            }
        }
        Random random = new Random();
        int first = random.nextInt(ROW * COL);
        int second = random.nextInt(ROW * COL);
        while (first == second) {
            second = random.nextInt(ROW * COL);
        }
        setValue(first, 2);
        setValue(second, 2);
        mCurrentSorce = 0;
    }

    private void setValue(final int position, final int value) {
        assert (position > 0 && position < ROW * COL);
        final int row = position / COL;
        final int col = position % COL;
        set(row, col, value);
    }

    private void set(final int row, final int col, final int value) {
        sMatrix[row][col] = value;
    }

    private void updateEmptyList() {
        sEmptyList.clear();
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (sMatrix[i][j] == EMPTY) {
                    sEmptyList.add(i * ROW + j);
                }
            }
        }
        newNum();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onPause() {
        super.onPause();
        final long max = mSharedPrefs.getLong(MAX_SCORE, 0L);
        if (max < mMaxScore) {
            final Editor editor = mSharedPrefs.edit();
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
                editor.putLong(MAX_SCORE, mMaxScore).apply();
            } else {
                editor.putLong(MAX_SCORE, mMaxScore).commit();
            }
        }
    }

    private boolean newNum() {
        udpateScore();
        final int len = sEmptyList.size();
        if (len > 0) {
            final int index = new Random().nextInt(len);
            final int position = sEmptyList.get(index);
            setValue(position, 2);
            final View child = mGridView.getChildAt(position);
            if (child != null) {
                child.startAnimation(AnimationUtils.loadAnimation(
                        child.getContext(), R.anim.fade_in));
            }
            return true;
        }
        return false;
    }

    private void udpateScore() {
        if (mScoreTextSwitcher != null && mMaxScoreTextSwitcher != null) {
            mScoreTextSwitcher.setText(String.valueOf(mCurrentSorce));
            if (mCurrentSorce > mMaxScore) {
                mMaxScore = mCurrentSorce;
                mMaxScoreTextSwitcher.setText(String.valueOf(mMaxScore));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGridView = (NumbersGridView) findViewById(R.id.gridView);
        mAdapter = new GridAdapter();
        mGridView.setOnChangedListener(new NumbersGridView.OnSwipeListener() {
            @Override
            public void onSwipe(final int direction) {
                switch (direction) {
                    case Direction.LEFT:
                        for (int row = 0; row < ROW; row++) {
                            for (int col = 0; col < COL - 1; col++) {
                                final int current = sMatrix[row][col];
                                final int next = sMatrix[row][col + 1];
                                if (current == next || current == EMPTY) {
                                    sMatrix[row][col] += next;
                                    sMatrix[row][col + 1] = EMPTY;
                                    mCurrentSorce += current;
                                }
                            }
                        }
                        break;
                    case Direction.RIGHT:
                        for (int row = 0; row < ROW; row++) {
                            for (int col = COL - 1; col > 0; col--) {
                                final int current = sMatrix[row][col];
                                final int next = sMatrix[row][col - 1];
                                if (current == next || current == EMPTY) {
                                    sMatrix[row][col] += next;
                                    sMatrix[row][col - 1] = EMPTY;
                                    mCurrentSorce += current;
                                }
                            }
                        }
                        break;
                    case Direction.UP:
                        for (int col = 0; col < COL; col++) {
                            for (int row = 0; row < ROW - 1; row++) {
                                final int current = sMatrix[row][col];
                                final int next = sMatrix[row + 1][col];
                                if (current == next || current == EMPTY) {
                                    sMatrix[row][col] += next;
                                    sMatrix[row + 1][col] = EMPTY;
                                    mCurrentSorce += current;
                                }
                            }
                        }
                        break;
                    case Direction.DOWN:
                        for (int col = 0; col < COL; col++) {
                            for (int row = ROW - 1; row > 0; row--) {
                                final int current = sMatrix[row][col];
                                final int next = sMatrix[row - 1][col];
                                if (current == next || current == EMPTY) {
                                    sMatrix[row][col] += next;
                                    sMatrix[row - 1][col] = EMPTY;
                                    mCurrentSorce += current;
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
                updateEmptyList();
                mAdapter.notifyDataSetChanged();
            }
        });
        init();
        mGridView.setAdapter(mAdapter);
        mSharedPrefs = getSharedPreferences(PREFS_FIlE_NAME, MODE_PRIVATE);
        mCurrentSorce = 0;
        mMaxScore = mSharedPrefs.getLong(MAX_SCORE, 0L);
        final ViewFactory factory = new ViewFactory() {

            @Override
            public View makeView() {
                return getLayoutInflater().inflate(R.layout.score_text_view,
                        null);
            }
        };
        mScoreTextSwitcher = (TextSwitcher) findViewById(R.id.score);
        mScoreTextSwitcher.setFactory(factory);
        final Animation outAnim = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.fade_out);
        final Animation inAnim = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.fade_in);
        mScoreTextSwitcher.setInAnimation(inAnim);
        mScoreTextSwitcher.setOutAnimation(outAnim);
        mScoreTextSwitcher.setText(String.valueOf(mCurrentSorce));
        mMaxScoreTextSwitcher = (TextSwitcher) findViewById(R.id.maxScore);
        mMaxScoreTextSwitcher.setFactory(factory);
        mMaxScoreTextSwitcher.setInAnimation(inAnim);
        mMaxScoreTextSwitcher.setOutAnimation(outAnim);
        mMaxScoreTextSwitcher.setText(String.valueOf(mMaxScore));
    }

    private static class ViewHolder {
        NumberTextView tv;
    }

    private class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return ROW * COL;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = (NumberTextView) getLayoutInflater().inflate(
                        R.layout.number_text_view, null);
                convertView.setBackgroundResource(BgIds[3]);
                holder = new ViewHolder();
                holder.tv = (NumberTextView) convertView;
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            int row = position / COL;
            int col = position % COL;
            assert (row >= 0 && col >= 0);
            final int value = sMatrix[row][col];
            if (value > 0) {
                holder.tv.setText(String.valueOf(value));
                int index = 0;
                for (int v = 2, i = 0, len = BgIds.length; v <= 2048 && i < len; v *= 2, i++) {
                    if (Math.pow(2, i) >= value) {
                        index = i;
                        break;
                    }
                }
                holder.tv.setBackgroundResource(BgIds[index]);
            } else {
                holder.tv.setText("");
                holder.tv.setBackgroundResource(BgIds[0]);
            }
            return convertView;
        }

    }

}
