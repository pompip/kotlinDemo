package com.example.chong.kotlindemo.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Window
import com.example.chong.kotlindemo.R
import com.example.chong.kotlindemo.util.loge
import com.example.chong.kotlindemo.util.toast
import com.tencent.ugc.TXRecordCommon
import com.tencent.ugc.TXUGCRecord
import kotlinx.android.synthetic.main.activity_video_recorder.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.chong.kotlindemo.widget.RecorderControllerLayout


class VideoRecorderActivity : AppCompatActivity() {
    private var mRecommendQuality = TXRecordCommon.VIDEO_QUALITY_MEDIUM
    private var mMinDuration: Int = 5 * 1000
    private var mMaxDuration: Int = 60 * 1000
    private var mAspectRatio: Int = TXRecordCommon.VIDEO_ASPECT_RATIO_9_16 // 视频比例
    private var mRecordResolution: Int = 0 // 录制分辨率
    private var mBiteRate: Int = 0 // 码率
    private var mFps: Int = 0 // 帧率
    private var mGop: Int = 0 // 关键帧间隔
    private var mRecording = false
    private var mStartPreview = false
    private var mFront = true
    private var mNeedEditer: Boolean = false

    lateinit var recorder: TXUGCRecord;
    lateinit var simpleConfig: TXRecordCommon.TXUGCSimpleConfig;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_video_recorder)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        recorder = TXUGCRecord.getInstance(this)

        initRecorderConfig();
        initView()
    }

    override fun onStart() {
        super.onStart()
        recorder.startCameraSimplePreview(simpleConfig, recorder_preview);
        recorder.setVideoRecordListener(RecordListener())

    }

    override fun onStop() {
        super.onStop()
        recorder.stopCameraPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun initRecorderConfig() {
        simpleConfig = TXRecordCommon.TXUGCSimpleConfig();
        simpleConfig.videoQuality = TXRecordCommon.VIDEO_QUALITY_MEDIUM
        simpleConfig.minDuration = mMinDuration
        simpleConfig.maxDuration = mMaxDuration
        simpleConfig.isFront = mFront
        simpleConfig.needEdit = mNeedEditer
    }

    private fun assembleVideoPath(): Array<String> {
        val format = SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.CHINA);
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val dirFile = File(dir, "/myVideo")
        val coverFile = File(dirFile, "/cover");
        if (!dirFile.exists()) {
            dirFile.mkdirs()
        }
        if (!coverFile.exists()) {
            coverFile.mkdirs()
        }
        val name = format.format(Date(System.currentTimeMillis()))
        val videoPath = dirFile.absolutePath + File.separator + name + ".mp4"
        val coverPath = coverFile.absolutePath + File.separator + name + ".jpg"

        return arrayOf(videoPath, coverPath);
    }

    var isFont = true;
    var isTorch = false;
    var isParse = false;
    fun initView() {
        val pathArr = assembleVideoPath();
        record_controller.recorderControllerCallback = object : RecorderControllerLayout.RecorderControllerCallback {
            override fun recordAspectRatio(ratio: RecorderControllerLayout.Ratio) {
                mAspectRatio = when (ratio) {
                    RecorderControllerLayout.Ratio.ratio11 -> TXRecordCommon.VIDEO_ASPECT_RATIO_1_1
                    RecorderControllerLayout.Ratio.ratio169 -> TXRecordCommon.VIDEO_ASPECT_RATIO_9_16
                    RecorderControllerLayout.Ratio.ratio43 -> TXRecordCommon.VIDEO_ASPECT_RATIO_3_4
                }
                recorder.setAspectRatio(mAspectRatio)
            }

            override fun torchClick(v: View) {
                recorder.toggleTorch(isTorch)
                isTorch = !isTorch;
            }

            override fun cameraClick(v: View) {
                recorder.switchCamera(!isFont)
                isFont = !isFont
            }

            override fun confirmClick(v: View) {
                recorder.stopRecord()
            }

            override fun deleteClick(v: View) {
                recorder.partsManager.deleteLastPart()
            }

            override fun recordClick(v: View) {
                val result = recorder.startRecord(pathArr[0], pathArr[1])
                if (result == TXRecordCommon.START_RECORD_OK) {
                    toast("start Record")
                } else if (TXRecordCommon.START_RECORD_ERR_IS_IN_RECORDING == result) {
                    if (isParse) {
                        recorder.resumeRecord()
                        toast("Record Resume")
                    } else {
                        recorder.pauseRecord()
                        toast("Record Parse")
                    }
                    isParse = !isParse;
                }

            }

        }


    }


    inner class RecordListener : TXRecordCommon.ITXVideoRecordListener {
        override fun onRecordEvent(event: Int, p1: Bundle?) {
            if (event == TXRecordCommon.EVT_ID_PAUSE) {
//                    mRecordProgressView.clipComplete()
            } else if (event == TXRecordCommon.EVT_CAMERA_CANNOT_USE) {
                toast("摄像头打开失败，请检查权限", Toast.LENGTH_SHORT)
            } else if (event == TXRecordCommon.EVT_MIC_CANNOT_USE) {
                toast("麦克风打开失败，请检查权限", Toast.LENGTH_SHORT)
            } else if (event == TXRecordCommon.EVT_ID_RESUME) {

            }
        }

        override fun onRecordComplete(p0: TXRecordCommon.TXRecordResult?) {
            sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse("file://" + p0?.videoPath)))
        }

        override fun onRecordProgress(p0: Long) {
            record_controller.progress = (p0.toInt() * 100 / 1000 / 60);
        }

    }


}
