package com.example.tiktok.Database;

import android.provider.BaseColumns;

public final class VideoContract {

    public static final String CREATE_SQL = "CREATE TABLE " + VideoInfo.Name_Table
            + "(" + VideoInfo._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + VideoInfo.Post_ID + " TEXT, "
            + VideoInfo.Student_Attribute + " TEXT, "
            + VideoInfo.User_Attribute + " TEXT, "
            + VideoInfo.Image_Attribute + " TEXT, "
            + VideoInfo.Video_Attribute + " TEXT, "
            + VideoInfo.Create_Attribute + " TEXT, "
            + VideoInfo.Update_Attribute + " TEXT)";


    public static final String ADD_PRIORITY_COLUMN_SQL =
            "ALTER TABLE" + VideoInfo.Name_Table + " ADD " + VideoInfo.Update_Attribute + " TEXT";

    public static class VideoInfo implements BaseColumns {
        public static final String Name_Table = "video";
        public static final String Post_ID = "post_id";
        public static final String Student_Attribute = "student_id";
        public static final String User_Attribute = "user_name";
        public static final String Image_Attribute = "image_url";
        public static final String Video_Attribute = "video_url";
        public static final String Create_Attribute = "createdAt";
        public static final String Update_Attribute = "updateAt";
    }
}

