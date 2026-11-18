package com.mohuerweima.uvc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.herohan.uvcapp.CameraHelper
// 注意这里！包名变了喵！
import com.serenegiant.widget.UVCCameraTextureView

class MainActivity : AppCompatActivity() {
    private lateinit var mCameraView: UVCCameraTextureView
    private var mCameraHelper: CameraHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        mCameraView = findViewById(R.id.camera_view)
        
        mCameraHelper = CameraHelper()
        // 这里不带参数喵！
        mCameraHelper?.init(this)
    }

    override fun onStart() {
        super.onStart()
        mCameraHelper?.openCamera()
        mCameraHelper?.addSurface(mCameraView.surfaceTexture, false)
    }

    override fun onStop() {
        mCameraHelper?.removeSurface(mCameraView.surfaceTexture)
        mCameraHelper?.closeCamera()
        super.onStop()
    }

    override fun onDestroy() {
        mCameraHelper?.release()
        mCameraHelper = null
        super.onDestroy()
    }
}
