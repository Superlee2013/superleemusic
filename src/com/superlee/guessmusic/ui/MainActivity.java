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
    // ��Ƭ��ض���
    private Animation mPanAnim;
    private LinearInterpolator mPanLin;

    // ���˶�������
    private Animation mBarInAnim;
    private LinearInterpolator mBarInLin;

    // ���˶�������
    private Animation mBarOutAnim;
    private LinearInterpolator mBarOutLin;

    // play����
    private ImageButton mBtnPlayStart;

    private ImageView mViewPan;
    private ImageView mViewPanBar;

    //��ѡ���ֿ�����������
    private ArrayList<WordButton> mAllWords;
    private MyGridView myGridView;

    //��ѡ���ֿ�����������
    private ArrayList<WordButton> mSelectWords;
    private LinearLayout selectContainer;

    //����������־
    private boolean mIsRunning = false;

    //��ǰ��������
    private Song mCurrentSong;

    //��ǰ��������
    private int mCurrentSongIndex = -1;

    //��״̬
    private final int ANSWER_WRONG = 0;
    private final int ANSWER_RIGHT = 1;
    private final int ANSWER_LACK = 2;

    //��˸����
    private final int SPARK_TIMES = 6;

    //���ؽ���
    private View passView;

    //�ؿ���Ϣ
    private TextView mViewLevel;

    //�ܹؿ�
    private final int TOTAL_LEVEL=Const.SONG_INFO.length;

    //�����Ŀ����Ӧ��textview;
    private int mCurrentCoins = Const.TOTAL_COINS;
    private TextView mViewCurrentCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //��ʼ������ѡ���ؼ�
        myGridView = (MyGridView) findViewById(R.id.gridview);
        //���ð�ť������
        myGridView.setOnWordButtonClickListener(new WordButtonClickListener() {
            @Override
            public void wordButtonClick(WordButton wordButton) {
                // TODO Auto-generated method stub
               /* setSelectWordButton(wordButton);

                int answerState = checkAnswer();
                if (answerState == ANSWER_LACK) {
                    //�𰸲�����ʱ�趨��ɫ
                    setAnswerLackWordColor();
                } else if (answerState == ANSWER_WRONG) {
                    //�𰸴���ʱ��˸Ч��
                    sparkTheWords();
                } else if (answerState == ANSWER_RIGHT) {
                    //����ȷʱ��ʾ���ؽ��漰��Ӧ�¼�
                    handlePassEvent();
                }*/

                onWordButtonClick(wordButton);
            }
        });



        //��ʼ�������Ŀ
        mViewCurrentCoins = (TextView) findViewById(R.id.txt_bar_coins);
        mViewCurrentCoins.setText(mCurrentCoins + "");

        //��ʼ����ѡ���ֿ�
        selectContainer = (LinearLayout) findViewById(R.id.word_select_container);

        // ��ʼ������
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

        //����ѡ���
        initCurrentStageData();

        //��ʼ���ؿ���Ϣ
        mViewLevel=(TextView)findViewById(R.id.txt_level_mark);
        mViewLevel.setText(mCurrentSongIndex+1+"");

        //����ɾ������ʾ�¼�
        handleDeleteWord();
        handleTips();
    }

    //�����Ű�ť����ʼ��������
    private void handlePlayButton() {
        if (mViewPanBar == null) {
            return;
        }
        if (!mIsRunning) {
            mViewPanBar.startAnimation(mBarInAnim);
            mBtnPlayStart.setVisibility(View.INVISIBLE);
            mIsRunning = true;

            //��������
            MyPlayer.playSong(MainActivity.this,mCurrentSong.getSongFileName());
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        mViewPan.clearAnimation();

        //ֹͣ����
        MyPlayer.stopTheSong(MainActivity.this);
        super.onPause();
    }

    //���ظ���
    public Song loadSongInfo(int index) {
        Song song = new Song();

        String[] currentSongInfo = Const.SONG_INFO[index];
        song.setSongFileName(currentSongInfo[Const.SONG_FILE_NAME]);
        song.setSongName(currentSongInfo[Const.SONG_NAME]);

        return song;
    }

    public void initCurrentStageData() {
        //���ظ���
        mCurrentSong = loadSongInfo(++mCurrentSongIndex);
        //���ִ�ѡ��
        mSelectWords = initWordSelect();
        LayoutParams params = new LayoutParams(120, 120);

        for (int i = 0; i < mSelectWords.size(); i++) {
            Button wordButton = mSelectWords.get(i).mViewButton;
            selectContainer.addView(wordButton, params);
        }

        //��ʼ������
        mAllWords = initAllData();
        //��������
        myGridView.updateData(mAllWords);
    }


    public ArrayList<WordButton> initAllData() {
        ArrayList<WordButton> data = new ArrayList<WordButton>();
        //���ʱ��û�г�ʼ��WordButton�е�Button����Button�����ʵ��������MyGridView����еġ�
        String[] words = genarateWords();
        for (int i = 0; i < MyGridView.WORD_COUNT; i++) {
            WordButton wordButton = new WordButton();
            wordButton.mWordString = words[i];
            data.add(wordButton);
        }
        return data;
    }

    //��ʼ����ѡ���ֿ�
    public ArrayList<WordButton> initWordSelect() {
        ArrayList<WordButton> data = new ArrayList<WordButton>();
        //�����ʼΪ4����֮�������޸�
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
                    //�𰸲�����ʱ���ô�������ɫ
                    setAnswerLackWordColor();
                }
            });
            data.add(holder);
        }
        return data;
    }

    //�����ѡ���ֿ��
    public void cleanAnswer(WordButton wordButton) {
        wordButton.setWordString("");
        if (wordButton.mIndex != -1) {
            mAllWords.get(wordButton.mIndex).setIsVisible(true);
        }
        wordButton.mIndex = -1;
    }

    //�������д�ѡ����
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

        //���Һ���˳��
        for (int i = 0; i < nameLength; i++) {
            int index = random.nextInt(MyGridView.WORD_COUNT - i - 1) + i;
            String tempWord = words[index];
            words[index] = words[i];
            words[i] = tempWord;
        }

        return words;
    }

    //�����ѡ���ֿ�����ʱ������ѡ���ֿ�����
    public void setSelectWordButton(WordButton wordButton) {
        for (int i = 0; i < mSelectWords.size(); i++) {
            WordButton wb = mSelectWords.get(i);
            if (wb.mWordString.length() == 0) {
                /*wb.mWordString=wordButton.mWordString;//����WordButton��������
                wb.mViewButton.setText(wordButton.mWordString);//����button������ʾ���ı�
				wb.mIsvisible=true;*/
                wb.setWordString(wordButton.mWordString);
                wb.setIsVisible(true);

                //��������
                wb.mIndex = wordButton.mIndex;

                Log.i("tag", "success~~");

                //������ѡ���ֿ�ɼ���

                wordButton.setIsVisible(false);

                break;
            }
        }
    }

    //����
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
     * ��������¼�
     */
    public void handlePassEvent() {
        if(passView==null) {
            passView = findViewById(R.id.pass_view);
        }
        passView.setVisibility(View.VISIBLE);
        passView.setClickable(true);

        //ֹͣ����
        mViewPan.clearAnimation();

        //ֹͣ���ڲ��ŵ�����
        MyPlayer.stopTheSong(MainActivity.this);

        TextView mViewSongLevel= (TextView) findViewById(R.id.txt_currsong_mark);
        TextView mViewSongName=(TextView) findViewById(R.id.txt_currsong_name);

        mViewSongLevel.setText(mCurrentSongIndex+1+"");
        mViewSongName.setText(mCurrentSong.getSongName());
        /**
         * ������һ��
         */
        Button btn_next = (Button) findViewById(R.id.next_song);
        btn_next.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                passView.setVisibility(View.GONE);
                if (mCurrentSongIndex == TOTAL_LEVEL-1) {
                    mCurrentSongIndex = -1;
                }
                //TODO:�����Ϣ�����ı�


                //ȥ����ѡ���ֿ�������Ŀؼ�
                selectContainer.removeAllViews();
                //������һ�׸���
                initCurrentStageData();

                //��������
                handlePlayButton();

                //�ؿ���Ϣ�ı�
                mViewLevel.setText(mCurrentSongIndex+1+"");
            }
        });
    }

    /**
     * ��˸�¼�����
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
                        //ִ����˸�߼�
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
     * �𰸲�����ʱ����������ɫΪ��ɫ
     */
    public void setAnswerLackWordColor() {
        for (int i = 0; i < mSelectWords.size(); i++) {
            mSelectWords.get(i).mViewButton.setTextColor(Color.WHITE);
        }
    }

    /**
     * ����ɾ����ѡ�����¼�
     */
    public void handleDeleteWord() {
        ImageButton button = (ImageButton) findViewById(R.id.btn_delete);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:Bug1:��ʣ���Ϊ��ȷ��ʱ������Ӧ����
                if (handleCoins(-getEventCoins(R.integer.pay_delete))) {
                    deleteButton();
                }
            }
        });
    }

    //�ж��Ƿ�Ϊ�����е���
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

    //�ж��Ƿ��ܹ�ɾ��
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

    //ɾ�����Ǹ����е���
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
     * ���ӻ��߼���ָ�������Ľ��
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

    //ȡ��ɾ������ʾ�¼���������Ŀ
    public int getEventCoins(int id) {
        return this.getResources().getInteger(id);
    }

    /**
     * ������ʾ�����¼�
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

    //������ʾ����
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

    //Ѱ����ȷ��
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

    //�������¼�
    public void onWordButtonClick(WordButton wordButton) {
        // TODO Auto-generated method stub
        setSelectWordButton(wordButton);

        int answerState = checkAnswer();
        if (answerState == ANSWER_LACK) {
            //�𰸲�����ʱ�趨��ɫ
            setAnswerLackWordColor();
        } else if (answerState == ANSWER_WRONG) {
            //�𰸴���ʱ��˸Ч��
            sparkTheWords();
        } else if (answerState == ANSWER_RIGHT) {
            //����ȷʱ��ʾ���ؽ��漰��Ӧ�¼�
            handlePassEvent();
        }


    }

}
