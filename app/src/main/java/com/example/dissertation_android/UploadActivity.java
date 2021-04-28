package com.example.dissertation_android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class UploadActivity extends AppCompatActivity {

    private static final int PDF_REQUEST_CODE = 2;
    public SQLiteDatabase db;
    private DBHelper dbHelper;

    // Permissions for accessing storage
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE //Do we need this?
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
    }


    public void upload_document(View view) {

        final int FILE_PICKED = 2;

        // Check permission to read document
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        //Select a PDF Document
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");

        //once chosen, reload this activity with code 2 ('resume' not restart)
        startActivityForResult(intent, FILE_PICKED);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TextView path = findViewById(R.id.filepathview);
        Uri pdfuri = null;

        if(requestCode == PDF_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {

                if (data != null) {
                    pdfuri = data.getData();
                }
                else { path.setText("Couldnt get file path"); }
            }
        }
        else { path.setText("Couldnt open files"); }

        path.setText(pdfuri.getPath());
        //Might also need to get metadata

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onBackButtonClick(View view) {
        Intent intent = new Intent(UploadActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onGenButtonClick(View view) {
        Intent intent = new Intent(UploadActivity.this, GenerateActivity.class);
        startActivity(intent);
    }
}