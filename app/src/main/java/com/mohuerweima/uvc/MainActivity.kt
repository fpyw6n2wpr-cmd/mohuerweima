package com.mohuerweima.uvc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.herohan.uvcapp.CameraHelper
import com.herohan.uvcapp.UVCCameraTextureView

class MainActivity : AppCompatActivity() {
    private lateinit var cameraView: UVCCameraTextureView
    private lateinit var cameraHelper: CameraHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cameraView = findViewById(R.id.camera_view)
        cameraHelper = CameraHelper()
        cameraHelper.init(this)
        cameraHelper.setUSBMonitor(this, cameraView)
    }

    override fun onStart() {
        super.onStart()
        cameraHelper.openCamera()
    }

    override fun onStop() {
        cameraHelper.closeCamera()
        super.onStop()
    }

    override fun onDestroy() {
        cameraHelper.release()
        super.onDestroy()
    }
}
