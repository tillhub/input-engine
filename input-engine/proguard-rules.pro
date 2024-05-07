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

-keeppackagenames de.tillhub.inputengine.data.**
-keeppackagenames de.tillhub.inputengine.contract.**

# Contracts
-keep class de.tillhub.inputengine.contract.** { *; }

# Data Inputs
-keep class de.tillhub.inputengine.data.MoneyIO { *; }
-keep class de.tillhub.inputengine.data.PercentIO { *; }
-keep class de.tillhub.inputengine.data.QuantityIO { *; }

# Params
-keep class de.tillhub.inputengine.data.MoneyParam { *; }
-keep class de.tillhub.inputengine.data.MoneyParam$* { *; }

-keep class de.tillhub.inputengine.data.PercentageParam { *; }
-keep class de.tillhub.inputengine.data.PercentageParam$* { *; }

-keep class de.tillhub.inputengine.data.QuantityParam { *; }
-keep class de.tillhub.inputengine.data.QuantityParam$* { *; }

-keep class de.tillhub.inputengine.data.StringParam { *; }
-keep class de.tillhub.inputengine.data.StringParam$* { *; }

# Breaking changes with AGP 8.0
# R8 upgrade documentation
-dontwarn java.lang.invoke.StringConcatFactory