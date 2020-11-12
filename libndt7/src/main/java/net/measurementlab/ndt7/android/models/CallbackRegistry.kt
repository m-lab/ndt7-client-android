package net.measurementlab.ndt7.android.models

import net.measurementlab.ndt7.android.NDTTest
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction3

data class CallbackRegistry(
    val speedtestProgressCbk: KFunction1<ClientResponse, Unit>,
    val measurementProgressCbk: KFunction1<Measurement, Unit>,
    val onFinishedCbk: KFunction3<ClientResponse, Throwable?, NDTTest.TestType, Unit>
)
