package com.example.tiktok.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PostDataListResponse {
    @SerializedName("feeds")
    public List<PostData> feeds;
    @SerializedName("success")
    public boolean success;
}
