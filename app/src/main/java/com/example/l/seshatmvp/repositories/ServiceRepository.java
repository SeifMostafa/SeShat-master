package com.example.l.seshatmvp.repositories;


import com.example.l.seshatmvp.model.ResponseBodyModel;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ServiceRepository
{

    @Headers({"Content-type: application/json","Accept: application/json"})
    @POST("v1/images:annotate?key=AIzaSyAERanQIuMWTNXIjqshwRym0RkbIrn089o")
    Call<ResponseBodyModel> OCRGoogleVision(@Body JsonObject jsonObject);
}
