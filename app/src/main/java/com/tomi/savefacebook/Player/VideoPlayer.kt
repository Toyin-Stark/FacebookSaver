package com.tomi.savefacebook.Player

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.halilibo.bettervideoplayer.BetterVideoCallback
import com.halilibo.bettervideoplayer.BetterVideoPlayer
import com.tomi.savefacebook.R
import com.tomi.savefacebook.Utility.Saveit
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.video_player.*
import java.lang.Exception


class VideoPlayer : AppCompatActivity(), BetterVideoCallback {

    var observable: Observable<String>? = null
    var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_player)


        val ints = intent.extras
        val source = ints.getString("source")
        player.setCallback(this)


        mInterstitialAd = InterstitialAd(this@VideoPlayer)
        mInterstitialAd!!.adUnitId = getString(R.string.intersistal)

        val adRequest = AdRequest.Builder()
                .build()

        // Load ads into Interstitial Ads
        mInterstitialAd!!.loadAd(adRequest)



        if (source.contains("facebook")){

            val videoID = ints.getString("url")
            Nue(videoID)

        }else{

            val videoURL = ints.getString("url")
            val uri = Uri.parse(videoURL)
            player.setSource(uri)

        }




    }


    override fun onPaused(player: BetterVideoPlayer?) {


    }

    override fun onToggleControls(player: BetterVideoPlayer?, isShowing: Boolean) {


    }

    override fun onError(player: BetterVideoPlayer?, e: Exception?) {


    }

    override fun onPreparing(player: BetterVideoPlayer?) {


    }

    override fun onBuffering(percent: Int) {


    }

    override fun onCompletion(player: BetterVideoPlayer?) {


    }

    override fun onStarted(player: BetterVideoPlayer?) {


    }

    override fun onPrepared(player: BetterVideoPlayer?) {

        player!!.start()

        mInterstitialAd!!.adListener = object : AdListener() {
            override fun onAdLoaded() {

                showInterstitial()

                val adRequest = AdRequest.Builder()
                        .build()

                // Load ads into Interstitial Ads
                mInterstitialAd!!.loadAd(adRequest)
            }

            override fun onAdClosed() {
                super.onAdClosed()
            }
        }

    }











    fun showInterstitial() {
        if (mInterstitialAd!!.isLoaded()) {
            mInterstitialAd!!.show();
        }
    }

    //Get video Url

    fun Nue(videoID:String)
    {
        observable = Observable.create(object: ObservableOnSubscribe<String> {
            override fun subscribe(subscriber: ObservableEmitter<String>) {


                try {

                    val start = "https://graph.facebook.com/$videoID"
                    val end = "?fields=source&access_token=140972349799682|WU_n4-HFLCNsHT0IWWO7injf-Xk"
                    val fbUrl = start+end
                    val downloadUrl = Saveit(fbUrl)
                    subscriber.onNext(downloadUrl)

                }catch (e:Exception){

                    subscriber.onError(e)
                }


                subscriber.onComplete()
            }
        })

        observable!!.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Observer<String> {
                    override fun onSubscribe(d: Disposable) {



                    }

                    override fun onComplete() {


                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(applicationContext,""+e.message, Toast.LENGTH_LONG).show()

                    }

                    override fun onNext(response: String) {
                        val uri = Uri.parse(response)
                        player.setSource(uri)


                    }
                })

    }


    override fun onDestroy() {
        super.onDestroy()
        observable!!.unsubscribeOn(Schedulers.io())
    }

}
