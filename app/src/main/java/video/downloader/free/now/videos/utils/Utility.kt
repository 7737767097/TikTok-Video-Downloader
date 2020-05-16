package video.downloader.free.now.videos.utils

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import video.downloader.free.now.videos.AppDelegate
import video.downloader.free.now.videos.R
import video.downloader.free.now.videos.utils.iUtils.isPackageInstalled

object Utility {

    /**
     * @param TAG
     * @param Message
     * @param LogType
     */
    fun Log(TAG: String, Message: String, LogType: Int) {
        when (LogType) {
            // Case 1- To Show Message as Debug
            1 -> android.util.Log.d("MyLog $TAG", Message)
            // Case 2- To Show Message as Error
            2 -> android.util.Log.e("MyLog $TAG", Message)
            // Case 3- To Show Message as Information
            3 -> android.util.Log.i("MyLog $TAG", Message)
            // Case 4- To Show Message as Verbose
            4 -> android.util.Log.v("MyLog $TAG", Message)
            // Case 5- To Show Message as Assert
            5 -> android.util.Log.w("MyLog $TAG", Message)
            // Case Default- To Show Message as System Print
            else -> println(Message)
        }
    }

    fun Log(TAG: String, Message: String) {
        Log(
            TAG,
            Message,
            1
        )
    }

    /* Function to show log for error message */
    fun LogD(Message: String) {
        Log(
            Tags.DATE,
            "" + Message,
            1
        )
    }

    /* Function to show log for error message */
    fun LogDB(Message: String) {
        Log(
            Tags.DATABASE,
            "" + Message,
            1
        )
    }

