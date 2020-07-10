package net.measurementlab.ndt7impl.androidTest

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import net.measurementlab.ndt7.android.models.AppInfo
import net.measurementlab.ndt7.android.models.ClientResponse
import net.measurementlab.ndt7.android.utils.DataConverter

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("net.measurementlab.ndt7impl.android", appContext.packageName)
    }

    @Test
    fun canConvertToMbps() {
        val speed = DataConverter.convertToMbps(ClientResponse(AppInfo(10000, 800000.0), test = "client"))
        assertEquals(speed, "640.0")
    }
}
