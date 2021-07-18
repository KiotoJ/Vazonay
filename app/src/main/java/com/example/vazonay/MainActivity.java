package com.example.vazonay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ListView listraHiraMp3;
    private Mp3Adapter adapter;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private LinearLayout linearFanehoanaAmbony;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SeekBar sb =  (SeekBar) findViewById(R.id.progress_bar_vazo);

        final Mp3Activity mp3 = new Mp3Activity(mediaPlayer, sb);


        final ImageButton btnPlay = (ImageButton) findViewById(R.id.alefa_hira);
        final TextView timeVazo = (TextView) findViewById(R.id.duration_vazo);
        TextView playTitraText = (TextView) findViewById(R.id.play_titra_text);
        TextView infoPlayTitraText = (TextView) findViewById(R.id.info_play_titra_text);

        String[] listraLohatenyMp3 = mp3.getAllMp3(getAssets());
        //ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.layout_list_view, R.id.titra_text, listraLohatenyMp3);
        listraHiraMp3 = (ListView) findViewById(R.id.listra_hira);
        btnPlay.setImageResource(R.drawable.play);
        final String[] hiraHalefa = {""};

        listraHiraMp3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                linearFanehoanaAmbony.setVisibility(View.VISIBLE);
                hiraHalefa[0] = parent.getItemAtPosition(position).toString();
                String[] splittedfullNameFile = hiraHalefa[0].split("\\.");

                if(mediaPlayer != null || mp3.isPaused(mediaPlayer)){
                    mediaPlayer.reset();
                    sb.setProgress(0);
                    btnPlay.setImageResource(R.drawable.play);
                }

                timeVazo.setText(mp3.getLengthOfMozika(hiraHalefa[0], MainActivity.this));
                playTitraText.setText(splittedfullNameFile[0]);
                infoPlayTitraText.setText(splittedfullNameFile[1]);

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

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp3.playMp3(getAssets(), hiraHalefa[0], btnPlay);
            }
        });

        adapter = new Mp3Adapter(this, new ArrayList<String>(Arrays.asList(listraLohatenyMp3)));
        listraHiraMp3.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.item, menu);

        MenuItem searchMenuItem = menu.findItem( R.id.act_cherch);
        MenuItem quitMenuItem = menu.findItem( R.id.quit);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
        linearFanehoanaAmbony = (LinearLayout) findViewById(R.id.linear_fanehoana_ambony);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    listraHiraMp3.clearTextFilter();
                } else {
                    //Toast.makeText(getApplicationContext(), "itady", Toast.LENGTH_SHORT).show();
                    adapter.getFilter().filter(newText);
                }
                return true;
            }
        });

        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                //Toast.makeText(MainActivity.this, "Expand", Toast.LENGTH_SHORT).show();
                linearFanehoanaAmbony.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                //Toast.makeText(MainActivity.this, "Collapse", Toast.LENGTH_SHORT).show();
                linearFanehoanaAmbony.setVisibility(View.VISIBLE);
                return true;
            }
        });

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.quit:
                System.exit(0);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        super.onDestroy();
    }
}