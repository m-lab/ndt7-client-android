# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


-keepparameternames
-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,
                SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-keep class net.measurementlab.ndt7.android.models.* { *; }
-keep class net.measurementlab.ndt7.android.NDTTest.TestType { *; }
-keep class net.measurementlab.ndt7.android.NDTTest {
    void startTest(net.measurementlab.ndt7.android.NDTTest.TestType);
}

-keep class net.measurementlab.ndt7.android.DataPublisher { *; }
-keep class net.measurementlab.ndt7.android.utils.DataConverter {
    java.lang.String convertToMbps(net.measurementlab.ndt7.android.models.ClientResponse);
}