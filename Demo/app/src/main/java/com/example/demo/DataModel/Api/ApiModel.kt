package com.example.demo.DataModel.Api

import android.content.Context
import com.example.demo.Interface.ApiInterface
import com.example.demo.Model.ModelPage
import com.google.gson.Gson
import com.lifeprint.digitalframe.Login.Interface.ApiCallBack
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiModel : ApiInterface {

    override fun getMedia(callBack: ApiCallBack<ModelPage>, page_no: String, context: Context,page_size:Int) {
            val apiClient = RetrofitInstance.client.create(RetrofitService::class.java)
            val call = apiClient?.getImageData(page_no,page_size.toString() )
            call?.enqueue(object : Callback<ModelPage> {

                override fun onFailure(call: Call<ModelPage>?, t: Throwable?) {
                    callBack.onError(t.toString()) }
                override fun onResponse(
                    call: Call<ModelPage>?,
                    response: Response<ModelPage>
                ) {
                    if (response.isSuccessful) {
                        callBack.onSuccess(response.body()!!)
                    } else {
                        callBack.onError(response.message())
                    }
                }
            })
        }
    }

