package com.tomi.savefacebook.Bunker


import android.Manifest
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.kennyc.bottomsheet.BottomSheet
import com.kennyc.bottomsheet.BottomSheetListener
import com.tomi.savefacebook.Bounds
import com.tomi.savefacebook.R
import com.tomi.savefacebook.Utility.AskPermission
import com.tomi.savefacebook.Utility.getScaledBounds
import com.tomi.savefacebook.Utility.snackUp
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.manager.*
import kotlinx.android.synthetic.main.manager.view.*
import kotlinx.android.synthetic.main.save_row.view.*
import java.io.File
import java.util.*


class Downloads : Fragment(),SwipeRefreshLayout.OnRefreshListener {
  

    var bitty: Bitmap? = null
    var type = ""
    private var observable: Observable<String>? = null
    private var adapter: RecyclerView_Adapter? = null
    private var arrayList: ArrayList<Data_Model>? = null
    private var urls: ArrayList<String>? = null

    private var path: String? = null
    internal var cur: Cursor? = null
    private var Mobservable: Observable<String>?= null


    var swipes:SwipeRefreshLayout? = null
    var recyclerviews :RecyclerView? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        

        val v = inflater!!.inflate(R.layout.manager, container, false)
        
        
        swipes = v.swipe_view
        recyclerviews = v.recycler_view

        swipes!!.setColorSchemeColors(Color.GRAY, Color.GREEN, Color.BLUE,
                Color.RED, Color.CYAN)
        swipes!!.setDistanceToTriggerSync(20)// in dips
        swipes!!.setSize(SwipeRefreshLayout.DEFAULT)// LARGE also can be used
        swipes!!.setOnRefreshListener(this)

