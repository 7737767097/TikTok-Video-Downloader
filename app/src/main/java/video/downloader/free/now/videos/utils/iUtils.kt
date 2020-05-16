package video.downloader.free.now.videos.utils

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import androidx.core.content.ContextCompat
import android.text.TextUtils
import android.util.Patterns
import android.view.MenuItem
import android.webkit.URLUtil
import android.widget.Toast
import video.downloader.free.now.videos.utils.Constants.PREF_APPNAME
import java.net.URL

object iUtils {
    //private InterstitialAd interstitialAd;
    fun isSameDomain(url: String, url1: String): Boolean {
        return getRootDomainUrl(
            url.toLowerCase()
        ) == getRootDomainUrl(
            url1.toLowerCase()
        )
    }

    private fun getRootDomainUrl(url: String): String {
        val domainKeys =
            url.split("/").toTypedArray()[2].split("\\.").toTypedArray()
        val length = domainKeys.size
        val dummy = if (domainKeys[0] == "www") 1 else 0
        return if (length - dummy == 2) domainKeys[length - 2] + "." + domainKeys[length - 1] else {
            if (domainKeys[length - 1].length == 2) {
                domainKeys[length - 3] + "." + domainKeys[length - 2] + "." + domainKeys[length - 1]
            } else {
                domainKeys[length - 2] + "." + domainKeys[length - 1]
            }
        }
    }

    fun tintMenuIcon(
        context: Context?,
        item: MenuItem,
        color: Int
    ) {
        val drawable = item.icon
        if (drawable != null) {
            // If we don't mutate the drawable, then all drawable's with this id will have a color
            // filter applied to it.
            drawable.mutate()
            drawable.setColorFilter(
                ContextCompat.getColor(context!!, color),
                PorterDuff.Mode.SRC_ATOP
            )
        }
    }

    fun bookmarkUrl(context: Context, url: String?) {
        val pref =
            context.getSharedPreferences(PREF_APPNAME, 0) // 0 - for private mode
        val editor = pref.edit()

        // if url is already bookmarked, unbookmark it
        if (pref.getBoolean(url, false)) {
            editor.remove(url).commit()
        } else {
            editor.putBoolean(url, true)
        }
        editor.commit()
    }

    fun isBookmarked(context: Context, url: String?): Boolean {
        val pref = context.getSharedPreferences(PREF_APPNAME, 0)
        return pref.getBoolean(url, false)
    }

    fun ShowToast(context: Context?, str: String?) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
    }

    fun checkURL(input: CharSequence): Boolean {
        if (TextUtils.isEmpty(input)) {
            return false
        }
        val URL_PATTERN = Patterns.WEB_URL
        var isURL = URL_PATTERN.matcher(input).matches()
        if (!isURL) {
            val urlString = input.toString() + ""
            if (URLUtil.isNetworkUrl(urlString)) {
                try {
                    URL(urlString)
                    isURL = true
                } catch (e: Exception) {
                }
            }
        }
        return isURL
    }

    //   public static void GetSessionID(final Context cntx){
    //       final String[] ID = new String[1];
    //
    //       AsyncTask.execute(new Runnable() {
    //           @Override
    //           public void run() {
    //
    //               try {
    //                   Document doc = Jsoup.connect(API_URL2).post();
    //
    //                   Elements scriptElements = doc.getElementsByTag("script");
    //                   for (Element element : scriptElements) {
    //                       if (element.data().contains("sid")) {
    //                            // find the line which contains 'infosite.token = <...>;'
    //                           Pattern pattern = Pattern.compile("(?is)sid=\'(.+?)\'");
    //                           Matcher matcher = pattern.matcher(element.data());
    //                           // we only expect a single match here so there's no need to loop through the matcher's groups
    //                           if (matcher.find()) {
    //                               //System.out.println(matcher.group());
    //                               //System.out.println(matcher.group(1));
    //                               ID[0] = matcher.group(1).toString();
    //                           } else {
    //                               System.err.println("No match found!");
    //                           }
    //                           break;
    //                       }
    //                   }
    //               } catch (IOException e) {
    //                   e.printStackTrace();
    //               }
    //
    //
    //               Session session;
    //               session = new Session(cntx);
    //               session.setSid(ID[0]);
    //                   }
    //
    //
    //       });
    //
    //
    //
    //
    //    //    return ID[0];
    //   }
    fun isPackageInstalled(
        context: Context,
        packageName: String?
    ): Boolean {
        var found = true
        try {
            context.packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            found = false
        }
        return found
    }
}