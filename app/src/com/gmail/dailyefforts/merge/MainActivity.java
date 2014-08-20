package com.gmail.dailyefforts.merge;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String MAX_SCORE = "max_score";
	private static final int EMPTY = 0;
	static int[][] a = new int[4][4];

	private static final int ROW = 4;
	private static final int COL = 4;

	private long mCurrentSorce;
	private long mMaxScore;
	private static NumbersGridView mGridView;

	private static final int[] BgIds = { R.drawable.item_bg,
			R.drawable.item_bg_2, R.drawable.item_bg_4, R.drawable.item_bg_8,
			R.drawable.item_bg_16, R.drawable.item_bg_32,
			R.drawable.item_bg_64, R.drawable.item_bg_128,
			R.drawable.item_bg_256, R.drawable.item_bg_512,
			R.drawable.item_bg_1024, R.drawable.item_bg_2048, };

	private GridAdapter adapter;
	private SharedPreferences mSharedPrefs;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_clear:
			init();
			adapter.notifyDataSetChanged();
			udpateScore();
			break;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		// Locate MenuItem with ShareActionProvider
		// MenuItem singleGrid = menu.findItem(R.id.menu_item_share);
		// Fetch and store ShareActionProvider
		// mShareActionProvider = (ShareActionProvider)
		// singleGrid.getActionProvider();
		return true;
	}

	private void init() {
		for (int row = 0; row < ROW; row++) {
			for (int col = 0; col < COL; col++) {
				a[row][col] = EMPTY;
			}
		}
		Random random = new Random();
		int first = random.nextInt(ROW * COL);
		int second = random.nextInt(ROW * COL);
		while (first == second) {
			second = random.nextInt(ROW * COL);
		}
		set(first, 2);
		set(second, 2);
		mCurrentSorce = 0;
	}

	private static ArrayList<Integer> emptyList = new ArrayList<Integer>();

	private void set(final int position, final int value) {
		assert (position > 0 && position < ROW * COL);
		final int row = position / COL;
		final int col = position % COL;
		set(row, col, value);
	}

	private void set(final int row, final int col, final int value) {
		a[row][col] = value;
	}

	private void updateEmptyList() {
		emptyList.clear();
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				if (a[i][j] == EMPTY) {
					emptyList.add(i * ROW + j);
				}
			}
		}
		newNum();
	}

	@Override
	protected void onPause() {
		super.onPause();
		long max = mSharedPrefs.getLong(MAX_SCORE, 0L);
		if (max < mMaxScore) {
			mSharedPrefs.edit().putLong(MAX_SCORE, mMaxScore).commit();
		}
	}

	private boolean newNum() {
		udpateScore();
		final int len = emptyList.size();
		if (len > 0) {
			int index = new Random().nextInt(len);
			int position = emptyList.get(index);
			set(position, 2);

			View child = mGridView.getChildAt(position);
			if (child != null) {
				child.startAnimation(AnimationUtils.loadAnimation(
						child.getContext(), R.anim.fade_in));
			}
			return true;
		}
		return false;
	}

	private void udpateScore() {
		TextView tv = (TextView) findViewById(R.id.score);
		if (tv != null) {
			tv.setText(String.valueOf(mCurrentSorce));
			if (mCurrentSorce > mMaxScore) {
				TextView max = (TextView) findViewById(R.id.maxScore);
				mMaxScore = mCurrentSorce;
				max.setText(String.valueOf(mMaxScore));
			}
		}
	}

	static void display() {
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				System.out.print(a[i][j] + "\t");
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mGridView = (NumbersGridView) findViewById(R.id.gridView);
		adapter = new GridAdapter();
		mGridView.setOnChangedListener(new NumbersGridView.OnChangedListener() {

			@Override
			public void swipeUp() {
				for (int col = 0; col < 4; col++) {
					for (int k = 0; k < 4; k++) {
						for (int row = 1; row < 4; row++) {
							if (a[row - 1][col] == EMPTY) {
								a[row - 1][col] = a[row][col];
								a[row][col] = EMPTY;
							} else if (a[row - 1][col] == a[row][col]) {
								a[row - 1][col] *= 2;
								mCurrentSorce += a[row][col];
								a[row][col] = EMPTY;
							}

						}
					}
				}
				updateEmptyList();
				adapter.notifyDataSetChanged();
			}

			@Override
			public void swipeRight() {
				for (int row = 0; row < 4; row++) {
					for (int k = 0; k < 4; k++) {
						for (int col = 4 - 1; col > 0; col--) {
							boolean merged = false;
							if (a[row][col] == EMPTY) {
								a[row][col] = a[row][col - 1];
								a[row][col - 1] = EMPTY;
							} else if (!merged
									&& a[row][col] == a[row][col - 1]) {
								a[row][col] *= 2;
								a[row][col - 1] = EMPTY;
								mCurrentSorce += a[row][col];
								merged = true;
							}
						}
					}
				}
				updateEmptyList();
				adapter.notifyDataSetChanged();
			}

			@Override
			public void swipeLeft() {
				for (int row = 0; row < 4; row++) {
					for (int k = 0; k < 4; k++) {
						for (int col = 0; col < 4 - 1; col++) {
							if (a[row][col] == EMPTY) {
								a[row][col] = a[row][col + 1];
								a[row][col + 1] = EMPTY;
							} else if (a[row][col] == a[row][col + 1]) {
								a[row][col] *= 2;
								mCurrentSorce += a[row][col];
								a[row][col + 1] = EMPTY;
							}
						}
					}
				}
				updateEmptyList();
				adapter.notifyDataSetChanged();
			}

			@Override
			public void swipeDown() {
				for (int col = 0; col < 4; col++) {
					for (int k = 0; k < 4; k++) {
						for (int row = 4 - 1; row > 0; row--) {
							if (a[row][col] == EMPTY) {
								a[row][col] = a[row - 1][col];
								a[row - 1][col] = EMPTY;
							} else if (a[row][col] == a[row - 1][col]) {
								a[row - 1][col] *= 2;
								mCurrentSorce += a[row][col];
								a[row][col] = EMPTY;
							}
						}
					}
				}
				updateEmptyList();
				adapter.notifyDataSetChanged();
			}
		});
		init();
		mGridView.setAdapter(adapter);
		mSharedPrefs = getSharedPreferences("prefs", MODE_PRIVATE);
		mCurrentSorce = 0;
		mMaxScore = mSharedPrefs.getLong(MAX_SCORE, 0L);
		TextView max = (TextView) findViewById(R.id.maxScore);
		max.setText(String.valueOf(mMaxScore));
	}

	private static class ViewHolder {
		SingleGrid singleGrid;
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
				convertView = (SingleGrid) getLayoutInflater().inflate(
						R.layout.single_grid, null);
				convertView.setBackgroundResource(BgIds[3]);
				holder = new ViewHolder();
				holder.singleGrid = (SingleGrid) convertView;
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			int row = position / COL;
			int col = position % COL;
			assert (row >= 0 && col >= 0);
			final int value = a[row][col];
			if (value > 0) {
				holder.singleGrid.setText(String.valueOf(value));
				int index = 0;
				for (int v = 2, i = 0, len = BgIds.length; v <= 2048
						&& i < len; v *= 2, i++) {
					if (Math.pow(2, i) >= value) {
						index = i;
						break;
					}
				}
				holder.singleGrid.setBackgroundResource(BgIds[index]);
			} else {
				holder.singleGrid.setText("");
				holder.singleGrid.setBackgroundResource(BgIds[0]);
			}
			return convertView;
		}

	}

}
