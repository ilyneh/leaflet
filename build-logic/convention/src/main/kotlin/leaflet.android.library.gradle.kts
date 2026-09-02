import com.ilynehdev.leaflet.buildlogic.convention.libs

plugins {
    id("com.android.library")
}

android {
    compileSdk {
        version = release(37) { minorApiLevel = 1 }
    }
    defaultConfig {
        minSdk = 23
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    testOptions {
        animationsDisabled = true
    }
}

dependencies {
    "testImplementation"(libs.findLibrary("junit").get())
    "androidTestImplementation"(libs.findLibrary("androidx.junit").get())
}
