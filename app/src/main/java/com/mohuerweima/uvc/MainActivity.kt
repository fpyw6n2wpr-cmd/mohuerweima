package com.mohuerweima.uvc

import android.hardware.usb.UsbDevice
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.herohan.uvcapp.CameraHelper
import com.serenegiant.widget.UVCCameraTextureView

class MainActivity : AppCompatActivity() {
    private lateinit var mCameraView: UVCCameraTextureView
    private var mCameraHelper: CameraHelper? = null
    private var debugText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 动态创建一个文字层，用来显示错误信息喵！
        val layout = android.widget.FrameLayout(this)
        layout.setBackgroundColor(android.graphics.Color.BLACK)
        
        mCameraView = UVCCameraTextureView(this)
        layout.addView(mCameraView, android.widget.FrameLayout.LayoutParams(-1, -1))
        
        debugText = TextView(this)
        debugText?.setTextColor(android.graphics.Color.RED)
        debugText?.text = "初始化中...\n请确保相机已开启实时取景\n插拔一下采集卡试试喵"
        layout.addView(debugText)
        
        setContentView(layout)

        mCameraHelper = CameraHelper()
        
        try {
            // 绑定USB监听
            mCameraHelper?.setUSBMonitor(this, object : com.herohan.uvcapp.CameraHelper.OnDeviceConnectListener {
                override fun onAttach(device: UsbDevice) {
                    runOnUiThread { debugText?.text = "检测到采集卡: ${device.deviceName}" }
                    // 发现设备后直接打开
                    mCameraHelper?.openCamera(device)
                }

                override fun onDetach(device: UsbDevice) {
                    runOnUiThread { debugText?.text = "采集卡已拔出" }
                }

                override fun onCancel(device: UsbDevice) {
                    runOnUiThread { debugText?.text = "采集卡连接取消" }
                }

                override fun onConnect(device: UsbDevice) {
                    runOnUiThread { debugText?.text = "已授权，准备获取画面..." }
                }

                override fun onDisconnect(device: UsbDevice) {
                    runOnUiThread { debugText?.text = "连接断开" }
                }
            })
            
            // 告诉相机助手把画面渲染到我们的 View 上
            mCameraHelper?.setSurface(mCameraView.surfaceTexture)
            
        } catch (e: Exception) {
            debugText?.text = "代码报错啦喵：\n${e.message}"
        }
    }

    override fun onStart() {
        super.onStart()
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
