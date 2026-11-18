plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.mohuerweima.uvc"
    compileSdk = 34 // 改用最稳定的34

    defaultConfig {
        applicationId = "com.mohuerweima.uvc"
        minSdk = 24 // 改成24，这样主人的安卓15手机绝对能装，别的手机也能装喵
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.herohan:UVCAndroid:1.0.11")
}
