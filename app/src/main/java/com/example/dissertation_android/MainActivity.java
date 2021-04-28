package com.example.dissertation_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onUploadButtonClick(View v) {
        Intent intent = new Intent(MainActivity.this, UploadActivity.class);
        startActivity(intent);
    }

    public void onQuizButtonClick(View v) {
        Intent intent = new Intent(MainActivity.this, UploadActivity.class);
        startActivity(intent);
    }
}