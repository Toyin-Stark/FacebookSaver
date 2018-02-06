package com.tomi.savefacebook.Utility

import android.annotation.TargetApi
import android.net.Uri
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

internal class KustomClient : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        val uri = Uri.parse(url)
        return handleUri(uri)
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        val uri = request.getUrl()
        return handleUri(uri)
    }

    private fun handleUri(uri: Uri): Boolean {
        val host = uri.getHost()
        val scheme = uri.getScheme()

     return false
    }
}