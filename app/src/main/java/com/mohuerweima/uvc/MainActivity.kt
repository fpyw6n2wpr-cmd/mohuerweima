package com.mohuerweima.uvc

import android.hardware.usb.UsbDevice
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jiangdg.ausbc.CameraClient
import com.jiangdg.ausbc.callback.ICameraClientCallback
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

        // 初始化相机客户端，并加入所有的监听回调喵！
        cameraClient = CameraClient(this, cameraView, object : ICameraClientCallback {
            override fun onAttach(device: UsbDevice?) {
                runOnUiThread { 
                    statusText.text = "【检测到设备】\n名称: ${device?.productName ?: "未知"}\n正在请求授权..." 
                }
            }

            override fun onDetach(device: UsbDevice?) {
                runOnUiThread { statusText.text = "【设备已拔出】请重新插入采集卡喵" }
            }

            override fun onCancel(device: UsbDevice?) {
                runOnUiThread { statusText.text = "【授权被拒绝】请拔下采集卡重新插入并点允许喵！" }
            }

            override fun onConnect(device: UsbDevice?) {
                runOnUiThread { statusText.text = "【授权成功】正在准备输出画面喵！" }
            }

            override fun onDisConnect(device: UsbDevice?) {
                runOnUiThread { statusText.text = "【连接已断开】" }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        try {
            // 传入 null 表示自动寻找第一个 UVC 设备并打开
            cameraClient.openCamera(null)
            statusText.text = "【搜索中】正在寻找 USB 相机设备..."
        } catch (e: Exception) {
            statusText.text = "【发生错误】初始化失败：\n${e.message}"
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            // 切出软件或者暂停时关闭相机，防止占用
            cameraClient.closeCamera()
        } catch (e: Exception) {
            // 忽略关闭时的异常
        }
    }
}
