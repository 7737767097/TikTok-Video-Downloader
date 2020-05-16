package video.downloader.free.now.videos

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.multidex.MultiDex
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import video.downloader.free.now.videos.utils.Tags
import video.downloader.free.now.videos.utils.Utility

class AppDelegate : Application() {

    lateinit var mFirebaseAnalytics: FirebaseAnalytics
    lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig
    var minimumFetchTime: Long = 3600

    override fun attachBaseContext(base: Context?) {
        MultiDex.install(this)
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        MultiDex.install(this)
        super.onCreate()
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mFirebaseAnalytics?.setUserProperty(
            "android_version",
            BuildConfig.VERSION_NAME
        )

        try {
            if (!isValidString(
                    DEVICE_IMEI
                )
            )
                DEVICE_IMEI = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: Exception) {
            Utility.LogE(e)
            DEVICE_IMEI = System.currentTimeMillis().toString() + ""
        }
        mFirebaseAnalytics?.setUserProperty(
            "device_imei",
            DEVICE_IMEI
        )
        MobileAds.initialize(this)
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        if (BuildConfig.DEBUG) {
            minimumFetchTime = 0
        }
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setDeveloperModeEnabled(BuildConfig.DEBUG)
            .setMinimumFetchIntervalInSeconds(minimumFetchTime)
            .build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (connectivityManager != null)
            connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected
        else
            false
    }


    fun traceCustomFP(traceName: String): Trace {
        val trace = FirebasePerformance.getInstance().newTrace(traceName)
        trace.start()
        return trace
    }


    fun logFirebaseEvent(
        videoUploadingTime: String,
        videoSize: String,
        videoDuration: String,
        reTriggeredCount: String,
        uploadSpeed: String
    ) {
        if (mFirebaseAnalytics != null) {
            val bundle = Bundle()
            bundle.putString(Tags.videoUploadingTime, videoUploadingTime)
            bundle.putString(Tags.videoSize, videoSize)
            bundle.putString(Tags.videoDuration, videoDuration)
            bundle.putString(Tags.reTriggeredCount, reTriggeredCount)
            bundle.putString(Tags.uploadSpeed, uploadSpeed)
            //            bundle.putString(Tags.INSTANCE.getDownloadSpeed(), downloadSpeed);
            Utility.Log("logEvent====> ", "" + bundle)

            mFirebaseAnalytics!!.logEvent(Tags.Android, bundle)
        }
    }

    companion object {
        var DEVICE_IMEI: String? = ""
        var mFirebaseAnalytics: FirebaseAnalytics? = null
        fun logFirebaseEvent(eventName: String?, bundle: Bundle?) {
            if (eventName != null) {
                Log.d("test", "logFirebaseEvent -> " + bundle.toString())
                mFirebaseAnalytics?.logEvent(eventName, bundle)
            }
        }

        fun isValidString(string: String?): Boolean {
            return string != null && !string.equals(
                "null",
                ignoreCase = true
            ) && string.isNotEmpty()
        }

        @JvmOverloads
        fun haveNetworkConnection(mContext: Context?, showAlert: Boolean = true): Boolean {
            var isConnected = false
            var haveConnectedWifi = false
            var haveConnectedMobile = false

            if (mContext == null) {
                return false
            } else {
                val cm = mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val netInfo = cm.allNetworkInfo
                for (ni in netInfo) {
                    if (ni.typeName.equals("WIFI", ignoreCase = true))
                        if (ni.isConnected)
                            haveConnectedWifi = true
                    if (ni.typeName.equals("MOBILE", ignoreCase = true))
                        if (ni.isConnected)
                            haveConnectedMobile = true
                }
                isConnected = haveConnectedWifi || haveConnectedMobile
                if (isConnected) {
                    return isConnected
                } else {
                    if (showAlert) {
                        showToast(
                            mContext,
                            mContext.resources.getString(R.string.active_internet_connection)
                        )
                    }
                }
                return isConnected
            }
        }

        fun showToast(mContext: Context?, Message: String) {
            try {
                if (mContext != null) {
                    val toast = Toast.makeText(mContext, Message, Toast.LENGTH_SHORT)
                    //                toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show()
                } else
                    Log.e("tag", "context is null at showing toast.")
            } catch (e: Exception) {
                Log.e("tag", "context is null at showing toast.", e)
            }
        }

        fun showToast(mContext: Context?, Message: String, type: Int) {
            try {
                if (mContext != null)
                    if (type == 0) {
                        val toast = Toast.makeText(mContext, Message, Toast.LENGTH_SHORT)
                        //                    toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show()
                    } else {
                        val toast = Toast.makeText(mContext, Message, Toast.LENGTH_LONG)
                        //                    toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show()
                    }
                else
                    Log.e("tag", "context is null at showing toast.")
            } catch (e: Exception) {
                Log.e("tag", "context is null at showing toast.", e)
            }

        }
    }


}