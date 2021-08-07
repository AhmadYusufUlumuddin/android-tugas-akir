package com.example.arkacamata.config;

import org.json.JSONArray;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Config {
    public static final String IMAGE_DIRECTORY_NAME = "AR Kacamata";
    public static final String BASE_URL = "http://tugasakhiryusuf.com/";
    public static final String URL = BASE_URL+"Api/";
    public static JSONArray jsonArray, jsonArray2 = null;
    public static final String URL_FOTO_KATEGORI = BASE_URL+"assets/upload/kategori/";
    public static final String URL_FOTO_KACAMATA = BASE_URL+"assets/upload/kacamata/";
    public static final String URL_3D = BASE_URL+"assets/upload/3d/";

    public static Retrofit getRetrofit(String url) {
        return new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build();
    }
}
