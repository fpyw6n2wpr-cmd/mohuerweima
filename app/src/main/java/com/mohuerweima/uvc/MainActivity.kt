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

    private val usbPermissionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_USB_PERMISSION) {
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    statusText.text = "【授权成功】\n正在以 MJPEG 格式启动..."
                    openCameraSafely()
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

        val filter = IntentFilter(ACTION_USB_PERMISSION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(usbPermissionReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(usbPermissionReceiver, filter)
        }

        try {
            val builder = CameraClient.Builder(this)
            // 反射绑定画面
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
            
            usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
            requestUsbPermission()
            
        } catch (e: Throwable) {
            statusText.text = "【初始化失败】\n${e.message}"
        }
    }

    private fun openCameraSafely() {
        try {
            // 终极反射：尝试寻找并实例化 CameraRequest，强制指定 MJPEG 格式
            val client = cameraClient ?: return
            var opened = false

            // 遍历所有 openCamera 方法
            for (method in client.javaClass.methods) {
                if (method.name == "openCamera" && method.parameterCount == 1) {
                    val paramType = method.parameterTypes[0]
                    // 如果参数是 IAspectRatio 或者是 CameraRequest
                    try {
                        // 尝试实例化一个 CameraRequest
                        val requestClass = Class.forName("com.jiangdg.ausbc.camera.bean.CameraRequest")
                        val request = requestClass.newInstance()
                        
                        // 设置格式为 MJPEG (对应枚举值)
                        try {
                            val formatField = requestClass.getField("previewFormat")
                            val formatEnumClass = formatField.type
                            // 寻找 MJPEG 枚举值
                            for (enumConstant in formatEnumClass.enumConstants) {
                                if (enumConstant.toString().contains("MJPEG", true)) {
                                    formatField.set(request, enumConstant)
                                    break
                                }
                            }
                        } catch (e: Throwable) { }

                        if (paramType.isInstance(request) || paramType.name.contains("IAspectRatio")) {
                            method.invoke(client, request)
                            opened = true
                            statusText.text = "【大成功】已强制 MJPEG 启动！"
                            break
                        }
                    } catch (e: Throwable) { }
                }
            }

            // 如果没找到CameraRequest，或者失败了，退回默认参数
            if (!opened) {
                try {
                    val defaultMethod = client.javaClass.getMethod("openCamera", Array<Any>::class.java)
                    defaultMethod.invoke(client, arrayOfNulls<Any>(0)) // 错误传参防范
                } catch (e: Throwable) { }
            }
        } catch (e: Throwable) {
            statusText.text = "【画面启动失败】\n${e.message}"
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
                    openCameraSafely()
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
