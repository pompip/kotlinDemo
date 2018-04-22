package com.example.chong.kotlindemo.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.example.chong.kotlindemo.R
import com.example.chong.kotlindemo.util.formattedTime

class VideoControllerLayout : FrameLayout, SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private lateinit var mPlayImg: ImageView //播放按钮
    private lateinit var mProgressSeekBar: SeekBar//播放进度条
    private lateinit var mTimeTxt1: TextView
    private lateinit var mTimeTxt2: TextView//播放时间
    private lateinit var mExpandImg: ImageView//最大化播放按钮
    private lateinit var mResolutionTxt: TextView//清晰度

    private lateinit var mMediaControl: VideoControllerCallBack
    private lateinit var mTitleStrList: ArrayList<String>
    private lateinit var mSupportedBitrates: ArrayList<String>

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        initView()
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, isFromUser: Boolean) {
        if (isFromUser)
            mMediaControl.onProgressTurn(ProgressState.DOING, progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        mMediaControl.onProgressTurn(ProgressState.START, 0)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        mMediaControl.onProgressTurn(ProgressState.STOP, 0)
    }

    override fun onClick(view: View) {
        when {
            view.id == R.id.pause -> mMediaControl.onPlayTurn()
            view.id == R.id.expand -> mMediaControl.onPageTurn()
            view.id == R.id.resolutionTxt -> mMediaControl.onResolutionTurn()
        }
    }

    val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, resources.displayMetrics)
    fun closeController() {
        if (isAnimateAction) {
            return
        }
        if (visibility == View.VISIBLE) {
            animate().setDuration(300).translationYBy(height).alpha(0f).withStartAction {
                isAnimateAction = true;
            }.withEndAction {
                visibility = View.GONE
                isAnimateAction = false;
            }.start()
        }
    }

    private var isAnimateAction = false;
    fun showOrHideController() {
        if (isAnimateAction) {
            return
        }
        if (visibility == View.VISIBLE) {
            closeController()
        } else {
            visibility = View.VISIBLE
            animate().setDuration(300).translationYBy(-height).alpha(1f).withStartAction {
                isAnimateAction = true;
            }.withEndAction {
                isAnimateAction = false;
            }.start()
        }
    }


    fun setProgressBar(progress: Int, secondProgress: Int) {
        mProgressSeekBar.progress =
                if (progress < 0) 0
                else if (progress > 100) 100
                else progress
        mProgressSeekBar.secondaryProgress =
                if (secondProgress < 0) 0
                else if (secondProgress > 100) 100
                else secondProgress
    }

    fun setPlayState(playState: PlayState) {
        mPlayImg.setImageResource(if (playState == PlayState.PLAY) R.drawable.ic_vod_pause_normal else R.drawable.ic_vod_play_normal)
    }

    fun setPageType(pageType: PageType) {
        mExpandImg.visibility = if (pageType == PageType.EXPAND) View.GONE else View.VISIBLE
        mResolutionTxt.visibility = if (pageType == PageType.SHRINK) View.GONE else View.VISIBLE
    }

    fun setPlayProgressTxt(nowSecond: Int, allSecond: Int) {
        mTimeTxt1.setText(formatPlayTime(nowSecond.toLong()))
        mTimeTxt2.setText(formatPlayTime(allSecond.toLong()))
    }

    private fun formatPlayTime(time: Long): String {
        return formattedTime(time / 1000, false)
    }

    fun playFinish(allTime: Int) {
        mProgressSeekBar.progress = 0
        setPlayProgressTxt(0, allTime)
        setPlayState(PlayState.PAUSE)
    }

    fun setMediaControl(mediaControl: VideoControllerCallBack) {
        mMediaControl = mediaControl
    }


    private fun initView() {
        View.inflate(context, R.layout.layout_video_controller, this)
        mPlayImg = findViewById(R.id.pause)
        mProgressSeekBar = findViewById(R.id.media_controller_progress)
        mTimeTxt1 = findViewById(R.id.time_pos)
        mTimeTxt2 = findViewById(R.id.time_duration)
        mExpandImg = findViewById(R.id.expand)
        mResolutionTxt = findViewById(R.id.resolutionTxt)
        initData()
    }

    private fun initData() {
        mTitleStrList = ArrayList()
        mTitleStrList.add("流畅")
        mTitleStrList.add("高清")
        mTitleStrList.add("超清")
        mTitleStrList.add("原画")
        mSupportedBitrates = ArrayList()
        mProgressSeekBar.setOnSeekBarChangeListener(this)
        mPlayImg.setOnClickListener(this)
        mResolutionTxt.setOnClickListener(this)
        mExpandImg.setOnClickListener(this)
        setPageType(PageType.SHRINK)
        setPlayState(PlayState.PAUSE)
        mResolutionTxt.text = mTitleStrList[0]
    }


    /**
     * 播放样式 展开、缩放
     */
    enum class PageType {
        EXPAND, SHRINK
    }

    /**
     * 播放状态 播放 暂停
     */
    enum class PlayState {
        PLAY, PAUSE
    }

    enum class ProgressState {
        START, DOING, STOP
    }

    interface VideoControllerCallBack {
        fun onPlayTurn()

        fun onPageTurn()

        fun onProgressTurn(state: ProgressState, progress: Int)

        fun onResolutionTurn()

        fun alwaysShowController()
    }

}