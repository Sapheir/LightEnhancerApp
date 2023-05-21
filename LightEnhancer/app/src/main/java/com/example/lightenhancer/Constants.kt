package com.example.lightenhancer

import android.Manifest

object Constants {
    const val TAG = "cameraX"
    const val REQUEST_CODE_PERMISSIONS = 123
    val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    val MODEL_PATHS = arrayOf("zero_dce_lite_128x128.tflite", "zero_dce_lite_256x256.tflite", "zero_dce_lite_512x512.tflite")
    val MODEL_SIZES = arrayOf(128, 256, 512)
}