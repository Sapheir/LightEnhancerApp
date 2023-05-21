package com.example.lightenhancer

import android.view.View
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowActivity


@RunWith(RobolectricTestRunner::class)
class MainActivityTest {
    private lateinit var activity: MainActivity

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(MainActivity::class.java)
            .create()
            .resume()
            .get()
    }

    @Test
    fun testPermissionHandler_allPermissionsGranted() {
        val permissionHandler = PermissionHandler(activity)
        assertFalse(permissionHandler.allPermissionsGranted())
    }

    @Test
    fun testPermissionHandler_requestPermissions() {
        val permissionHandler = PermissionHandler(activity)
        permissionHandler.requestPermissions()
        val expectedIntent = shadowOf(activity).nextStartedActivityForResult
        assertEquals(Constants.REQUEST_CODE_PERMISSIONS, expectedIntent.requestCode)
    }

    @Test
    fun testButtonVisibilityHandler_start() {
        val mockView = mock<View>()
        val buttonVisibilityHandler = ButtonVisibilityHandler(listOf(mockView), mockView, 5000)

        buttonVisibilityHandler.start()
        verify(mockView).setOnClickListener(any())
    }

    @Test
    fun testButtonVisibilityHandler_showAndHideButtons() {
        val mockView = mock<View>()
        val buttonVisibilityHandler = ButtonVisibilityHandler(listOf(mockView), mockView, 5000)
        val method = ButtonVisibilityHandler::class.java.getDeclaredMethod("showAndHideButtons")
        method.isAccessible = true

        method.invoke(buttonVisibilityHandler)
        verify(mockView).visibility = View.VISIBLE
    }
}