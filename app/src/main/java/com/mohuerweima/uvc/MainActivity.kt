package com.mohuerweima.uvc

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jiangdg.ausbc.CameraClient
import com.jiangdg.ausbc.widget.AspectRatioTextureView

class MainActivity : AppCompatActivity() {
    private lateinit var cameraView: AspectRatioTextureView
    private lateinit var statusText: TextView
    private var cameraClient: CameraClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraView = findViewById(R.id.camera_view)
        statusText = findViewById(R.id.status_text)
        statusText.text = "【初始化中】\n准备连接设备..."

        try {
            val builder = CameraClient.Builder(this)
            
            // 使用反射，把 Builder 里所有接受一个参数且名字包含 CameraView 的方法全试一遍喵！
            var isSet = false
            for (method in builder.javaClass.methods) {
                if (method.parameterCount == 1 && method.name.contains("CameraView", ignoreCase = true)) {
                    try {
                        method.invoke(builder, cameraView)
                        isSet = true
                        break
                    } catch (e: Throwable) { }
                }
            }
            
            // 如果没找到带 CameraView 名字的，就试试 fluent api (直接叫 cameraView 的)
            if (!isSet) {
                try {
                    val m = builder.javaClass.getMethod("cameraView", com.jiangdg.ausbc.ICameraView::class.java)
                    m.invoke(builder, cameraView)
                } catch (e: Throwable) { }
            }

            cameraClient = builder.build()
            statusText.text = "【初始化成功】\n等待插拔采集卡喵..."
        } catch (e: Exception) {
            statusText.text = "【初始化报错】\n${e.message}"
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            cameraClient?.openCamera(null)
            statusText.text = "【搜索中】\n正在寻找 USB 相机，请确保已插入..."
        } catch (e: Exception) {
            statusText.text = "【打开报错】\n${e.message}"
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            cameraClient?.closeCamera()
        } catch (e: Exception) {
            // 忽略
        }
    }
}
