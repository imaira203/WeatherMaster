import java.util.Properties
import java.io.FileInputStream

// ---- Load key.properties if present
val keyProperties = Properties()
val keyPropertiesFile = rootProject.file("key.properties")
if (keyPropertiesFile.exists()) {
    keyPropertiesFile.inputStream().use { keyProperties.load(it) }
}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")          // dùng id mới
    id("dev.flutter.flutter-gradle-plugin")
}

android {
    namespace = "com.pranshulgg.weather_master_app"
    compileSdk = flutter.compileSdkVersion
    // ndkVersion = flutter.ndkVersion          // tuỳ bạn, có thể bỏ nếu không cần

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions { jvmTarget = JavaVersion.VERSION_17.toString() }

    defaultConfig {
        applicationId = "com.pranshulgg.weather_master_app"
        minSdk = flutter.minSdkVersion
        targetSdk = flutter.targetSdkVersion
        versionCode = flutter.versionCode
        versionName = flutter.versionName
    }

    signingConfigs {
        // Chỉ tạo release signing nếu file tồn tại
        if (keyPropertiesFile.exists()) {
            create("release") {
                keyAlias = keyProperties["keyAlias"] as String
                keyPassword = keyProperties["keyPassword"] as String
                storeFile = file(keyProperties["storeFile"] as String)
                storePassword = keyProperties["storePassword"] as String
            }
        }
    }

    buildTypes {
        getByName("debug") {
            // debug không cần signing riêng
            // signingConfig = signingConfigs.getByName("debug") // mặc định
            isMinifyEnabled = false
        }
        getByName("release") {
            // Chỉ gán signing khi có file, nếu không Gradle sẽ dùng debug keystore khi assembleRelease
            if (keyPropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            } else {
                // Tùy chọn: fallback về debug để vẫn build được release nội bộ
                // signingConfig = signingConfigs.getByName("debug")
            }
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

    packaging {
        jniLibs { useLegacyPackaging = true }
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

flutter { source = "../.." }

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.4")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("com.google.android.material:material:1.12.0")
}
