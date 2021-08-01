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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ListView listraHiraMp3;
    private Mp3Adapter adapter;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private LinearLayout linearFanehoanaAmbony;
    String[] listraLohatenyMp3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SeekBar sb =  new SeekBar(this);

        listraHiraMp3 = (ListView) findViewById(R.id.listra_hira);

        final Mp3Activity mp3 = new Mp3Activity(mediaPlayer, sb);

        listraLohatenyMp3 = mp3.getAllMp3(getAssets());

        adapter = new Mp3Adapter(this, new ArrayList<String>(Arrays.asList(listraLohatenyMp3)));
        listraHiraMp3.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        final String[] hiraHalefa = {""};

        listraHiraMp3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                //linearFanehoanaAmbony.setVisibility(View.VISIBLE);
                hiraHalefa[0] = parent.getItemAtPosition(position).toString();

                if(mediaPlayer != null || mp3.isPaused(mediaPlayer)){
                    mediaPlayer.reset();
                }
                intent.putExtra("HIRA_HALEFA", hiraHalefa[0]);
                intent.putExtra("POSITION_CURRENT_HIRA", String.valueOf(position));

                startActivity(intent);

            }
        });

       /* final ImageButton ButtonStar = (ImageButton) findViewById(R.id.star);
        ButtonStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEnable){
                    ButtonStar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),android.R.drawable.btn_star_big_off));
                }else{
                    ButtonStar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),android.R.drawable.btn_star_big_on));
                }
                isEnable = !isEnable;
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.item, menu);

        MenuItem searchMenuItem = menu.findItem( R.id.act_cherch);
        //MenuItem quitMenuItem = menu.findItem( R.id.quit);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
        linearFanehoanaAmbony = (LinearLayout) findViewById(R.id.linear_fanehoana_ambony);
        searchView.setQueryHint("Tadiavo...");

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
                    listraHiraMp3.setAdapter(null);
                    adapter.getFilter().filter(newText);
                    listraHiraMp3.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        });

        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                adapter = new Mp3Adapter(MainActivity.this, new ArrayList<String>(Arrays.asList(listraLohatenyMp3)));
                listraHiraMp3.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.quit:
                System.exit(0);*/
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer = null;
        super.onDestroy();
    }
}