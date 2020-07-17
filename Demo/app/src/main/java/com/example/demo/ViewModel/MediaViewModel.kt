package com.example.demo.ViewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.demo.DataModel.Db.GalleryDbManager
import com.example.demo.Interface.ApiInterface
import com.example.demo.Model.Data
import com.example.demo.Model.ModelPage
import com.lifeprint.digitalframe.Login.Interface.ApiCallBack


class MediaViewModel(private val mMediaViewModel : ApiInterface) : ViewModel() {

    private val mImageSuccess = MutableLiveData<ModelPage>()
    private val mImageFaild = MutableLiveData<String>()
    private var mList = ArrayList<Data>()

    fun getImages():LiveData<ModelPage>{
        return mImageSuccess
    }

    fun getImageFaild():LiveData<String>{
        return mImageFaild
    }

    fun getMedia(context: Context, page_no: String, page_size: Int) {
      mMediaViewModel.getMedia(object :ApiCallBack<ModelPage>{
          override fun onError(error: String?) {
             mImageFaild.postValue(error.toString())
          }

          override fun onSuccess(data: ModelPage) {
              mList.addAll(data?.data as ArrayList<Data>)
              mImageSuccess.postValue(data)
          }
      },page_no,context,page_size)
    }

     fun addDataToDb(arrayList: ArrayList<Data>) {
        val dbManager = GalleryDbManager()
        for (data in arrayList){
            dbManager.addGalleryData(data)
        }
    }

    fun getMediaList(): ArrayList<Data> {
          return mList
    }

    fun clearList() {
        mList.clear()
    }

    fun getImageDataFromDb(): ArrayList<Data> {
         val db = GalleryDbManager()
        return db.getImageData()
    }


}