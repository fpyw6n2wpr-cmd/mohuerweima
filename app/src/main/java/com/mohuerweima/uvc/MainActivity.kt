package com.mohuerweima.uvc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.herohan.uvcapp.CameraHelper
import com.serenegiant.widget.UVCCameraTextureView

class MainActivity : AppCompatActivity() {
    private lateinit var mCameraView: UVCCameraTextureView
    private lateinit var mCameraHelper: CameraHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mCameraView = findViewById(R.id.camera_view)
        
        // 这才是真身！没有 init，也没有 setSurface，只有这一句喵！
        mCameraHelper = CameraHelper()
        mCameraHelper.setUSBMonitor(this, mCameraView)
    }

    override fun onStart() {
        super.onStart()
        mCameraHelper.openCamera()
    }

    override fun onStop() {
        super.onStop()
        mCameraHelper.closeCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCameraHelper.release()
    }
}