        recyclerviews!!.setHasFixedSize(true)
        recyclerviews!!.isNestedScrollingEnabled = true
        recyclerviews!!.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)

        v.fabe.setOnClickListener {

            kmate()
        }




        kmate()
        return v
    }





    fun kmate(){

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            FileQuery()

        }else{

            AskPermission(activity,context)
        }

    }


    fun FileQuery() {
        swipes!!.isRefreshing = true
        arrayList = ArrayList<Data_Model>()
        arrayList!!.clear()
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/fbsave/"
        val dex = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath, "fbsave")
        if (!dex.exists())
            dex.mkdirs()


        observable = Observable.create(object: ObservableOnSubscribe<String> {
            override fun subscribe(subscriber: ObservableEmitter<String>) {


                val projection = arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DISPLAY_NAME, MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.MIME_TYPE, MediaStore.Files.FileColumns.DATE_ADDED, MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.SIZE, MediaStore.Video.Thumbnails.DATA)


                val queryUri = MediaStore.Files.getContentUri("external")


                cur = activity.contentResolver.query(queryUri, projection, MediaStore.Files.FileColumns.DATA + " LIKE ? AND " + MediaStore.Files.FileColumns.DATA + " NOT LIKE ?", arrayOf(path + "%", path + "%/%"), MediaStore.Files.FileColumns.DATE_ADDED + " DESC")

                var data: String
                var name: String
                var mime: String?
                var id: String
                var type: String
                var time: String
                var url: String
                var size: String

                if (cur != null) {

                    if (cur!!.moveToFirst()) {

                        val dataColumn = cur!!.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                        val nameColumn = cur!!.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
                        val mimeColumn = cur!!.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE)
                        val idColumn = cur!!.getColumnIndex(MediaStore.Files.FileColumns._ID)
                        val timeColumn = cur!!.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)
                        val typeColumn = cur!!.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE)
                        val sizeColumn = cur!!.getColumnIndex(MediaStore.Files.FileColumns.SIZE)


                        do {

                            data = cur!!.getString(dataColumn)
                            name = cur!!.getString(nameColumn)
                            mime = cur!!.getString(mimeColumn)
                            id = cur!!.getString(idColumn)
                            time = cur!!.getString(timeColumn)
                            type = cur!!.getString(typeColumn)
                            size = cur!!.getString(sizeColumn)
                            val date = Date(cur!!.getLong(cur!!.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)) * 1000)

                            val big = java.lang.Long.parseLong(size)
                            size = Formatter.formatFileSize(activity, big)

                            if (mime != null && mime.contains("video")) {

                                val uri = Uri.parse(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString() + "/" + id)
                                url = uri.toString()


                            }

                            if (mime != null && mime.contains("audio")) {

                                val uri = Uri.parse(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString() + "/" + id)
                                url = uri.toString()


                            }


                            else {

                                url = data
                            }

                            val milliSeconds = date.time
                            arrayList!!.add(Data_Model(name, mime, url, milliSeconds, data, size))

                        } while (cur!!.moveToNext())


                    } else {

                    }
                }

                cur!!.close()

                subscriber.onNext("")
                subscriber.onComplete()

            }
        })


        observable!!.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Observer<String> {
                    override fun onComplete() {

                        adapter = RecyclerView_Adapter(activity, arrayList!!, this@Downloads)

                        if (adapter == null) {


                        } else {
                            swipes!!.isRefreshing = false

                            recyclerviews!!.adapter = adapter// set adapter on recyclerview
                            adapter!!.notifyDataSetChanged()

                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                    override fun onError(e: Throwable) {

                        Toast.makeText(activity, "" + e, Toast.LENGTH_LONG).show()


                    }

                    override fun onNext(response: String) {


                    }


                })


    }

    companion object {
        var thumbColumns = arrayOf(MediaStore.Video.Thumbnails.DATA)
        var mediaColumns = arrayOf(MediaStore.Video.Media._ID)
    }




    override fun onRefresh() {

        kmate()
    }





    fun optionalImage(url: String) {


        BottomSheet.Builder(activity)
                .setSheet(R.menu.image_menu)
                .setTitle(R.string.options_image)
                .setListener(object : BottomSheetListener {
                    override fun onSheetShown(p0: BottomSheet) {


                    }

                    override fun onSheetDismissed(p0: BottomSheet, p1: Int) {


                    }

                    override fun onSheetItemSelected(p0: BottomSheet, item: MenuItem?) {

                        val id = item!!.itemId
                        when (id) {

                            R.id.set -> {

                                Babaria(url)

                            }


                            R.id.repeat -> {

                                MediaScannerConnection.scanFile(activity, arrayOf(url), null) { path, uri ->

                                    val untent = Intent(Intent.ACTION_SEND)
                                    untent.setDataAndType(uri, "image/*")
                                    untent.`package` = "com.instagram.android"
                                    untent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    startActivity(untent)
                                }


                            }

                            R.id.view -> {

                                MediaScannerConnection.scanFile(activity, arrayOf(url), null) { path, uri ->
                                    val untent = Intent(Intent.ACTION_VIEW)
                                    untent.setDataAndType(uri, "image/*")
                                    startActivity(untent)
                                }



                            }

                            R.id.share -> {

                                MediaScannerConnection.scanFile(activity, arrayOf(url), null) { path, uri ->

                                    val untent = Intent(Intent.ACTION_SEND)
                                    untent.setDataAndType(uri, "image/*")
                                    startActivity(untent)

                                }

                            }


                        }

                    }


                })
                .show();

    }



    //===============================Audio Options=====================================================//

    fun optionalAudio(murl:String){


        MediaScannerConnection.scanFile(activity, arrayOf(murl), null) { path, uri ->

            val binta = Intent()
            binta.action = Intent.ACTION_VIEW
            binta.setDataAndType(uri, "audio/*")
            startActivity(binta)

        }

    }



    //===============================Video Options=====================================================//



    fun optionalVideo(url: String) {


        BottomSheet.Builder(activity)
                .setSheet(R.menu.video_menu)
                .setTitle(R.string.options_video)
                .setListener(object : BottomSheetListener {
                    override fun onSheetShown(p0: BottomSheet) {


                    }

                    override fun onSheetDismissed(p0: BottomSheet, p1: Int) {


                    }

                    override fun onSheetItemSelected(p0: BottomSheet, item: MenuItem?) {

                        val id = item!!.itemId
                        when (id) {




                            R.id.repeat -> {


                                MediaScannerConnection.scanFile(activity, arrayOf(url), null) { path, uri ->

                                    val untent = Intent(Intent.ACTION_SEND)
                                    untent.putExtra(Intent.EXTRA_STREAM,uri)
                                    untent.type = "video/*"
                                    untent.`package` = "com.instagram.android"
                                    untent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    startActivity(untent)

                                }


                            }

                            R.id.view -> {


                                MediaScannerConnection.scanFile(activity, arrayOf(url), null) { path, uri ->

                                    val untent = Intent(Intent.ACTION_VIEW)
                                    untent.setDataAndType(uri, "video/*")
                                    startActivity(untent)
                                }



                            }

                            R.id.share -> {

                                MediaScannerConnection.scanFile(activity, arrayOf(url), null) { path, uri ->

                                    val untent = Intent(Intent.ACTION_SEND)
                                    untent.putExtra(Intent.EXTRA_STREAM,uri)
                                    untent.type = "video/*"
                                    untent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    startActivity(untent)
                                }


                            }


                        }

                    }


                })
                .show();

    }



    fun Babaria(linku:String) {


        swipes!!.isRefreshing = true

        Mobservable = Observable.create(object: ObservableOnSubscribe<String> {
            override fun subscribe(subscriber: ObservableEmitter<String>) {

                val wallmap = BitmapFactory.decodeFile(linku)


                val bally = WallpaperManager.getInstance(activity)
                val displayMetrics = resources.displayMetrics

                val maxWidth = displayMetrics.heightPixels * 2

                val wallpaperSize = Bounds(maxWidth.toDouble(), displayMetrics.heightPixels.toDouble())

                val imageSize = Bounds(wallmap.width.toDouble(), wallmap.height.toDouble())
                val (m_width, m_height) = getScaledBounds(imageSize, wallpaperSize)


                val scaledBitmap = Bitmap.createScaledBitmap(wallmap, m_width.toInt(), m_height.toInt(), false)
                bally.setBitmap(scaledBitmap)




                subscriber.onNext("finish")

                subscriber.onComplete()

            }
        })


        Mobservable!!.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Observer<String> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onComplete() {
                        swipes!!.isRefreshing = false
                        snackUp(context,getString(R.string.done),constantine)

                    }

                    override fun onError(e: Throwable) {



                    }

                    override fun onNext(response: String) {


                    }
                })


    }













    override fun onResume() {
        super.onResume()


        kmate()


    }





}

