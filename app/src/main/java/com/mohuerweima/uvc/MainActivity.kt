package com.mohuerweima.uvc

import android.content.Context
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.herohan.uvcapp.CameraHelper
import com.serenegiant.widget.UVCCameraTextureView

class MainActivity : AppCompatActivity() {
    private lateinit var mCameraView: UVCCameraTextureView
    private var mCameraHelper: CameraHelper? = null
    private lateinit var debugText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 用代码画一个界面，包含一个黑底画面和红色日志文字
        val layout = FrameLayout(this)
        layout.setBackgroundColor(android.graphics.Color.BLACK)
        
        mCameraView = UVCCameraTextureView(this)
        layout.addView(mCameraView, FrameLayout.LayoutParams(-1, -1))
        
        debugText = TextView(this)
        debugText.setTextColor(android.graphics.Color.RED)
        debugText.textSize = 14f
        debugText.text = "初始化中...\n请确保相机已开启实时取景喵\n"
        layout.addView(debugText)
        
        setContentView(layout)

        mCameraHelper = CameraHelper()
        
        try {
            // 1. 尝试用反射初始化
            val initMethod = CameraHelper::class.java.getMethod("init", Context::class.java)
            initMethod.invoke(mCameraHelper, this)
            debugText.append("【成功】init 初始化完毕喵\n")
            
            // 2. 尝试用反射绑定画面
            val setSurfaceMethod = CameraHelper::class.java.getMethod("setSurface", android.graphics.SurfaceTexture::class.java)
            setSurfaceMethod.invoke(mCameraHelper, mCameraView.surfaceTexture)
            debugText.append("【成功】画面绑定完毕喵\n")
        } catch (e: Exception) {
            debugText.append("【报错】初始化失败：${e.message}\n")
        }
    }

    override fun onStart() {
        super.onStart()
        try {
            // 尝试用反射打开相机
            val openMethod = CameraHelper::class.java.getMethod("openCamera")
            openMethod.invoke(mCameraHelper)
            debugText.append("【成功】相机打开指令已发送喵\n等待USB授权...\n")
        } catch (e: Exception) {
            debugText.append("【报错】打开相机失败：${e.message}\n")
        }
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
