package com.mohuerweima.uvc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.herohan.uvcapp.CameraHelper
import com.herohan.uvcapp.UVCCameraTextureView

class MainActivity : AppCompatActivity() {
    private lateinit var mCameraView: UVCCameraTextureView
    private var mCameraHelper: CameraHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mCameraView = findViewById(R.id.camera_view)
        
        mCameraHelper = CameraHelper()
        mCameraHelper?.init(this)
        mCameraHelper?.setSurface(mCameraView.surfaceTexture)
    }

    override fun onStart() {
        super.onStart()
        mCameraHelper?.openCamera()
    }

    override fun onStop() {
        mCameraHelper?.closeCamera()
        super.onStop()
    }

    override fun onDestroy() {
        mCameraHelper?.release()
        mCameraHelper = null
        super.onDestroy()
    }
}
