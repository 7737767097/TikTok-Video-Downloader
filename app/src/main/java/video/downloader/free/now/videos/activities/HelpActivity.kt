package video.downloader.free.now.videos.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import video.downloader.free.now.videos.AppDelegate
import video.downloader.free.now.videos.R

class HelpActivity : AppCompatActivity() {

    val TAG = "Help"
    lateinit var mAdView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())
        setContentView(R.layout.activity_help)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        initAddView()
    }

    private fun initAddView() {
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()

        mAdView.loadAd(adRequest)
        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.e("AdLoadeed", "AdLoaded")
                AppDelegate.logFirebaseEvent(
                    "adsLoaded_" + TAG,
                    null
                )
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
                Log.e("AdLoadeed", "onAdFailedToLoad - " + errorCode)
                AppDelegate.logFirebaseEvent(
                    "adsFailed_" + TAG,
                    null
                )
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.e("AdLoadeed", "onAdOpened - ")
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.e("AdLoadeed", "onAdClicked ")
                AppDelegate.logFirebaseEvent(
                    "adsClicked_" + TAG,
                    null
                )
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.e("AdLoadeed", "onAdLeftApplication ")
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.e("AdLoadeed", "onAdClosed ")
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}