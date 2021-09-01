package com.hira.antsivaskoto;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class PlayActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer = new MediaPlayer();
    String[] listraLohatenyMp3;
    public int laharanaHiraVakiana;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        setTitle("MANAN-JARA SOA");
        final ImageButton btnPlay = findViewById(R.id.alefa_hira);
        final ImageButton btnNext = findViewById(R.id.next);
        final ImageButton btnPrevious = findViewById(R.id.previous);
        final ImageButton btnPlayLoop = findViewById(R.id.play_loop);
        final TextView timeVazo = findViewById(R.id.duration_vazo);
        final ImageView fb = findViewById(R.id.fb);
        final ImageView paypal = findViewById(R.id.paypal);
        TextView infoPlayTitraText = findViewById(R.id.info_play_titra_text);
        TextView playTitraText = findViewById(R.id.play_titra_text);
        TextView showTononKira = findViewById(R.id.show_tonon_kira);
        RelativeLayout linearFanehoanaAmbony = findViewById(R.id.linear_fanehoana_ambony);
        LinearLayout linearFanehoanaAmbany = findViewById(R.id.linear_fanehoana_ambany);
        LinearLayout showFacebookPaypal = findViewById(R.id.show_facebook_paypal);

        fb.setVisibility(View.GONE);
        paypal.setVisibility(View.GONE);

        SeekBar sb = findViewById(R.id.progress_bar_vazo);
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

        if(splittedfullNameFile[0].equals("00")){
            fb.setVisibility(View.VISIBLE);
            paypal.setVisibility(View.VISIBLE);
            showTononKira.setGravity(Gravity.LEFT);
            playTitraText.setText("Fanolorana");
            infoPlayTitraText.setText("Fisaorana");
        }else if(!splittedfullNameFile[0].equals("00")){
            showTononKira.setGravity(Gravity.CENTER);
        }
        showTononKira.setText(mp3.mamakyTononkira(getAssets(), splittedfullNameFile[0]+'.'+splittedfullNameFile[1]+'.'+splittedfullNameFile[2]));

        fb.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
            String url = "https://www.facebook.com/Antsiva-Skoto-app-103599312047583";
            browserIntent.setData(Uri.parse(url));
            if(browserIntent.resolveActivity(getPackageManager()) != null){
                startActivity(browserIntent);
            }
        });

        paypal.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/donate?hosted_button_id=WMQGV4YJV7MYL"));
            if(browserIntent.resolveActivity(getPackageManager()) != null){
                startActivity(browserIntent);
            }
        });

        btnPlay.setOnClickListener(v -> {
            if(mediaPlayer == null) return;
            mp3.playMp3(getAssets(), hiraVoatsindry, btnPlay);
        });

        btnPlayLoop.setOnClickListener(v -> {
            sb.setProgress(0);
            mediaPlayer.reset();
            mp3.playMp3(getAssets(), hiraVoatsindry, btnPlay);
            mediaPlayer.setLooping(true);
        });

        int legthInMillisecond = mp3.lengthTotalMozika(hiraVoatsindry, PlayActivity.this);

        btnNext.setOnClickListener(v -> {
            int nextPosition = mediaPlayer.getCurrentPosition() + 5000;
            if (nextPosition < legthInMillisecond){
                sb.setProgress(nextPosition);
                mediaPlayer.seekTo(nextPosition);
            } else {
                sb.setProgress(legthInMillisecond);
            }
        });

        btnPrevious.setOnClickListener(v -> {
            int prevPosition = mediaPlayer.getCurrentPosition() - 5000;
            if ( prevPosition > 0) {
                sb.setProgress(mediaPlayer.getCurrentPosition() + 5000);
                mediaPlayer.seekTo(prevPosition);
            }
            else {
                sb.setProgress(0);
                mediaPlayer.seekTo(0);
            }

        });

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        ViewTreeObserver vto = linearFanehoanaAmbony.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                RelativeLayout.LayoutParams parameterAmbony = (RelativeLayout.LayoutParams) linearFanehoanaAmbony.getLayoutParams();
                RelativeLayout.LayoutParams parameterImageViews = (RelativeLayout.LayoutParams) showFacebookPaypal.getLayoutParams();
                ViewTreeObserver obs = linearFanehoanaAmbany.getViewTreeObserver();
                ViewTreeObserver obsShowPaypalFacebook = fb.getViewTreeObserver();


                parameterAmbony.setMargins(parameterAmbony.leftMargin, parameterAmbony.topMargin, parameterAmbony.rightMargin, linearFanehoanaAmbany.getHeight());
                parameterImageViews.setMargins(linearFanehoanaAmbony.getWidth() - 130, linearFanehoanaAmbony.getHeight() - linearFanehoanaAmbany.getHeight() - 280, 10, 10);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                    obsShowPaypalFacebook.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                    obsShowPaypalFacebook.removeGlobalOnLayoutListener(this);
                }
                showFacebookPaypal.setLayoutParams(parameterImageViews);
                linearFanehoanaAmbony.setLayoutParams(parameterAmbony);
            }

        });

        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    //INCOMING call
                    //do all necessary action to pause the audio
                    if(mediaPlayer != null){//check mp
                        if(mediaPlayer.isPlaying()){
                            mediaPlayer.pause();
                        }
                    }

                } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                    mp3.resumeMedia();
                    //Not IN CALL
                    //do anything if the phone-state is idle
                } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    //A call is dialing, active or on hold
                    //do all necessary action to pause the audio
                    //do something here
                    if(mediaPlayer!=null){//check mp
                        //setPlayerButton(true, false, true);
                        if(mediaPlayer.isPlaying()){
                            mediaPlayer.pause();
                        }
                    }
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };//end PhoneStateListener

        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer = null;
        super.onDestroy();
    }
}