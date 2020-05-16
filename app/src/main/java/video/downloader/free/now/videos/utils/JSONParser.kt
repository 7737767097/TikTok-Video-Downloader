package video.downloader.free.now.videos.utils

import android.util.Log
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class JSONParser {
    fun getJSONFromUrl(url: String?): JSONArray? {
        val builder = StringBuilder()
        val client: HttpClient = DefaultHttpClient()
        val httpGet = HttpGet(url)
        try {
            val response = client.execute(httpGet)
            val statusLine = response.statusLine
            val statusCode = statusLine.statusCode
            if (statusCode == 200) {
                val entity = response.entity
                val content = entity.content
                val reader =
                    BufferedReader(InputStreamReader(content))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    builder.append(line)
                }
            } else {
                Log.e("==>", "Failed to download file")
            }
        } catch (e: ClientProtocolException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Parse String to JSON object
        try {
            jarray =
                JSONArray(builder.toString())
        } catch (e: JSONException) {
            Log.e("JSON Parser", "Error parsing data $e")
        }

        // return JSON Object
        return jarray
    }

    fun getOJSONFromUrl(url: String?): JSONObject? {
        val builder = StringBuilder()
        val client: HttpClient = DefaultHttpClient()
        val httpGet = HttpGet(url)
        try {
            val response = client.execute(httpGet)
            val statusLine = response.statusLine
            val statusCode = statusLine.statusCode
            if (statusCode == 200) {
                val entity = response.entity
                val content = entity.content
                val reader =
                    BufferedReader(InputStreamReader(content))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    builder.append(line)
                }
            } else {
                Log.e("==>", "Failed to download file")
            }
        } catch (e: ClientProtocolException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Parse String to JSON object
        try {
            jarrayo =
                JSONObject(builder.toString())
        } catch (e: JSONException) {
            Log.e("JSON Parser", "Error parsing data $e")
        }

        // return JSON Object
        return jarrayo
    }

    fun getSJSONFromUrl(url: String?): String? {
        val builder = StringBuilder()
        val client: HttpClient = DefaultHttpClient()
        val httpGet = HttpGet(url)
        httpGet.addHeader("header-name", "header-value")
        try {
            val response = client.execute(httpGet)
            val statusLine = response.statusLine
            val statusCode = statusLine.statusCode
            if (statusCode == 200) {
                val entity = response.entity
                val content = entity.content
                val reader =
                    BufferedReader(InputStreamReader(content))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    builder.append(line)
                }
            } else {
                Log.e("==>", "Failed to download file")
            }
        } catch (e: ClientProtocolException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        jarrayq = builder.toString()
        // return String
        return jarrayq
    }

    companion object {
        var iStream: InputStream? = null
        var jarray: JSONArray? = null
        var jarrayq: String? = null
        var jarrayo: JSONObject? = null
        var json = ""
    }
}