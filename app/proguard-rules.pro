# ============================================================
# QuickApply ProGuard / R8 rules
# ============================================================

# ------------------------------------------------------------
# General Kotlin / Compose
# ------------------------------------------------------------
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes SourceFile,LineNumberTable

-dontwarn kotlin.**
-dontwarn kotlinx.**
-keep class kotlin.Metadata { *; }

# ------------------------------------------------------------
# kotlinx.serialization
# Required so @Serializable data classes (CvEntry, Draft, etc.)
# keep their serializers and field names
# ------------------------------------------------------------
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

-keep,includedescriptorclasses class com.t.quickapply.**$$serializer { *; }
-keepclassmembers class com.t.quickapply.** {
    *** Companion;
}
-keepclasseswithmembers class com.t.quickapply.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep all data/model classes used with serialization, Gson, or Firestore
-keep class com.t.quickapply.data.local.CvEntry { *; }
-keep class com.t.quickapply.data.model.Draft { *; }
-keep class com.t.quickapply.domain.model.SentApplication { *; }

# ------------------------------------------------------------
# Retrofit + Gson (Groq AI API models)
# ------------------------------------------------------------
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes Exceptions

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepattributes RuntimeVisibleAnnotations

# Gson
-dontwarn com.google.gson.**
-keep class com.google.gson.** { *; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep AI model classes (request/response bodies parsed by Gson)
-keep class com.t.quickapply.data.remote.ai.** { *; }
-keepclassmembers class com.t.quickapply.data.remote.ai.** {
    <fields>;
    <init>(...);
}

# ------------------------------------------------------------
# Firebase (Auth, Firestore)
# ------------------------------------------------------------
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Firestore needs no-arg constructors + getters/setters on model classes
-keepclassmembers class com.t.quickapply.data.model.Draft {
    <init>();
    <fields>;
    <methods>;
}
-keepclassmembers class com.t.quickapply.domain.model.SentApplication {
    <init>();
    <fields>;
    <methods>;
}

# ------------------------------------------------------------
# Google Sign-In / Google Play Services Auth
# ------------------------------------------------------------
-keep class com.google.android.gms.auth.** { *; }
-keep class com.google.android.gms.common.** { *; }
-dontwarn com.google.android.gms.**

# ------------------------------------------------------------
# Gmail API / Google API Client (used for sending applications)
# ------------------------------------------------------------
-keep class com.google.api.client.** { *; }
-keep class com.google.api.services.gmail.** { *; }
-keep class com.google.auth.** { *; }
-dontwarn com.google.api.client.**
-dontwarn com.google.api.services.**
-dontwarn com.google.auth.**
-dontwarn org.apache.http.**
-dontwarn org.apache.commons.**

# Generic Google API model classes use reflection-based field access
-keepclassmembers class com.google.api.client.json.GenericJson {
    *;
}
-keepclassmembers class * extends com.google.api.client.json.GenericJson {
    <fields>;
}

# ------------------------------------------------------------
# javax.mail / Android Mail (com.sun.mail) — used by GmailSender
# ------------------------------------------------------------
-keep class javax.mail.** { *; }
-keep class javax.activation.** { *; }
-keep class com.sun.mail.** { *; }
-dontwarn javax.mail.**
-dontwarn javax.activation.**
-dontwarn com.sun.mail.**
-dontwarn javax.naming.**

# ------------------------------------------------------------
# PDFBox Android (resume extraction / writing)
# ------------------------------------------------------------
-keep class com.tom_roush.pdfbox.** { *; }
-keep class com.tom_roush.fontbox.** { *; }
-keep class com.tom_roush.harmony.** { *; }
-dontwarn com.tom_roush.**
-dontwarn org.apache.pdfbox.**

# ------------------------------------------------------------
# Apache POI (DOCX extraction)
# ------------------------------------------------------------
-keep class org.apache.poi.** { *; }
-keep class org.apache.xmlbeans.** { *; }
-keep class org.openxmlformats.** { *; }
-keep class schemasMicrosoftComOfficeOffice.** { *; }
-keep class schemaorg_apache_xmlbeans.** { *; }
-dontwarn org.apache.poi.**
-dontwarn org.apache.xmlbeans.**
-dontwarn org.openxmlformats.**
-dontwarn org.w3c.dom.**
-dontwarn javax.xml.stream.**
-dontwarn org.apache.commons.compress.**

# ------------------------------------------------------------
# AndroidX DataStore
# ------------------------------------------------------------
-keep class androidx.datastore.*.** { *; }
-dontwarn androidx.datastore.**

# ------------------------------------------------------------
# Coroutines
# ------------------------------------------------------------
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# ------------------------------------------------------------
# Compose / Navigation (safety nets — AGP usually handles these)
# ------------------------------------------------------------
-keep class androidx.navigation.** { *; }
-dontwarn androidx.navigation.**

# ------------------------------------------------------------
# ViewModel Factories — keep constructors so reflection-based
# instantiation (if any) doesn't break
# ------------------------------------------------------------
-keepclassmembers class com.t.quickapply.presentation.**.*ViewModel {
    <init>(...);
}
-keepclassmembers class com.t.quickapply.presentation.**.*ViewModelFactory {
    <init>(...);
}

# ------------------------------------------------------------
# Enum classes (used in ResumeWriter.LineType etc.)
# ------------------------------------------------------------
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ------------------------------------------------------------
# Parcelable / Serializable safety (if used anywhere)
# ------------------------------------------------------------
-keepnames class * implements android.os.Parcelable
-keepnames class * implements java.io.Serializable

# ------------------------------------------------------------
# Remove debug logging in release builds
# ------------------------------------------------------------
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# ------------------------------------------------------------
# Firebase Remote Config
# ------------------------------------------------------------
-keep class com.google.firebase.remoteconfig.** { *; }
-dontwarn com.google.firebase.remoteconfig.**

# Keep our provider object intact (object + init block)
-keep class com.t.quickapply.data.remote.config.RemoteConfigProvider { *; }

-dontwarn java.awt.**
-dontwarn com.graphbuilder.**
-keep class com.graphbuilder.** { *; }

# ------------------------------------------------------------
# Google Sign-In / Credentials — more complete coverage
# ------------------------------------------------------------
-keep class com.google.android.gms.auth.api.signin.** { *; }
-keep class com.google.android.gms.tasks.** { *; }
-keep class com.google.android.gms.common.api.** { *; }
-keep interface com.google.android.gms.common.api.** { *; }

# GoogleSignInAccount and related model classes need full reflection access
-keepclassmembers class com.google.android.gms.auth.api.signin.GoogleSignInAccount {
    *;
}

# Firebase Auth credential classes
-keep class com.google.firebase.auth.** { *; }
-keepclassmembers class com.google.firebase.auth.** {
    *;
}

# Keep your own Auth-related classes fully intact (not just constructors)
-keep class com.t.quickapply.data.remote.firebase.GoogleAuthClient { *; }
-keep class com.t.quickapply.presentation.auth.AuthViewModel { *; }