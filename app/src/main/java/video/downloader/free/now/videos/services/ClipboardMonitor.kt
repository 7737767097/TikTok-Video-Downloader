package video.downloader.free.now.videos.services

import android.app.*
import android.content.ClipboardManager
import android.content.ClipboardManager.OnPrimaryClipChangedListener
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import android.util.Log
import video.downloader.free.now.videos.activities.MainActivity
import video.downloader.free.now.videos.R
import video.downloader.free.now.videos.tasks.DownloadVideoTask
import video.downloader.free.now.videos.utils.Constants.STARTFOREGROUND_ACTION
import video.downloader.free.now.videos.utils.Constants.STOPFOREGROUND_ACTION

class ClipboardMonitor : Service() {
    private var mCM: ClipboardManager? = null
    var mBinder: IBinder? = null
    var editor: SharedPreferences.Editor? = null
    var prefs: SharedPreferences? = null
    var mStartMode = 0
    override fun onCreate() {
        super.onCreate()
        mCM =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        mCM!!.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        try {
            val action = intent.action
            when (action) {
                STARTFOREGROUND_ACTION -> startInForeground()
                STOPFOREGROUND_ACTION -> stopForegroundService()
            }
        } catch (e: Exception) {
            println("Null pointer exception")
        }
        return START_STICKY
    }

    private fun stopForegroundService() {
        Log.d("Foreground", "Stop foreground service.")
        prefs = getSharedPreferences("tikVideoDownloader", Context.MODE_PRIVATE)
        editor = prefs!!.edit()
        editor!!.putBoolean("csRunning", false)
        editor!!.commit()

        // Stop foreground service and remove the notification.
        stopForeground(true)

        // Stop the foreground service.
        stopSelf()
        mCM!!.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener)
    }

    private fun startInForeground() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val builder =
            NotificationCompat.Builder(
                this,
                packageName + "-" + getString(R.string.app_name)
            )
                .setSmallIcon(R.drawable.notification_template_icon_bg)
                .setContentTitle("Auto Download Service")
                .setContentText("Copy the link of video to start download")
                .setTicker("TICKER")
                .addAction(
                    R.drawable.navigation_empty_icon,
                    "Stop",
                    makePendingIntent(STOPFOREGROUND_ACTION)
                )
                .setContentIntent(pendingIntent)
        val notification = builder.build()
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                packageName + "--" + getString(R.string.app_name),
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Tiktok Auto Download"
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        prefs = getSharedPreferences("tikVideoDownloader", Context.MODE_PRIVATE)
        editor = prefs!!.edit()
        //stopSelf();
        startForeground(1002, notification)
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO Auto-generated method stub
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        val restartServicePendingIntent = PendingIntent.getService(
            applicationContext,
            1,
            restartServiceIntent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val alarmService =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmService[AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000] =
            restartServicePendingIntent
        super.onTaskRemoved(rootIntent)
        // this.stopSelf();
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("destroyed", "123123")
        stopForeground(true)
        stopSelf()
        if (mCM != null) {
            mCM!!.removePrimaryClipChangedListener(
                mOnPrimaryClipChangedListener
            )
        }
        prefs = getSharedPreferences("tikVideoDownloader", Context.MODE_PRIVATE)

//        if(prefs.getBoolean("csRunning",false)) {
//                Intent broadcastIntent = new Intent();
//                broadcastIntent.setAction("restartservice");
//                broadcastIntent.setClass(this, Restarter.class);
//                this.sendBroadcast(broadcastIntent);
//            }
    }

    fun makePendingIntent(name: String?): PendingIntent {
        val intent = Intent(applicationContext, ClipboardMonitor::class.java)
        intent.action = name
        return PendingIntent.getService(applicationContext, 0, intent, 0)
    }

    private val mOnPrimaryClipChangedListener =
        OnPrimaryClipChangedListener {
            val newClip = mCM!!.primaryClip.getItemAt(0).text.toString()
            //   Toast.makeText(getApplicationContext(), newClip, Toast.LENGTH_LONG).show();
            Log.i("LOGClipboard", newClip + "")
            DownloadVideoTask.Start(applicationContext, newClip, true)
        }
}