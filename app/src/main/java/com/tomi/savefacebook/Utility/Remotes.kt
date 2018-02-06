package com.tomi.savefacebook.Utility

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import im.delight.android.webview.AdvancedWebView
import java.io.IOException
import java.util.*
import android.util.Base64
import android.view.View
import java.io.InputStream
import java.util.regex.Pattern
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Patterns
import android.view.Gravity
import com.google.android.gms.internal.zzahn.runOnUiThread
import com.tomi.savefacebook.Bounds
import com.tomi.savefacebook.MainActivity
import com.tomi.savefacebook.NYSC.Services
import com.tomi.savefacebook.Player.PhotoPlayer
import com.tomi.savefacebook.Player.VideoPlayer
import com.tomi.savefacebook.R
import design.ivisionblog.apps.reviewdialoglibrary.FeedBackActionsListeners
import design.ivisionblog.apps.reviewdialoglibrary.FeedBackDialog
import okhttp3.OkHttpClient
import okhttp3.Request
import org.aviran.cookiebar2.CookieBar
import org.json.JSONObject
import rebus.permissionutils.AskAgainCallback
import rebus.permissionutils.FullCallback
import rebus.permissionutils.PermissionEnum
import rebus.permissionutils.PermissionManager
import java.util.concurrent.TimeUnit

















// INJECT JAVASCRIPT INTO WEBVIEW
 fun injectScriptFile(context: Context,scriptFile: String,mWebView: AdvancedWebView) {
    val rand = Random()
    val verse = rand.nextInt(80 - 65) + 65
    val input: InputStream
    try {
        input = context.assets.open(scriptFile)
        val buffer = ByteArray(input.available())
        input.read(buffer)
        input.close()

        // String-ify the script byte-array using BASE64 encoding !!!
        val encoded = Base64.encodeToString(buffer, Base64.NO_WRAP)

        mWebView.evaluateJavascript("javascript:(function() {" +
                "var parent = document.getElementsByTagName('head').item(0);" +
                "var script = document.createElement('script');" +
                "script.type = 'text/javascript';" +
                // Tell the browser to BASE64-decode the string into your script !!!
                "script.innerHTML = decodeURIComponent(escape(window.atob('" + encoded + "')));" +
                "parent.appendChild(script)" +
                "})()") { }


    } catch (e: IOException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    }

}


// INJECT CSS INTO WEBVIEW
 fun injectCSS(context: Context,filespaces: String,mWebView: AdvancedWebView) {

    try {
        val inputStream = context.assets.open(filespaces)
        val buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
        inputStream.close()
        val encoded = Base64.encodeToString(buffer, Base64.NO_WRAP)
        mWebView!!.loadUrl("javascript:(function() {" +
                "var parent = document.getElementsByTagName('head').item(0);" +
                "var style = document.createElement('style');" +
                "style.type = 'text/css';" +
                // Tell the browser to BASE64-decode the string into your script !!!
                "style.innerHTML = decodeURIComponent(escape(window.atob('" + encoded + "')));" +
                "parent.appendChild(style)" +
                "})()")
    } catch (e: Exception) {
        e.printStackTrace()
    }

}



// EXTRACT LINKS FROM STRINGS
fun pullLinks(text: String): ArrayList<String> {
    val links = ArrayList<String>()

    //String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
    val regex = "\\(?\\b(https?://|www[.]|ftp://)[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]"

    val p = Pattern.compile(regex)
    val m = p.matcher(text)

    while (m.find()) {
        var urlStr = m.group()

        if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
            urlStr = urlStr.substring(1, urlStr.length - 1)
        }

        links.add(urlStr)
    }

    return links
}




// SHOW PERMISSION PROMT DIALOG

fun showDialog(context: Context)
{
    AlertDialog.Builder(context)
            .setTitle(R.string.permissionTitle)
            .setMessage(R.string.permissionMessage)
            .setPositiveButton(R.string.permissionPositive,object: DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                }


            })

            .setNegativeButton(R.string.permissionNegative,object:DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                }


            })
            .setCancelable(false)
            .show()

}



//SHOW SNACKBAR
fun snackUp(context: Context,message:String,view: View)
{
    val snacks = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
    snacks.view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGreen))
    snacks.show()
}


//KONTROLLER CLASS
fun loopBoy(mWebView: AdvancedWebView,context: Context){

    val handler = Handler()
    val delay = 3000 //milliseconds

    handler.postDelayed(object : Runnable {
        override fun run() {

            handler.postDelayed(this, delay.toLong())
        }
    }, delay.toLong())


}

// INITIAL DOWNLOAD NOTIFICATION
fun Alariwo(context: Context){

    val notificationBuilder =  NotificationCompat.Builder(context, "M_CH_ID")

    notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(context.getString(R.string.downloadVideo))
            .setProgress(0, 0, true)

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(1011, notificationBuilder.build());
}




// REQUEST STORAGE PERMISSION

