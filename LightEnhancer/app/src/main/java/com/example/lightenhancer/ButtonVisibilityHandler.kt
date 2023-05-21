package com.example.lightenhancer

import android.os.Handler
import android.os.Looper
import android.view.View

class ButtonVisibilityHandler(private val buttons: List<View>, private val rootView: View, private val visibilityPeriodMs: Long) {

    private val handler: Handler = Handler(Looper.getMainLooper())
    private val hideButtonsRunnable: Runnable = Runnable {
        buttons.forEach { button ->
            button.visibility = View.INVISIBLE
        }
    }

    fun start() {
        rootView.setOnClickListener {
            showAndHideButtons()
        }
    }

    private fun showAndHideButtons() {
        buttons.forEach { button ->
            button.visibility = View.VISIBLE
        }

        handler.removeCallbacks(hideButtonsRunnable)
        handler.postDelayed(hideButtonsRunnable, visibilityPeriodMs)
    }
}