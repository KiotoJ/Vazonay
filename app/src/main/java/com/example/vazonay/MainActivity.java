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
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ListView listraHiraMp3;
    private Mp3Adapter adapter;
    private MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SeekBar sb =  (SeekBar) findViewById(R.id.progress_bar_vazo);

        final Mp3Activity mp3 = new Mp3Activity(mediaPlayer, sb);


        final ImageButton btnPlay = (ImageButton) findViewById(R.id.alefa_hira);
        final TextView timeVazo = (TextView) findViewById(R.id.duration_vazo);
        TextView playTitraText = (TextView) findViewById(R.id.play_titra_text);
        //SeekBar sb =  (SeekBar) findViewById(R.id.progress_bar_vazo);
        //final Mp3Activity mp3 = new Mp3Activity(mediaPlayer, sb);

        String[] listraLohatenyMp3 = mp3.getAllMp3(getAssets());
        //ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.layout_list_view, R.id.titra_text, listraLohatenyMp3);
        listraHiraMp3 = (ListView) findViewById(R.id.listra_hira);
        btnPlay.setImageResource(R.drawable.play);
        final String[] hiraHalefa = {""};

        listraHiraMp3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                hiraHalefa[0] = parent.getItemAtPosition(position).toString();

                if(mediaPlayer != null || mp3.isPaused(mediaPlayer)){
                    // mediaPlayer.stop();
                    mediaPlayer.reset();
                    btnPlay.setImageResource(R.drawable.play);
                }

                timeVazo.setText(mp3.getLengthOfMozika(hiraHalefa[0], MainActivity.this));
                playTitraText.setText(hiraHalefa[0]);

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
        listraHiraMp3.setTextFilterEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.item, menu);

        MenuItem searchMenuItem = menu.findItem( R.id.act_cherch);
        MenuItem quitMenuItem = menu.findItem( R.id.quit);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();

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
                    listraHiraMp3.setFilterText(newText);

                   /*adapter.getFilter().filter(newText);
                   listraHiraMp3.setAdapter(adapter);
                   adapter.notifyDataSetChanged();*/
                }
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