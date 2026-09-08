import com.ilynehdev.leaflet.buildlogic.convention.libs

plugins {
    id("com.android.library")
}

android {
    compileSdk {
        version = release(37)
    }
    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    testOptions {
        animationsDisabled = true
    }
}

dependencies {
    "implementation"(libs.findLibrary("androidx.core.ktx").get())

    "testImplementation"(libs.findLibrary("junit").get())
    "testImplementation"(libs.findLibrary("androidx.junit").get())
    "testImplementation"(libs.findLibrary("robolectric").get())
    "testImplementation"(libs.findLibrary("kotlinx.coroutines.test").get())
    "androidTestImplementation"(libs.findLibrary("androidx.junit").get())
}