fun AskPermission(activity: Activity,context: Context)
{
    PermissionManager.Builder()
            .permission(PermissionEnum.WRITE_EXTERNAL_STORAGE)
            .askAgain(true)
            .askAgainCallback(object : AskAgainCallback {
                override fun showRequestPermission(response: AskAgainCallback.UserResponse?) {

                    showDialog(context)
                }


            }).callback(object: FullCallback {
        override fun result(permissionsGranted: ArrayList<PermissionEnum>?, permissionsDenied: ArrayList<PermissionEnum>?, permissionsDeniedForever: ArrayList<PermissionEnum>?, permissionsAsked: ArrayList<PermissionEnum>?) {

        }


    }).ask(activity)

}


//Permission Checker


fun Checkmate(activity: Activity,context: Context){

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {


    }else{

        AskPermission(activity,context)
    }

}


//Save Video Dialog

fun videoDialog(context: Context,videoID: String,playUrl:String,activity: Activity):Boolean{
    var bawo :Boolean? = null
    var mDialog = FeedBackDialog(context)
            .setBackgroundColor(R.color.colorDialogBack)
            .setIcon(R.drawable.ic_file_download)
            .setIconColor(android.R.color.primary_text_light)
            .setTitle(R.string.saveVideo)
            .setPositiveFeedbackIcon(R.drawable.ic_file_download)
            .setNegativeFeedbackIcon(R.drawable.ic_play)
            .setAmbiguityFeedbackIcon(R.drawable.ic_clear)
            .setDescription(R.string.dialogVideoMessage)
            .setReviewQuestion(R.string.dialogJambVideo)
            .setPositiveFeedbackText(R.string.dialogYes)
            .setNegativeFeedbackText(R.string.dialogNoVideo)
            .setAmbiguityFeedbackText(R.string.dialogCancel)
            .setOnReviewClickListener(object : FeedBackActionsListeners{
                override fun onNegativeFeedback(dialog: FeedBackDialog?) {

                    dialog!!.dismiss()
                    bawo = false
                    val intes = Intent(context, VideoPlayer::class.java )
                    intes.putExtra("url",videoID)
                    intes.putExtra("source","facebook")
                    context.startActivity(intes)

                }

                override fun onCancelListener(dialog: DialogInterface?) {

                    bawo = false

                }

                override fun onAmbiguityFeedback(dialog: FeedBackDialog?) {
                    bawo = false
                    dialog!!.dismiss()



                }

                override fun onPositiveFeedback(dialog: FeedBackDialog?) {

                    val ints = Intent(context,Services::class.java)
                    ints.putExtra("url",videoID)
                    ContextCompat.startForegroundService(context,ints)


                    runOnUiThread(Runnable {

                        downloadToast(activity,"video",context)
                        bawo = false

                    })

                    dialog!!.dismiss()


                }


            })
            .show()

    return bawo!!
}








fun downloadToast(activity: Activity,mime:String,context: Context){

    var type = ""
    if (mime.contains("video")){
        type =  context.getString(R.string.downloadVideo)

    }else{

        type =  context.getString(R.string.downloadPhoto)

    }


    CookieBar.build(activity)
            .setTitle(type)
            .setMessage(R.string.downloadStart)
            .setIcon(R.drawable.ic_save)
            .setTitleColor(android.R.color.white)
            .setMessageColor(android.R.color.white)
            .setBackgroundColor(R.color.colorGreen)
            .setDuration(4000)
            .setLayoutGravity(Gravity.TOP)
            .show()
}




// Wallpaper Setting  TOAST

fun wallpaperToast(activity: Activity,context: Context){


    CookieBar.build(activity)
            .setTitle(R.string.fab_set)
            .setMessage(R.string.wait)
            .setIcon(R.drawable.ic_settings)
            .setIconAnimation(R.animator.iconspin)
            .setTitleColor(android.R.color.white)
            .setMessageColor(android.R.color.white)
            .setBackgroundColor(R.color.colorInsta)
            .setDuration(34000)
            .setLayoutGravity(Gravity.TOP)
            .show()
}




// Wallpaper Done TOAST

fun wallpaperDoneToast(activity: Activity,context: Context){


    CookieBar.build(activity)
            .setTitle(R.string.fab_set)
            .setMessage(R.string.done)
            .setIcon(R.drawable.ic_done_white)
            .setTitleColor(android.R.color.white)
            .setMessageColor(android.R.color.white)
            .setBackgroundColor(R.color.colorGreen)
            .setDuration(5000)
            .setLayoutGravity(Gravity.TOP)
            .show()
}




// Wallpaper Done TOAST

fun imageDoneToast(activity: Activity,context: Context){


    CookieBar.build(activity)
            .setTitle(R.string.savePhoto)
            .setMessage(R.string.complete)
            .setIcon(R.drawable.ic_done_white)
            .setTitleColor(android.R.color.white)
            .setMessageColor(android.R.color.white)
            .setBackgroundColor(R.color.colorGreen)
            .setDuration(5000)
            .setLayoutGravity(Gravity.TOP)
            .show()
}




