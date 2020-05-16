package video.downloader.free.now.videos.fragments

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import video.downloader.free.now.videos.AppDelegate
import video.downloader.free.now.videos.adapters.VideosAdapter
import video.downloader.free.now.videos.models.VideoModel
import video.downloader.free.now.videos.utils.Constants.DOWNLOAD_DIRECTORY
import video.downloader.free.now.videos.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import kotlinx.android.synthetic.main.fragment_gallery.view.*

class GalleryFragment : androidx.fragment.app.Fragment() {

    var obj_Videos_adapter: VideosAdapter? = null
    var al_video = ArrayList<VideoModel>()
    public var recyclerView1: androidx.recyclerview.widget.RecyclerView? = null
    var recyclerViewLayoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager? = null
    lateinit var mAdView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater!!.inflate(R.layout.fragment_gallery, container, false)

        recyclerView1 = view.recyclerView

        recyclerViewLayoutManager = androidx.recyclerview.widget.GridLayoutManager(
            context!!,
            3
        ) as androidx.recyclerview.widget.RecyclerView.LayoutManager?
        recyclerView1!!.setLayoutManager(recyclerViewLayoutManager)
        fn_video(context!!, requireActivity(), true)
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
                    "adsLoaded_gallery",
                    null
                )
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
                Log.e("AdLoadeed", "onAdFailedToLoad - " + errorCode)
                AppDelegate.logFirebaseEvent(
                    "adsFailed_gallery",
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
                    "adsClicked_gallery",
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

    fun fn_video(cn: Context, activity: androidx.fragment.app.FragmentActivity, f: Boolean) {
        al_video = ArrayList<VideoModel>()
        val int_position = 0
        val uri: Uri
        val cursor: Cursor
        val column_index_data: Int
        val column_index_folder_name: Int
        val column_id: Int
        val thum: Int
        val duration: Int

        var absolutePathOfImage: String? = null
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val condition = MediaStore.Video.Media.DATA + " like?"
        val selectionArguments = arrayOf("%$DOWNLOAD_DIRECTORY%")
        val sortOrder = MediaStore.Video.Media.DATE_TAKEN + " DESC"
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Video.Media.DURATION
        )
        cursor = cn!!.getContentResolver()
            .query(uri, projection, condition, selectionArguments, "$sortOrder")

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        column_index_folder_name =
            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
        column_id = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA)
        duration = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
        var i: Int = 0
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data)
            Log.e("Column", absolutePathOfImage)
            Log.e("Folder", cursor.getString(column_index_folder_name))
            Log.e("column_id", cursor.getString(column_id))
            Log.e("thum", cursor.getString(thum))
            Log.e("duration", cursor.getString(duration))

            val obj_model =
                VideoModel()
            obj_model.isBoolean_selected = false
            obj_model.str_path = absolutePathOfImage
            obj_model.str_thumb = cursor.getString(thum)
            obj_model.duration = cursor.getInt(duration)
            obj_model.id = i

            al_video.add(obj_model)
            i = i + 1
        }


        obj_Videos_adapter =
            VideosAdapter(
                cn!!,
                al_video,
                activity!!
            )

        recyclerView1!!.setAdapter(null);
        recyclerView1!!.setAdapter(obj_Videos_adapter)
        obj_Videos_adapter!!.notifyDataSetChanged();

//
//        //recyclerView1!!.setLayoutManager(null);
//        recyclerView1!!.getRecycledViewPool().clear();
//        recyclerView1!!.swapAdapter(obj_adapter, false);
//       // recyclerView1!!.setLayoutManager(layoutManager);
//        obj_adapter!!.notifyDataSetChanged();


    }


    override fun setMenuVisibility(visible: Boolean) {
        super.setMenuVisibility(visible)
        if (visible) {
            fn_video(context!!, requireActivity(), true);
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("resume", "12412535")

    }

}

