package com.hira.antsivaskoto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout linearLayout = findViewById(R.id.splash_act);
        linearLayout.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ListMp3Activity.class)));
    }
}