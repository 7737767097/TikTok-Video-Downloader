package video.downloader.free.now.videos.tasks

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import video.downloader.free.now.videos.utils.Constants.DOWNLOAD_DIRECTORY
import video.downloader.free.now.videos.utils.Constants.PREF_APPNAME
import video.downloader.free.now.videos.utils.iUtils
import java.io.File

object DownloadFileTask {
    var downloadManager: DownloadManager? = null
    var downloadID: Long = 0
    private var mBaseFolderPath: String? = null

    fun Downloading(
        context: Context,
        url: String?,
        title: String,
        ext: String
    ) {
        var cutTitle = title
        val characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]"
        cutTitle = cutTitle.replace(characterFilter.toRegex(), "")
        cutTitle = cutTitle.replace("['+.^:,#\"]".toRegex(), "")
        cutTitle = cutTitle.replace(" ", "-").replace("!", "").replace(":", "") + ext
        if (cutTitle.length > 100) cutTitle = cutTitle.substring(0, 100) + ext
        downloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(url))
        request.setTitle(title)
        request.setDescription("Downloading...")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        val folderName: String = DOWNLOAD_DIRECTORY
        val preferences =
            context.getSharedPreferences(PREF_APPNAME, Context.MODE_PRIVATE)
        mBaseFolderPath = if (preferences.getString("path", "DEFAULT") != "DEFAULT") {
            preferences.getString("path", "DEFAULT")
        } else {
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + folderName
        }
        val bits =
            mBaseFolderPath!!.split("/").toTypedArray()
        val Dir = bits[bits.size - 1]
        //  request.setDestinationUri(new File(mBaseFolderPath).);
        request.setDestinationInExternalPublicDir(Dir, cutTitle)
        request.allowScanningByMediaScanner()
        downloadID = downloadManager!!.enqueue(request)
        Log.e("downloadFileName", cutTitle)
        iUtils.ShowToast(context, "Downloading Start!")
    }
}