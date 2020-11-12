
package net.measurementlab.ndt7.androidTest

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import net.measurementlab.ndt7.android.NDTTest
import net.measurementlab.ndt7.android.models.ClientResponse
import net.measurementlab.ndt7.android.models.Measurement
import net.measurementlab.ndt7.android.utils.DataConverter
import org.junit.Assert
import org.junit.Test

class NDT7HealthTest {

    @Test
    fun test_networkCallable_can_perform_ndt7_speed_test() {
        val client = NDTTestImpl()
        client.startTest(NDTTest.TestType.DOWNLOAD_AND_UPLOAD)

        // let speed test run for a bit
        Thread.sleep(25000L)

        Assert.assertTrue(client.dataWrapper.downloadSpeedAppNDT7.size > 5)
        Assert.assertTrue(client.dataWrapper.downloadSpeedNDT7.size > 5)
        Assert.assertTrue(client.dataWrapper.uploadSpeedAppNDT7.size > 5)
        Assert.assertTrue(client.dataWrapper.uploadSpeedNDT7.size > 5)
        Assert.assertTrue(client.dataWrapper.uploadFinished)
        Assert.assertTrue(client.dataWrapper.downloadFinished)
    }

    @Test
    fun test_networkCallable_can_continue_speed_test_despite_error() {

        val client = NDTTestImpl()
        client.startTest(NDTTest.TestType.DOWNLOAD_AND_UPLOAD)
        client.onFinished(null, Throwable("test", null), NDTTest.TestType.DOWNLOAD)
        // let speed test run for a bit
        Thread.sleep(25000)

        Assert.assertTrue(client.dataWrapper.downloadSpeedAppNDT7.size > 5)
        Assert.assertTrue(client.dataWrapper.downloadSpeedNDT7.size > 5)
        Assert.assertTrue(client.dataWrapper.uploadSpeedAppNDT7.size > 5)
        Assert.assertTrue(client.dataWrapper.uploadSpeedNDT7.size > 5)
        Assert.assertTrue(client.dataWrapper.uploadFinished)
        Assert.assertTrue(client.dataWrapper.downloadFinished)
    }

    private class NDTTestImpl : NDTTest() {
        var dataWrapper: NDTWrapper = NDTWrapper()
        private var gson: Gson = Gson()

        override fun onMeasurementDownloadProgress(measurement: Measurement) {
            super.onMeasurementDownloadProgress(measurement)
            dataWrapper.downloadSpeedNDT7.add(gson.toJson(measurement))
        }

        override fun onMeasurementUploadProgress(measurement: Measurement) {
            super.onMeasurementUploadProgress(measurement)
            dataWrapper.uploadSpeedNDT7.add(gson.toJson(measurement))
        }

        override fun onDownloadProgress(clientResponse: ClientResponse) {
            super.onDownloadProgress(clientResponse)
            dataWrapper.downloadSpeedAppNDT7.add(gson.toJson(clientResponse))
        }

        override fun onUploadProgress(clientResponse: ClientResponse) {
            super.onUploadProgress(clientResponse)
            dataWrapper.uploadSpeedAppNDT7.add(gson.toJson(clientResponse))
        }

        override fun onFinished(clientResponse: ClientResponse?, error: Throwable?, testType: TestType) {
            error?.let {
                error.printStackTrace()
            }

            if (testType === TestType.UPLOAD) {
                dataWrapper.uploadFinished = true
            }
            if (testType === TestType.DOWNLOAD) {
                dataWrapper.downloadFinished = true
            }

            clientResponse?.let {
                val speed = DataConverter.convertToMbps(it)
                // do something with the speed
            }
        }
    }

    data class NDTWrapper(
        @SerializedName("downloadSpeedNDT7") var downloadSpeedNDT7: ArrayList<String> = arrayListOf(),
        @SerializedName("uploadSpeedNDT7") var uploadSpeedNDT7: ArrayList<String> = arrayListOf(),
        @SerializedName("downloadSpeedAppNDT7") var downloadSpeedAppNDT7: ArrayList<String> = arrayListOf(),
        @SerializedName("uploadSpeedAppNDT7") var uploadSpeedAppNDT7: ArrayList<String> = arrayListOf(),
        @SerializedName("downloadFinished") var downloadFinished: Boolean = false,
        @SerializedName("uploadFinished") var uploadFinished: Boolean = false

    )
}
