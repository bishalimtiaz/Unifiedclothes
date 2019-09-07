package com.blz.prisoner.unifiedclothes;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public  static final String base_url = "https://unifiedclothes.com/Unifiedclothes/Scripts/";
    public static Retrofit retrofit = null;

    public static Retrofit getApiClient()
    {

        if(retrofit==null){

            retrofit = new Retrofit.Builder().baseUrl(base_url).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;

    }
}
