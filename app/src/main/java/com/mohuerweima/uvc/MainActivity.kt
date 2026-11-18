package com.mohuerweima.uvc

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jiangdg.ausbc.CameraClient
import com.jiangdg.ausbc.widget.AspectRatioTextureView

class MainActivity : AppCompatActivity() {
    private lateinit var cameraView: AspectRatioTextureView
    private lateinit var statusText: TextView
    private lateinit var cameraClient: CameraClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraView = findViewById(R.id.camera_view)
        statusText = findViewById(R.id.status_text)
        statusText.text = "【初始化中】\n准备连接设备..."

        try {
            // 使用官方的 Builder 模式构建，绝对不会再报 internal 错误喵！
            cameraClient = CameraClient.Builder(this)
                .setCameraView(cameraView)
                .build()
            statusText.text = "【初始化成功】\n等待插拔采集卡喵..."
        } catch (e: Exception) {
            statusText.text = "【初始化报错】\n${e.message}"
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            // 传入 null 自动寻找并打开 USB 设备
            cameraClient.openCamera(null)
            statusText.text = "【搜索中】\n正在寻找 USB 相机，请确保已插入..."
        } catch (e: Exception) {
            statusText.text = "【打开报错】\n${e.message}"
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            cameraClient.closeCamera()
        } catch (e: Exception) {
            // 忽略关闭时的异常
        }
    }
}
