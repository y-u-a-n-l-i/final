package com.example.tiktok.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataListResponse {
    @SerializedName("feeds")
    public List<Data> feeds;
    @SerializedName("success")
    public boolean success;
}
