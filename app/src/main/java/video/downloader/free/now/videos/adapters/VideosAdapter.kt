package video.downloader.free.now.videos.adapters

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import android.util.Log
import android.view.*
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import video.downloader.free.now.videos.R
import video.downloader.free.now.videos.models.VideoModel
import video.downloader.free.now.videos.utils.Constants.DEL_CONFIRM
import video.downloader.free.now.videos.utils.iUtils
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

class VideosAdapter(
    private val context: Context,
    private val al_videoModel: ArrayList<VideoModel>,
    activity: Activity?
) : androidx.recyclerview.widget.RecyclerView.Adapter<VideosAdapter.ViewHolder>() {
    private var mActiveActionMode: ActionMode? = null
    private var multiSelect = false
    private val selectedItems = ArrayList<Int>()
    private val actionModeCallbacks: ActionMode.Callback =
        object : ActionMode.Callback {
            override fun onCreateActionMode(
                mode: ActionMode,
                menu: Menu
            ): Boolean {
                mActiveActionMode = mode
                multiSelect = true
                mode.menuInflater.inflate(R.menu.delete, menu)
                return true
            }

            override fun onPrepareActionMode(
                mode: ActionMode,
                menu: Menu
            ): Boolean {
                return false
            }

            override fun onActionItemClicked(
                mode: ActionMode,
                item: MenuItem
            ): Boolean {
                AlertDialog.Builder(context)
                    .setTitle("Delete " + selectedItems.size + " video?")
                    .setMessage(DEL_CONFIRM)
                    .setCancelable(false)
                    .setPositiveButton(
                        "DELETE",
                        DialogInterface.OnClickListener { dialog, whichButton ->
                            Collections.sort(
                                selectedItems,
                                Collections.reverseOrder<Any>()
                            )
                            for (intItem in selectedItems) {
                                //  Log.e("Deleted",intItem.toString());
                                // al_video.remove(intItem);
                                deleteItem(Integer.valueOf(intItem.toString()))
                            }
                            mode.finish()
                        })
                    .setNegativeButton("CANCEL", null).show()
                return true
            }

            override fun onDestroyActionMode(mode: ActionMode) {
                multiSelect = false
                selectedItems.clear()
                notifyDataSetChanged()
            }
        }

    inner class ViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
        var iv_image: ImageView
        var rl_select: FrameLayout
        var tvDuration: TextView
        var vCheckBackColor: View
        var chkVideoSelected: CheckBox
        fun selectItem(item: Int) {
            if (multiSelect) {
                if (selectedItems.contains(item)) {
                    selectedItems.remove(Integer.valueOf(item))
                    rl_select.setBackgroundColor(Color.WHITE)
                    chkVideoSelected.visibility = View.GONE
                    vCheckBackColor.visibility = View.GONE
                    Log.e("selctedItems", "$selectedItems---$item")
                    if (selectedItems.isEmpty()) {
                        multiSelect = false
                        mActiveActionMode!!.finish()
                    }
                } else {
                    selectedItems.add(item)
                    rl_select.setBackgroundColor(Color.LTGRAY)
                    chkVideoSelected.visibility = View.VISIBLE
                    vCheckBackColor.visibility = View.VISIBLE
                    Log.e("UnselctedItems", "$selectedItems---$item")
                }
                mActiveActionMode!!.title = Integer.toString(selectedItems.size) + " Selected"
            }
        }

        fun update(videoModel: VideoModel, id: Int) {
            Glide.with(context).load(videoModel.str_thumb).skipMemoryCache(false).into(iv_image)
            tvDuration.text = secToTime(videoModel.duration)
            rl_select.setOnClickListener {
                //  iUtils.ShowToast(context,"clicked :*");
                if (multiSelect) {
                    selectItem(id)
                } else {
                    val options =
                        arrayOf("Watch", "Delete", "Share")
                    val builder =
                        AlertDialog.Builder(context)
                    builder.setTitle("Choose")
                    builder.setItems(options) { dialog, which ->
                        if (options[which].contains("Watch")) {
                            try {
                                val Path = videoModel.str_path
                                val mVideoWatch = Intent(Intent.ACTION_VIEW)
                                mVideoWatch.setDataAndType(
                                    Uri.parse(Path),
                                    "video/mp4"
                                )
                                context.startActivity(mVideoWatch)
                            } catch (e: ActivityNotFoundException) {
                                iUtils.ShowToast(
                                    context,
                                    "Something went wrong while playing video! Please try again "
                                )
                                Log.e("Error", e.message)
                            }
                        } else if (options[which].contains("Delete")) {
                            AlertDialog.Builder(context)
                                .setTitle("Delete")
                                .setMessage(DEL_CONFIRM)
                                .setCancelable(false)
                                .setPositiveButton(
                                    "DELETE",
                                    DialogInterface.OnClickListener { dialog, whichButton -> //  Log.e("Deleted",intItem.toString());
                                        // al_video.remove(intItem);
                                        deleteItem(id)
                                    })
                                .setNegativeButton("CANCEL", null).show()
                        } else {
                            val intentShareFile = Intent(Intent.ACTION_SEND)
                            val fileWithinMyDir =
                                File(videoModel.str_path)
                            if (fileWithinMyDir.exists()) {
                                try {
                                    intentShareFile.type = "video/mp4"
                                    intentShareFile.putExtra(
                                        Intent.EXTRA_STREAM,
                                        Uri.parse(videoModel.str_path)
                                    )
                                    intentShareFile.putExtra(
                                        Intent.EXTRA_SUBJECT,
                                        context.getString(R.string.SharingVideoSubject)
                                    )
                                    intentShareFile.putExtra(
                                        Intent.EXTRA_TEXT,
                                        context.getString(R.string.SharingVideoBody)
                                    )
                                    context.startActivity(
                                        Intent.createChooser(
                                            intentShareFile,
                                            context.getString(R.string.SharingVideoTitle)
                                        )
                                    )
                                } catch (e: ActivityNotFoundException) {
                                    iUtils.ShowToast(
                                        context,
                                        "Something went wrong while sharing video! Please try again "
                                    )
                                }
                            }
                        }
                    }
                    builder.show()
                }
            }
            rl_select.setOnLongClickListener { view ->
                (view.context as AppCompatActivity).startSupportActionMode(
                    actionModeCallbacks
                )
                selectItem(id)
                true
            }
            //textView.setText(value + "");
            if (selectedItems.contains(id)) {
                chkVideoSelected.visibility = View.VISIBLE
                vCheckBackColor.visibility = View.VISIBLE
                rl_select.setBackgroundColor(Color.LTGRAY)
            } else {
                rl_select.setBackgroundColor(Color.WHITE)
                chkVideoSelected.visibility = View.GONE
                vCheckBackColor.visibility = View.GONE
            }
        }

        init {
            iv_image =
                v.findViewById<View>(R.id.media_img_bck) as ImageView
            rl_select = v.findViewById<View>(R.id.frameLayout) as FrameLayout
            tvDuration = v.findViewById<View>(R.id.tvDuration) as TextView
            vCheckBackColor =
                v.findViewById(R.id.vCheckBackColor) as View
            chkVideoSelected = v.findViewById<View>(R.id.chkVideoSelected) as CheckBox
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.video_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        viewHolder: ViewHolder,
        i: Int
    ) {
        viewHolder.update(al_videoModel[i], i)
    }

    private fun deleteItem(position: Int) {
        val video = al_videoModel[position].str_path
        // context.getContentResolver().delete(Uri.parse(video), null, null);


        //   Boolean del =   new File(video).getAbsoluteFile().delete();

        //  Log.e("Deleted", new File(Uri.parse(video).getPath()).getAbsoluteFile().toString());
        context.contentResolver.delete(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            MediaStore.Video.Media.DATA + "=?", arrayOf(video)
        )
        al_videoModel.removeAt(position)
        notifyItemRemoved(position)
        this.notifyItemRangeChanged(position, al_videoModel.size)
        notifyDataSetChanged()

        // v.ViewHolder.setVisibility(View.GONE);
    }

    override fun getItemCount(): Int {
        return al_videoModel.size
    }

    private fun secToTime(sec: Int): String {
        return String.format(
            "%d:%d",
            TimeUnit.MILLISECONDS.toMinutes(sec.toLong()),
            TimeUnit.MILLISECONDS.toSeconds(sec.toLong()) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(
                            sec.toLong()
                        )
                    )
        )
    }

    init {
        notifyDataSetChanged()
        Log.e("updated", "yesupdate$al_videoModel")
    }
}