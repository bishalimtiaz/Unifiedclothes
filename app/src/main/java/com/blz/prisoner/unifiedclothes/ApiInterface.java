package com.blz.prisoner.unifiedclothes;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("get_all_images.php")
    Call<List<DataResponse>> getImages();
}
