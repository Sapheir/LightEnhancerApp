package com.example.lightenhancer

import android.app.Activity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.lightenhancer.databinding.ActivityMainBinding

class RealtimeAnalyzer(private val activity: Activity, private val binding: ActivityMainBinding, val lightEnhancer: LightEnhancer) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        if (!binding.settingsPopup.enhanceSwitch.isChecked) {
            image.close()
            return
        }
        if (lightEnhancer.enhancerAddress == 0L) {
            lightEnhancer.enhancerAddress = lightEnhancer.initEnhancer(activity.assets, Constants.MODEL_PATHS[binding.settingsPopup.modelsSpinner.selectedItemPosition])
        }
        var bitmap = binding.camera.bitmap ?: return
        lightEnhancer.enhance(bitmap, Constants.MODEL_SIZES[binding.settingsPopup.modelsSpinner.selectedItemPosition], lightEnhancer.enhancerAddress)
        activity.runOnUiThread {
            binding.enhancedView.setImageBitmap(bitmap)
        }
        image.close()
    }
}