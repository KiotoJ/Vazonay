package com.example.vazonay;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class PlayActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer = new MediaPlayer();
    String[] listraLohatenyMp3;
    public int laharanaHiraVakiana;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        setTitle("MANAN-JARA SOA");
        final ImageButton btnPlay = (ImageButton) findViewById(R.id.alefa_hira);
        final ImageButton btnNext = (ImageButton) findViewById(R.id.next);
        final ImageButton btnPrevious = (ImageButton) findViewById(R.id.previous);
        final ImageButton btnPlayAgain = (ImageButton) findViewById(R.id.play_again);
        final TextView timeVazo = (TextView) findViewById(R.id.duration_vazo);
        TextView infoPlayTitraText = (TextView) findViewById(R.id.info_play_titra_text);
        TextView playTitraText = (TextView) findViewById(R.id.play_titra_text);
        TextView showTononKira = (TextView) findViewById(R.id.show_tonon_kira);

        SeekBar sb =  (SeekBar) findViewById(R.id.progress_bar_vazo);
        final Mp3Activity mp3 = new Mp3Activity(mediaPlayer, sb);
        listraLohatenyMp3 = mp3.getAllMp3(getAssets());

        Bundle extras = getIntent().getExtras();
        String hiraVoatsindry = extras.getString("HIRA_HALEFA");
        String positionCurrentHira = extras.getString("POSITION_CURRENT_HIRA");
        this.laharanaHiraVakiana = Integer.parseInt(positionCurrentHira);

        btnPlay.setImageResource(R.drawable.play);
        timeVazo.setText(mp3.getLengthOfMozika(hiraVoatsindry, PlayActivity.this));
        playTitraText.setText(hiraVoatsindry);

        showTononKira.setMovementMethod(new ScrollingMovementMethod());

        String[] splittedfullNameFile = hiraVoatsindry.split("\\.");
        timeVazo.setText(mp3.getLengthOfMozika(hiraVoatsindry, PlayActivity.this));
        playTitraText.setText(splittedfullNameFile[1]);
        infoPlayTitraText.setText(splittedfullNameFile[2]);
        showTononKira.setText(mp3.mamakyTononkira(getAssets(), splittedfullNameFile[0]+'.'+splittedfullNameFile[1]+'.'+splittedfullNameFile[2]));

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer == null) return;
                mp3.playMp3(getAssets(), hiraVoatsindry, btnPlay);
            }
        });

        btnPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sb.setProgress(0);
                mediaPlayer.reset();
                mp3.playMp3(getAssets(), hiraVoatsindry, btnPlay);
            }
        });

        int legthInMillisecond = mp3.lengthTotalMozika(hiraVoatsindry, PlayActivity.this);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextPosition = mediaPlayer.getCurrentPosition() + 5000;
                if (nextPosition < legthInMillisecond){
                    sb.setProgress(nextPosition);
                    mediaPlayer.seekTo(nextPosition);
                } else {
                    sb.setProgress(legthInMillisecond);
                }
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int prevPosition = mediaPlayer.getCurrentPosition() - 5000;
                if ( prevPosition > 0) {
                    sb.setProgress(mediaPlayer.getCurrentPosition() + 5000);
                    mediaPlayer.seekTo(prevPosition);
                }
                else {
                    sb.setProgress(0);
                    mediaPlayer.seekTo(0);
                }

            }
        });

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int mProgressAtStartTracking;
            private int SENSITIVITY;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // mProgressAtStartTracking = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // if(Math.abs(mProgressAtStartTracking - seekBar.getProgress()) <= SENSITIVITY){
                // react to thumb click
                //}
            }
        });
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer = null;
        super.onDestroy();
    }
}