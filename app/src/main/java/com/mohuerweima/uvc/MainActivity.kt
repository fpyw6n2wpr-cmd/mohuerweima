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

        // 这次不用 Builder 的 setCameraView 了，直接用这个！
        cameraClient = CameraClient(this, cameraView)
        statusText.text = "【初始化成功】\n等待插拔采集卡喵..."
    }

    override fun onResume() {
        super.onResume()
        try {
            // 这一步是灵魂！只要插上 USB 就能读到！
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
        } catch (e: Exception) { }
    }
}
