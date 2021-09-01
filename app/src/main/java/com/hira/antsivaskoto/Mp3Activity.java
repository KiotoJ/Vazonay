package com.hira.antsivaskoto;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class Mp3Activity extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener, AudioManager.OnAudioFocusChangeListener, Runnable {

    private String nameFile;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    static final int READ_BLOCK_SIZE = 100;

    public String getNameFile() {
        return nameFile;
    }

    private final IBinder iBinder = new LocalBinder();

    //path to the audio file
    private String mediaFile;
    //Used to pause/resume MediaPlayer
    private int resumePosition;

    ImageButton btnPlay;

    //Handle incoming phone calls
    private final boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    private final SeekBar seekBar;
    boolean playedAtLeastOnce;

    public Mp3Activity(SeekBar seekBar){
        this.seekBar = seekBar;
    }

    public Mp3Activity(MediaPlayer mediaPlayer, SeekBar sb) {
        this.mediaPlayer = mediaPlayer;
        this.seekBar = sb;
    }

    public String[] getAllMp3(AssetManager am) {
        String[] allMp3 = {};
        try {
            allMp3 = am.list("hira");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allMp3;
    }

    public String  mamakyTononkira(AssetManager am, String name){
        StringBuilder tonony= new StringBuilder();
        try {
            InputStream is = am.open("tonony/"+name+".txt");

            InputStreamReader InputRead= new InputStreamReader(is);

            char[] inputBuffer= new char[READ_BLOCK_SIZE];
            int charRead;

            while ((charRead=InputRead.read(inputBuffer))>0) {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                tonony.append(readstring);
            }
            InputRead.close();

        } catch (Exception e) {
            e.printStackTrace();
            tonony = new StringBuilder("Miala Tsiny: Misy olana ny famakiana ilay tonon-kira");
        }
        return tonony.toString();
    }

    public void playMp3(AssetManager am, String pathMp3, ImageButton btnPlay) {
        this.btnPlay = btnPlay;
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            pauseMedia();
            this.btnPlay.setImageResource(R.drawable.play);
        }
        else{
            try {
                if(resumePosition > 0 && mediaPlayer != null && this.seekBar.getProgress() > 0) {
                    resumeMedia();
                }
                else {
                    if(mediaPlayer != null) mediaPlayer.reset();
                    AssetFileDescriptor afd = am.openFd("hira/"+pathMp3);
                    mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    new Thread(this).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.btnPlay.setImageResource(R.drawable.pause);
        }
    }

    private void initMediaPlayer() {
        //Set up MediaPlayer event listeners
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        //Reset so that the MediaPlayer is not pointing to another data source
        mediaPlayer.reset();

        //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());
        } else {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }

        try {
            // Set the data source to the mediaFile location
            mediaPlayer.setDataSource(mediaFile);
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
        mediaPlayer.prepareAsync();
    }

    private void playMedia() {
        mediaPlayer.isPlaying();//mediaPlayer.start();
    }

    private void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    public void resumeMedia() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            resumePosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
            new Thread(this).start();
        }
    }

    int lengthTotalMozika(String pathMp3, Context ctx){
        int millisecondLength = 0;
        try {
            final AssetFileDescriptor afd = ctx.getAssets().openFd("hira/"+pathMp3);

            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

            mediaMetadataRetriever.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();

            String durationMozika = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            millisecondLength = Integer.parseInt(durationMozika);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return millisecondLength;
    }

    String getLengthOfMozika(String pathMp3, Context ctx) {
        int millisecond = this.lengthTotalMozika(pathMp3, ctx);
        String durationMp3 = "";

        durationMp3 = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisecond),
                        TimeUnit.MILLISECONDS.toSeconds(millisecond) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisecond)));
        return durationMp3;
    }

    private boolean requestAudioFocus() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        //Focus gained
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        //Could not gain focus
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

        //Invoked when the audio focus of the system is updated.
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mediaPlayer == null) initMediaPlayer();
                else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //Invoked when playback of a media source has completed.
        mp.reset();
        stopMedia();
        //stop the service
        stopSelf();
        mp.release();
        //btnPlay.setImageResource(R.drawable.play);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //Invoked when there has been an error during an asynchronous operation
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //Invoked when the media source is ready for playback.
        playMedia();
        playedAtLeastOnce = true;
    }

    public boolean isPaused(MediaPlayer mp) {
        return !mp.isPlaying() && playedAtLeastOnce;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
    }

    @Override
    public void run() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        int total = mediaPlayer.getDuration();
        this.seekBar.setMax(total);

        while (mediaPlayer != null && mediaPlayer.isPlaying() && currentPosition < total) {
            try {
                Thread.sleep(1000);
                currentPosition = mediaPlayer.getCurrentPosition();
                if(currentPosition >= total){
                    this.seekBar.setProgress(0);
                    this.btnPlay.setImageResource(R.drawable.play);
                    return;
                }
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                return;
            }

            this.seekBar.setProgress(currentPosition);
        }
    }

    public class LocalBinder extends Binder {
        public Mp3Activity getService() {
            return Mp3Activity.this;
        }
    }
}