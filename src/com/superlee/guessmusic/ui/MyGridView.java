package com.superlee.guessmusic.ui;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.superlee.guessmusic.R;
import com.superlee.guessmusic.model.Util;
import com.superlee.guessmusic.model.WordButton;

public class MyGridView extends GridView {
	public static final int WORD_COUNT = 24;
	private ArrayList<WordButton> mArratList = new ArrayList<WordButton>();
	private Context myContext;
	private MyAdapter myAdapter;
	private Animation mScaleAnimation;

	private int count = 0;
	
	/*********************接口模板实现，接口回调*************/
	private WordButtonClickListener wordButonClickListener;
	
	public interface WordButtonClickListener{
		public void wordButtonClick(WordButton wordButton);
	}
	
	public void setOnWordButtonClickListener(WordButtonClickListener listener){
		this.wordButonClickListener=listener;
	}
	
	/*********************接口模板实现结束*************/

	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		myContext = context;
		myAdapter = new MyAdapter();
		this.setAdapter(myAdapter);

	}

	public void updateData(ArrayList<WordButton> list) {
		mArratList = list;
		this.setAdapter(myAdapter);
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mArratList.size();
		}

		@Override
		public Object getItem(int pos) {
			return mArratList.get(pos);
		}

		@Override
		public long getItemId(int pos) {
			return pos;
		}

		@Override
		public View getView(int pos, View v, ViewGroup vg) {
			final WordButton holder;
			if (v == null) {
				v = Util.getView(myContext, R.layout.self_ui_gridview_item);
				holder = mArratList.get(pos);
				/* 加载动画 */
				mScaleAnimation = AnimationUtils.loadAnimation(myContext,
						R.anim.scale);

				// 设置动画延迟
				mScaleAnimation.setStartOffset(pos * 100);

				holder.mIndex = pos;
				if (holder.mViewButton == null) {
					holder.mViewButton = (Button) v.findViewById(R.id.item_btn);
					holder.mViewButton.setText(holder.mWordString);
					// 设置按钮事件
					holder.mViewButton
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									wordButonClickListener.wordButtonClick(holder);
								}
							});
					Log.i("Tag1", pos + " " + (++count));
				}
				v.setTag(holder);
			} else {
				holder = (WordButton) v.getTag();
			}

			// 启动动画
			v.startAnimation(mScaleAnimation);
			return v;
		}

	}

}
