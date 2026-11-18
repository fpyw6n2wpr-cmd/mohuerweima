package com.mohuerweima.uvc

import android.hardware.usb.UsbDevice
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.serenegiant.widget.UVCCameraTextureView
import com.herohan.uvcapp.UVCCameraHelper

class MainActivity : AppCompatActivity() {
    private lateinit var mCameraView: UVCCameraTextureView
    private lateinit var mCameraHelper: UVCCameraHelper
    private lateinit var debugText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 搭建界面：画面 + 红色调试文字
        val layout = FrameLayout(this)
        layout.setBackgroundColor(android.graphics.Color.BLACK)
        
        mCameraView = UVCCameraTextureView(this)
        layout.addView(mCameraView, FrameLayout.LayoutParams(-1, -1))
        
        debugText = TextView(this)
        debugText.setTextColor(android.graphics.Color.RED)
        debugText.textSize = 14f
        debugText.text = "初始化中...\n"
        layout.addView(debugText)
        
        setContentView(layout)

        // 使用真正的单例大管家
        mCameraHelper = UVCCameraHelper.getInstance()
        
        try {
            // 初始化 USB 监听
            mCameraHelper.initUSBMonitor(this, mCameraView, object : UVCCameraHelper.OnDeviceConnectListener {
                override fun onAttach(device: UsbDevice) {
                    runOnUiThread { debugText.append("检测到采集卡：${device.deviceName}\n正在尝试连接...\n") }
                    // 发现设备后立刻连接
                    mCameraHelper.connectDevice(device)
                }

                override fun onDetach(device: UsbDevice) {
                    runOnUiThread { debugText.append("采集卡被拔出了喵\n") }
                }

                override fun onCancel(device: UsbDevice) {
                    runOnUiThread { debugText.append("USB授权被取消了喵\n") }
                }

                override fun onConnect(device: UsbDevice) {
                    runOnUiThread { debugText.append("USB授权成功！正在获取画面...\n") }
                }

                override fun onDisconnect(device: UsbDevice) {
                    runOnUiThread { debugText.append("USB连接断开\n") }
                }
            })
            debugText.append("初始化成功喵！\n请确保相机开启了实时取景\n等待插入采集卡...\n")
        } catch (e: Exception) {
            debugText.append("初始化报错喵：${e.message}\n")
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        if (::mCameraHelper.isInitialized) {
            mCameraHelper.closeCamera()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mCameraHelper.isInitialized) {
            mCameraHelper.release()
        }
    }
}
