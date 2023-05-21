package com.example.lightenhancer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lightenhancer.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var cameraHandler: CameraHandler
    private lateinit var realtimeAnalyzer: RealtimeAnalyzer
    private lateinit var buttonVisibilityHandler: ButtonVisibilityHandler

    var permissionHandler = PermissionHandler(this)
    var lightEnhancer = LightEnhancer()


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        realtimeAnalyzer = RealtimeAnalyzer(this, binding, lightEnhancer)
        cameraHandler = CameraHandler(this, binding, realtimeAnalyzer)

        buttonVisibilityHandler = ButtonVisibilityHandler(
            listOf(binding.captureButton, binding.settingsButton, binding.editButton),
            binding.root,
            5000
        )

        buttonVisibilityHandler.start()

        binding.enhancedView.visibility = View.GONE
        binding.settingsPopup.enhanceSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.enhancedView.visibility = View.VISIBLE
                binding.camera.visibility = View.GONE
            } else {
                binding.enhancedView.visibility = View.GONE
                binding.camera.visibility = View.VISIBLE
            }
        }

        binding.settingsButton.setOnClickListener {
            toggleSettingsPopup()
        }

        binding.settingsPopup.closeSettingsButton.setOnClickListener {
            toggleSettingsPopup()
        }

        binding.settingsPopup.torchSwitch.setOnClickListener {
            cameraHandler.toggleTorchLight()
        }

        binding.settingsPopup.modelsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                lightEnhancer.enhancerAddress = lightEnhancer.initEnhancer(assets, Constants.MODEL_PATHS[id.toInt()])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        binding.editButton.setOnClickListener {
            val intent = Intent(this, ImageEditActivity::class.java)
            startActivity(intent)
        }

        binding.captureButton.setOnClickListener {
            cameraHandler.captureCamera()
        }

        if (permissionHandler.allPermissionsGranted()) {
            cameraHandler.startCamera()
        } else {
            permissionHandler.requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.REQUEST_CODE_PERMISSIONS) {
            if (permissionHandler.allPermissionsGranted()) {
                cameraHandler.startCamera()
            } else {
                Toast.makeText(
                    this,
                    "No permission",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    fun toggleSettingsPopup() {
        if (binding.settingsPopup.root.visibility == View.GONE) {
            binding.settingsPopup.root.visibility = View.VISIBLE
        } else {
            binding.settingsPopup.root.visibility = View.GONE
        }
    }

    companion object {
        init {
            if (!isJUnitTest()) {
                System.loadLibrary("lightenhancer")
            }
        }

        private fun isJUnitTest(): Boolean {
            return try {
                Class.forName("org.junit.Test")
                true
            } catch (ex: ClassNotFoundException) {
                false
            }
        }
    }
}