    /* Function to show log for error message */
    fun LogE(e: Exception?) {
        if (e != null) {
            try {
                val bundle = Bundle()
                bundle.putString("message", e.message)
                bundle.putString("lMessage", e.localizedMessage)
//                    bundle.putString("stackTrace", e.stackTrace.toString())
                bundle.putString("cause", e.cause.toString())
                AppDelegate.logFirebaseEvent(
                    Tags.TRY_CATCH, bundle
                )

                FirebaseCrashlytics.getInstance().recordException(e)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            LogE(e.message)
            e.printStackTrace()
        } else {
            Log(
                Tags.ERROR,
                "exception object is also null.",
                2
            )
        }
    }

    /* Function to show log for error message */
    fun LogE(e: OutOfMemoryError?) {
        if (e != null) {
            LogE(e.message)
            e.printStackTrace()
        } else {
            Log(
                Tags.ERROR,
                "exception object is also null.",
                2
            )
        }
    }

    /* Function to show log for error message */
    fun LogE(message: String, exception: Exception?) {
        if (exception != null) {
            LogE(
                "from = " + message + " => "
                        + exception.message
            )
            exception.printStackTrace()
        } else {
            Log(
                Tags.ERROR,
                "exception object is also null. at $message",
                2
            )
        }
    }

    /**
     * Funtion to log with tag RESULT, you need to just pass the message.
     *
     * @String Message = pass your message that you want to log.
     */
    fun LogR(Message: String) {
        Log(
            Tags.RESULT,
            "" + Message,
            1
        )
    }

    /**
     * Funtion to log with tag RESULT, you need to just pass the message.
     *
     * @String Message = pass your message that you want to log.
     */
    fun LogI(Message: String) {
        Log(
            Tags.INTERNET,
            "" + Message,
            1
        )
    }

    /**
     * Funtion to log with tag ERROR, you need to just pass the message. This
     * method is used to exeception .
     *
     * @String Message = pass your message that you want to log.
     */
    fun LogE(Message: String?) {
        Log(
            Tags.ERROR,
            "" + Message,
            2
        )
    }

    /**
     * Funtion to log with tag your name, you need to just pass the message.
     * This method is used to log url of your api calling.
     *
     * @String Message = pass your message that you want to log.
     */
    fun LogB(Message: String) {
        Log(
            Tags.BHARAT,
            "" + Message,
            1
        )
    }

    /**
     * Funtion to log with tag URL, you need to just pass the message. This
     * method is used to log url of your api calling.
     *
     * @String Message = pass your message that you want to log.
     */
    fun LogU(Message: String) {
        Log(
            Tags.URL,
            "" + Message,
            1
        )
    }

    /**
     * Funtion to log with tag URL_API, you need to just pass the message. This
     * method is used to log url of your api calling.
     *
     * @String Message = pass your message that you want to log.
     */
    fun LogUA(Message: String) {
        Log(
            Tags.URL_API,
            "" + Message,
            1
        )
    }

    /**
     * Funtion to log with tag URL_POST, you need to just pass the message. This
     * method is used to log post param of your api calling.
     *
     * @String Message = pass your message that you want to log.
     */
    fun LogUP(Message: String) {
        Log(
            Tags.URL_POST,
            "" + Message,
            1
        )
    }

    /**
     * Funtion to log with tag URL_RESPONSE, you need to just pass the message.
     * This method is used to log response of your api calling.
     *
     * @String Message = pass your message that you want to log.
     */
    fun LogUR(Message: String) {
        Log(
            Tags.URL_RESPONSE,
            "URL_RESPONSE $Message",
            1
        )
    }

    /**
     * Funtion to log with tag TEST, you need to just pass the message.
     *
     * @String Message = pass your message that you want to log.
     */
    fun LogT(Message: String) {
        Log(
            Tags.TEST,
            "" + Message,
            1
        )
    }

    /**
     * Funtion to log with tag TEST, you need to just pass the message.
     *
     * @String Message = pass your message that you want to log.
     */
    fun LogPN(Message: String) {
        Log(
            Tags.PUBNUB,
            "" + Message,
            1
        )
    }

    /**
     * Funtion to log with tag TEST, you need to just pass the message.
     *
     * @String Message = pass your message that you want to log.
     */
    fun LogCh(Message: String) {
        Log(
            "check",
            "" + Message,
            1
        )
    }

    fun LogTR(Message: String) {
        Log(
            Tags.TRACKER,
            "" + Message,
            1
        )
    }

    /**
     * Funtion to log with tag TEST, you need to just pass the message.
     *
     * @String Message = pass your message that you want to log.
     */
    fun LogF(Message: String) {
        Log(
            Tags.FORMATED,
            "" + Message,
            1
        )
    }

    /**
     * Funtion to log with tag TEST, you need to just pass the message.
     *
     * @String Message = pass your message that you want to log.
     */
    fun LogFB(Message: String) {
        Log(
            Tags.FACEBOOK,
            "" + Message,
            1
        )
    }

    /**
     * Funtion to log with tag TEST, you need to just pass the message.
     *
     * @String Message = pass your message that you want to log.
     */
    fun LogS(Message: String) {
        Log(
            Tags.SERVICE,
            "" + Message,
            1
        )
    }

    /**
     * Funtion to log with tag TEST, you need to just pass the message.
     *
     * @Message = pass your message that you want to log.
     * @int type = you need to pass int value to print in different color. 0 =
     * default color; 1 = fro print in exception style in red color; 2 =
     * info style in orange color;
     */
    fun LogT(Message: String, type: Int) {
        Log(
            Tags.TEST,
            "" + Message,
            type
        )
    }

    /**
     * Funtion to log with tag PREF, you need to just pass the message.
     *
     * @String Message = pass your message that you want to log.
     */
    fun LogP(Message: String) {
        Log(
            Tags.PREF,
            "" + Message,
            1
        )
    }

    /**
     * Funtion to log with tag PREF, you need to just pass the message.
     *
     * @String Message = pass your message that you want to log.
     */
    fun LogPU(Message: String) {
        Log(
            Tags.PUSHER,
            "" + Message,
            1
        )
    }

    /**
     * Funtion to log with tag GCM, you need to just pass the message.
     *
     * @String Message = pass your message that you want to log.
     */
    fun LogGC(Message: String) {
        Log(
            Tags.GCM,
            "" + Message,
            1
        )
    }

    /**
     * Funtion to log with tag Chat, you need to just pass the message.
     *
     * @String Message = pass your message that you want to log.
     */
    fun LogC(Message: String) {
        Log(
            Tags.CHAT,
            "" + Message,
            1
        )
    }


    /**
     * Funtion to log with tag GPS, you need to just pass the message.
     *
     * @String Message = pass your message that you want to log.
     */
    fun LogGP(Message: String) {
        Log(
            Tags.GPS,
            "" + Message,
            1
        )
    }

    fun applyFontAtAlertDialog(
        mContext: Context?,
        alertDialog: androidx.appcompat.app.AlertDialog?
    ) {
        try {
            val textView: TextView =
                alertDialog!!.getWindow().findViewById(android.R.id.message) as TextView
            textView.setTypeface(
                Typeface.createFromAsset(
                    mContext?.assets,
                    mContext?.getString(R.string.font_regular)
                )
            )
            val alertTitle: TextView =
                alertDialog!!.getWindow().findViewById(R.id.alertTitle) as TextView
            alertTitle.setTypeface(
                Typeface.createFromAsset(
                    mContext?.assets,
                    mContext?.getString(R.string.font_medium)
                )
            )
            val button1: Button =
                alertDialog!!.getWindow().findViewById(android.R.id.button1) as Button
            button1.setTypeface(
                Typeface.createFromAsset(
                    mContext?.assets,
                    mContext?.getString(R.string.font_medium)
                )
            )
            val button2: Button =
                alertDialog!!.getWindow().findViewById(android.R.id.button2) as Button
            button2.setTypeface(
                Typeface.createFromAsset(
                    mContext?.assets,
                    mContext?.getString(R.string.font_medium)
                )
            )
        } catch (e: java.lang.Exception) {
            LogE(e)
        }
    }

    fun applyFontAtAlertDialog(mContext: Context?, alertDialog: AlertDialog) {
        try {
            val textView: TextView =
                alertDialog!!.getWindow().findViewById(android.R.id.message) as TextView
            textView.setTypeface(
                Typeface.createFromAsset(
                    mContext?.assets,
                    mContext?.getString(R.string.font_regular)
                )
            )
            val alertTitle: TextView =
                alertDialog!!.getWindow().findViewById(R.id.alertTitle) as TextView
            alertTitle.setTypeface(
                Typeface.createFromAsset(
                    mContext?.assets,
                    mContext?.getString(R.string.font_medium)
                )
            )
            val button1: Button =
                alertDialog!!.getWindow().findViewById(android.R.id.button1) as Button
            button1.setTypeface(
                Typeface.createFromAsset(
                    mContext?.assets,
                    mContext?.getString(R.string.font_medium)
                )
            )
            val button2: Button =
                alertDialog!!.getWindow().findViewById(android.R.id.button2) as Button
            button2.setTypeface(
                Typeface.createFromAsset(
                    mContext?.assets,
                    mContext?.getString(R.string.font_medium)
                )
            )
        } catch (e: java.lang.Exception) {
            LogE(e)
        }
    }

    fun shareAppUrl(context: Context) {
        try {
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
            var sAux =
                "\"${context.getString(R.string.app_name)}\" will allow you to download video from Facebook, Instagram, TikTok, etc. Download Now.\n\n"
            sAux += " https://play.google.com/store/apps/details?id=" + context.packageName
            i.putExtra(Intent.EXTRA_TEXT, sAux)
            context.startActivity(Intent.createChooser(i, "choose one"))
        } catch (e: Exception) {
            Utility.LogE(e)
        }
    }

    fun rateApp(context: Context) {
        try {
            val appPackageName = context.getPackageName()
            try {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appPackageName")
                    )
                )
            } catch (anfe: android.content.ActivityNotFoundException) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }
        } catch (e: Exception) {
            Utility.LogE(e)
        }
    }

    fun openThisApp(context: Context, packageName: String, appName: String) {
        if (isPackageInstalled(context, packageName)) {
            try {
                var uri = "facebook:/newsfeed"
                if (appName.toLowerCase().contains("tiktok")) {
                    uri = "http://vm.tiktok.com/"
                } else if (appName.toLowerCase().contains("instagram")) {
                    uri = "https://www.instagram.com/"
                }
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                iUtils.ShowToast(context, "Error occurred!")
            }
        } else {
            iUtils.ShowToast(context, "Install $appName app")
            try {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                )
            } catch (anfe: android.content.ActivityNotFoundException) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            }
        }
    }
}