package com.example.tiktok.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class VideoDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "todo.db";
    private static final int DB_VERSION = 2;

    public VideoDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(VideoContract.CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(int version = oldVersion; version < newVersion; version++){
            if(version == 1){
                try {
                    db.execSQL(VideoContract.ADD_PRIORITY_COLUMN_SQL);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
