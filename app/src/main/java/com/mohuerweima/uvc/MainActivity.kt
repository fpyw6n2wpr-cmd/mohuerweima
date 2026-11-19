package com.mohuerweima.uvc

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jiangdg.ausbc.CameraClient
import com.jiangdg.ausbc.widget.AspectRatioTextureView

class MainActivity : AppCompatActivity() {
    private lateinit var cameraView: AspectRatioTextureView
    private lateinit var statusText: TextView
    private var cameraClient: CameraClient? = null
    private lateinit var usbManager: UsbManager

    // 监听 USB 授权结果
    private val usbPermissionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_USB_PERMISSION) {
                val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                } else {
                    intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                }
                
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    statusText.text = "【授权成功】\n正在启动采集卡画面..."
                    try {
                        // 尝试安全打开相机
                        cameraClient?.openCamera(device)
                    } catch (e: Throwable) {
                        statusText.text = "【画面启动失败】\n底层不兼容：${e.message}"
                    }
                } else {
                    statusText.text = "【授权被拒绝】\n请重新插拔采集卡并在弹窗点允许！"
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraView = findViewById(R.id.camera_view)
        statusText = findViewById(R.id.status_text)
        statusText.text = "【初始化中】\n正在准备底层环境..."

        // 注册授权广播
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(usbPermissionReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(usbPermissionReceiver, filter)
        }

        try {
            // 创建 CameraClient
            val builder = CameraClient.Builder(this)
            // 利用反射强行绑定画面
            for (method in builder.javaClass.methods) {
                if (method.name.lowercase().contains("view") && method.parameterCount == 1) {
                    try {
                        if (method.parameterTypes[0].isInstance(cameraView)) {
                            method.invoke(builder, cameraView)
                            break
                        }
                    } catch (e: Throwable) { }
                }
            }
            cameraClient = builder.build()
            
            // 开始主动寻找 USB 设备并请求权限！
            usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
            requestUsbPermission()
            
        } catch (e: Throwable) {
            statusText.text = "【初始化失败】\n${e.message}"
        }
    }

    private fun requestUsbPermission() {
        val deviceList = usbManager.deviceList
        if (deviceList.isEmpty()) {
            statusText.text = "【等待设备】\n未发现USB设备，请插上采集卡喵..."
            return
        }

        for (device in deviceList.values) {
            if (device.deviceClass == 239 || device.deviceClass == 14) { 
                if (usbManager.hasPermission(device)) {
                    statusText.text = "【已授权】\n正在启动画面..."
                    try {
                        // 直接传 device，库内部会自动处理比例
                        cameraClient?.openCamera(device)
                    } catch (e: Throwable) {
                        statusText.text = "【画面启动失败】\n底层崩溃：${e.message}"
                    }
                } else {
                    statusText.text = "【请求授权】\n请在手机弹窗中点击“允许”喵！"
                    
                    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        PendingIntent.FLAG_MUTABLE
                    } else {
                        0
                    }
                    val permissionIntent = PendingIntent.getBroadcast(
                        this, 0, Intent(ACTION_USB_PERMISSION), flags
                    )
                    usbManager.requestPermission(device, permissionIntent)
                }
                return
            }
        }
        statusText.text = "【错误】找到了USB设备，但不是采集卡喵。"
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(usbPermissionReceiver)
        } catch (e: Exception) { }
        try {
            cameraClient?.closeCamera()
        } catch (e: Throwable) { }
    }

    companion object {
        private const val ACTION_USB_PERMISSION = "com.mohuerweima.uvc.USB_PERMISSION"
    }
}
