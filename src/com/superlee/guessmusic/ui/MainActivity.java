package com.superlee.guessmusic.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.superlee.guessmusic.R;
import com.superlee.guessmusic.data.Const;
import com.superlee.guessmusic.model.Song;
import com.superlee.guessmusic.model.Util;
import com.superlee.guessmusic.model.WordButton;
import com.superlee.guessmusic.ui.MyGridView.WordButtonClickListener;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    // 唱片相关动画
    private Animation mPanAnim;
    private LinearInterpolator mPanLin;

    // 拨杆动画进入
    private Animation mBarInAnim;
    private LinearInterpolator mBarInLin;

    // 拨杆动画出来
    private Animation mBarOutAnim;
    private LinearInterpolator mBarOutLin;

    // play按键
    private ImageButton mBtnPlayStart;

    private ImageView mViewPan;
    private ImageView mViewPanBar;

    //待选文字框容器及内容
    private ArrayList<WordButton> mAllWords;
    private MyGridView myGridView;

    //已选文字框容器及内容
    private ArrayList<WordButton> mSelectWords;
    private LinearLayout selectContainer;

    //启动动画标志
    private boolean mIsRunning = false;

    //当前歌曲对象
    private Song mCurrentSong;

    //当前歌曲索引
    private int mCurrentSongIndex = -1;

    //答案状态
    private final int ANSWER_WRONG = 0;
    private final int ANSWER_RIGHT = 1;
    private final int ANSWER_LACK = 2;

    //闪烁次数
    private final int SPARK_TIMES = 6;

    //过关界面
    private View passView;

    //关卡信息
    private TextView mViewLevel;

    //总关卡
    private final int TOTAL_LEVEL=Const.SONG_INFO.length;

    //金币数目及对应的textview;
    private int mCurrentCoins = Const.TOTAL_COINS;
    private TextView mViewCurrentCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化文字选择框控件
        myGridView = (MyGridView) findViewById(R.id.gridview);
        //设置按钮监听器
        myGridView.setOnWordButtonClickListener(new WordButtonClickListener() {
            @Override
            public void wordButtonClick(WordButton wordButton) {
                // TODO Auto-generated method stub
               /* setSelectWordButton(wordButton);

                int answerState = checkAnswer();
                if (answerState == ANSWER_LACK) {
                    //答案不完整时设定颜色
                    setAnswerLackWordColor();
                } else if (answerState == ANSWER_WRONG) {
                    //答案错误时闪烁效果
                    sparkTheWords();
                } else if (answerState == ANSWER_RIGHT) {
                    //答案正确时显示过关界面及相应事件
                    handlePassEvent();
                }*/

                onWordButtonClick(wordButton);
            }
        });



        //初始化金币数目
        mViewCurrentCoins = (TextView) findViewById(R.id.txt_bar_coins);
        mViewCurrentCoins.setText(mCurrentCoins + "");

        //初始化已选文字框
        selectContainer = (LinearLayout) findViewById(R.id.word_select_container);

        // 初始化动画
        mPanAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        mPanLin = new LinearInterpolator();
        mPanAnim.setInterpolator(mPanLin);
        mPanAnim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                // TODO Auto-generated method stub
                mViewPanBar.startAnimation(mBarOutAnim);
            }
        });

        mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
        mBarInLin = new LinearInterpolator();
        mBarInAnim.setInterpolator(mBarInLin);
        mBarInAnim.setFillAfter(true);
        mBarInAnim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                // TODO Auto-generated method stub
                mViewPan.startAnimation(mPanAnim);
            }
        });

        mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_d_45);
        mBarOutLin = new LinearInterpolator();
        mBarOutAnim.setInterpolator(mBarOutLin);
        mBarOutAnim.setFillAfter(true);
        mBarOutAnim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                // TODO Auto-generated method stub
                mIsRunning = false;
                mBtnPlayStart.setVisibility(View.VISIBLE);

            }
        });

        mViewPan = (ImageView) findViewById(R.id.imageView1);
        mViewPanBar = (ImageView) findViewById(R.id.imageView2);

        mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);
        mBtnPlayStart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                handlePlayButton();
            }
        });

        //文字选择框
        initCurrentStageData();

        //初始化关卡信息
        mViewLevel=(TextView)findViewById(R.id.txt_level_mark);
        mViewLevel.setText(mCurrentSongIndex+1+"");

        //处理删除与提示事件
        handleDeleteWord();
        handleTips();
    }

    //处理播放按钮，开始播放音乐
    private void handlePlayButton() {
        if (mViewPanBar == null) {
            return;
        }
        if (!mIsRunning) {
            mViewPanBar.startAnimation(mBarInAnim);
            mBtnPlayStart.setVisibility(View.INVISIBLE);
            mIsRunning = true;

            //播放音乐
            MyPlayer.playSong(MainActivity.this,mCurrentSong.getSongFileName());
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        mViewPan.clearAnimation();

        //停止音乐
        MyPlayer.stopTheSong(MainActivity.this);
        super.onPause();
    }

    //加载歌曲
    public Song loadSongInfo(int index) {
        Song song = new Song();

        String[] currentSongInfo = Const.SONG_INFO[index];
        song.setSongFileName(currentSongInfo[Const.SONG_FILE_NAME]);
        song.setSongName(currentSongInfo[Const.SONG_NAME]);

        return song;
    }

    public void initCurrentStageData() {
        //加载歌曲
        mCurrentSong = loadSongInfo(++mCurrentSongIndex);
        //文字待选框
        mSelectWords = initWordSelect();
        LayoutParams params = new LayoutParams(120, 120);

        for (int i = 0; i < mSelectWords.size(); i++) {
            Button wordButton = mSelectWords.get(i).mViewButton;
            selectContainer.addView(wordButton, params);
        }

        //初始化数据
        mAllWords = initAllData();
        //传递数据
        myGridView.updateData(mAllWords);
    }


    public ArrayList<WordButton> initAllData() {
        ArrayList<WordButton> data = new ArrayList<WordButton>();
        //这个时候还没有初始化WordButton中的Button对象，Button对象的实例化是在MyGridView里进行的。
        String[] words = genarateWords();
        for (int i = 0; i < MyGridView.WORD_COUNT; i++) {
            WordButton wordButton = new WordButton();
            wordButton.mWordString = words[i];
            data.add(wordButton);
        }
        return data;
    }

    //初始化已选文字框
    public ArrayList<WordButton> initWordSelect() {
        ArrayList<WordButton> data = new ArrayList<WordButton>();
        //假设最开始为4个，之后再作修改
        int songLength = mCurrentSong.getSongNameLength();

        for (int i = 0; i < songLength; i++) {
            View v = Util.getView(MainActivity.this, R.layout.self_ui_gridview_item);
            final WordButton holder = new WordButton();
            holder.mViewButton = (Button) v.findViewById(R.id.item_btn);
            holder.mViewButton.setTextColor(Color.WHITE);
            holder.mViewButton.setBackgroundResource(R.drawable.game_wordblank);
            holder.mViewButton.setText("");
            holder.mIsvisible = false;

            holder.mViewButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    cleanAnswer(holder);
                    //答案不完整时设置答案字体颜色
                    setAnswerLackWordColor();
                }
            });
            data.add(holder);
        }
        return data;
    }

    //清除已选文字框答案
    public void cleanAnswer(WordButton wordButton) {
        wordButton.setWordString("");
        if (wordButton.mIndex != -1) {
            mAllWords.get(wordButton.mIndex).setIsVisible(true);
        }
        wordButton.mIndex = -1;
    }

    //生成所有待选文字
    public String[] genarateWords() {
        Random random = new Random();

        String[] words = new String[MyGridView.WORD_COUNT];

        int nameLength = mCurrentSong.getSongNameLength();

        for (int i = 0; i < nameLength; i++) {
            words[i] = mCurrentSong.getNameCharacters()[i] + "";
        }

        for (int i = nameLength; i < MyGridView.WORD_COUNT; i++) {
            words[i] = Util.getRandomChar() + "";
        }

        //打乱汉字顺序
        for (int i = 0; i < nameLength; i++) {
            int index = random.nextInt(MyGridView.WORD_COUNT - i - 1) + i;
            String tempWord = words[index];
            words[index] = words[i];
            words[i] = tempWord;
        }

        return words;
    }

    //点击待选文字框文字时设置已选文字框内容
    public void setSelectWordButton(WordButton wordButton) {
        for (int i = 0; i < mSelectWords.size(); i++) {
            WordButton wb = mSelectWords.get(i);
            if (wb.mWordString.length() == 0) {
                /*wb.mWordString=wordButton.mWordString;//设置WordButton对象属性
                wb.mViewButton.setText(wordButton.mWordString);//设置button对象显示的文本
				wb.mIsvisible=true;*/
                wb.setWordString(wordButton.mWordString);
                wb.setIsVisible(true);

                //设置索引
                wb.mIndex = wordButton.mIndex;

                Log.i("tag", "success~~");

                //设置已选文字框可见性

                wordButton.setIsVisible(false);

                break;
            }
        }
    }

    //检查答案
    public int checkAnswer() {
        String answer = "";
        String rightAnswer = mCurrentSong.getSongName();
        for (int i = 0; i < mSelectWords.size(); i++) {
            if (mSelectWords.get(i).mWordString.length() == 0) {
                return ANSWER_LACK;
            }
        }
        for (int i = 0; i < mSelectWords.size(); i++) {
            answer += mSelectWords.get(i).mWordString;
        }
        return (answer.equals(rightAnswer)) ? ANSWER_RIGHT : ANSWER_WRONG;
    }

    /**
     * 处理过关事件
     */
    public void handlePassEvent() {
        if(passView==null) {
            passView = findViewById(R.id.pass_view);
        }
        passView.setVisibility(View.VISIBLE);
        passView.setClickable(true);

        //停止动画
        mViewPan.clearAnimation();

        //停止正在播放的音乐
        MyPlayer.stopTheSong(MainActivity.this);

        TextView mViewSongLevel= (TextView) findViewById(R.id.txt_currsong_mark);
        TextView mViewSongName=(TextView) findViewById(R.id.txt_currsong_name);

        mViewSongLevel.setText(mCurrentSongIndex+1+"");
        mViewSongName.setText(mCurrentSong.getSongName());
        /**
         * 开启下一关
         */
        Button btn_next = (Button) findViewById(R.id.next_song);
        btn_next.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                passView.setVisibility(View.GONE);
                if (mCurrentSongIndex == TOTAL_LEVEL-1) {
                    mCurrentSongIndex = -1;
                }
                //TODO:金币信息发生改变


                //去掉已选文字框容器里的控件
                selectContainer.removeAllViews();
                //加载下一首歌曲
                initCurrentStageData();

                //播放音乐
                handlePlayButton();

                //关卡信息改变
                mViewLevel.setText(mCurrentSongIndex+1+"");
            }
        });
    }

    /**
     * 闪烁事件处理
     */
    private void sparkTheWords() {
        TimerTask task = new TimerTask() {
            private boolean mChange = false;
            private int mSparkTimes = 0;

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (++mSparkTimes > SPARK_TIMES) {
                            return;
                        }
                        //执行闪烁逻辑
                        for (int i = 0; i < mSelectWords.size(); i++) {
                            mSelectWords.get(i).mViewButton.setTextColor(
                                    mChange ? Color.RED : Color.WHITE);
                        }
                        mChange = !mChange;
                    }
                });
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, 1, 150);
    }

    /**
     * 答案不完整时设置文字颜色为白色
     */
    public void setAnswerLackWordColor() {
        for (int i = 0; i < mSelectWords.size(); i++) {
            mSelectWords.get(i).mViewButton.setTextColor(Color.WHITE);
        }
    }

    /**
     * 处理删除待选文字事件
     */
    public void handleDeleteWord() {
        ImageButton button = (ImageButton) findViewById(R.id.btn_delete);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:Bug1:当剩余答案为正确答案时，作相应处理
                if (handleCoins(-getEventCoins(R.integer.pay_delete))) {
                    deleteButton();
                }
            }
        });
    }

    //判断是否为歌曲中的字
    public boolean IsinSongName(WordButton wordButton) {
        char[] songName = mCurrentSong.getNameCharacters();
        String word = wordButton.mWordString;
        for (int i = 0; i < songName.length; i++) {
            if (word.equals(songName[i] + "")) {
                return true;
            }
        }
        return false;
    }

    //判断是否能够删除
    public boolean JudgeDelete(){
        int indexSelect=0;
        int leaveNum=0;
        for(int i=0;i<mSelectWords.size();i++){
            if(mSelectWords.get(i).mWordString.length()!=0){
                indexSelect+=1;
            }
        }

        for(int i=0;i<mAllWords.size();i++){
            if(mAllWords.get(i).mIsvisible){
                leaveNum+=1;
            }
        }

        if(indexSelect+leaveNum>mCurrentSong.getSongNameLength()){
            return true;
        }
        return false;
    }

    //删除不是歌曲中的字
    public void deleteButton() {
        Random random = new Random();
        WordButton buff;
        while (JudgeDelete()) {
            int index = random.nextInt(MyGridView.WORD_COUNT);
            buff = mAllWords.get(index);
            if (buff.mIsvisible && !IsinSongName(buff)) {
                buff.setIsVisible(false);
                break;
            }
        }
    }

    /**
     * 增加或者减少指定数量的金币
     *
     * @return
     */
    private boolean handleCoins(int data) {
        if (mCurrentCoins + data < 0) {
            return false;
        }
        mCurrentCoins = mCurrentCoins + data;
        mViewCurrentCoins.setText(mCurrentCoins + "");

        return true;
    }

    //取得删除或提示事件所需金币数目
    public int getEventCoins(int id) {
        return this.getResources().getInteger(id);
    }

    /**
     * 处理提示文字事件
     */
    public void handleTips() {
        ImageButton button = (ImageButton) findViewById(R.id.btn_tip);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (handleCoins(-getEventCoins(R.integer.pay_tips))) {
                    tipAnswer();
                }

            }
        });
    }

    //处理提示文字
    public void tipAnswer() {
        for (int i = 0; i < mSelectWords.size(); i++) {
            if (mSelectWords.get(i).mWordString.length() == 0) {
                WordButton rightButton = findAnswerButton(i);
                if(rightButton==null){
                    break;
                }
                /*WordButton buf = mSelectWords.get(i);
                buf.mIndex = rightButton.mIndex;
                buf.setWordString(rightButton.mWordString);
                buf.setIsVisible(true);*/
                onWordButtonClick(rightButton);
                return;
            }
        }
        sparkTheWords();
    }

    //寻找正确答案
    public WordButton findAnswerButton(int index) {
        WordButton buf;
        char rightChar = mCurrentSong.getNameCharacters()[index];
        for (int i = 0; i < mAllWords.size(); i++) {
            buf = mAllWords.get(i);
            if (buf.mIsvisible&&buf.mWordString.equals(rightChar + "")) {
                buf.setIsVisible(false);
                return buf;
            }
        }
        return null;
    }

    //处理点击事件
    public void onWordButtonClick(WordButton wordButton) {
        // TODO Auto-generated method stub
        setSelectWordButton(wordButton);

        int answerState = checkAnswer();
        if (answerState == ANSWER_LACK) {
            //答案不完整时设定颜色
            setAnswerLackWordColor();
        } else if (answerState == ANSWER_WRONG) {
            //答案错误时闪烁效果
            sparkTheWords();
        } else if (answerState == ANSWER_RIGHT) {
            //答案正确时显示过关界面及相应事件
            handlePassEvent();
        }


    }

}
