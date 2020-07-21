package net.measurementlab.ndt7.android

data class Settings(var hostname: String = "",
                    var port: Int = 0,
                    var skipTlsCertificateVerification: Boolean = false,
                    var subTest: String = "") //ToDo for single client
