package video.downloader.free.now.videos.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import kotlinx.android.synthetic.main.layout_supported_apps.*
import video.downloader.free.now.videos.R
import video.downloader.free.now.videos.utils.Constants
import video.downloader.free.now.videos.utils.Utility
import video.downloader.free.now.videos.AppDelegate

class AboutActivity : AppCompatActivity() {
    val TAG = "About"
    lateinit var mAdView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())
        setContentView(R.layout.activity_about_us)
        supportActionBar?.setTitle(getString(R.string.nav_about_us))
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        initView()
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

    private fun initView() {
        llFacebook.setOnClickListener { View ->
            Utility.openThisApp(
                this,
                Constants.FACEBOOK_PACKAGE_NAME,
                Constants.FACEBOOK_APP_NAME
            )
        }

        llTikTok.setOnClickListener { view ->
            Utility.openThisApp(
                this,
                Constants.TIKTOK_PACKAGE_NAME,
                Constants.TIKTOK_APP_NAME
            )
        }
        llInstagram.setOnClickListener { view ->
            Utility.openThisApp(
                this,
                Constants.INSTAGRAM_PACKAGE_NAME,
                Constants.INSTAGRAM_APP_NAME
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}