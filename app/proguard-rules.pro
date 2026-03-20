# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Preserve line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep @Keep annotated classes
-keep @androidx.annotation.Keep class * { *; }

# Gson serialization models
-keep class app.krafted.tradeacademy.data.Asset { *; }
-keep class app.krafted.tradeacademy.data.Article { *; }
-keep class app.krafted.tradeacademy.data.Tip { *; }
-keep class app.krafted.tradeacademy.data.NewsResponse { *; }
-keep class app.krafted.tradeacademy.data.TipsResponse { *; }

# Room entities and converters
-keep class app.krafted.tradeacademy.data.WalletEntity { *; }
-keep class app.krafted.tradeacademy.data.HoldingEntity { *; }
-keep class app.krafted.tradeacademy.data.TradeEntity { *; }
-keep class app.krafted.tradeacademy.data.TradeType { *; }
-keep class app.krafted.tradeacademy.data.TradeTypeConverter { *; }

# Gson internals
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
