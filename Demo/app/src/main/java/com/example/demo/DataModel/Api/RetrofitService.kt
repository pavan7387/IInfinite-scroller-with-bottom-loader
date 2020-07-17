package com.example.demo.DataModel.Api

import android.telecom.Call
import com.example.demo.Model.ModelPage
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitService{

    @GET("api/users")
    fun getImageData(@Query(value = "page", encoded = true) page_no :String,
                     @Query(value = "per_page", encoded = true) page_size :String)  : retrofit2.Call<ModelPage>

}