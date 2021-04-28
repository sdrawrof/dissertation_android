package com.example.dissertation_android;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {super(context, "dissertation_database", null, 1);}

    //intialise database tables and get handle
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE materials (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name VARCHAR(128)" +
                "); ");

        db.execSQL("CREATE TABLE questions (" +
                "question_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "materials_id INTEGER, " +
                "question_text TEXT, "+
                "CONSTRAINT fk_materials FOREIGN KEY (materials_id) REFERENCES materials (_id) " +
                ");");
        Log.d("dissertation_android", "created questions database");
    }

    //onUpgrade will be called if the version number is incremented from the previous version number
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + "dissertation_database");
        onCreate(db);
        Log.d("dissertation_android", "Updated table");
    }
}
