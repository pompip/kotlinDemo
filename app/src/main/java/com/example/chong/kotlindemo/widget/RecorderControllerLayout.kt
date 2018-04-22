package com.example.chong.kotlindemo.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.example.chong.kotlindemo.R
import com.example.chong.kotlindemo.util.formattedTime
import kotlinx.android.synthetic.main.layout_video_record_controller.view.*

class RecorderControllerLayout : FrameLayout, View.OnClickListener {
    override fun onClick(v: View?) {
        recorderControllerCallback?.let {
            when (v?.id) {
                R.id.btn_torch -> it.torchClick(v)
                R.id.btn_switch_camera -> {
                    it.cameraClick(v)
                }
                R.id.btn_confirm -> {
                    it.confirmClick(v)
                }
                R.id.btn_delete_last_part -> {
                    it.deleteClick(v)
                }
                R.id.compose_record_btn -> {
                    it.recordClick(v)
                }
            }
        }
    }

    var recorderControllerCallback: RecorderControllerCallback? = null


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        View.inflate(context, R.layout.layout_video_record_controller, this);
        btn_torch.setOnClickListener(this)
        btn_switch_camera.setOnClickListener(this)
        btn_confirm.setOnClickListener(this)
        btn_delete_last_part.setOnClickListener(this)
        compose_record_btn.setOnClickListener(this)
        record_aspect_ratio.setOnCheckedChangeListener { group, checkedId ->
            recorderControllerCallback?.let {
                when (checkedId) {
                    R.id.record_ratio11 -> {
                        it.recordAspectRatio(Ratio.ratio11)
                    }
                    R.id.record_ratio169 -> {
                        it.recordAspectRatio(Ratio.ratio169)
                    }
                    R.id.record_ratio43 -> {
                        it.recordAspectRatio(Ratio.ratio43)
                    }
                }
            }
        }
    }

    var progress: Int = 0
        set(value) {
            recorder_progress.progress = value;
            progress_time.text = formattedTime(value.toLong(), false)
        }

    enum class Ratio {
        ratio11, ratio169, ratio43
    }

    interface RecorderControllerCallback {
        fun torchClick(v: View)
        fun cameraClick(v: View)
        fun confirmClick(v: View)
        fun deleteClick(v: View)
        fun recordClick(v: View)
        fun recordAspectRatio(ratio: Ratio)
    }
}