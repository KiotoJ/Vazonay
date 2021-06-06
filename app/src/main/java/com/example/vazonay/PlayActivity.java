package com.example.vazonay;


import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class PlayActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        final ImageButton btnPlay = (ImageButton) findViewById(R.id.alefa_hira);
        final TextView timeVazo = (TextView) findViewById(R.id.duration_vazo);
        TextView playTitraText = (TextView) findViewById(R.id.play_titra_text);
        SeekBar sb =  (SeekBar) findViewById(R.id.progress_bar_vazo);
        final Mp3Activity mp3 = new Mp3Activity(mediaPlayer, sb);
        Bundle extras = getIntent().getExtras();
        String hiraVoatsindry = extras.getString("HIRA_ALEFA");

        btnPlay.setImageResource(R.drawable.play);
        timeVazo.setText(mp3.getLengthOfMozika(hiraVoatsindry, PlayActivity.this));
        playTitraText.setText(hiraVoatsindry);
        //Toast.makeText(getApplicationContext(), hiraVoatsindry, Toast.LENGTH_SHORT).show();

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(extras != null ) {
                    mp3.playMp3(getAssets(), hiraVoatsindry, btnPlay);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        super.onDestroy();
    }
}