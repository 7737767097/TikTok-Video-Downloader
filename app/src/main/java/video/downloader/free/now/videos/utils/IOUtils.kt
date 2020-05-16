package video.downloader.free.now.videos.utils

import android.os.Build

object IOUtils {
    private val TAG = IOUtils::class.java.simpleName
    fun hasGingerbread(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
    }

    fun hasLolipop(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }

    fun hasMarsallow(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }
}