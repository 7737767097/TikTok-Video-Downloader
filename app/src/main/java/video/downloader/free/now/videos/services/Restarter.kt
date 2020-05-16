package video.downloader.free.now.videos.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import video.downloader.free.now.videos.utils.Constants.STARTFOREGROUND_ACTION

class Restarter : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i("Broadcast Listened", "Service tried to stop")
        Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(
                Intent(context, ClipboardMonitor::class.java).setAction(
                    STARTFOREGROUND_ACTION
                )
            )
        } else {
            context.startService(Intent(context, ClipboardMonitor::class.java))
        }
    }
}