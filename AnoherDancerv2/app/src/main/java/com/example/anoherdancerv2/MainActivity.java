package com.example.anoherdancerv2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.content.SharedPreferences;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


public final class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String MESSAGE_INTENT = "intVariableName";
    public static final int MEDIA_RES_ID = R.raw.bud_spancer;
    private static final String STATE_COUNTER = "counter";
    private ImageView img1, img2, img3, img4;
    private ImageView[] IMGS = {img1, img2, img3, img4};






    private SeekBar mSeekbarAudio;
    private MediaPlayerHolder mPlayerAdapter;
    private boolean mUserIsSeeking = false;
    private int pointCount = 0;
    private TextView pointCountView;
    public boolean gameOver;
    private int currIm = 0;
    private int lastIm = 0;


    Date c = Calendar.getInstance().getTime();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    public String date = df.format(c);




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUI();
        initializeSeekbar();
        initializePlaybackController();

        pointCountView =(TextView) findViewById(R.id.pointCounter);

        img1 = (ImageView)findViewById(R.id.img02);
        img2 = (ImageView)findViewById(R.id.img04);
        img3 = (ImageView)findViewById(R.id.img03);
        img4 = (ImageView)findViewById(R.id.img01);

        IMGS[0] = img1;
        IMGS[1] = img2;
        IMGS[2] = img3;
        IMGS[3] = img4;

    }

    @Override
    protected void onStart() {
        super.onStart();
        mPlayerAdapter.loadMedia(MEDIA_RES_ID);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isChangingConfigurations() && mPlayerAdapter.isPlaying()) {
        } else {
            mPlayerAdapter.release();
        }
    }

    private void initializeUI() {
        Button mPlayButton = (Button) findViewById(R.id.button_play);
        Button mPauseButton = (Button) findViewById(R.id.button_pause);
        Button mResetButton = (Button) findViewById(R.id.button_reset);
        mSeekbarAudio = (SeekBar) findViewById(R.id.seekbar_audio);

        mPauseButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPlayerAdapter.pause();
                        IMGS[0].setVisibility(View.INVISIBLE);
                        IMGS[1].setVisibility(View.INVISIBLE);
                        IMGS[2].setVisibility(View.INVISIBLE);
                        IMGS[3].setVisibility(View.INVISIBLE);
                    }
                });
        mPlayButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPlayerAdapter.play();
                        randomImgage();
                        if(gameOver == true) {
                            TextView highScore = findViewById(R.id.highScore);
                            TextView highScoreText = findViewById(R.id.highScoreText);
                            TextView finalScoreText = findViewById(R.id.finalScoreText);
                            TextView dateView = findViewById(R.id.dateView);
                            pointCount = 0;
                            highScore.setVisibility(View.INVISIBLE);
                            finalScoreText.setVisibility(View.INVISIBLE);
                            highScoreText.setVisibility(View.INVISIBLE);
                            dateView.setVisibility(View.INVISIBLE);
                            gameOver = false;
                        }
                    }
                });
        mResetButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPlayerAdapter.reset();
                        pointCount = 0;
                        IMGS[0].setVisibility(View.INVISIBLE);
                        IMGS[1].setVisibility(View.INVISIBLE);
                        IMGS[2].setVisibility(View.INVISIBLE);
                        IMGS[3].setVisibility(View.INVISIBLE);
                        pointCountView.setText("");
                    }
                });
    }

    private void initializePlaybackController() {
        MediaPlayerHolder mMediaPlayerHolder = new MediaPlayerHolder(this);
        mMediaPlayerHolder.setPlaybackInfoListener(new PlaybackListener());
        mPlayerAdapter = mMediaPlayerHolder;
    }

    private void initializeSeekbar() {
        mSeekbarAudio.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int userSelectedPosition = 0;

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        mUserIsSeeking = true;
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            userSelectedPosition = progress;
                        }
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mUserIsSeeking = false;
                        mPlayerAdapter.seekTo(userSelectedPosition);
                    }
                });
    }

    public class PlaybackListener extends PlaybackInfoListener {

        @Override
        public void onDurationChanged(int duration) {
            mSeekbarAudio.setMax(duration);
        }

        @Override
        public void onPositionChanged(int position) {
            if (!mUserIsSeeking) {
                mSeekbarAudio.setProgress(position, true);
            }
        }

        @Override
        public void onStateChanged(@State int state) {
            String stateToString = PlaybackInfoListener.convertStateToString(state);
        }

        @Override
        public void onPlaybackCompleted() {
            gameOver = true;
            setHighScore();
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if( savedInstanceState != null){
            pointCount = savedInstanceState.getInt(STATE_COUNTER);
            pointCountView.setText(Integer.toString(pointCount));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_COUNTER, pointCount);
    }
    private void setHighScore(int value) {

        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("key", value);
        editor.putString("date",date);
        editor.commit();
    }

    public int getHighScore(){

        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        int score = prefs.getInt("key", 0);
        String date = prefs.getString("date", "1970-01-01");
        return score;
    }

    public void countUp(View view) {
        pointCount++;

        if (pointCountView != null) {

            pointCountView.setText(Integer.toString(pointCount));
        }


        randomImgage();

        if(pointCount > getHighScore()){
            setHighScore(pointCount);
        }
    }

    public void isPlaying(){
        IMGS[0].setVisibility(View.VISIBLE);
    }

    public void randomImgage(){

        System.out.println(currIm + " " + lastIm);

        Random random = new Random();
        currIm = random.nextInt(IMGS.length);

        if(lastIm != currIm) {

            IMGS[lastIm].setVisibility(View.INVISIBLE);
            IMGS[currIm].setVisibility(View.VISIBLE);
            lastIm = currIm;
            currIm = -1;

        }else{

            currIm = -1;
            randomImgage();
        }

    }

    public void setHighScore(){
        IMGS[lastIm].setVisibility(View.INVISIBLE);
        TextView highScore = findViewById(R.id.highScore);
        TextView highScoreText = findViewById(R.id.highScoreText);
        TextView finalScoreText = findViewById(R.id.finalScoreText);
        TextView dateView = findViewById(R.id.dateView);

        highScore.setVisibility(View.VISIBLE);
        highScoreText.setVisibility(View.VISIBLE);
        finalScoreText.setVisibility(View.VISIBLE);
        dateView.setVisibility(View.VISIBLE);

        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        int score = prefs.getInt("key", 0);
        highScoreText.setText(Integer.toString(score));
        String date = prefs.getString("date","1970.01.01");
        dateView.setText(date);

    }






}