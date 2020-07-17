package com.example.demo

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demo.Interface.MediaInjection
import com.example.demo.Model.Data
import com.example.demo.Model.ModelPage
import com.example.demo.Util.AppUtil
import com.example.demo.Util.DeviceManager
import com.example.demo.Util.InfiniteScrollListener
import com.example.demo.View.Adapter.GalleryAdapter
import com.example.demo.ViewModel.MediaViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),InfiniteScrollListener.OnLoadMoreListener {

    private lateinit var infiniteScrollListener: InfiniteScrollListener
    private var totalPage: Int=1
    private var mList: ArrayList<Data> = ArrayList()
    private var page_no: Int = 1
    private var totalitems: Int = 0
    private var isApiCalled: Boolean = false
    private lateinit var wifiReceiver: BroadcastReceiver
    private lateinit var mGalleryListAdapter: GalleryAdapter
    private lateinit var mMediaViewModel: MediaViewModel
    private  var mDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerBroadcast()
        setUpViewModel()
        setPhotoAdapter()
        setSwipeRefresh()
    }

    /**
     * handle swipe refresh event
     */
    private fun setSwipeRefresh() {
        swipeRefresh.setOnRefreshListener {
            Handler().postDelayed({
                swipeRefresh.isRefreshing = false
            }, 1000)
            if (DeviceManager.isNetworkAvailable()) {
                page_no = 1
                totalPage = 1
                totalitems = 0
                mGalleryListAdapter.clearList()
                getMediaList(false)
            }
        }
    }

    private fun registerBroadcast() {
        wifiReceiver = WifiReceiver()
        try {
            registerReceiver(wifiReceiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
        }catch (e: Exception){}
    }

    inner class WifiReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val conMan: ConnectivityManager? =context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo: NetworkInfo? = conMan?.getActiveNetworkInfo()
            if (netInfo != null){
                tv_no_data.visibility = View.GONE
                rv_image.visibility = View.VISIBLE
                getMediaList(true)
            }else{
                if (mMediaViewModel.getMediaList().size == 0){
                    if (mMediaViewModel.getImageDataFromDb().size == 0){
                        tv_no_data.visibility = View.VISIBLE
                        tv_no_data.text = getString(R.string.no_internet_nplease_connect_to_internet)
                        rv_image.visibility = View.GONE
                    }else{
                        tv_no_data.visibility = View.GONE
                        rv_image.visibility = View.VISIBLE
                        mList.addAll(mMediaViewModel.getImageDataFromDb())
                        setPhotoAdapter()
                    }
                }
            }
        }
    }

    /**
     * set up view model using DI
     */
    private fun setUpViewModel() {
        mMediaViewModel = ViewModelProvider(this,MediaInjection.provideViewModelFactory()).get(MediaViewModel::class.java)
        setObserver()
    }

    /**
     * this method observe data change
     */
    private fun setObserver() {
        mMediaViewModel.getImages().observe(this, Observer { it ->
            handleApiResponse(it);
        })
        mMediaViewModel.getImageFaild().observe(this,Observer{
            Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
            mDialog?.dismiss()
            isApiCalled = false
        })
    }

    /**
     * this method handle api response and update list
     */
    private fun handleApiResponse(it: ModelPage) {
        if (it.data?.size != 0)
            tv_no_data.visibility = View.GONE
        if (page_no != 1) {
            mGalleryListAdapter.removeNullData()
        }
        totalitems = it.data?.size!!
        totalPage = it.totalPages!!
        page_no++
        mGalleryListAdapter.notifyData(mMediaViewModel.getMediaList())
        mDialog?.dismiss()
        addToDb(it?.data as ArrayList<Data>)
        isApiCalled = false
        infiniteScrollListener.setLoaded()
    }

    /**
     * add images to database
     */
    private fun addToDb(arrayList: ArrayList<Data>) {
        Thread(Runnable {
            Thread.sleep(5000)
            mMediaViewModel.addDataToDb(arrayList)
        }).start()
    }

    /**
     * get media list
     */
    private fun getMediaList(isLoaderShow: Boolean) {
        if (isLoaderShow){
            mDialog = AppUtil.showProgressDialog(this)
        }
        isApiCalled = true
        mMediaViewModel.getMedia(this,page_no.toString(),page_size = 5)
    }

    /**
     * set adapter to show list
     */
    private fun setPhotoAdapter() {
        val manager = LinearLayoutManager(this)
        infiniteScrollListener = InfiniteScrollListener(manager, this)
        infiniteScrollListener.setLoaded()
        rv_image.setLayoutManager(manager)
        rv_image.addOnScrollListener(infiniteScrollListener)
        mGalleryListAdapter = GalleryAdapter(this,mList)
        rv_image.adapter = mGalleryListAdapter
        rv_image.itemAnimator = null
    }

    /**
     * handle infinite scroll
     */
    override fun onLoadMore() {
        if (DeviceManager.isNetworkAvailable()) {
            if (totalPage >= page_no) {
                mGalleryListAdapter.addNullData()
                Handler().postDelayed({
                    Thread(Runnable {
                        isApiCalled = true
                        mMediaViewModel.getMedia(this@MainActivity, page_no.toString(), 5)
                    }).start()
                }, 2000)
            }
        }
    }

}
