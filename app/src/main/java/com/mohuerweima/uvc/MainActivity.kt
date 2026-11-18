package com.mohuerweima.uvc

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var viewFinder: PreviewView
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewFinder = findViewById(R.id.camera_view)
        statusText = findViewById(R.id.status_text)

        // 申请相机权限喵
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 10)
        } else {
            startCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            statusText.text = "【错误】未获得相机权限喵，请在手机设置中允许！"
        }
    }

    private fun startCamera() {
        statusText.text = "【搜索中】正在扫描系统摄像头..."
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                
                val preview = Preview.Builder().build()
                preview.setSurfaceProvider(viewFinder.surfaceProvider)

                // 默认使用手机后置摄像头
                var selector = CameraSelector.DEFAULT_BACK_CAMERA
                
                try {
                    // 寻找外接的 USB 采集卡相机喵！
                    val externalSelector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_EXTERNAL)
                        .build()
                        
                    if (cameraProvider.hasCamera(externalSelector)) {
                        selector = externalSelector
                        statusText.text = "【大成功】已找到外接采集卡！\n正在渲染画面..."
                    } else {
                        statusText.text = "【提示】系统未识别到采集卡，当前为手机后置相机画面喵。\n请检查相机是否开启取景喵！"
                    }
                } catch (e: Exception) {
                    statusText.text = "【提示】系统不支持外部相机接口，当前为手机后置..."
                }

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, selector, preview)
                
            } catch (e: Exception) {
                statusText.text = "【相机启动失败】\n${e.message}"
            }
        }, ContextCompat.getMainExecutor(this))
    }
}
