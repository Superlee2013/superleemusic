package com.superlee.guessmusic.ui;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * 音乐播放类
 * Created by Super on 2015/5/10.
 */
public class MyPlayer {

    private static MediaPlayer myMusicMediaPlayer;

    public static void playSong(Context context, String songName) {
        if (myMusicMediaPlayer == null) {
            myMusicMediaPlayer = new MediaPlayer();
        }

        //强制重置状态,重置为可播放状态，主要针对为非第一次播放
        myMusicMediaPlayer.reset();

        //加载声音
        AssetManager assetManager = context.getAssets();
        //设定数据源
        try {
            AssetFileDescriptor fileDescriptor = assetManager.openFd(songName);
            //设定声音数据源
            myMusicMediaPlayer.setDataSource(
                    fileDescriptor.getFileDescriptor(),
                    fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength()
            );
            //准备声音播放
            myMusicMediaPlayer.prepare();
            //开始声音播放
            myMusicMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stopTheSong(Context context){
        if(myMusicMediaPlayer!=null){
            myMusicMediaPlayer.stop();
        }
    }
}
