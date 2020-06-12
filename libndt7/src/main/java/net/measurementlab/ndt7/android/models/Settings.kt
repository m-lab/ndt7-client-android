@file:JvmName("Settings")
package net.measurementlab.ndt7.android.models

data class Settings(var hostname: String = "",
                    var port: Int = 0,
                    var skipTlsCertificateVerification: Boolean = false)