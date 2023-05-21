package com.example.lightenhancer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.ImageView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowActivity

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, shadows = [ShadowLightEnhancer::class])
class ImageEditActivityTest {
    private lateinit var activity: ImageEditActivity
    private lateinit var shadowActivity: ShadowActivity

    @Before
    fun setUp() {
        activity = Robolectric.setupActivity(ImageEditActivity::class.java)
        shadowActivity = shadowOf(activity)
    }

    @Test
    fun `onCreate initializes views correctly`() {
        assertNotNull(activity.findViewById(R.id.enhanceImageButton))
        assertNotNull(activity.findViewById(R.id.saveImageButton))
        assertNotNull(activity.findViewById(R.id.imageSelectButton))
        assertNotNull(activity.findViewById(R.id.loadingPanel))
        assertNotNull(activity.findViewById(R.id.imageView))
    }

    @Test
    fun `clicking on imageSelectButton starts new activity for result`() {
        activity.findViewById<Button>(R.id.imageSelectButton).performClick()

        val startedActivityForResult = shadowActivity.nextStartedActivityForResult

        if (startedActivityForResult != null) {
            val startedIntent = startedActivityForResult.intent
            assertEquals(Intent.ACTION_PICK, startedIntent.action)
            assertEquals("image/*", startedIntent.type)
            assertEquals(ImageEditActivity.IMAGE_REQUEST_CODE, startedActivityForResult.requestCode)
        } else {
            fail("Activity not started")
        }
    }

    @Test
    fun `onActivityResult sets image to imageView and makes enhanceImageButton and saveImageButton visible`() {
        val uri = mock(Uri::class.java)
        val data = Intent().apply { this.data = uri }

        activity.onActivityResult(ImageEditActivity.IMAGE_REQUEST_CODE, Activity.RESULT_OK, data)

        assertEquals(uri, activity.findViewById<ImageView>(R.id.imageView).tag)
        assertEquals(View.VISIBLE, activity.findViewById<Button>(R.id.enhanceImageButton).visibility)
        assertEquals(View.VISIBLE, activity.findViewById<Button>(R.id.saveImageButton).visibility)
    }

}
