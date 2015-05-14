package com.superlee.guessmusic.model;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class Util {
	public static View getView(Context context,int resouceID){
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout=inflater.inflate(resouceID, null);
		return layout;
	}
	
	
	//生成随机汉字
	public static char getRandomChar(){
		String str="";
		int hightPos;
		int lowPos;
		
		Random random=new Random();
		
		hightPos=(176+Math.abs(random.nextInt(39)));
		lowPos=(161+Math.abs(random.nextInt(93)));
		//一个汉字有两字节组成
		byte[] b=new byte[2];
		b[0]=(Integer.valueOf(hightPos)).byteValue();
		b[1]=(Integer.valueOf(lowPos)).byteValue();
		
		try {
			str=new String(b, "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return str.charAt(0);
	}

}
