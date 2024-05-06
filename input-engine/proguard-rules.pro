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

# Contracts
-keep class de.tillhub.inputengine.contract.** { *; }

# Data Inputs
-keep class de.tillhub.inputengine.data.MoneyIO {
#    public static de.tillhub.inputengine.data.MoneyIO of(java.lang.Number, java.util.Currency);
}
-keep class de.tillhub.inputengine.data.PercentIO {
    *;
}
-keep class de.tillhub.inputengine.data.QuantityIO {
    *;
}

# Params
-keepclassmembers class de.tillhub.inputengine.data.MoneyParam { *; }
-keepclassmembers class de.tillhub.inputengine.data.PercentageParam { *; }
-keepclassmembers class de.tillhub.inputengine.data.QuantityParam { *; }
-keepclassmembers class de.tillhub.inputengine.data.StringParam { *; }

# Breaking changes with AGP 8.0
# R8 upgrade documentation
-dontwarn java.lang.invoke.StringConcatFactory