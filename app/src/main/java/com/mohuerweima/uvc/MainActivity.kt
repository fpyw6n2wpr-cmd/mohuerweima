package com.mohuerweima.uvc

import android.content.Context
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.View
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
        
        // 实例化管家
        mCameraHelper = CameraHelper()
        
        // 使用反射，不管它改了什么名字，只要是初始化方法，统统抓出来喵！
        try {
            val clazz = mCameraHelper!!.javaClass
            
            // 尝试调用 init(Context)
            try {
                val initMethod = clazz.getMethod("init", Context::class.java)
                initMethod.invoke(mCameraHelper, this)
            } catch (e: Throwable) {}

            // 尝试调用 setSurface(SurfaceTexture)
            try {
                val surfaceMethod = clazz.getMethod("setSurface", SurfaceTexture::class.java)
                surfaceMethod.invoke(mCameraHelper, mCameraView.surfaceTexture)
            } catch (e: Throwable) {}

            // 尝试调用 setUSBMonitor(Context, View)
            try {
                val setUsbMethod = clazz.getMethod("setUSBMonitor", Context::class.java, View::class.java)
                setUsbMethod.invoke(mCameraHelper, this, mCameraView)
            } catch (e: Throwable) {}

        } catch (e: Throwable) {
            // 就算有错也绝不崩溃喵！
        }
    }

    override fun onStart() {
        super.onStart()
        try {
            val openMethod = mCameraHelper!!.javaClass.getMethod("openCamera")
            openMethod.invoke(mCameraHelper)
        } catch (e: Throwable) {}
    }

    override fun onStop() {
        super.onStop()
        try {
            val closeMethod = mCameraHelper!!.javaClass.getMethod("closeCamera")
            closeMethod.invoke(mCameraHelper)
        } catch (e: Throwable) {}
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            val releaseMethod = mCameraHelper!!.javaClass.getMethod("release")
            releaseMethod.invoke(mCameraHelper)
        } catch (e: Throwable) {}
    }
}
