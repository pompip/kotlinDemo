package com.example.chong.kotlindemo.activity

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.*
import com.bumptech.glide.Glide
import com.example.chong.kotlindemo.R
import com.example.chong.kotlindemo.util.formattedTime
import com.example.chong.kotlindemo.util.loge
import com.example.chong.kotlindemo.widget.VideoControllerLayout
import com.tencent.rtmp.*
import kotlinx.android.synthetic.main.activity_local_video_play.*
import kotlinx.android.synthetic.main.item_local_video.view.*

import java.io.File
import java.util.ArrayList


class LocalVideoPlayActivity : AppCompatActivity() {
    lateinit var allVideoList: ArrayList<TCVideoFileInfo>
    lateinit var player: TXVodPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_local_video_play)
        video_controller.setMediaControl(VideoControllerImpl())
        allVideoList = findAllVideo()

        player = TXVodPlayer(this)
        initPlayer()

        initRecyclerView()
        tx_video_view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                video_controller.showOrHideController()
                return@setOnTouchListener true
            } else {
                return@setOnTouchListener false
            }
        }
    }

    override fun onStop() {
        super.onStop()
        player.pause()
    }

    override fun onStart() {
        super.onStart()
        player.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stopPlay(false)
        tx_video_view.onDestroy()
    }

    private fun initPlayer() {
        val config = TXVodPlayConfig();
        player.setPlayerView(tx_video_view)
        player.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION)//RENDER_MODE_FILL_SCREEN ：0
        player.setVodListener(VideoEventListener())
        player.setConfig(config)
    }


    inner class VideoEventListener : ITXVodPlayListener {
        override fun onPlayEvent(txVodPlayer: TXVodPlayer?, event: Int, param: Bundle?) {
            if (event == TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
                val progress = param?.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS)!!
                val duration = param?.getInt(TXLiveConstants.EVT_PLAY_DURATION_MS)
                val playable = param?.getInt(TXLiveConstants.EVT_PLAYABLE_DURATION_MS)

                video_controller.setPlayProgressTxt(progress, duration)
                video_controller.setProgressBar(progress * 100 / duration,
                        playable * 100 / duration)

            } else if (event == TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION) {
                val width = param?.getInt(TXLiveConstants.EVT_PARAM1)!!//width
                val height = param.getInt(TXLiveConstants.EVT_PARAM2)//height
                if (width>height){
                    player.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN)
                }else{
                    player.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION)
                }
            }
            loge("onPlayEvent", event)
            loge("onPlayEvent", param!!)
        }

        override fun onNetStatus(txVodPlayer: TXVodPlayer?, param: Bundle?) {

            loge("onNetStatus", param!!)
        }

    }

    inner class VideoControllerImpl : VideoControllerLayout.VideoControllerCallBack {
        override fun onPlayTurn() {
            val state = if (player.isPlaying) {
                player.pause()
                VideoControllerLayout.PlayState.PLAY
            } else {
                player.resume()
                VideoControllerLayout.PlayState.PAUSE
            }
            video_controller.setPlayState(state)
        }

        override fun onPageTurn() {
            requestedOrientation =
                    if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    } else {
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    }
        }

        override fun onProgressTurn(state: VideoControllerLayout.ProgressState, progress: Int) {
            if (state == VideoControllerLayout.ProgressState.DOING) {
                val time = progress * player.duration / 100
                player.seek(time)
            }

        }

        override fun onResolutionTurn() {
        }

        override fun alwaysShowController() {
        }
    }

    private fun startPlay(url: String) {
        player.startPlay(url)
    }


    private fun initRecyclerView() {
        video_recycler_view.layoutManager = LinearLayoutManager(this)
        video_recycler_view.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        video_recycler_view.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
                val itemView = View.inflate(this@LocalVideoPlayActivity, R.layout.item_local_video, null)
                return object : RecyclerView.ViewHolder(itemView) {}
            }

            override fun getItemCount(): Int {
                return allVideoList.size
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
                val info = allVideoList[position]
                val itemView = holder?.itemView!!
                Glide.with(this@LocalVideoPlayActivity).load(Uri.fromFile(File(info.filePath))).into(itemView.iv_video_thumb!!)
                itemView.tv_video_duration.text = formattedTime(info.duration / 1000)
                itemView.tv_video_title.text = info.fileName
                itemView.setOnClickListener {
                    startPlay(info.filePath!!)
                }
            }

        }
    }

    private fun findAllVideo(): ArrayList<TCVideoFileInfo> {
        val videos = ArrayList<TCVideoFileInfo>()
        val mediaColumns = arrayOf(
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.VideoColumns.DISPLAY_NAME,
                MediaStore.Video.VideoColumns.DURATION)
        val cursor = contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                mediaColumns, null, null, null) ?: return videos

        if (cursor.moveToFirst()) {
            do {
                val fileItem = TCVideoFileInfo()
                fileItem.filePath = (cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)))
                val file = File(fileItem.filePath)
                val canRead = file.canRead()
                val length = file.length()
                if (!canRead || length == 0L) {
                    continue
                }
                fileItem.fileName = (cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)))
                var duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                if (duration < 0)
                    duration = 0
                fileItem.duration = (duration)

                if (fileItem.fileName != null && fileItem.fileName!!.endsWith(".mp4")) {
                    videos.add(fileItem)
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return videos
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (null == tx_video_view) return
        /***
         * 根据屏幕方向重新设置播放器的大小
         */
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.decorView.invalidate()
            val height = resources.displayMetrics.widthPixels
            val width = resources.displayMetrics.heightPixels
            tx_video_view!!.layoutParams.height = width.toInt()
            tx_video_view!!.layoutParams.width = height.toInt()
        } else if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val attrs = window.attributes
            attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
            window.attributes = attrs
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            val width = resources.displayMetrics.widthPixels
            val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200f, resources.displayMetrics)
            tx_video_view!!.layoutParams.height = height.toInt()
            tx_video_view!!.layoutParams.width = width.toInt()
        }
    }
}
