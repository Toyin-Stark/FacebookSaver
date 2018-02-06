package com.tomi.savefacebook

import android.app.DownloadManager
import android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.esafirm.rxdownloader.RxDownloader
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.internal.zzahn
import com.tomi.savefacebook.Bunker.Downloads
import com.tomi.savefacebook.Bunker.Facebook
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    var observable: Observable<String>? = null
    var mInterstitialAd: InterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        Starters()

        val face = AHBottomNavigationItem(R.string.facebook, R.drawable.ic_facebook,android.R.color.white)
        val save = AHBottomNavigationItem(R.string.saver, R.drawable.ic_saved,android.R.color.white)

        bottomNavigation.addItem(face)
        bottomNavigation.addItem(save)

        bottomNavigation.defaultBackgroundColor = ContextCompat.getColor(applicationContext, R.color.colorBase)
        bottomNavigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
        bottomNavigation.isForceTint = true
        bottomNavigation.accentColor = ContextCompat.getColor(applicationContext,android.R.color.white)

        bottomNavigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
        bottomNavigation.isForceTint = true


        val faces = Facebook()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frames, faces)
        transaction.addToBackStack(null)
        transaction.commit()

        mInterstitialAd = InterstitialAd(this@MainActivity)
        mInterstitialAd!!.adUnitId = getString(R.string.intersistal)

        val adRequest = AdRequest.Builder()
                .build()

        // Load ads into Interstitial Ads
        mInterstitialAd!!.loadAd(adRequest)

        mInterstitialAd!!.adListener = object : AdListener() {
            override fun onAdLoaded() {

                showInterstitial()
            }

            override fun onAdClosed() {
                super.onAdClosed()
            }
        }





        bottomNavigation.setOnTabSelectedListener(object:AHBottomNavigation.OnTabSelectedListener{
            override fun onTabSelected(position: Int, wasSelected: Boolean): Boolean {


                if (position == 0){

                    val faces = Facebook()
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.frames, faces)
                    transaction.addToBackStack(null)
                    transaction.commit()

                }


                if (position == 1){

                    val instas = Downloads()
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.frames, instas)
                    transaction.addToBackStack(null)
                    transaction.commit()

                }


                return true
            }


        })


    }



    fun showInterstitial() {
        if (mInterstitialAd!!.isLoaded()) {
            mInterstitialAd!!.show();
        }
    }


    fun Starters(){

        val t = Thread(Runnable {
            //  Initialize SharedPreferences
            val getPrefs = PreferenceManager
                    .getDefaultSharedPreferences(this@MainActivity)

            //  Create a new boolean and preference and set it to true
            val isFirstStart = getPrefs.getBoolean("firstStart", true)

            //  If the activity has never started before...
            if (isFirstStart) {

                //  Launch app intro
                val i = Intent(this@MainActivity, Tutors::class.java)

                zzahn.runOnUiThread { startActivity(i) }

                //  Make a new preferences editor
                val e = getPrefs.edit()

                //  Edit preference to make it false because we don't want this to run again
                e.putBoolean("firstStart", false)

                //  Apply changes
                e.apply()
            }else{

                runOnUiThread(Runnable {


                })



            }
        })

        // Start the thread
        t.start()

    }













    fun mrSave(urld: String,variety:String){

        val rxDownloader = RxDownloader(this@MainActivity)
        var extension = ""
        var desc = ""

        if (variety.contains("photo")){

            val bean = urld.substring(urld.lastIndexOf(".") + 1)

            if(bean.contains("jpg")){
                extension = "jpg"
            }

            if(bean.contains("png")){
                extension = "png"
            }


            if(bean.contains("gif")){
                extension = "gif"
            }
            desc = getString(R.string.downloadPhoto)


        }

        if (variety.contains("video")){

            extension = "mp4"
            desc = getString(R.string.downloadVideo)


        }



        val timeStamp =  System.currentTimeMillis()
        val filename = "fb_$variety"+"_"+timeStamp
        val name = filename + "." + extension
        val dex = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "fbsave")
        if (!dex.exists())
            dex.mkdirs()

        val Download_Uri = Uri.parse(urld)
        val downloadManager =  getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request =  DownloadManager.Request(Download_Uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false)
        request.setTitle(name)
        request.setDescription(desc)
        request.setVisibleInDownloadsUi(true)
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/fbsave/" + name)

        rxDownloader.download(request).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Observer<String> {
                    override fun onComplete() {


                    }

                    override fun onError(e: Throwable) {


                    }

                    override fun onNext(t: String) {


                    }

                    override fun onSubscribe(d: Disposable) {


                    }


                })

    }


}
