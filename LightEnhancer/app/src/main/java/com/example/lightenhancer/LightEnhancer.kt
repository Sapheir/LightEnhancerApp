package com.example.lightenhancer

import android.content.res.AssetManager
import android.graphics.Bitmap

class LightEnhancer {
    var enhancerAddress = 0L

    external fun initEnhancer(assetManager: AssetManager?, modelPath: String): Long
    external fun destroyEnhancer(enhancerAddress: Long)
    external fun enhance(bitmapTarget: Bitmap, modelResolution: Int, enhancerAddress: Long)
    external fun enhanceFullResolution(bitmapTarget: Bitmap, modelResolution: Int, enhancerAddress: Long)
}