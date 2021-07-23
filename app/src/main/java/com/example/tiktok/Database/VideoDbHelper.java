package com.example.tiktok.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.tiktok.Data.PostDataUtil;

public class VideoDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "todo.db";
    private static final int DB_VERSION = 2;

    public VideoDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(VideoContract.CREATE_Like_SQL);
        db.execSQL(VideoContract.CREATE_History_SQL);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        String selection = " name = ?";
        String[] selectionArgs = new String[]{VideoContract.VideoInfo.History_Table};
        Cursor cursor = db.query("sqlite_master",
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);
        if(!cursor.moveToNext()) db.execSQL(VideoContract.CREATE_History_SQL);

        selectionArgs = new String[]{VideoContract.VideoInfo.Save_Table};
         cursor = db.query("sqlite_master",
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);
        if(!cursor.moveToNext()) db.execSQL(VideoContract.CREATE_Like_SQL);
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
