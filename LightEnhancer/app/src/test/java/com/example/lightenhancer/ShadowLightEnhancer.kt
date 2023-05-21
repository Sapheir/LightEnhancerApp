package com.example.lightenhancer

import android.content.res.AssetManager
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

@Implements(LightEnhancer::class)
open class ShadowLightEnhancer {
    @Implementation
    protected fun initEnhancer(assetManager: AssetManager?, string: String?): Long {
        // Provide your mock behavior here
        return 0
    }
}