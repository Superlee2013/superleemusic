package com.superlee.guessmusic.model;

import android.view.View;
import android.widget.Button;

public class WordButton {
	public int mIndex;
	public boolean mIsvisible;
	public String mWordString;
	
	public Button mViewButton;
	
	public WordButton(){
		mIsvisible=true;
		mWordString="";
	}
	
	public void setWordString(String s){
		mWordString=s;
		mViewButton.setText(s);
	}
	
	public void setIsVisible(boolean b){
		mIsvisible=b;
		mViewButton.setVisibility((b)?View.VISIBLE:View.INVISIBLE);
	}

}
