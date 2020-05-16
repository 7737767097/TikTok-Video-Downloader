package video.downloader.free.now.videos.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import video.downloader.free.now.videos.services.ClipboardMonitor

class Receiver : BroadcastReceiver() {
    //static FloatingViewService service;
    lateinit var editor: SharedPreferences.Editor
    lateinit var prefs: SharedPreferences
    override fun onReceive(context: Context, intent: Intent) {
        val whichAction = intent.action
        prefs = context.getSharedPreferences("tikVideoDownloader", Context.MODE_PRIVATE)
        editor = prefs.edit()
        when (whichAction) {
            "quit_action" -> {
                Log.e("loged", "quite")
                editor.putBoolean("csRunning", false)
                editor.commit()
                context.stopService(
                    Intent(
                        context,
                        ClipboardMonitor::class.java
                    )
                )
                return
            }
        }
    }
}