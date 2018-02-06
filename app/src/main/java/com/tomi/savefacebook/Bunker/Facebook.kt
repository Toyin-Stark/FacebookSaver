package com.tomi.savefacebook.Bunker

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import com.tomi.savefacebook.MainActivity
import com.tomi.savefacebook.R
import com.tomi.savefacebook.Utility.*
import im.delight.android.webview.AdvancedWebView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.facebook.view.*
import java.util.concurrent.TimeUnit

class Facebook : Fragment(), SwipeRefreshLayout.OnRefreshListener, AdvancedWebView.Listener {


   var observable :Disposable? = null
    var handler = Handler()
    var delay = 3000
    var runnables:Runnable? = null
    var showing = false
    var webby:AdvancedWebView? = null
    var swipes:SwipeRefreshLayout? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val v = inflater!!.inflate(R.layout.facebook, container, false)

        webby = v.mWebView
        swipes = v.swipeView

        val address = "http://facebook.com"
        initBrowser(address,webby!!)
        webby!!.setListener(activity,this@Facebook)
        return v
    }




    override fun onPageStarted(url: String?, favicon: Bitmap?) {
        swipes!!.isRefreshing = true
        webby!!.visibility = View.GONE


    }

    override fun onExternalPageRequest(url: String?) {


    }

    override fun onDownloadRequested(url: String?, suggestedFilename: String?, mimeType: String?, contentLength: Long, contentDisposition: String?, userAgent: String?) {


    }

    override fun onPageError(errorCode: Int, description: String?, failingUrl: String?) {


    }

    override fun onPageFinished(url: String?) {
        swipes!!.isRefreshing = false
        injectScriptFile(activity, "jquery.js", webby!!)
        injectScriptFile(activity, "fb/witty.js", webby!!)
        injectCSS(activity, "fb/style.css", webby!!)
        webby!!.visibility = View.VISIBLE
        Rebrand()

    }

    override fun onRefresh() {
        val address = "http://facebook.com"
        webby!!.loadUrl(address)

    }







    fun initBrowser( address:String, mWebView: AdvancedWebView){

        val jsInterface = JavaScriptInterface(activity)
        val webSettings = webby!!.settings
        webSettings.javaScriptEnabled = true
        webSettings.allowUniversalAccessFromFileURLs = true
        webSettings.domStorageEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webby!!.webChromeClient = WebChromeClient()
        webby!!.loadUrl(address)
        webby!!.addJavascriptInterface(jsInterface, "JSInterface")
        webby!!.webViewClient = KustomClient()
        webby!!.setOnKeyListener(object:View.OnKeyListener{
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {

                if (event!!.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (webby!!.canGoBack()) {
                        webby!!.goBack()
                    } else {

                        (activity as MainActivity).onBackPressed()
                    }
                    return true
                }
                return false

            }


        })

    }


    fun canGoBack(): Boolean {
        return webby!!.canGoBack()
    }

    fun goBack() {
        webby!!.goBack()
        webby!!!!.evaluateJavascript("test()") { }
    }

    fun reScript(){
        val video = context.getString(R.string.saveVideo)
        val photo = context.getString(R.string.savePhoto)
        webby!!.evaluateJavascript("test('$video','$photo')") {}
    }





    override fun onResume() {
        super.onResume()
    }



    inner class JavaScriptInterface(private val activity: Activity) {

        @JavascriptInterface
        fun startVideo(videoAddress: String) {


            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {




                if (showing){



                }else{


                    if (videoAddress.contains("background-image")){

                        val active = getActivity() as MainActivity

                        val rebook = videoAddress.substring(0, videoAddress.indexOf("background-repeat"))
                        val tube = rebook.substring(rebook.indexOf("(")+1).replace(")","").replace(";","").replace("'","").replace("\\26","&").replace("\\3d","=").replace("\\3a",":").replace(" ","")

                        showing = photoDialog(context,tube,activity)



                    }

                    if (videoAddress.contains("\"videoID\"")){

                        val playresult = videoAddress.substring(0, videoAddress.indexOf("\"width\""))
                        val playtunes = playresult.substring(playresult.indexOf("\"src\"")+1).replace("\"", "").replace("src","").replace(",","").replace(":","").replace("/","").replace("https","https:")


                        val result = videoAddress.substring(0, videoAddress.indexOf(","))
                        val pattern = "\\D+".toRegex()
                        val tunes = result.replace(pattern,"")

                        showing = videoDialog(activity,tunes,playtunes,activity)


                    }

                }





            }else{

                AskPermission(activity, context)
            }


        }




        @JavascriptInterface
        fun reloader(vinky: String){

        }


    }


    override fun onDestroy() {
        super.onDestroy()
        if (runnables != null ) {
            handler.removeCallbacks(runnables)

        }

    }








    fun Rebrand(){


        handler.postDelayed(object :Runnable{
            override fun run() {

               runnables = this
                reScript()
                handler.postDelayed(runnables, delay.toLong());

            }

        },delay.toLong())
    }


    // Downloader






}