fun Saveit(link:String):String{

    var pink = ""
    val saveclient = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS).build()
    val saverequest = Request.Builder()
            .url(link)
            .build()
    val response = saveclient.newCall(saverequest).execute()


    val json = JSONObject(response.body()!!.string())
    pink = json.getString("source")


    return pink
}





//Save Video Dialog

fun InstagramVideoDialog(context: Context,playUrl:String,activity: Activity):Boolean{

    var bawo :Boolean? = null

    var mDialog = FeedBackDialog(context)
            .setBackgroundColor(R.color.colorDialogBack)
            .setIcon(R.drawable.ic_file_download)
            .setIconColor(android.R.color.primary_text_light)
            .setTitle(R.string.saveVideo)
            .setPositiveFeedbackIcon(R.drawable.ic_file_download)
            .setNegativeFeedbackIcon(R.drawable.ic_play)
            .setAmbiguityFeedbackIcon(R.drawable.ic_clear)
            .setDescription(R.string.dialogVideoMessage)
            .setReviewQuestion(R.string.dialogJambVideo)
            .setPositiveFeedbackText(R.string.dialogYes)
            .setNegativeFeedbackText(R.string.dialogNoVideo)
            .setAmbiguityFeedbackText(R.string.dialogCancel)
            .setOnReviewClickListener(object : FeedBackActionsListeners{
                override fun onNegativeFeedback(dialog: FeedBackDialog?) {

                    dialog!!.dismiss()
                    bawo = false
                    val intes = Intent(context, VideoPlayer::class.java )
                    intes.putExtra("url",playUrl)
                    intes.putExtra("source","instagram")
                    context.startActivity(intes)

                }

                override fun onCancelListener(dialog: DialogInterface?) {
                    bawo = false
                    dialog!!.dismiss()

                }

                override fun onAmbiguityFeedback(dialog: FeedBackDialog?) {



                    bawo = false
                    dialog!!.dismiss()


                }

                override fun onPositiveFeedback(dialog: FeedBackDialog?) {



                    runOnUiThread(Runnable {

                        downloadToast(activity,"video",context)
                        (context as MainActivity).mrSave(playUrl,"video")
                        bawo = false


                    })

                    dialog!!.dismiss()


                }


            })
            .show()


    return bawo!!
}











//Save Video Dialog

fun photoDialog(context: Context,photoUrl: String,activity: Activity):Boolean{
    var bawo :Boolean? = null

    var mDialog = FeedBackDialog(context)
            .setBackgroundColor(R.color.colorDialogBack)
            .setIcon(R.drawable.ic_file_download)
            .setIconColor(android.R.color.primary_text_light)
            .setTitle(R.string.savePhoto)
            .setPositiveFeedbackIcon(R.drawable.ic_file_download)
            .setNegativeFeedbackIcon(R.drawable.ic_view)
            .setAmbiguityFeedbackIcon(R.drawable.ic_clear)
            .setDescription(R.string.dialogPhotoMessage)
            .setReviewQuestion(R.string.dialogJambPhoto)
            .setPositiveFeedbackText(R.string.dialogYes)
            .setNegativeFeedbackText(R.string.dialogNoPhoto)
            .setAmbiguityFeedbackText(R.string.dialogCancel)
            .setOnReviewClickListener(object : FeedBackActionsListeners{
                override fun onNegativeFeedback(dialog: FeedBackDialog?) {
                    dialog!!.dismiss()
                    bawo = false
                    val intes = Intent(context, PhotoPlayer::class.java )
                    intes.putExtra("url",photoUrl)
                    context.startActivity(intes)

                }

                override fun onCancelListener(dialog: DialogInterface?) {

                    bawo = false



                }

                override fun onAmbiguityFeedback(dialog: FeedBackDialog?) {

                    dialog!!.dismiss()
                    bawo = false

                }

                override fun onPositiveFeedback(dialog: FeedBackDialog?) {

                    runOnUiThread(Runnable {
                        downloadToast(activity,"photo",context)
                        (context as MainActivity).mrSave(photoUrl,"photo")


                    })

                    bawo = false
                    dialog!!.dismiss()

                }


            })
            .show()

    return bawo!!
}






fun extracTors(text: String): Array<String> {
    val links = ArrayList<String>()
    val m = Patterns.WEB_URL.matcher(text)
    while (m.find()) {
        val urls = m.group()
        links.add(urls)
    }

    return links.toTypedArray()
}













fun getScaledBounds(imageSize: Bounds, boundary: Bounds): Bounds
{
    val widthRatio = boundary.m_width / imageSize.m_width
    val heightRatio = boundary.m_height / imageSize.m_height
    val ratio = Math.min(widthRatio, heightRatio)
    return Bounds((imageSize.m_width * ratio).toInt().toDouble(), (imageSize.m_height * ratio).toInt().toDouble())
}









