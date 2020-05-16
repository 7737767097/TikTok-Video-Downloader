package video.downloader.free.now.videos.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import video.downloader.free.now.videos.AppDelegate
import video.downloader.free.now.videos.activities.MainActivity
import video.downloader.free.now.videos.R
import video.downloader.free.now.videos.receiver.Receiver
import video.downloader.free.now.videos.services.ClipboardMonitor
import video.downloader.free.now.videos.tasks.DownloadVideoTask
import video.downloader.free.now.videos.utils.Constants.STARTFOREGROUND_ACTION
import video.downloader.free.now.videos.utils.Constants.STOPFOREGROUND_ACTION
import video.downloader.free.now.videos.utils.iUtils
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import kotlinx.android.synthetic.main.fragment_download.*
import kotlinx.android.synthetic.main.fragment_download.view.*
import kotlinx.android.synthetic.main.layout_auto_video_download.view.*

class DownloadFragment : androidx.fragment.app.Fragment() {

    private lateinit var mInterstitialAd: InterstitialAd
    private var NotifyID = 1001;

    private var csRunning = false;

    lateinit var prefEditor: SharedPreferences.Editor;
    lateinit var pref: SharedPreferences;
    lateinit var mAdView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater!!.inflate(R.layout.fragment_download, container, false)
        mInterstitialAd = InterstitialAd(context!!)
        mInterstitialAd.adUnitId = getString(R.string.adsInsertialId_live)
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.e("AdLoadeed", "AdLoaded")
                AppDelegate.logFirebaseEvent(
                    "insetLoaded_download",
                    null
                )
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
                Log.e("AdLoadeed", "onAdFailedToLoad - " + errorCode)
                AppDelegate.logFirebaseEvent(
                    "insetFailed_download",
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
                    "insetClicked_download",
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


        pref = context!!.getSharedPreferences("tikVideoDownloader", 0); // 0 - for private mode
        prefEditor = pref.edit();
        csRunning = pref.getBoolean("csRunning", false)

        createNotificationChannel(
            requireActivity(),
            NotificationManagerCompat.IMPORTANCE_LOW,
            true,
            getString(R.string.app_name),
            "Tiktok Auto Download"
        );

        view.btnDownload.setOnClickListener { view ->
            //Log.d("clicked","true")
            // view.btnDownload.visibility=View.GONE
            //  pbFetchingVideo.visibility=View.VISIBLE
            if (!(activity as MainActivity).isNeedGrantPermission()) {
                val url = etURL.text.toString();
                if (url.length > 0) {
                    if (URLUtil.isValidUrl(url) && Patterns.WEB_URL.matcher(url).matches()) {
                        DownloadVideo(url);
                    } else {
                        AppDelegate.showToast(activity, "Please enter valid video url.")
                    }
                } else {
                    AppDelegate.showToast(activity, "Please enter valid video url.")
                }
            }
        }


//        view.llFacebook.setOnClickListener { View ->
//            Utility.openThisApp(
//                activity!!,
//                Constants.FACEBOOK_PACKAGE_NAME,
//                Constants.FACEBOOK_APP_NAME
//            )
//        }
//
//        view.llTikTok.setOnClickListener { view ->
//            Utility.openThisApp(
//                activity!!,
//                Constants.TIKTOK_PACKAGE_NAME,
//                Constants.TIKTOK_APP_NAME
//            )
//        }
//        view.llInstagram.setOnClickListener { view ->
//            Utility.openThisApp(
//                activity!!,
//                Constants.INSTAGRAM_PACKAGE_NAME,
//                Constants.INSTAGRAM_APP_NAME
//            )
//        }

        view.ivLink.setOnClickListener(fun(view: View) {
            val clipBoardManager =
                context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            val primaryClipData = clipBoardManager.primaryClip
            val clip = primaryClipData.getItemAt(0).text.toString()

            etURL.text = Editable.Factory.getInstance().newEditable(clip)
            DownloadVideo(clip);
        })
        if (csRunning) {
            view.chkAutoDownload.isChecked = true;
            startClipboardMonitor()
        } else {
            view.chkAutoDownload.isChecked = false;
            stopClipboardMonitor()
        }
        view.chkAutoDownload.setOnClickListener { view ->
            val checked = view.chkAutoDownload.isChecked;

            if (checked) {
                Log.e("loged", "testing checked!")
                startClipboardMonitor()
            } else {
                Log.e("loged", "testing unchecked!")
                stopClipboardMonitor()
                // setNofication(false);
            }

        }
        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdView = view.findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()

        mAdView.loadAd(adRequest)
        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.e("AdLoadeed", "AdLoaded")
                AppDelegate.logFirebaseEvent(
                    "adsLoaded_download",
                    null
                )
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
                Log.e("AdLoadeed", "onAdFailedToLoad - " + errorCode)
                AppDelegate.logFirebaseEvent(
                    "adsFailed_download",
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
                    "adsClicked_download",
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

    fun createNotificationChannel(
        context: Context,
        importance: Int,
        showBadge: Boolean,
        name: String,
        description: String
    ) {
        // 1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // 2
            val channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            // 3
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.e("loged", "Notificaion Channel Created!")
        }
    }

    private fun setNofication(b: Boolean) {

        if (b) {
            val channelId = "${context!!.packageName}-${context!!.getString(R.string.app_name)}"
            val notificationBuilder = NotificationCompat.Builder(context!!, channelId).apply {
                setSmallIcon(R.drawable.notification_template_icon_bg) // 3
                // setStyle(NotificationCompat.)
                setLargeIcon(
                    BitmapFactory.decodeResource(
                        context!!.getResources(),
                        R.drawable.notification_template_icon_bg
                    )
                )
                setContentTitle("Auto Download Service") // 4
                setContentText("Copy the link of video to start download") // 5
                setOngoing(true)
                priority = NotificationCompat.PRIORITY_LOW // 7
                setSound(null)
                setOnlyAlertOnce(true)
                setAutoCancel(false)
                addAction(
                    R.drawable.navigation_empty_icon,
                    "Stop",
                    makePendingIntent("quit_action")
                )

                val intent = Intent(requireActivity(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                val pendingIntent = PendingIntent.getActivity(requireActivity(), 0, intent, 0)

                setContentIntent(pendingIntent)
            }
            with(NotificationManagerCompat.from(requireActivity())) {
                // notificationId is a unique int for each notification that you must define
                notify(NotifyID, notificationBuilder.build())

                Log.e("loged", "testing notification notify!")


            }


        } else {
            NotificationManagerCompat.from(requireActivity()).cancel(NotifyID);
        }
    }

    fun startClipboardMonitor() {
        prefEditor.putBoolean("csRunning", true);
        prefEditor.commit()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val service = requireActivity()!!.startForegroundService(
                Intent(
                    requireContext(),
                    ClipboardMonitor::class.java
                ).setAction(STARTFOREGROUND_ACTION)
            );
        } else {
            val service = requireActivity()!!.startService(
                Intent(
                    requireContext(),
                    ClipboardMonitor::class.java
                )
            );
        }

    }

    fun stopClipboardMonitor() {
        prefEditor.putBoolean("csRunning", false);
        prefEditor.commit()

        val service = requireActivity()!!.stopService(
            Intent(
                requireContext(),
                ClipboardMonitor::class.java
            ).setAction(STOPFOREGROUND_ACTION)
        );


    }

    fun makePendingIntent(name: String): PendingIntent {
        val intent = Intent(requireActivity(), Receiver::class.java)
        intent.setAction(name)
        return PendingIntent.getBroadcast(requireActivity(), 0, intent, 0)
    }

    fun DownloadVideo(url: String) {

        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.")
        }


        if (url.equals("") && !!iUtils.checkURL(url)) {
            iUtils.ShowToast(context!!, "Please Enter a valid URI")
            val bundle = Bundle()
            bundle.putString("url", url)
            AppDelegate.logFirebaseEvent(
                "downloadInvalid",
                bundle
            )
        } else {
            DownloadVideoTask.Start(context!!, url!!, false);
            val bundle = Bundle()
            bundle.putString("url", url)
            AppDelegate.logFirebaseEvent(
                "download",
                bundle
            )
        }
    }
}
