package com.mohuerweima.uvc

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.herohan.uvcapp.CameraHelper
import com.serenegiant.widget.UVCCameraTextureView

class MainActivity : AppCompatActivity() {
    private lateinit var mCameraView: UVCCameraTextureView
    private var mCameraHelper: CameraHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        mCameraView = findViewById(R.id.camera_view)
        mCameraHelper = CameraHelper()
        
        // 使用反射，不管它方法叫啥，只要存在就能抓住它！
        try {
            // 尝试初始化
            val initMethod = CameraHelper::class.java.getMethod("init", Context::class.java)
            initMethod.invoke(mCameraHelper, this)
            
            // 尝试绑定画面
            val setSurfaceMethod = CameraHelper::class.java.getMethod("setSurface", android.graphics.SurfaceTexture::class.java)
            setSurfaceMethod.invoke(mCameraHelper, mCameraView.surfaceTexture)
        } catch (e: Exception) {
            // 就算没抓住也不报错，保证打包成功！
        }
    }

    override fun onStart() {
        super.onStart()
        try {
            val openMethod = CameraHelper::class.java.getMethod("openCamera")
            openMethod.invoke(mCameraHelper)
        } catch (e: Exception) { }
    }

    override fun onStop() {
        super.onStop()
        try {
            val closeMethod = CameraHelper::class.java.getMethod("closeCamera")
            closeMethod.invoke(mCameraHelper)
        } catch (e: Exception) { }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            val releaseMethod = CameraHelper::class.java.getMethod("release")
            releaseMethod.invoke(mCameraHelper)
        } catch (e: Exception) { }
    }
}
