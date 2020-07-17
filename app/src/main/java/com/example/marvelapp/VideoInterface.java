package com.example.marvelapp;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface VideoInterface {

    String VIDEOURL = "http://inventivepartner.com/Inventive_fruits/";
    @Multipart
    @POST("uploadVideo.php")
    Call<String> uploadImage(
            @Part MultipartBody.Part file, @Part("filename") RequestBody name
    );

}