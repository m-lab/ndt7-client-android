package net.measurementlab.ndt7.androidTest

import net.measurementlab.ndt7.android.utils.NDT7Constants
import net.measurementlab.ndt7.android.utils.PayloadTransformer
import okio.ByteString
import org.junit.Assert
import org.junit.Test

class PayloadTransformerTest {

    @Test
    fun test_dynamic_tuning_doesnt_change_if_max_size() {
        val oldBytes = ByteString.of(*ByteArray(NDT7Constants.MAX_MESSAGE_SIZE))/* (1<<13) */

        val newBytes = PayloadTransformer.performDynamicTuning(oldBytes, 0, 0.0)

        Assert.assertTrue(newBytes.size == oldBytes.size)
    }

    @Test
    fun test_dynamic_tuning_doesnt_change_if_queue_is_saturated() {
        val oldBytes = ByteString.of(*ByteArray(1000))/* (1<<13) */

        val newBytes = PayloadTransformer.performDynamicTuning(oldBytes, 0, 16000.0)

        Assert.assertTrue(newBytes.size == oldBytes.size)
    }
    @Test
    fun test_dynamic_tuning_will_double() {
        val oldBytes = ByteString.of(*ByteArray(10))/* (1<<13) */

        val newBytes = PayloadTransformer.performDynamicTuning(oldBytes, 10000, 16000.0)

        Assert.assertTrue(newBytes.size == oldBytes.size * 2)
    }
}