data class Data_Model(var title:String,var desc:String,var image:String,var time:Long,var link:String,var size:String)

class RecyclerView_Adapter(var context: Context, var arraylists: ArrayList<Data_Model>, var downloads: Downloads) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        var v = LayoutInflater.from(context).inflate(R.layout.save_row, parent, false)
        return Item(v)
    }

    override fun getItemCount(): Int {
        return arraylists.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as Item).bindData(arraylists[position],downloads)


    }

    class Item(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(_data: Data_Model, _cube:Downloads) {
            itemView.title.text = _data.title
            itemView.desc.text  = _data.desc
            itemView.links.text = _data.link
            itemView.size.text = _data.size

            itemView.time.setReferenceTime(_data.time)

            val extension = _data.title.substring(_data.title.lastIndexOf(".") + 1)
            val ext = _data.desc;
            var urx = ""

            if (ext.contains("video")){
                urx = _data.image
                itemView.playable.visibility = View.VISIBLE

            }


            if(ext.contains("image")){

                urx = "file://"+_data.link;
                itemView.playable.visibility = View.GONE

            }


            if(ext.contains("audio")){

                urx = _data.image
                itemView.playable.visibility = View.GONE

            }



            if (ext.contains("audio")){


                itemView.cover.setImageResource(R.drawable.musica)

            }else{

                if (extension.contains("gif")){
                    Glide.with(itemView.context).load(urx).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(itemView.cover);

                }else{

                    Glide.with(itemView.context)
                            .load(urx)
                            .asBitmap()
                            .into(object: SimpleTarget<Bitmap>(150,150){
                                override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {

                                    itemView.cover.setImageBitmap(resource)


                                }


                            })

                }



            }






            itemView.setOnClickListener {

                if ( ext.contains("video"))
                {

                    _cube.optionalVideo(_data.link)

                }

                if ( ext.contains("image")) {
                    _cube.optionalImage(_data.link)

                }

                if ( ext.contains("audio")) {
                    _cube.optionalAudio(_data.link)

                }

            }
        }
    }

}
