package com.tomi.savefacebook.Player

import android.app.WallpaperManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Environment
import android.support.design.widget.FloatingActionButton
import android.view.View
import android.widget.TextView
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.Glide
import io.github.kobakei.materialfabspeeddial.FabSpeedDial
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import com.bumptech.glide.request.animation.GlideAnimation
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.tomi.savefacebook.Bounds
import com.tomi.savefacebook.R
import com.tomi.savefacebook.Utility.*

import kotlinx.android.synthetic.main.photo_player.*
import java.io.File
import java.io.FileOutputStream


class PhotoPlayer : AppCompatActivity() {
    private var Mobservable: Observable<String>?= null
    private var Wobservable: Observable<String>?= null
    var mInterstitialAd: InterstitialAd? = null
    var file:File? = null

    var bitty:Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( R.layout.photo_player)

        val ints = intent.extras
        var photoID = ints.getString("url")
        loadImage(photoID)

        mInterstitialAd = InterstitialAd(this@PhotoPlayer)
        mInterstitialAd!!.adUnitId = getString(R.string.intersistal)

        val adRequest = AdRequest.Builder()
                .build()

        // Load ads into Interstitial Ads
        mInterstitialAd!!.loadAd(adRequest)



        fab.addOnMenuItemClickListener(object: FabSpeedDial.OnMenuItemClickListener{
            override fun onMenuItemClick(miniFab: FloatingActionButton?, label: TextView?, itemId: Int) {

                when(itemId){

                    R.id.set ->{


                        Babaria(bitty!!)
                    }

                    R.id.save ->{


                        saveVakia(bitty!!)

                    }




                }



            }


        })

    }

   fun loadImage(url:String){

        Glide.with(this)
                .load(url)    // you can pass url too
                .asBitmap()
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {
                        // you can do something with loaded bitmap here

                        bitty = resource
                        groove.setImageBitmap(resource)
                        spinner.visibility = View.GONE
                        fab.visibility = View.VISIBLE



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
                })
    }



    fun showInterstitial() {
        if (mInterstitialAd!!.isLoaded()) {
            mInterstitialAd!!.show();
        }
    }
















    fun Babaria(bitman:Bitmap) {


        Mobservable = Observable.create(object: ObservableOnSubscribe<String> {
            override fun subscribe(subscriber: ObservableEmitter<String>) {



                val bally = WallpaperManager.getInstance(applicationContext)
                val displayMetrics = resources.displayMetrics

                val maxWidth = displayMetrics.heightPixels * 2

                val wallpaperSize = Bounds(maxWidth.toDouble(), displayMetrics.heightPixels.toDouble())

                val imageSize = Bounds(bitman.width.toDouble(), bitman.height.toDouble())
                val (m_width, m_height) = getScaledBounds(imageSize, wallpaperSize)


                val scaledBitmap = Bitmap.createScaledBitmap(bitman, m_width.toInt(), m_height.toInt(), false)
                bally.setBitmap(scaledBitmap)




                subscriber.onNext("finish")

                subscriber.onComplete()

            }
        })


        Mobservable!!.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Observer<String> {
                    override fun onSubscribe(d: Disposable) {

                        wallpaperToast(this@PhotoPlayer,applicationContext)

                    }

                    override fun onComplete() {


                        wallpaperDoneToast(this@PhotoPlayer,applicationContext)

                    }

                    override fun onError(e: Throwable) {



                    }

                    override fun onNext(response: String) {


                    }
                })


    }






    //Save Bitmap

    fun saveVakia(bitchild:Bitmap){


        Wobservable = Observable.create(object: ObservableOnSubscribe<String> {
            override fun subscribe(subscriber: ObservableEmitter<String>) {


                val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
                val myDir = File(root + "/fbsave")
                myDir.mkdirs()
                val timeStamp =  System.currentTimeMillis()
                val filename = "fb_photo"+"_"+timeStamp
                val fname = filename + ".jpg"
                 file = File(myDir, fname)
                if (file!!.exists())
                //  only try to save picture if it doesn't exist already
                    try {
                        val out = FileOutputStream(file)
                        bitchild.compress(Bitmap.CompressFormat.JPEG, 100, out)
                        out.flush()
                        out.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                subscriber.onNext("finish")

                subscriber.onComplete()

            }
        })


        Wobservable!!.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Observer<String> {
                    override fun onSubscribe(d: Disposable) {

                        val message = getString(R.string.downloadPhoto)
                        downloadToast(this@PhotoPlayer,message,applicationContext)

                    }

                    override fun onComplete() {
                        val paths = file!!.absolutePath
                        MediaScannerConnection.scanFile(applicationContext, arrayOf(paths), null) { p0, uri ->

                        }

                        imageDoneToast(this@PhotoPlayer,applicationContext)

                    }

                    override fun onError(e: Throwable) {



                    }

                    override fun onNext(response: String) {


                    }
                })


    }



}
