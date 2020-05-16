package video.downloader.free.now.videos.tasks

import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.util.Log
import video.downloader.free.now.videos.AppDelegate
import video.downloader.free.now.videos.utils.Constants.DOWNLOADING_MSG
import video.downloader.free.now.videos.utils.Constants.TiktokApi
import video.downloader.free.now.videos.utils.Constants.WEB_DISABLE
import video.downloader.free.now.videos.utils.Constants.WENT_WRONG
import video.downloader.free.now.videos.utils.iUtils
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.util.*

object DownloadVideoTask {
    var Mcontext: Context? = null
    var pd: ProgressDialog? = null
    var dialog: Dialog? = null
    var SessionID: String? = null
    var Title: String? = null
    var error = 1
    var prefs: SharedPreferences? = null
    var fromService: Boolean? = null
    fun Start(
        context: Context?,
        url: String,
        service: Boolean?
    ) {
        var url = url
        Mcontext = context
        fromService = service

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://$url"
        }
        if (!fromService!!) {
            pd = ProgressDialog(context)
            pd!!.setMessage(DOWNLOADING_MSG)
            pd!!.setCancelable(false)
            pd!!.show()
        }
        if (url.contains("tiktok.com")) {
            GetTikTokVideo()
                .execute(url)
        } else if (url.contains("facebook.com")) {
            GetFacebookVideo()
                .execute(url)
        } else if (url.contains("instagram.com")) {
            GetInstagramVideo()
                .execute(url)
        } else {
            if (!fromService!!) {
                pd!!.dismiss()
                iUtils.ShowToast(
                    Mcontext, WEB_DISABLE
                )
            }
        }
        prefs = Mcontext!!.getSharedPreferences(
            "AppConfig",
            Context.MODE_PRIVATE
        )
    }

    private class GetTikTokVideo :
        AsyncTask<String?, Void?, Document?>() {
        var doc: Document? = null

        override fun doInBackground(vararg urls: String?): Document? {
            try {
                doc = Jsoup.connect(urls[0]).get()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.d(ContentValues.TAG, "doInBackground: Error")
            }
            return doc
        }

        override fun onPostExecute(result: Document?) {
            try {
                var URL =
                    result!!.select("link[rel=\"canonical\"]").last().attr("href")
                if (URL != "" && URL.contains("video/")) {
                    URL = URL.split("video/").toTypedArray()[1]
                    Title = result.title()
                    DownloadTikTokVideo()
                        .execute(URL)
                } else {
                    if (!fromService!!) {
                        pd!!.dismiss()
                    }
                    iUtils.ShowToast(
                        Mcontext, WENT_WRONG
                    )
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
                if (!fromService!!) {
                    pd!!.dismiss()
                }
                iUtils.ShowToast(
                    Mcontext, WENT_WRONG
                )
            }
        }
    }

    private class GetFacebookVideo :
        AsyncTask<String?, Void?, Document?>() {
        var doc: Document? = null
        protected override fun doInBackground(vararg urls: String?): Document? {
            try {

//doc = Jsoup.connect(FacebookApi).data("v",urls[0]).get();
                doc = Jsoup.connect(urls[0]).get()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.d(ContentValues.TAG, "doInBackground: Error")
                iUtils.ShowToast(
                    Mcontext, WENT_WRONG
                )
            }
            return doc
        }

        override fun onPostExecute(result: Document?) {
            if (!fromService!!) {
                pd!!.dismiss()
            }
            // Log.d("GetResult", );
            val URL =
                result!!.select("meta[property=\"og:video\"]").last().attr("content")
            try {
                Title = result.title()
                // iUtils.ShowToast(Mcontext,URL);
                DownloadFileTask.Downloading(
                    Mcontext!!,
                    URL,
                    Title!!,
                    ".mp4"
                )
                AppDelegate.logFirebaseEvent("downloadFbStart", null)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                DownloadFileTask.Downloading(
                    Mcontext!!,
                    URL,
                    "Facebook-" + System.currentTimeMillis().toString()!!,
                    ".mp4"
                )
                AppDelegate.logFirebaseEvent("downloadFbStart", null)
            } catch (e: Exception) {
                e.printStackTrace()
                iUtils.ShowToast(
                    Mcontext, WENT_WRONG
                )
            }
            // new DownloadTikTokVideo().execute(URL);
        }
    }

    private class GetInstagramVideo :
        AsyncTask<String?, Void?, Document?>() {
        var doc: Document? = null
        protected override fun doInBackground(vararg urls: String?): Document? {
            try {
                doc = Jsoup.connect(urls[0]).get()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.d(ContentValues.TAG, "doInBackground: Error")
            }
            return doc
        }

        override fun onPostExecute(result: Document?) {
            if (!fromService!!) {
                pd!!.dismiss()
            }
            // Log.d("GetResult", );
            var URL = ""
            try {
                URL =
                    result!!.select("meta[property=\"og:video\"]").last().attr("content")
                Title = result.title()
                //iUtils.ShowToast(Mcontext, URL);
                DownloadFileTask.Downloading(
                    Mcontext!!,
                    URL,
                    Title!!,
                    ".mp4"
                )
                AppDelegate.logFirebaseEvent("downloadInstaStart", null)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                DownloadFileTask.Downloading(
                    Mcontext!!,
                    URL,
                    "Facebook-" + System.currentTimeMillis().toString()!!,
                    ".mp4"
                )
                AppDelegate.logFirebaseEvent("downloadFbStart", null)
            } catch (e: Exception) {
                e.printStackTrace()
                iUtils.ShowToast(
                    Mcontext, WENT_WRONG
                )
            }
        }
    }

    private class DownloadTikTokVideo :
        AsyncTask<String?, Void?, Document?>() {
        var doc: Document? = null
        protected override fun doInBackground(vararg urls: String?): Document? {
            try {
                val Headers: MutableMap<String, String> =
                    HashMap()
                Headers["Cookie"] = "1"
                Headers["User-Agent"] = "1"
                Headers["Accept"] = "application/json"
                Headers["Host"] = "api2-16-h2.musical.ly"
                Headers["Connection"] = "keep-alive"
                doc = Jsoup.connect(TiktokApi).data("aweme_id", urls[0]).ignoreContentType(true)
                    .headers(Headers).get()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.d(ContentValues.TAG, "doInBackground: Error")
                iUtils.ShowToast(
                    Mcontext, WENT_WRONG
                )
            }
            return doc
        }

        override fun onPostExecute(result: Document?) {
            if (!fromService!!) {
                pd!!.dismiss()
            }
            val URL =
                result!!.body().toString().replace("<body>", "").replace("</body>", "")
            var jsonObject: JSONObject? = null
            try {
                jsonObject = JSONObject(URL)
                val URLs =
                    jsonObject.getJSONObject("aweme_detail").getJSONObject("video")
                        .getJSONObject("play_addr").getJSONArray("url_list").getString(0)
                DownloadFileTask.Downloading(
                    Mcontext!!,
                    URLs,
                    Title!!,
                    ".mp4"
                )
                AppDelegate.logFirebaseEvent("downloadTikTokStart", null)
                // iUtils.ShowToast(Mcontext,URLs);
            } catch (err: JSONException) {
                Log.d("Error", err.toString())
                iUtils.ShowToast(
                    Mcontext, WENT_WRONG
                )
            }
        }
    }
}