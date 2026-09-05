import com.ilynehdev.leaflet.buildlogic.convention.libs

plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    "testImplementation"(libs.findLibrary("junit").get())
}